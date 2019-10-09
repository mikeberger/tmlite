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

package com.mbb.TicketMaven.model.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Seat;

/**
 * JDBC layer for Seat Entitys
 */
public class SeatJdbcDB extends JdbcDB<Seat> {

	/**
	 * Instantiates a new seat jdbc db.
	 *
	 */
	SeatJdbcDB() {
		super("seats");
		new JdbcDBUpgrader("select label from seats;", "alter table seats add label varchar(10) default '';").upgrade();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Seat seat) throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("INSERT INTO seats ( row, seat, weight, \"END\", available, layout, zone, label )"
						+ " VALUES( ?, ?, ?, ?, ?, ?, ?, ? )")) {

			stmt.setString(1, seat.getRow());
			stmt.setInt(2, seat.getSeat().intValue());
			stmt.setInt(3, seat.getWeight().intValue());
			stmt.setString(4, seat.getEnd());
			stmt.setString(5, seat.getAvailable());
			stmt.setInt(6, seat.getLayout().intValue());
			if (seat.getZone() != null)
				stmt.setInt(7, seat.getZone().intValue());
			else
				stmt.setNull(7, java.sql.Types.INTEGER);
			stmt.setString(8, seat.getLabel());

			stmt.executeUpdate();

			seat.setKey(getIdentity());
		}
		writeCache(seat);
		return seat.getKey();

	}

	/**
	 * Gets the Seat keys sorted by row and seat.
	 *
	 * @return the keys
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT record_id FROM seats ORDER BY row, seat")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				keys.add(Integer.valueOf(rs.getInt("record_id")));
			}
		}
		return (keys);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#newObj()
	 */
	@Override
	public Seat newObj() {
		return (new Seat());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM seats WHERE record_id = ?");
		stmt.setInt(1, key);
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSAll()
	 */
	@Override
	PreparedStatement getPSAll() throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM seats");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Seat createFrom(ResultSet r) throws SQLException {
		Seat seat = new Seat();
		seat.setKey(r.getInt("record_id"));
		seat.setRow(r.getString("row"));
		seat.setSeat(Integer.valueOf(r.getInt("seat")));
		seat.setWeight(Integer.valueOf(r.getInt("weight")));
		seat.setEnd(r.getString("end"));
		seat.setAvailable(r.getString("available"));
		seat.setLayout(Integer.valueOf(r.getInt("layout")));
		int zi = r.getInt("zone");
		if (r.wasNull())
			seat.setZone(null);
		else
			seat.setZone(Integer.valueOf(zi));
		seat.setLabel(r.getString("label"));

		return seat;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Seat seat) throws Exception, Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE seats SET "
				+ "row = ?, seat = ?, weight = ?, \"END\" = ?, available = ?, layout = ?, zone = ?, label = ?"
				+ " WHERE record_id = ?")) {

			stmt.setString(1, seat.getRow());
			stmt.setInt(2, seat.getSeat().intValue());
			stmt.setInt(3, seat.getWeight().intValue());
			stmt.setString(4, seat.getEnd());
			stmt.setString(5, seat.getAvailable());
			stmt.setInt(6, seat.getLayout().intValue());
			if (seat.getZone() != null)
				stmt.setInt(7, seat.getZone().intValue());
			else
				stmt.setNull(7, java.sql.Types.INTEGER);
			stmt.setString(8, seat.getLabel());

			stmt.setInt(9, seat.getKey());

			stmt.executeUpdate();
		}
		delCache(seat.getKey());
		writeCache(seat);
	}

	/**
	 * Gets the available seats for a show - does not include exclusive zones
	 *
	 * @param show_id
	 *            the show key
	 * @param layout_id
	 *            the layout key for the show
	 *
	 * @return the available seats for show
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getAvailableSeatsForShow(int show_id, int layout_id) throws Exception {
		// select seats that have no matching ticket record for a given show
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from seats "
				+ "LEFT OUTER JOIN tickets ON (seats.record_id = tickets.seat_id and tickets.show_id = ?) "
				+ "LEFT OUTER JOIN zones ON (seats.zone = zones.record_id) "
				+ "where seats.layout = ? AND tickets.show_id is null AND seats.available = 'Y' AND ( zones.exclusive is null OR zones.exclusive != 'Y') ORDER BY seats.row, seats.seat");
		stmt.setInt(1, show_id);
		stmt.setInt(2, layout_id);
		return (query(stmt));
	}

	/**
	 * Gets the unsold seats for a show including exclusive zones
	 *
	 * @param show_id
	 *            the show key
	 * @param layout_id
	 *            the layout key for the show
	 *
	 * @return the available seats for show
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getUnsoldSeatsForShow(int show_id, int layout_id) throws Exception {
		// select seats that have no matching ticket record for a given show
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from seats "
				+ "LEFT OUTER JOIN tickets ON (seats.record_id = tickets.seat_id and tickets.show_id = ?) "
				+ "LEFT OUTER JOIN zones ON (seats.zone = zones.record_id) "
				+ "where seats.layout = ? AND tickets.show_id is null AND seats.available = 'Y' ORDER BY seats.row, seats.seat");
		stmt.setInt(1, show_id);
		stmt.setInt(2, layout_id);
		return (query(stmt));
	}

	/**
	 * Gets all seats for a layout.
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
		// select seats that have no matching ticket record for a given show
		PreparedStatement stmt = connection_
				.prepareStatement("SELECT * from seats " + "where layout = ? ORDER BY seats.row, seats.seat");
		stmt.setInt(1, layout_id);
		return (query(stmt));
	}

	/**
	 * Delete seats for a layout.
	 *
	 * @param layout_id
	 *            the layout key
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void deleteSeatsForLayout(int layout_id) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("delete FROM seats where layout = ?")) {

			stmt.setInt(1, layout_id);
			stmt.executeUpdate();
		}
		emptyCache();
	}

	/**
	 * Gets the available special needs seats for a show.
	 *
	 * @param show_id
	 *            the show key
	 * @param layout_id
	 *            the layout key for the show
	 * @param zoneid
	 *            the special needs zone key
	 *
	 * @return the available special seats for show
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Seat> getAvailableSpecialSeatsForShow(int show_id, int layout_id, int zoneid) throws Exception {
		// select seats that have no matching ticket record for a given show
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from seats LEFT JOIN tickets ON "
				+ "(seats.record_id = tickets.seat_id and tickets.show_id = ?) "
				+ "where seats.layout = ? AND tickets.show_id is null AND seats.available = 'Y' AND seats.zone = ? ORDER BY seats.row, seats.seat");
		stmt.setInt(1, show_id);
		stmt.setInt(2, layout_id);
		stmt.setInt(3, zoneid);
		return (query(stmt));
	}

	/**
	 * Gets a seat given layout, row, and seat number
	 *
	 * @param row
	 *            the row
	 * @param seat
	 *            the seat
	 * @param layout
	 *            the layout key
	 *
	 * @return the seat
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Seat getSeat(String row, int seat, int layout) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM seats " + "WHERE seats.row = ? AND seats.seat = ? AND seats.layout = ?");
		stmt.setString(1, row);
		stmt.setInt(2, seat);
		stmt.setInt(3, layout);
		Collection<Seat> col = query(stmt);
		if (col.size() > 0)
			return (col.iterator().next());

		return (null);
	}

}
