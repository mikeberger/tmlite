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

import java.sql.SQLException;
import java.util.logging.Logger;

import com.mbb.TicketMaven.util.Errmsg;

/**
 * Class JdbcDBUpgrader is used to upgrade HSQL database in-place when the schema changes for a 
 * release. This class will check an update condition, and if true, execute SQL to upgrade the DB.
 * It is meant to be called during model initialization - preferably from the constructors
 * of the JdbcDB classes
 */

public class JdbcDBUpgrader {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	/** The check sql. */
	private String checkSql;

	/** The upd sql. */
	private String updSql[];

	/**
	 * Instantiates a new jdbc db upgrader.
	 * 
	 * @param checkSql the sql that checks if an upgrade is needed
	 * @param usql the sql to upgrade the db if needed
	 */
	public JdbcDBUpgrader(String checkSql, String usql) {
		updSql = new String[1];
		this.updSql[0] = usql;
		this.checkSql = checkSql;
	}
	
	/**
	 * Instantiates a new jdbc db upgrader.
	 * 
	 * @param checkSql the sql that checks if an upgrade is needed
	 * @param usql an array of SQL statements to execute to perform the upgrade
	 */
	public JdbcDBUpgrader(String checkSql, String usql[]) {
		this.updSql = usql;
		this.checkSql = checkSql;
	}

	/**
	 * check if db Needs upgrade.
	 * 
	 * @return true, if upgrade needed
	 * 
	 * @throws Exception the exception
	 */
	private boolean needsUpgrade() throws Exception {
		
		if( checkSql == null )
			return true;
		
		try {
			JdbcDB.execSQL(checkSql);
		} catch (Exception e) {
			if (e instanceof SQLException)
				return true;
		}

		return false;
	}

	/**
	 * Execute the upgrade SQL.
	 * If MYSQL - just show the SQL to the user - do not upgrade
	 * 
	 * @throws Exception the exception
	 */
	private void performUpgrade() throws Exception {
		for( int i = 0; i < updSql.length; i++ )
		{
			log.info("Running Upgrade SQL:" + updSql[i]);
			JdbcDB.execSQL(updSql[i]);
		}
	}

	/**
	 * run the upgrade check and then upgrade if needed
	 */
	public void upgrade() {
		try {
			if (needsUpgrade()) {
				performUpgrade();
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}
}
