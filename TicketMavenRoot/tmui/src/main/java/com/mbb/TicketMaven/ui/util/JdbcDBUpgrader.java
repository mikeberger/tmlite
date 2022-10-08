/*
 * #%L
 * tmui
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

package com.mbb.TicketMaven.ui.util;

import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

import java.sql.SQLException;

/**
 * This class performs automatic release transition upgrades on a jdbc database. 
 * Each instance of this class corresponds to a single DB upgrade. Each instance contains
 * a check SQL that should fail if the database needs to be upgraded. If the check SQL fails, the
 * user will be warned of the upgrade and then the upgrade SQL will be run. Upgrades can include any valid SQL,
 * including cross-table updates, new table creation, table structure alterations, etc...
 */
public class JdbcDBUpgrader {
	
	private static boolean user_warned = false; // flag to indicate if the user has been warned for any
	// upgrade. The warning is only shown once no matter how many upgrades there are.

	private String checkSql;

	private String updSql;

	/**
	 * Instantiates a new jdbc db upgrader.
	 * 
	 * @param checkSql the SQL to evaluate to check if an upgrade is needed
	 * @param usql the SQL to run if an upgrade is needed
	 */
	public JdbcDBUpgrader(String checkSql, String usql) {
		this.updSql = usql;
		this.checkSql = checkSql;
	}

	/**
	 * check if a database needs this particular upgrade
	 * 
	 * @return true, if an upgrade is needed
	 * 
	 * @throws Exception the exception
	 */
	private boolean needsUpgrade() throws Exception {
		try {
			JdbcDB.execSQL(checkSql);
		} catch (Exception e) {
			if (e instanceof SQLException)
				return true;
		}

		return false;
	}
	
	// warning displayed to the user before an upgrade
	static private final String upgrade_warning = "The program has detected that the database needs to be upgraded for a new version of TicketMaven. Backing up your database is recommended before proceeding. If you need to backup your database or if this warning is unexpected, select cancel. Otherwise, select OK to proceed.";

	/**
	 * Perform an upgrade.
	 * 
	 * @throws Exception the exception
	 */
	private void performUpgrade() throws Exception {
		if( !user_warned )
		{
			// show the user a warning and give them the option to exit and do a backup
			int ret = ConfirmDialog.showNotice(upgrade_warning);
			if (ret != ConfirmDialog.OK)
				System.exit(1);	
		}
		user_warned = true;
		System.out.println("Running Upgrade SQL:" + updSql);
		JdbcDB.execSQL(updSql);
	}

	private String upd_err = "Failed to automatically upgrade the database. The following SQL must be executed manually:\n";

	/**
	 * perform an upgrade check and then execute the upgrade if needed
	 */
	public void upgrade() {
		try {
			if (needsUpgrade()) {
				performUpgrade();
				if (needsUpgrade()) {
					Errmsg.getErrorHandler().notice(upd_err + updSql);
				}
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().notice(upd_err + updSql);
		}
	}
}
