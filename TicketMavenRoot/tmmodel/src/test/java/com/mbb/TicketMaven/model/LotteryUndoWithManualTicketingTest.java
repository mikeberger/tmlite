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
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.util.Errmsg;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LotteryUndoWithManualTicketingTest {

	@BeforeClass
	public static void setUp() throws Exception {

		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);

		CreateTestData.createDB();

		// reset to pristine state
		Collection<Show> shows = ShowModel.getReference().getRecords();
		for (Show s : shows) {
			System.out.println("SETUP::Undoing Lottery for Show: "
					+ s.getName());
			new LotteryManager(s.getKey()).undoLottery();
		}

		// reset customer history
		JdbcDB.execSQL("UPDATE Customers set total_quality=0;");
		JdbcDB.execSQL("UPDATE Customers set total_tickets=0;");

	}

	@Test
	public void testLotteryUndo() throws Exception {
		Collection<Customer> customers = CustomerModel.getReference()
				.getRecords();
		System.out.println("num custs=" + customers.size());

		// save number fo requests and tickets
		int num_req = TicketRequestModel.getReference().numRows();
		int num_tkt = TicketModel.getReference().numRows();

		System.out.println("Before Lotteries: requests=" + num_req
				+ "  tickets=" + num_tkt);

		// run all possible lotteries
		Collection<Show> shows = ShowModel.getReference().getRecords();
		Collection<Show> runshows = new ArrayList<Show>();
		for (Show s : shows) {

			Collection<Ticket> tkts = TicketModel.getReference()
					.getTicketsForShow(s.getKey());
			if (tkts == null || !tkts.isEmpty())
				continue;
			runshows.add(s);
			System.out.println("Running Lottery for Show: " + s.getName());
			new LotteryManager(s.getKey()).runLottery();
		}

		int num_req_a = TicketRequestModel.getReference().numRows();
		int num_tkt_a = TicketModel.getReference().numRows();
		System.out.println("After Lotteries: requests=" + num_req_a
				+ "  tickets=" + num_tkt_a);

		// randomly delete tickets
		Collection<Ticket> tickets = TicketModel.getReference().getRecords();
		Ticket[] ta = tickets.toArray(new Ticket[0]);
		for (int i = 0; i < ta.length; i += ta.length / 50) {
			CustomerModel.getReference().subtractTicketQuality(ta[i]);
			TicketModel.getReference().delete(ta[i].getKey());
			//System.out.println("Delete ticket: " + ta[i].getCustomerName() + " " + ta[i].getShowName() + " " + ta[i].getRowAisle());
		}

		// randomly add tickets
		shows = ShowModel.getReference().getRecords();
		Collection<Customer> ac = CustomerModel.getReference().getRecords();
		Iterator<Customer> it = ac.iterator();
		for (Show s : shows) {
			Collection<Seat> avail = SeatModel.getReference()
					.getAvailableSeatsForShow(s.getKey());
			for (Seat as : avail) {
				if( !it.hasNext())
					it = ac.iterator();
				Ticket t = TicketModel.getReference().newRecord();
				t.setSeatId(as.getKey());
				t.setCustomerId(it.next().getKey());
				t.setShowId(s.getKey());
				TicketModel.getReference().saveRecord(t);
				CustomerModel.getReference().addTicketQuality(t);
				//System.out.println("Add ticket: " + t.getCustomerId() + " " + s.getName() + " " + as.getSeat());
			}
		}

		// undo all lotteries
		for (Show s : runshows) {
			System.out.println("Undo Lottery for show: " + s.getName());
			new LotteryManager(s.getKey()).undoLottery();
		}

		num_req_a = TicketRequestModel.getReference().numRows();
		num_tkt_a = TicketModel.getReference().numRows();

		Assert.assertTrue("Number of tickets don't match: " + num_tkt + "!="
				+ num_tkt_a, num_tkt == num_tkt_a);

		// verify that every customer has been reset properly
		for (Customer c : CustomerModel.getReference().getRecords()) {
			Assert.assertTrue("total tickets does not match for customer: "
					+ c.getKey() + " " + c.getTotalTickets().intValue() + "!="
					+ 0, c.getTotalTickets()
					.intValue() == 0);
			Assert.assertTrue("total quality does not match for customer: "
					+ c.getKey() + " " + c.getTotalQuality().intValue() + "!="
					+ 0, c.getTotalQuality()
					.intValue() == 0);
		}

		num_req_a = TicketRequestModel.getReference().numRows();
		num_tkt_a = TicketModel.getReference().numRows();
		System.out.println("After Undos: requests=" + num_req_a + "  tickets="
				+ num_tkt_a);
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
