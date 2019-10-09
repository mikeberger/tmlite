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

import com.mbb.TicketMaven.model.entity.Ticket;

/**
 * JDBC layer for Ticket Entitys
 */
public class TicketJdbcDB extends JdbcDB<Ticket> {

	/**
	 * Instantiates a new ticket jdbc db.
	 */
	TicketJdbcDB() {
		super("tickets");
		// new JdbcDBUpgrader("select ticket_price from tickets;",
		// "alter table tickets add ticket_price integer;").upgrade();
		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Ticket ticket) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO tickets ( customer_id, show_id, seat_id, ticket_price )" + " VALUES( ?, ?, ?, ?)")) {

			stmt.setInt(1, ticket.getCustomerId().intValue());
			stmt.setInt(2, ticket.getShowId().intValue());
			stmt.setInt(3, ticket.getSeatId().intValue());
			if (ticket.getPrice() != null)
				stmt.setInt(4, ticket.getPrice().intValue());
			else
				stmt.setInt(4, 0);

			stmt.executeUpdate();
			ticket.setKey(getIdentity());
		}
		writeCache(ticket);
		return ticket.getKey();

	}

	/**
	 * Gets the ticket keys sorted by customer.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_
				.prepareStatement("SELECT record_id FROM tickets ORDER BY customer_id")) {
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
	public Ticket newObj() {
		return (new Ticket());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM tickets " + "INNER JOIN customers ON (tickets.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (tickets.show_id = shows.record_id) "
						+ "INNER JOIN seats ON (tickets.seat_id = seats.record_id) " + "WHERE tickets.record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT TOP 50000 * FROM tickets "
				+ "INNER JOIN customers ON (tickets.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (tickets.show_id = shows.record_id) "
				+ "INNER JOIN seats ON (tickets.seat_id = seats.record_id) ");
		return stmt;
	}

	/**
	 * Gets the tickets for a show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @return the tickets for show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public ArrayList<Ticket> getTicketsForShow(int show_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM tickets " + "INNER JOIN customers ON (tickets.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (tickets.show_id = shows.record_id) "
						+ "INNER JOIN seats ON (tickets.seat_id = seats.record_id)"
						+ "WHERE shows.record_id = ? ORDER BY customers.last_name");
		stmt.setInt(1, show_id);
		return (query(stmt));
	}

	/**
	 * Gets the tickets for a customer.
	 * 
	 * @param cust_id
	 *            the cust key
	 * 
	 * @return the tickets for customer
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Ticket> getTicketsForCustomer(int cust_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM tickets " + "INNER JOIN customers ON (tickets.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (tickets.show_id = shows.record_id) "
						+ "INNER JOIN seats ON (tickets.seat_id = seats.record_id)" + "WHERE customers.record_id = ?");
		stmt.setInt(1, cust_id);
		return (query(stmt));
	}

	/**
	 * Gets the ticket for a seat in a show.
	 * 
	 * @param show_id
	 *            the show key
	 * @param row
	 *            the row
	 * @param seat
	 *            the seat
	 * 
	 * @return the ticket for seat in show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Ticket getTicketForSeatInShow(int show_id, String row, int seat) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM tickets " + "INNER JOIN customers ON (tickets.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (tickets.show_id = shows.record_id) "
						+ "INNER JOIN seats ON (tickets.seat_id = seats.record_id)"
						+ "WHERE shows.record_id = ? AND seats.row = ? AND seats.seat = ?");
		stmt.setInt(1, show_id);
		stmt.setString(2, row);
		stmt.setInt(3, seat);
		Collection<Ticket> col = query(stmt);
		if (col.size() > 0)
			return (col.iterator().next());

		return (null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Ticket createFrom(ResultSet r) throws SQLException {
		Ticket ticket = new Ticket();
		ticket.setKey(r.getInt("record_id"));
		ticket.setCustomerId(Integer.valueOf(r.getInt("customer_id")));
		ticket.setShowId(Integer.valueOf(r.getInt("show_id")));
		ticket.setSeatId(Integer.valueOf(r.getInt("seat_id")));
		ticket.setPrice(Integer.valueOf(r.getInt("ticket_price")));
		if (r.wasNull())
			ticket.setPrice(null);

		// customer
		ticket.setCustomerName(r.getString("last_name") + ", " + r.getString("first_name"));

		ticket.setSpecialNeeds(r.getString("special_needs"));
		ticket.setResident(r.getString("resident"));

		// seat
		ticket.setRow(r.getString("row"));

		// show
		ticket.setShowName(r.getString("name"));
		if (r.getTimestamp("time") != null)
			ticket.setShowDate(new java.util.Date(r.getTimestamp("time").getTime()));
		if (ticket.getPrice() == null)
			ticket.setPrice(Integer.valueOf(r.getInt("price")));

		return ticket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Ticket ticket) throws Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE tickets SET "
				+ "customer_id = ?, show_id = ?, seat_id = ?, ticket_price = ?" + " WHERE record_id = ?")) {

			stmt.setInt(1, ticket.getCustomerId().intValue());
			stmt.setInt(2, ticket.getShowId().intValue());
			stmt.setInt(3, ticket.getSeatId().intValue());

			if (ticket.getPrice() != null)
				stmt.setInt(4, ticket.getPrice().intValue());
			else
				stmt.setInt(4, 0);
			stmt.setInt(5, ticket.getKey());

			stmt.executeUpdate();
		}
		delCache(ticket.getKey());
		writeCache(ticket);
	}

	/**
	 * Delete tickets for a show.
	 * 
	 * @param show_id
	 *            the show key
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void deleteTicketsForShow(int show_id) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM tickets WHERE show_id = ?")) {
			stmt.setInt(1, show_id);
			stmt.executeUpdate();
		}
		sync();
	}

	public void deleteTicketsForCustomer(int key) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM tickets WHERE customer_id = ?")) {
			stmt.setInt(1, key);
			stmt.executeUpdate();
		}
		sync();

	}
}
