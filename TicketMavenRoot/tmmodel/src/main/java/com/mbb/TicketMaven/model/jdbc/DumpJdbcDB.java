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

import java.sql.*;

/**
 * This class connects to a database and dumps all the tables and contents out
 * to stdout in the form of a set of SQL executable statements
 */
public class DumpJdbcDB {

	public static String dumpData() {
		Connection conn = JdbcDB.connection_;

		StringBuffer result = new StringBuffer();
		result.append("SET SCHEMA PUBLIC;\n");

		dumpTable(conn, result, "CUSTOMERS");
		dumpTable(conn, result, "LAYOUTS");
		dumpTable(conn, result, "ZONES");
		dumpTable(conn, result, "SEATS");
		dumpTable(conn, result, "SHOWS");
		dumpTable(conn, result, "PACKAGES");
		dumpTable(conn, result, "PKGSHOWS");
		dumpTable(conn, result, "REQUESTS");
		dumpTable(conn, result, "TICKETS");
		dumpTable(conn, result, "TMTABLES");
		dumpTable(conn, result, "RESERVATIONS");

		return result.toString();
	}

	/** dump this particular table to the string buffer */
	private static void dumpTable(Connection dbConn, StringBuffer result,
			String tableName) {
		try {
			// First we output the create table stuff
			PreparedStatement stmt = dbConn.prepareStatement("SELECT * FROM "
					+ tableName);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Now we can output the actual data
			result.append("\n\n-- Data for " + tableName + "\n");
			while (rs.next()) {
				result.append("INSERT INTO " + tableName + " (");
				for (int i = 0; i < columnCount; i++) {
					if (i > 0) {
						result.append(", ");
					}
					String name = metaData.getColumnName(i + 1);

					name = name.replace("'", "''");
					result.append("\"" + name + "\"");

				}
				result.append(") VALUES (");
				for (int i = 0; i < columnCount; i++) {
					if (i > 0) {
						result.append(", ");
					}
					Object value = rs.getObject(i + 1);
					if (value == null) {
						result.append("NULL");
					} else {
						String outputValue = value.toString();
						outputValue = outputValue.replace("'", "''");
						result.append("'" + outputValue + "'");
					}
				}
				result.append(");\n");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.err.println("Unable to dump table " + tableName
					+ " because: " + e);
		}
	}

	

}
