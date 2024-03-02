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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;

/**
 * test to see if requests are properly randomized if everyone is equal
 * @author mbb
 *
 */
public class LotteryAllEqualQualityTest {

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

		// run a lottery
		Collection<Show> shows = ShowModel.getReference().getRecords();
		for (Show s : shows) {

			Collection<Ticket> tkts = TicketModel.getReference()
					.getTicketsForShow(s.getKey());
			if (tkts == null || !tkts.isEmpty())
				continue;
			System.out.println("Running Lottery for Show: " + s.getName());
			new LotteryManager(s.getKey()).runLottery();

			// stop after 1 show
			break;
		}



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
