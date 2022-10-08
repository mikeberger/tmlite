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

import com.mbb.TicketMaven.model.entity.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JDBC Layer for Customer Entitys
 */
public class CustomerJdbcDB extends JdbcDB<Customer> {

	/**
	 * Instantiates a new customer jdbc db.
	 *
	 */
	public CustomerJdbcDB() {
		super("customers");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Customer cust) throws Exception, Exception {

		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO customers ( first_name, last_name, email, phone, notes, allowed_tickets, special_needs, total_tickets, total_quality, address, resident )"
						+ " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )")) {

			stmt.setString(1, cust.getFirstName());
			stmt.setString(2, cust.getLastName());
			stmt.setString(3, cust.getEmail());
			stmt.setString(4, cust.getPhone());
			stmt.setString(5, cust.getNotes());
			if (cust.getAllowedTickets() != null)
				stmt.setInt(6, cust.getAllowedTickets().intValue());
			else
				stmt.setInt(6, 0);
			stmt.setString(7, cust.getSpecialNeedsType());
			if (cust.getTotalTickets() != null)
				stmt.setInt(8, cust.getTotalTickets().intValue());
			else
				stmt.setInt(8, 0);
			if (cust.getTotalQuality() != null)
				stmt.setInt(9, cust.getTotalQuality().intValue());
			else
				stmt.setInt(9, 0);
			stmt.setString(10, cust.getAddress());
			stmt.setString(11, cust.getResident());
			stmt.executeUpdate();

			cust.setKey(getIdentity());
			writeCache(cust);
			return cust.getKey();
		}

	}

	/**
	 * Gets the list of customer keys ordered by name.
	 *
	 * @return the keys
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_
				.prepareStatement("SELECT record_id FROM customers ORDER BY last_name, first_name")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				keys.add(Integer.valueOf(rs.getInt("record_id")));
			}

			return (keys);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#newObj()
	 */
	@Override
	public Customer newObj() {
		return (new Customer());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM customers WHERE record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM customers");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Customer createFrom(ResultSet r) throws SQLException {
		Customer cust = new Customer();
		cust.setKey(r.getInt("record_id"));
		cust.setFirstName(r.getString("first_name"));
		cust.setLastName(r.getString("last_name"));
		cust.setEmail(r.getString("email"));
		cust.setPhone(r.getString("phone"));
		cust.setNotes(r.getString("notes"));
		cust.setAllowedTickets(Integer.valueOf(r.getInt("allowed_tickets")));
		cust.setSpecialNeedsType(r.getString("special_needs"));
		cust.setTotalTickets(Integer.valueOf(r.getInt("total_tickets")));
		cust.setTotalQuality(Integer.valueOf(r.getInt("total_quality")));
		cust.setAddress(r.getString("address"));
		cust.setResident(r.getString("resident"));

		return cust;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Customer cust) throws Exception, Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE customers SET "
				+ "first_name = ?, last_name = ?, email = ?, phone = ?,"
				+ "notes = ?, allowed_tickets = ?, special_needs = ?, total_tickets = ?, total_quality = ?, address = ?, resident = ? "
				+ " WHERE record_id = ?")) {

			stmt.setString(1, cust.getFirstName());
			stmt.setString(2, cust.getLastName());
			stmt.setString(3, cust.getEmail());
			stmt.setString(4, cust.getPhone());
			stmt.setString(5, cust.getNotes());
			if (cust.getAllowedTickets() != null)
				stmt.setInt(6, cust.getAllowedTickets().intValue());
			stmt.setString(7, cust.getSpecialNeedsType());
			if (cust.getTotalTickets() != null)
				stmt.setInt(8, cust.getTotalTickets().intValue());
			if (cust.getTotalQuality() != null)
				stmt.setInt(9, cust.getTotalQuality().intValue());
			stmt.setString(10, cust.getAddress());
			stmt.setString(11, cust.getResident());
			stmt.setInt(12, cust.getKey());

			stmt.executeUpdate();

			delCache(cust.getKey());
			writeCache(cust);
		}
	}

	/**
	 * Bulk chg allowed tickets.
	 *
	 * @param num
	 *            the number of allowed tickets to set for all customers
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void bulkChgAllowedTickets(int num) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE customers SET allowed_tickets = ?")) {
			stmt.setInt(1, num);
			stmt.executeUpdate();
			sync();
		}
	}

	/**
	 * Erase all total tickets and total quality values
	 *
	 * @throws Exception
	 */
	public void eraseAllQualityValues() throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("UPDATE customers SET total_tickets=0, total_quality=0")) {
			stmt.executeUpdate();
			sync();
		}
	}

}
