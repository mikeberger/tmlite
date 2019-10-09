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

import com.mbb.TicketMaven.model.entity.TicketRequest;

/**
 * JDBC layer for Request Entitys
 */
public class RequestJdbcDB extends JdbcDB<TicketRequest> {

	/**
	 * Instantiates a new request jdbc db.
	 *
	 */
	RequestJdbcDB() {
		super("requests");
		// new JdbcDBUpgrader("select discount from requests;",
		// "alter table requests add discount double;").upgrade();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(TicketRequest request) throws Exception, Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO requests ( customer_id, show_id, tickets, discount )" + " VALUES( ?, ?, ?, ? )")) {

			stmt.setInt(1, request.getCustomerId().intValue());
			stmt.setInt(2, request.getShowId().intValue());
			stmt.setInt(3, request.getTickets().intValue());
			if (request.getDiscount() != null)
				stmt.setDouble(4, request.getDiscount().doubleValue());
			else
				stmt.setDouble(4, 0);

			stmt.executeUpdate();
		}
		request.setKey(getIdentity());
		writeCache(request);
		return request.getKey();

	}

	/**
	 * Gets the request keys sorted by show and customer.
	 *
	 * @return the keys
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_
				.prepareStatement("SELECT record_id FROM requests ORDER BY show_id, customer_id")) {
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
	public TicketRequest newObj() {
		return (new TicketRequest());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM requests " + "INNER JOIN customers ON (requests.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (requests.show_id = shows.record_id) " + "WHERE requests.record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT TOP 50000 * FROM requests "
				+ "INNER JOIN customers ON (requests.customer_id = customers.record_id) "
				+ "INNER JOIN shows ON (requests.show_id = shows.record_id) ");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	TicketRequest createFrom(ResultSet r) throws SQLException {
		TicketRequest request = new TicketRequest();
		request.setKey(r.getInt("record_id"));
		request.setCustomerId(Integer.valueOf(r.getInt("customer_id")));
		request.setShowId(Integer.valueOf(r.getInt("show_id")));
		request.setTickets(Integer.valueOf(r.getInt("tickets")));
		request.setCustomerName(r.getString("last_name") + ", " + r.getString("first_name"));
		request.setShowName(r.getString("name"));
		if (r.getTimestamp("time") != null)
			request.setShowDate(new java.util.Date(r.getTimestamp("time").getTime()));
		request.setSpecialNeeds(r.getString("special_needs"));
		request.setTotalTickets(Integer.valueOf(r.getInt("total_tickets")));
		request.setTotalQuality(Integer.valueOf(r.getInt("total_quality")));
		request.setDiscount(Double.valueOf(r.getDouble("discount")));

		return request;
	}

	/**
	 * Gets the requests for a show.
	 *
	 * @param show_id
	 *            the show key
	 *
	 * @return the requests for show
	 *
	 * @throws Exception
	 *             the exception
	 */
	public ArrayList<TicketRequest> getRequestsForShow(int show_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM requests " + "INNER JOIN customers ON (requests.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (requests.show_id = shows.record_id) WHERE requests.show_id = ?");
		stmt.setInt(1, show_id);
		return query(stmt);
	}

	/**
	 * Gets the requests for a customer.
	 *
	 * @param cust_id
	 *            the Customer key
	 *
	 * @return the requests for customer
	 *
	 * @throws Exception
	 *             the exception
	 */
	public ArrayList<TicketRequest> getRequestsForCustomer(int cust_id) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement(
				"SELECT * FROM requests " + "INNER JOIN customers ON (requests.customer_id = customers.record_id) "
						+ "INNER JOIN shows ON (requests.show_id = shows.record_id) WHERE requests.customer_id = ?");
		stmt.setInt(1, cust_id);
		return query(stmt);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(TicketRequest request) throws Exception, Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE requests SET "
				+ "customer_id = ?, show_id = ?, tickets = ?, discount = ?" + " WHERE record_id = ?")) {

			stmt.setInt(1, request.getCustomerId().intValue());
			stmt.setInt(2, request.getShowId().intValue());
			stmt.setInt(3, request.getTickets().intValue());
			if (request.getDiscount() != null)
				stmt.setDouble(4, request.getDiscount().doubleValue());
			else
				stmt.setDouble(4, 0);

			stmt.setInt(5, request.getKey());

			stmt.executeUpdate();
		}
		delCache(request.getKey());
		writeCache(request);
	}

	/**
	 * Delete all requests for given show.
	 *
	 * @param show_id
	 *            the show key
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void deleteRequestsForShow(int show_id) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM requests WHERE show_id = ?")) {
			stmt.setInt(1, show_id);
			stmt.executeUpdate();
		}
		sync();
	}

	public void deleteRequestsForCustomer(int key) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM requests WHERE customer_id = ?")) {
			stmt.setInt(1, key);
			stmt.executeUpdate();
		}
		sync();
	}

	public void moveRequests(int fromShow, int toShow) throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("UPDATE requests SET show_id = ? WHERE show_id = ?")) {
			stmt.setInt(1, toShow);
			stmt.setInt(2, fromShow);
			stmt.executeUpdate();
		}
		sync();
	}

}
