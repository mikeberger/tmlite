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
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.entity.TicketRequest;
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
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LotteryUndoTest {

	@BeforeClass
	public static void setUp() throws Exception {

		LogManager.getLogManager().getLogger("").setLevel(Level.OFF);

		String dbdir = "jdbc:hsqldb:mem:whatever";
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);

		// import test data
		InputStream is = new Errmsg().getClass().getResourceAsStream(
				"/test/data/tm_adjusted.exp");
		InputStreamReader r = new InputStreamReader(is);

		JdbcDB.executeMultiSQL(new BufferedReader(r));
		is.close();

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

		// make a copy of all customers
		Collection<Customer> orig_custs = new ArrayList<Customer>();
		for (Customer c : customers) {
			Customer oc = (Customer) c.copy();
			orig_custs.add(oc);
		}

		// make a copy of all requests
		Collection<TicketRequest> reqs = TicketRequestModel.getReference()
				.getRecords();
		Collection<TicketRequest> orig_reqs = new ArrayList<TicketRequest>();
		for (TicketRequest c : reqs) {
			TicketRequest oc = (TicketRequest) c.copy();
			orig_reqs.add(oc);
		}

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


		// undo all lotteries
		for (Show s : runshows) {
			System.out.println("Undo Lottery for show: " + s.getName());
			new LotteryManager(s.getKey()).undoLottery();
		}

		num_req_a = TicketRequestModel.getReference().numRows();
		num_tkt_a = TicketModel.getReference().numRows();

		Assert.assertTrue("Number of requests don't match: " + num_req + "!="
				+ num_req_a, num_req == num_req_a);
		Assert.assertTrue("Number of tickets don't match: " + num_tkt + "!="
				+ num_tkt_a, num_tkt == num_tkt_a);

		// verify that every customer has been reset properly
		for (Customer oc : orig_custs) {
			Customer c = CustomerModel.getReference().getCustomer(oc.getKey());
			Assert.assertTrue("total tickets does not match for customer: "
					+ c.getKey() + " " + c.getTotalTickets().intValue() + "!="
					+ oc.getTotalTickets().intValue(), c.getTotalTickets()
					.intValue() == oc.getTotalTickets().intValue());
			Assert.assertTrue("total quality does not match for customer: "
					+ c.getKey() + " " + c.getTotalQuality().intValue() + "!="
					+ oc.getTotalQuality().intValue(), c.getTotalQuality()
					.intValue() == oc.getTotalQuality().intValue());
		}

		// verify equivalent requests
		for (TicketRequest oc : orig_reqs) {
			Collection<TicketRequest> crs = TicketRequestModel.getReference()
					.getRequestsForCustomer(oc.getCustomerId().intValue());
			TicketRequest c = null;
			for (TicketRequest tr : crs) {
				if (tr.getCustomerId().intValue() == oc.getCustomerId()
						.intValue()
						&& tr.getShowId().intValue() == oc.getShowId()
								.intValue()) {
					// equivalent request
					c = tr;
					break;
				}
			}
			Assert.assertNotNull("Didn't find matching request for tr: "
					+ oc.getKey(), c);
			Assert.assertTrue("total tickets does not match for request: "
					+ c.getKey() + " " + c.getTickets().intValue() + "!="
					+ oc.getTickets().intValue(),
					c.getTickets().intValue() == oc.getTickets().intValue());

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
