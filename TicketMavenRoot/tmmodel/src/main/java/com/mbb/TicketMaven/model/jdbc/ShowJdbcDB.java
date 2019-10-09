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
import java.util.Calendar;
import java.util.Collection;

import com.mbb.TicketMaven.model.TicketFormat;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;

/**
 * JDBC layer for show Entitys
 */
public class ShowJdbcDB extends JdbcDB<Show> {

	/**
	 * Instantiates a new show jdbc db.
	 */
	ShowJdbcDB() {
		super("shows");
		// new JdbcDBUpgrader("select cost from shows;",
		// "alter table shows add cost integer default '0' NOT NULL;")
		// .upgrade();
		// new JdbcDBUpgrader("select format from shows;",
		// "alter table shows add format longvarchar;").upgrade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Show show) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement(
				"INSERT INTO shows ( name, time, price, layout, cost, format )" + " VALUES( ?, ?, ?, ?, ?, ?)")) {

			stmt.setString(1, show.getName());
			stmt.setTimestamp(2, new java.sql.Timestamp(show.getDateTime().getTime()), Calendar.getInstance());
			if (show.getPrice() != null)
				stmt.setInt(3, show.getPrice().intValue());
			else
				stmt.setInt(3, 0);
			stmt.setInt(4, show.getLayout().intValue());
			if (show.getCost() != null)
				stmt.setInt(5, show.getCost().intValue());
			else
				stmt.setInt(5, 0);

			if (show.getFormat() != null)
				stmt.setString(6, show.getFormat().toXml());
			else
				stmt.setNull(6, java.sql.Types.LONGVARCHAR);

			stmt.executeUpdate();
			show.setKey(getIdentity());
		}
		writeCache(show);
		return show.getKey();

	}

	/**
	 * Gets the Show keys, sorted by show date/time.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT record_id FROM shows ORDER BY time")) {
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
	public Show newObj() {
		return (new Show());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM shows WHERE record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM shows");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Show createFrom(ResultSet r) throws SQLException {
		Show show = new Show();
		show.setKey(r.getInt("record_id"));
		show.setName(r.getString("name"));
		if (r.getTimestamp("time") != null)
			show.setDateTime(new java.util.Date(r.getTimestamp("time").getTime()));
		show.setPrice(Integer.valueOf(r.getInt("price")));
		show.setLayout(Integer.valueOf(r.getInt("layout")));
		show.setCost(Integer.valueOf(r.getInt("cost")));
		if (r.getString("format") != null) {
			try {
				TicketFormat tf = TicketFormat.fromXml(r.getString("format"));
				show.setFormat(tf);
			} catch (Exception e) {
				// empty
			}
		}

		return show;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Show show) throws Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE shows SET "
				+ "name = ?, time = ?, price = ?, layout = ?, cost = ?, format = ?" + " WHERE record_id = ?")) {

			stmt.setString(1, show.getName());
			stmt.setTimestamp(2, new java.sql.Timestamp(show.getDateTime().getTime()), Calendar.getInstance());
			if (show.getPrice() != null)
				stmt.setInt(3, show.getPrice().intValue());
			stmt.setInt(4, show.getLayout().intValue());
			if (show.getCost() != null)
				stmt.setInt(5, show.getCost().intValue());

			if (show.getFormat() != null)
				stmt.setString(6, show.getFormat().toXml());
			else
				stmt.setNull(6, java.sql.Types.LONGVARCHAR);
			stmt.setInt(7, show.getKey());

			stmt.executeUpdate();
		}
		delCache(show.getKey());
		writeCache(show);
	}

	/**
	 * Gets the shows for a particular seating type (aud/tbl).
	 * 
	 * @param seating
	 *            the seating from LayoutModel.java
	 * 
	 * @return the shows
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Show> getShows(String seating) throws Exception {
		// select seats that have no matching ticket record for a given show
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from shows LEFT JOIN layouts ON "
				+ "(layouts.record_id = shows.layout) " + "where layouts.seating = ?");
		stmt.setString(1, seating);
		return (query(stmt));
	}

	/**
	 * get the shows that reference a given layout
	 * 
	 * @param l
	 *            the layout
	 * @return the shows
	 * @throws Exception
	 */
	public Collection<Show> getShowsForLayout(Layout l) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from shows where layout = ?");
		stmt.setInt(1, l.getKey());
		return (query(stmt));
	}
}
