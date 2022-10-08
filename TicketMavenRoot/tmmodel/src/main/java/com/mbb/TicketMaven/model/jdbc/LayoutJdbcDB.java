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

import com.mbb.TicketMaven.model.entity.Layout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JDBC layer for Layout Entitys
 */
public class LayoutJdbcDB extends JdbcDB<Layout> {

	/**
	 * Instantiates a new layout jdbc db.
	 * 
	 */
	LayoutJdbcDB() {
		super("layouts");
		// new JdbcDBUpgrader("select seating from layouts;", "alter table layouts add
		// seating varchar(15) default '' NOT NULL;" +
		// "update layouts set seating = 'Auditorium';").upgrade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Layout layout) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO layouts ( name, centerseat, num_rows, num_seats, seating )" + " VALUES( ?, ?, ?, ?, ?)")) {

			stmt.setString(1, layout.getName());
			stmt.setInt(2, layout.getCenterSeat().intValue());
			stmt.setInt(3, layout.getNumRows().intValue());
			stmt.setInt(4, layout.getNumSeats().intValue());
			stmt.setString(5, layout.getSeating());

			stmt.executeUpdate();
			layout.setKey(getIdentity());
			writeCache(layout);
			return layout.getKey();
		}

	}

	/**
	 * Gets the Layout keys.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_
				.prepareStatement("SELECT record_id FROM layouts ORDER BY record_id")) {
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
	public Layout newObj() {
		// return (new Layout());
		Layout l = new Layout();
		l.setKey(-1);
		l.setCenterSeat(Integer.valueOf(0));
		l.setNumRows(Integer.valueOf(1));
		l.setNumSeats(Integer.valueOf(1));
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM layouts WHERE record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM layouts");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Layout createFrom(ResultSet r) throws SQLException {
		Layout layout = new Layout();
		layout.setKey(r.getInt("record_id"));
		layout.setName(r.getString("name"));
		layout.setCenterSeat(Integer.valueOf(r.getInt("centerseat")));
		layout.setNumRows(Integer.valueOf(r.getInt("num_rows")));
		layout.setNumSeats(Integer.valueOf(r.getInt("num_seats")));
		layout.setSeating(r.getString("seating"));
		return layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Layout layout) throws Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE layouts SET "
				+ "name = ?, centerseat = ?, num_rows = ?, num_seats = ?, seating = ?" + " WHERE record_id = ?")) {

			stmt.setString(1, layout.getName());
			stmt.setInt(2, layout.getCenterSeat().intValue());
			stmt.setInt(3, layout.getNumRows().intValue());
			stmt.setInt(4, layout.getNumSeats().intValue());
			stmt.setString(5, layout.getSeating());
			stmt.setInt(6, layout.getKey());

			stmt.executeUpdate();
		}

		delCache(layout.getKey());
		writeCache(layout);
	}

}
