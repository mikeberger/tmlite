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

import com.mbb.TicketMaven.model.entity.TMPackage;

/**
 * JDBC layer for Package Entitys
 */
public class PackageJdbcDB extends JdbcDB<TMPackage> {

	/**
	 * Instantiates a new package jdbc db.
	 * 
	 */
	PackageJdbcDB() {
		super("packages");

		// upgrade the db if the user doesn't have a packages table
		// new JdbcDBUpgrader(
		// "select record_id from packages;",
		// "CREATE CACHED TABLE packages ("
		// + "record_id IDENTITY,"
		// + "name varchar(100) default '' NOT NULL,"
		// + "price integer default '0' NOT NULL,"
		// + "PRIMARY KEY (record_id));"
		// + "CREATE CACHED TABLE pkgshows ("
		// + "pkg_id integer default '0' NOT NULL,"
		// + "show_id integer default '0' NOT NULL,"
		// + "FOREIGN KEY (pkg_id) REFERENCES packages (record_id),"
		// + "FOREIGN KEY (show_id) REFERENCES shows (record_id),"
		// + "PRIMARY KEY (pkg_id,show_id));"
		// + "CREATE INDEX pkgs1 ON pkgshows (pkg_id);").upgrade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(TMPackage p) throws Exception {
		try {
			startTransaction();
			try (PreparedStatement stmt = connection_
					.prepareStatement("INSERT INTO packages ( name, price )" + " VALUES( ?, ?)")) {

				stmt.setString(1, p.getName());
				if (p.getPrice() != null)
					stmt.setInt(2, p.getPrice().intValue());
				else
					stmt.setInt(2, 0);

				stmt.executeUpdate();
				p.setKey(getIdentity());

				insertShows(p);

				writeCache(p);
				commitTransaction();
				return p.getKey();
			}
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	TMPackage createFrom(ResultSet r) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		TMPackage p = new TMPackage();
		try {
			p.setKey(r.getInt("record_id"));
			p.setName(r.getString("name"));
			p.setPrice(Integer.valueOf(r.getInt("price")));

			ArrayList<Integer> shows = new ArrayList<Integer>();
			stmt = connection_.prepareStatement("SELECT * FROM pkgshows WHERE pkg_id = ?");
			stmt.setInt(1, p.getKey());
			rs = stmt.executeQuery();
			while (rs.next()) {
				shows.add(Integer.valueOf(rs.getInt("show_id")));
			}
			p.setShows(shows);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return p;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#delete(int)
	 */
	@Override
	public void delete(int key) throws Exception {
		try {
			startTransaction();
			deleteShows(key);
			try (PreparedStatement stmt = connection_
					.prepareStatement("DELETE FROM " + tablename_ + " WHERE record_id = ?")) {
				stmt.setInt(1, key);
				stmt.executeUpdate();
			}
			delCache(key);
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
	}

	/**
	 * Delete the many-to-many mapping records that associate a given package with
	 * shows.
	 * 
	 * @param key
	 *            the key
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void deleteShows(int key) throws Exception {
		try (PreparedStatement stmt2 = connection_.prepareStatement("DELETE FROM pkgshows WHERE pkg_id = ?")) {
			stmt2.setInt(1, key);
			stmt2.executeUpdate();
		}
	}

	/**
	 * Gets the Package keys.
	 * 
	 * @return the keys
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT record_id FROM packages")) {
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
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSAll()
	 */
	@Override
	PreparedStatement getPSAll() throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM packages");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM packages WHERE record_id = ?");
		stmt.setInt(1, key);
		return stmt;
	}

	/**
	 * Insert the needed many-to-many mapping records to map shows to this package
	 * 
	 * @param p
	 *            the package
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void insertShows(TMPackage p) throws Exception {
		Collection<Integer> c = p.getShows();
		for (Integer i : c) {
			try (PreparedStatement stmt2 = connection_
					.prepareStatement("INSERT INTO pkgshows ( pkg_id, show_id )" + " VALUES( ?, ?)")) {
				stmt2.setInt(1, p.getKey());
				stmt2.setInt(2, i.intValue());
				stmt2.executeUpdate();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntityDB#newObj()
	 */
	@Override
	public TMPackage newObj() {
		return (new TMPackage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven
	 * .model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(TMPackage p) throws Exception {

		try {
			startTransaction();

			try (PreparedStatement stmt = connection_
					.prepareStatement("UPDATE packages SET " + "name = ?,  price = ?" + " WHERE record_id = ?")) {

				stmt.setString(1, p.getName());
				if (p.getPrice() != null)
					stmt.setInt(2, p.getPrice().intValue());
				stmt.setInt(3, p.getKey());

				stmt.executeUpdate();
			}
			deleteShows(p.getKey());
			insertShows(p);

			delCache(p.getKey());
			writeCache(p);
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
	}

	public void deletePackagesForShow(int key) throws SQLException {
		try (PreparedStatement stmt = connection_.prepareStatement("DELETE FROM pkgshows WHERE show_id = ?")) {
			stmt.setInt(1, key);
			stmt.executeUpdate();
		}
		sync();
	}

}
