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

import com.mbb.TicketMaven.model.entity.*;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;


public class JdbcTest {


	@BeforeClass
	public static void setUp() throws Exception {

		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);

		String dbdir = "jdbc:hsqldb:mem:whatever";
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);

	}

	@Test
	public void testCrud() throws Exception {


		Customer c = CustomerModel.getReference().newRecord();
		c.setFirstName("m");
		c.setLastName("b");
		c.setResident("Y");
		int custid = CustomerModel.getReference().saveRecord(c);

		Layout l = LayoutModel.getReference().newRecord();
		l.setName("t1");
		l.setSeating(LayoutModel.TABLE);
		l.setCenterSeat(1);
		l.setNumRows(1);
		l.setNumSeats(1);
		int layoutid = LayoutModel.getReference().saveRecord(l);

		Show s = ShowModel.getReference().newRecord();
		s.setDateTime(new Date());
		s.setLayout(layoutid);
		s.setName("s1");
		int showid = ShowModel.getReference().saveRecord(s);

		Table t = TableModel.getReference().newRecord();
		t.setLayout(layoutid);
		t.setSeats(5);
		t.setHeight(1);
		t.setWidth(1);
		t.setTblType(TableModel.CIRC_TABLE);
		t.setX(1);
		t.setY(2);
		int tblid = TableModel.getReference().saveRecord(t);



		Reservation r = ReservationModel.getReference().newRecord();
		r.setCustomerId(custid);
		r.setNotes("xxx");
		r.setShowId(showid);
		r.setTableId(tblid);
		r.setNum(2);


		ReservationModel.getReference().saveRecord(r);

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
