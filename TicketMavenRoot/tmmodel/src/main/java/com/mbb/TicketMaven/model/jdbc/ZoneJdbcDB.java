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

import com.mbb.TicketMaven.model.entity.Zone;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JDBC layer for Zone Entitys
 */
public class ZoneJdbcDB extends JdbcDB<Zone> {

	/**
	 * Instantiates a new zone jdbc db.
	 * 
	 */
	ZoneJdbcDB() {
		super("zones");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Zone zone) throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("INSERT INTO zones ( name, exclusive )" + " VALUES( ?, ?)")) {

			stmt.setString(1, zone.getName());
			stmt.setString(2, zone.getExclusive());

			stmt.executeUpdate();
			zone.setKey(getIdentity());
		}
		writeCache(zone);
		return zone.getKey();

	}

	/**
	 * Gets the zone keys.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT record_id FROM zones ORDER BY record_id")) {
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
	public Zone newObj() {
		// return (new Zone());
		Zone l = new Zone();
		l.setKey(-1);
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM zones WHERE record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM zones");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Zone createFrom(ResultSet r) throws SQLException {
		Zone zone = new Zone();
		zone.setKey(r.getInt("record_id"));
		zone.setName(r.getString("name"));
		zone.setExclusive(r.getString("exclusive"));

		return zone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Zone zone) throws Exception {

		try (PreparedStatement stmt = connection_
				.prepareStatement("UPDATE zones SET " + "name = ?, exclusive = ?" + " WHERE record_id = ?")) {

			stmt.setString(1, zone.getName());
			stmt.setString(2, zone.getExclusive());
			stmt.setInt(3, zone.getKey());

			stmt.executeUpdate();
		}
		delCache(zone.getKey());
		writeCache(zone);
	}

}
