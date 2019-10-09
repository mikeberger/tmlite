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

import com.mbb.TicketMaven.model.entity.Table;

/**
 * JDBC LAyer for Table Entitys
 */
public class TableJdbcDB extends JdbcDB<Table> {

	/**
	 * Instantiates a new table jdbc db.
	 */
	TableJdbcDB() {
		super("tmtables");
		// new JdbcDBUpgrader("select record_id from tmtables;",
		// "CREATE CACHED TABLE tmtables ( record_id IDENTITY, " +
		// "seats integer default '0' NOT NULL," +
		// "layout integer default '0' NOT NULL," +
		// "xpos integer default '0' NOT NULL ," +
		// "ypos integer default '0' NOT NULL ," +
		// "width integer default '0' NOT NULL, " +
		// "height integer default '0' NOT NULL," +
		// "label varchar(30) default ''," +
		// "tbltype varchar(5) default ''," +
		// "PRIMARY KEY (record_id)," +
		// "FOREIGN KEY (layout) REFERENCES layouts (record_id));"
		// ).upgrade();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#addObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public int addObj(Table table) throws Exception {
		try (PreparedStatement stmt = connection_
				.prepareStatement("INSERT INTO tmtables ( seats, layout, xpos, ypos, width, height, label, tbltype )"
						+ " VALUES( ?, ?, ?, ?, ?, ?, ?, ? )")) {

			stmt.setInt(1, table.getSeats().intValue());
			stmt.setInt(2, table.getLayout().intValue());
			stmt.setInt(3, table.getX().intValue());
			stmt.setInt(4, table.getY().intValue());
			stmt.setInt(5, table.getWidth().intValue());
			stmt.setInt(6, table.getHeight().intValue());
			stmt.setString(7, table.getLabel());
			stmt.setString(8, table.getTblType());

			stmt.executeUpdate();
		}
		table.setKey(getIdentity());
		writeCache(table);
		return table.getKey();

	}

	/**
	 * Gets the Table keys sorted by layout.
	 *
	 * @return the keys
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Integer> getKeys() throws Exception {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		try (PreparedStatement stmt = connection_.prepareStatement("SELECT record_id FROM tmtables ORDER BY layout")) {
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
	public Table newObj() {
		return (new Table());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#getPSOne(int)
	 */
	@Override
	PreparedStatement getPSOne(int key) throws SQLException {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM tmtables WHERE record_id = ?");
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
		PreparedStatement stmt = connection_.prepareStatement("SELECT * FROM tmtables");
		return stmt;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.model.jdbc.JdbcDB#createFrom(java.sql.ResultSet)
	 */
	@Override
	Table createFrom(ResultSet r) throws SQLException {
		Table table = new Table();
		table.setKey(r.getInt("record_id"));
		table.setSeats(Integer.valueOf(r.getInt("seats")));
		table.setLayout(Integer.valueOf(r.getInt("layout")));
		table.setX(Integer.valueOf(r.getInt("xpos")));
		table.setY(Integer.valueOf(r.getInt("ypos")));
		table.setWidth(Integer.valueOf(r.getInt("width")));
		table.setHeight(Integer.valueOf(r.getInt("height")));
		table.setLabel(r.getString("label"));
		table.setTblType(r.getString("tbltype"));

		return table;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.mbb.TicketMaven.model.entity.KeyedEntityDB#updateObj(com.mbb.TicketMaven.
	 * model.entity.KeyedEntity)
	 */
	@Override
	public void updateObj(Table table) throws Exception, Exception {

		try (PreparedStatement stmt = connection_.prepareStatement("UPDATE tmtables SET "
				+ "seats = ?, layout = ?, xpos = ?, ypos = ?, width = ?, height = ?, label = ?, tbltype = ?"
				+ " WHERE record_id = ?")) {

			stmt.setInt(1, table.getSeats().intValue());
			stmt.setInt(2, table.getLayout().intValue());
			stmt.setInt(3, table.getX().intValue());
			stmt.setInt(4, table.getY().intValue());
			stmt.setInt(5, table.getWidth().intValue());
			stmt.setInt(6, table.getHeight().intValue());
			stmt.setString(7, table.getLabel());
			stmt.setString(8, table.getTblType());

			stmt.setInt(9, table.getKey());

			stmt.executeUpdate();
		}
		delCache(table.getKey());
		writeCache(table);
	}

	/**
	 * Gets the tables for a layout.
	 *
	 * @param layout
	 *            the layout key
	 *
	 * @return the tables for layout
	 *
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Table> getTablesForLayout(int layout) throws Exception {
		PreparedStatement stmt = connection_.prepareStatement("SELECT * from tmtables where tmtables.layout = ?");
		stmt.setInt(1, layout);
		return (query(stmt));
	}

	/**
	 * Delete tables for a layout.
	 *
	 * @param layout_id
	 *            the layout key
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void deleteTablesForLayout(int layout_id) throws Exception {
		try (PreparedStatement stmt = connection_.prepareStatement("delete FROM tmtables where layout = ?")) {

			stmt.setInt(1, layout_id);
			stmt.executeUpdate();
		}
		emptyCache();
	}

}
