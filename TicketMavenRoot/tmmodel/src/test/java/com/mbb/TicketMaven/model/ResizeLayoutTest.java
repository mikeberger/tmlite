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

import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * test to see if requests are properly randomized if everyone is equal
 * @author mbb
 *
 */
public class ResizeLayoutTest {

	@BeforeClass
	public static void setUp() throws Exception {
		
		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);

		String dbdir = "jdbc:hsqldb:mem:whatever";
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);


	}

	@Test
	public void testResizeLayout() throws Exception {
		
		Layout l = LayoutModel.getReference().newLayout();
		l.setName("Layout 1");
		l.setSeating(LayoutModel.AUDITORIUM);
		l.setNumRows(5);
		l.setNumSeats(10);
		int lid = LayoutModel.getReference().saveRecord(l);
		
		SeatModel.generateMissingSeats(lid);
		
		// check number of seats
		Collection<Seat> seats = SeatModel.getReference().getSeatsForLayout(lid);
		Assert.assertEquals(50, seats.size());
		
		l = LayoutModel.getReference().getLayout(lid);
		
		l.setNumRows(4);
		LayoutModel.getReference().saveRecord(l);
		SeatModel.generateMissingSeats(lid);
		seats = SeatModel.getReference().getSeatsForLayout(lid);
		Assert.assertEquals(40, seats.size());
		
		l.setNumSeats(5);
		LayoutModel.getReference().saveRecord(l);
		SeatModel.generateMissingSeats(lid);
		seats = SeatModel.getReference().getSeatsForLayout(lid);
		Assert.assertEquals(20, seats.size());
		
		l.setNumSeats(20);
		l.setNumRows(20);
		LayoutModel.getReference().saveRecord(l);
		SeatModel.generateMissingSeats(lid);
		seats = SeatModel.getReference().getSeatsForLayout(lid);
		Assert.assertEquals(400, seats.size());

		
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
