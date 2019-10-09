/*
 * #%L
 * tmmodel
 * %%
 * Copyright (C) 2019 Michael Berger
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.mbb.TicketMaven.model;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.model.jdbc.SeatJdbcDB;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * The Seat Model
 */
public class SeatModel extends KeyedEntityModel<Seat> {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	protected SeatModel() {
		super(Seat.class);
	}

	static private SeatModel self_ = new SeatModel();

	// the Aisle types
	static public final String NONE = "None";
	static public final String LEFT = "Left";
	static public final String RIGHT = "Right";
	static public final String FRONT = "Front";

	// max number for weight values
	static public final int MAX_WEIGHT = 30;

	public static final String rowletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static SeatModel getReference() {
		return (self_);
	}

	/**
	 * Gets all seats.
	 * 
	 * @return the seats
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getAllSeats() throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		return sdb.readAll();
	}

	/**
	 * Generate missing in a Layout. Used to generate the initial set of seats
	 * in a rectangular grid. In the past, users could delete seats and this
	 * method added them back. That no longer should be needed.
	 * 
	 * @param layoutid
	 *            the layout key
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public static void generateMissingSeats(int layoutid) throws Exception {
		SeatModel sm = SeatModel.getReference();

		Layout l = LayoutModel.getReference().getLayout(layoutid);
		int rows = l.getNumRows().intValue();
		int seats = l.getNumSeats().intValue();
		
		Set<Integer> existingSeatIds = new HashSet<Integer>();
		Collection<Seat> existingSeats = SeatModel.getReference().getSeatsForLayout(layoutid);
		for( Seat seat : existingSeats )
			existingSeatIds.add(seat.getKey());
		

		// loop through the grid
		for (int r = 0; r < rows; r++) {

			String row = SeatModel.rowletters.substring(r, r + 1);
			for (int s = 1; s <= seats; s++) {

				// try to find the seat
				Seat seat = SeatModel.getReference().getSeat(row, s, layoutid);
				if (seat != null)
				{
					existingSeatIds.remove(seat.getKey());
					continue;
				}

				// seat was not there, so create it
				seat = sm.newSeat();
				seat.setRow(row);
				seat.setSeat(Integer.valueOf(s));

				// set aisle
				if (s == 1)
					seat.setEnd(SeatModel.LEFT);
				else if (s == seats)
					seat.setEnd(SeatModel.RIGHT);
				else
					seat.setEnd(SeatModel.NONE);

				// set the default quality based solely on row
				int q = MAX_WEIGHT - r;
				if (q < 1)
					q = 1;
				seat.setWeight(Integer.valueOf(q));

				seat.setAvailable("Y");
				seat.setLayout(Integer.valueOf(l.getKey()));
				sm.saveRecord(seat);
			}

		}
		
		// remove all of the seats that are no longer needed
		for( Integer id : existingSeatIds)
		{
			SeatModel.getReference().delete(id);
		}
		
		if( log.isLoggable(Level.FINE))
		{
			existingSeats = SeatModel.getReference().getSeatsForLayout(layoutid);
			for( Seat s : existingSeats )
			{
				log.fine("[" + s.getRow() + "," + s.getNumber() + "]\n");
			}

		}

	}

	/**
	 * create a new seat.
	 * 
	 * @return the seat
	 */
	public Seat newSeat() {
		return (super.newRecord());
	}

	/**
	 * Gets s seat by key.
	 * 
	 * @param num
	 *            the key
	 * 
	 * @return the seat
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Seat getSeat(int num) throws Exception {
		return (super.getRecord(num));
	}

	/**
	 * Gets the available seats for a show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @return the available seats for show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getAvailableSeatsForShow(int show_id)
			throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		Show show = ShowModel.getReference().getShow(show_id);
		Collection<Seat> seats = sdb.getAvailableSeatsForShow(show_id, show
				.getLayout().intValue());
		Collection<Seat> avail = new ArrayList<Seat>();
		int maxseat = LayoutModel.getNumSeats(show_id);
		for (Seat s : seats) {
			if (s.getSeat().intValue() <= maxseat)
				avail.add(s);
		}
		return avail;

	}
	
	/**
	 * Gets the unsold seats for a show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @return the unsold seats for show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getUnsoldSeatsForShow(int show_id)
			throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		Show show = ShowModel.getReference().getShow(show_id);
		Collection<Seat> seats = sdb.getUnsoldSeatsForShow(show_id, show
				.getLayout().intValue());
		Collection<Seat> avail = new ArrayList<Seat>();
		int maxseat = LayoutModel.getNumSeats(show_id);
		for (Seat s : seats) {
			if (s.getSeat().intValue() <= maxseat)
				avail.add(s);
		}
		return avail;

	}

	/**
	 * Gets the seats for a layout.
	 * 
	 * @param layout_id
	 *            the layout key
	 * 
	 * @return the seats for layout
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getSeatsForLayout(int layout_id) throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		Collection<Seat> seats = sdb.getSeatsForLayout(layout_id);
		return seats;

	}

	/**
	 * Delete seats for layout.
	 * 
	 * @param layout_id
	 *            the layout_id
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteSeatsForLayout(int layout_id) throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		sdb.deleteSeatsForLayout(layout_id);
	}

	/**
	 * Gets the available special seats for show.
	 * 
	 * @param show_id
	 *            the show_id
	 * @param zoneid
	 *            the zoneid
	 * 
	 * @return the available special seats for show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getAvailableSpecialSeatsForShow(int show_id,
			int zoneid) throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		Show show = ShowModel.getReference().getShow(show_id);
		Collection<Seat> seats = sdb.getAvailableSpecialSeatsForShow(show_id,
				show.getLayout().intValue(), zoneid);
		Collection<Seat> avail = new ArrayList<Seat>();
		int maxseat = LayoutModel.getNumSeats(show_id);
		for (Seat s : seats) {
			if (s.getSeat().intValue() <= maxseat)
				avail.add(s);
		}
		return avail;

	}

	/**
	 * Gets the seat.
	 * 
	 * @param row
	 *            the row
	 * @param seat
	 *            the seat
	 * @param layout
	 *            the layout
	 * 
	 * @return the seat
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Seat getSeat(String row, int seat, int layout) throws Exception {
		SeatJdbcDB sdb = (SeatJdbcDB) db_;
		return sdb.getSeat(row, seat, layout);
	}

	/**
	 * Number seats from left.
	 * 
	 * @param layout_id
	 *            the layout_id
	 * 
	 * @return the hash map< integer, integer>
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private HashMap<Integer, Integer> numberSeatsFromLeft(int layout_id)
			throws Exception {

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		Layout l = LayoutModel.getReference().getLayout(layout_id);

		for (int row = 0; row < l.getNumRows().intValue(); row++) {
			int number_from_left = 1;
			for (int seatnum = 1; seatnum <= l.getNumSeats().intValue(); seatnum++) {
				Seat s = getSeat(rowletters.substring(row, row + 1), seatnum,
						layout_id);
				if (s != null && "Y".equals(s.getAvailable())) {
					map.put(Integer.valueOf(s.getKey()), Integer.valueOf(
							number_from_left));
					number_from_left++;
				}
			}
		}

		seatNumberFromLeftMap.put(Integer.valueOf(layout_id), map);

		return map;
	}

	/**
	 * Clear seat numbers.
	 * 
	 * @param layout_id
	 *            the layout_id
	 */
	private void clearSeatNumbersFromLeft(int layout_id) {
		seatNumberFromLeftMap.remove(Integer.valueOf(layout_id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.KeyedEntityModel#saveRecord(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int saveRecord(Seat bean) throws Exception {
		int i = super.saveRecord(bean);
		clearSeatNumbersFromLeft(bean.getLayout().intValue());
		return i;
	}

	/**
	 * Gets the seat number from the left.
	 * 
	 * @param s
	 *            the s
	 * 
	 * @return the seat number
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Integer getSeatNumberFromLeft(Seat s) throws Exception {
		// look in cache first
		HashMap<Integer, Integer> map = seatNumberFromLeftMap
				.get(s.getLayout());
		if (map == null)
			map = numberSeatsFromLeft(s.getLayout().intValue());

		return map.get(Integer.valueOf(s.getKey()));
	}

	private HashMap<Integer, HashMap<Integer, Integer>> seatNumberFromLeftMap = new HashMap<Integer, HashMap<Integer, Integer>>();

	/**
	 * create a temporary table to hold the seat to seat number mapping so that
	 * reports can use it
	 * 
	 * @param layout_id
	 *            the layout
	 * @throws Exception
	 */
	public void loadSeatMappingTable(int layout_id) throws Exception {
		
		// really needed?
		if (Prefs.is(PrefName.NUMBER_SEATS_FROM_LEFT, "true")) {
			clearSeatNumbersFromLeft(layout_id);
		}
		
		try {
			JdbcDB.execSQL("drop table seatmapping");
		} catch (Exception e) { // empty
		}

		String createSql = "create temporary table seatmapping ("
				+ " seat_id integer," + " seat_number varchar(10) )"
				+ "ON COMMIT PRESERVE ROWS;";
		JdbcDB.execSQL(createSql);

		for( Seat s : getSeatsForLayout(layout_id) )
		{
			JdbcDB.execSQL("insert into seatmapping values(" + s.getKey()
					+ ",'" + s.getNumber() + "');");
		}

	}

}
