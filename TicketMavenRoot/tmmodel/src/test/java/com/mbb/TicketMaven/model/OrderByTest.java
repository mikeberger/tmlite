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

package com.mbb.TicketMaven.model;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.jdbc.CustomerJdbcDB;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;


public class OrderByTest {

	@BeforeClass
	public static void setUp() throws Exception {

		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);

		String dbdir = "jdbc:hsqldb:mem:whatever";
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);


	}

	@Test
	public void testResizeLayout() throws Exception {

		CustomerJdbcDB db = new CustomerJdbcDB();
		Customer c = CustomerModel.getReference().newRecord();
		c.setFirstName("m");
		c.setLastName("b");
		c.setResident("Y");
		CustomerModel.getReference().saveRecord(c);
		System.out.println(db.getKeys()); // checking for sql error in order by

	}

	@AfterClass
	public static void tearDown() {
		try {
			JdbcDB.cleanup();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
