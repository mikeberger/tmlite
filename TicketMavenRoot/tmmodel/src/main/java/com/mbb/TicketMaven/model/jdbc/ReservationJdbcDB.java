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

import com.mbb.TicketMaven.model.entity.Reservation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JDBC layer for Reservation Entitys
 */
public class ReservationJdbcDB extends JdbcDB<Reservation> {

	/**
	 * Instantiates a new reservation jdbc db.
	 * 
	 */
	ReservationJdbcDB() {
		super("reservations");
		new JdbcDBUpgrader("select notes from reservations;",
				"alter table reservations add notes varchar(400) default '';").upgrade();
		new JdbcDBUpgrader("select payment from reservations;",
				"alter table reservations add payment varchar(50) default '';").upgrade();
		new JdbcDBUpgrader("select amount from reservations;",
				"alter table reservations add amount varchar(10) default '';").upgrade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Reservation reservation) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO reservations ( customer_id, show_id, table_id, num, payment, notes, amount )"
						+ " VALUES( ?, ?, ?, ?, ?,?,?)")) {

			stmt.setInt(1, reservation.getCustomerId().intValue());
			stmt.setInt(2, reservation.getShowId().intValue());
			stmt.setInt(3, reservation.getTableId().intValue());
			stmt.setInt(4, reservation.getNum().intValue());
			stmt.setString(5, reservation.getPayment());
			stmt.setString(6, reservation.getNotes());
			stmt.setString(7, reservation.getAmount());

			stmt.executeUpdate();
		}
		writeCache(reservation);
		return reservation.getKey();

	}

	/**
	 * Gets the reservation keys sorted by customer.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_
				.prepareStatement("SELECT record_id FROM reservations ORDER BY customer_id")) {
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
	public Reservation newObj() {
		return (new Reservation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM reservations "
				+ "INNER JOIN customers ON (reservations.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (reservations.show_id = shows.record_id) "
				+ "INNER JOIN tmtables ON (reservations.table_id = tmtables.record_id) "
				+ "WHERE reservations.record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT TOP 50000 * FROM reservations "
				+ "INNER JOIN customers ON (reservations.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (reservations.show_id = shows.record_id) "
				+ "INNER JOIN tmtables ON (reservations.table_id = tmtables.record_id) ");
		return stmt;
	}

	/**
	 * Gets the reservations for a show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @return the reservations for show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public ArrayList<Reservation> getReservationsForShow(int show_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM reservations "
				+ "INNER JOIN customers ON (reservations.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (reservations.show_id = shows.record_id) "
				+ "INNER JOIN tmtables ON (reservations.table_id = tmtables.record_id)"
				+ "WHERE shows.record_id = ? ORDER BY customers.last_name");
		stmt.setInt(1, show_id);
		return (query(stmt));
	}

	/**
	 * Gets the reservations for a customer.
	 * 
	 * @param cust_id
	 *            the customer key
	 * 
	 * @return the reservations for customer
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Reservation> getReservationsForCustomer(int cust_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM reservations "
				+ "INNER JOIN customers ON (reservations.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (reservations.show_id = shows.record_id) "
				+ "INNER JOIN tmtables ON (reservations.table_id = tmtables.record_id)"
				+ "WHERE customers.record_id = ?");
		stmt.setInt(1, cust_id);
		return (query(stmt));
	}

	/**
	 * Gets the reservations for a given table in a show.
	 * 
	 * @param show_id
	 *            the show key
	 * @param table_id
	 *            the table key
	 * 
	 * @return the reservations for table in show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Reservation> getReservationsForTableInShow(int show_id, int table_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM reservations "
				+ "INNER JOIN customers ON (reservations.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (reservations.show_id = shows.record_id) "
				+ "INNER JOIN tmtables ON (reservations.table_id = tmtables.record_id)"
				+ "WHERE shows.record_id = ? AND tmtables.record_id = ?");
		stmt.setInt(1, show_id);
		stmt.setInt(2, table_id);
		return (query(stmt));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Reservation createFrom(ResultSet r) throws SQLException {
		Reservation reservation = new Reservation();
		reservation.setKey(r.getInt("record_id"));
		reservation.setCustomerId(Integer.valueOf(r.getInt("customer_id")));
		reservation.setShowId(Integer.valueOf(r.getInt("show_id")));
		reservation.setTableId(Integer.valueOf(r.getInt("table_id")));
		reservation.setCustomerName(r.getString("last_name") + ", " + r.getString("first_name"));
		reservation.setShowName(r.getString("name"));
		reservation.setNum(Integer.valueOf(r.getInt("num")));
		reservation.setTableName(r.getString("label"));
		reservation.setPayment(r.getString("payment"));
		reservation.setNotes(r.getString("notes"));
		reservation.setAmount(r.getString("amount"));

		if (r.getTimestamp("time") != null)
			reservation.setShowDate(new java.util.Date(r.getTimestamp("time").getTime()));

		return reservation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Reservation request) throws Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE reservations SET "
				+ "customer_id = ?, show_id = ?, table_id = ?, num = ?, payment = ?, notes = ?, amount = ?"
				+ " WHERE record_id = ?")) {

			stmt.setInt(1, request.getCustomerId().intValue());
			stmt.setInt(2, request.getShowId().intValue());
			stmt.setInt(3, request.getTableId().intValue());
			stmt.setInt(4, request.getNum().intValue());
			stmt.setString(5, request.getPayment());
			stmt.setString(6, request.getNotes());
			stmt.setString(7, request.getAmount());

			stmt.setInt(8, request.getKey());

			stmt.executeUpdate();
		}
		delCache(request.getKey());
		writeCache(request);
	}

	/**
	 * Delete reservations for a given show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteReservationsForShow(int show_id) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM reservations WHERE show_id = ?")) {
			stmt.setInt(1, show_id);
			stmt.executeUpdate();
		}
		sync();
	}

	public void deleteReservationsForCustomer(int key) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM reservations WHERE customer_id = ?")) {
			stmt.setInt(1, key);
			stmt.executeUpdate();
		}
		sync();
	}
}
