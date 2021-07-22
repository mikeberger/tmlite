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

package com.mbb.TicketMaven.ui.report;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

/**
 * Class for running Jasper Reports
 */
class RunReport {

	/**
	 * Run a Jasper report from an InputStream.
	 * 
	 * @param is
	 *            the input Stream
	 * @param parms
	 *            the report parameters for Jasper
	 */
	public static void runReport(InputStream is, Map<String, Object> parms) {
		try {
			// get the db connection for Jasper
			Connection conn = JdbcDB.getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(is, parms,
					conn);
			// launch a viewer
			JasperViewer.viewReport(jasperPrint, false);
			
		} catch (NoClassDefFoundError r) {
			Errmsg.getErrorHandler().errmsg(r);
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * Run a Jasper report that is located in the TM JAR in the reports folder.
	 * This is where the canned reports are delivered.
	 * 
	 * @param name
	 *            the report name (minus the .jasper)
	 * @param parms
	 *            the parameters for Jasper
	 */
	public static void runReport(String name, Map<String, Object> parms) {

		String resourcePath = "/reports/" + name + ".jasper";
		InputStream is = RunReport.class.getResourceAsStream(resourcePath);
		if( is == null )
		{
			Errmsg.getErrorHandler().notice("Report not included in Lite version");
			return;
		}
		runReport(is, parms);

	}

}
