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


import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.model.jdbc.LayoutJdbcDB;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Warning;

import java.sql.SQLException;
import java.util.Collection;

/**
 * The Layout Model.
 */
public class LayoutModel extends KeyedEntityModel<Layout> implements CascadeDeleteProvider<Layout> {

	protected LayoutModel() {
		super(Layout.class);
	}

	static private LayoutModel self_ = new LayoutModel();

	// the 2 different LAyout Seating types
	static public final String AUDITORIUM = "Auditorium";
	static public final String TABLE = "Table";

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static LayoutModel getReference() {
		return (self_);
	}

	/**
	 * Delete a layout. This will first delete seats and tables for the layout.
	 * @param layout - the Layout
	 * @throws Exception
	 */
	public void delete(Layout layout) throws Exception {
		SeatModel.getReference().setNotifyListeners(false);
		TableModel.getReference().setNotifyListeners(false);
		JdbcDB.startTransaction();
		try {
			SeatModel.getReference().deleteSeatsForLayout(layout.getKey());
			TableModel.getReference().deleteTablesForLayout(layout.getKey());
			db_.delete(layout.getKey());
		} catch (SQLException se) {
			if (se.getSQLState().equalsIgnoreCase("23000")) {
				Errmsg.getErrorHandler().notice("Cannot delete record. Another record references it");
				JdbcDB.rollbackTransaction();
				return;
			}
			JdbcDB.rollbackTransaction();
			throw se;
		} catch (Exception e) {
			JdbcDB.rollbackTransaction();
			throw e;
		}
		finally{
			SeatModel.getReference().setNotifyListeners(true);
			TableModel.getReference().setNotifyListeners(true);
		}
		JdbcDB.commitTransaction();
		refresh();
	}

	/**
	 * Gets  all layouts.
	 * 
	 * @return  all layouts
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Layout> getAllLayouts() throws Exception {
		LayoutJdbcDB sdb = (LayoutJdbcDB) db_;
		return sdb.readAll();
	}

	/**
	 * create new layout.
	 * 
	 * @return the layout
	 */
	public Layout newLayout() {
		return (super.newRecord());
	}

	/**
	 * Gets a layout by key.
	 * 
	 * @param num
	 *            the key
	 * 
	 * @return the layout
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Layout getLayout(int num) throws Exception {
		return (super.getRecord(num));
	}

	/**
	 * Gets the number of seats in the layout for a show.
	 * 
	 * @param showid
	 *            the show key
	 * 
	 * @return the number of seats
	 */
	public static int getNumSeats(int showid) {
		try {
			Show s = ShowModel.getReference().getShow(showid);
			Layout l = getReference().getLayout(s.getLayout().intValue());
			return l.getNumSeats().intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Gets the number of rows in the layout for a show.
	 * 
	 * @param showid
	 *            the show key
	 * 
	 * @return the number of rows
	 */
	public static int getNumRows(int showid) {
		try {
			Show s = ShowModel.getReference().getShow(showid);
			Layout l = getReference().getLayout(s.getLayout().intValue());
			return l.getNumRows().intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Gets the center seat value of the layout for a show.
	 * 
	 * @param showid
	 *            the show key
	 * 
	 * @return the center seat value
	 */
	public static int getCenter(int showid) {
		try {
			Show s = ShowModel.getReference().getShow(showid);
			Layout l = getReference().getLayout(s.getLayout().intValue());
			int c = l.getCenterSeat().intValue();
			if (c > 0)
				return c;

			return l.getNumSeats().intValue() / 2;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Copy a layout to create a new one. This copies all of the seats or tables.
	 * 
	 * @param oldLayout
	 *            the old layout
	 * @param newName
	 *            the new name
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void copyLayout(Layout oldLayout, String newName) throws Exception {
		
		JdbcDB.startTransaction();
		try {
			// shut off listener notification or the Ui will go wild during the update
			SeatModel.getReference().setNotifyListeners(false);
			
			// copy the layout
			Layout newLayout = (Layout) oldLayout.copy();
			newLayout.setKey(-1);
			newLayout.setName(newName);
			int newKey = saveRecord(newLayout);
			newLayout = getLayout(newKey);
			
			// copy the seats or tables
			if (newLayout.getSeating().equals(AUDITORIUM)) {
				Collection<Seat> seats = SeatModel.getReference()
						.getSeatsForLayout(oldLayout.getKey());
				for (Seat seat : seats) {
					Seat newSeat = (Seat) seat.copy();
					newSeat.setKey(-1);
					newSeat.setLayout(Integer.valueOf(newLayout.getKey()));
					SeatModel.getReference().saveRecord(newSeat);
				}
			} else {
				Collection<Table> tables = TableModel.getReference()
						.getTablesForLayout(oldLayout.getKey());
				for (Table table : tables) {
					Table newTable = (Table) table.copy();
					newTable.setKey(-1);
					newTable.setLayout(Integer.valueOf(newLayout.getKey()));
					TableModel.getReference().saveRecord(newTable);
				}
			}

		} catch (Exception e) {
			SeatModel.getReference().setNotifyListeners(true);
			JdbcDB.rollbackTransaction();
			if (e.getMessage().contains("Unique")) {
				throw new Warning(
						"Please enter a unique name for the new Layout");
			}
			throw e;
		}
		
		// turn listener notification back on
		SeatModel.getReference().setNotifyListeners(true);
		JdbcDB.commitTransaction();
		
		// notifiy listeners that the Seat model has changed
		SeatModel.getReference().refreshListeners();
		
		// notify listeners that this Model has changed
		refresh();
	}

	@Override
	public String getCascadeDeleteWarning() {
		return null;
	}

	@Override
	public void cascadeDelete(Layout entity) {
		try {
			this.delete(entity);
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}
	
}
