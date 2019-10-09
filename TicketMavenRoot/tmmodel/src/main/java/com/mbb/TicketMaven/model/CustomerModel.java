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



import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.logging.Logger;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.jdbc.CustomerJdbcDB;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

/**
 * The Customer Model.
 */

public class CustomerModel extends KeyedEntityModel<Customer>implements CascadeDeleteProvider<Customer> {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	protected CustomerModel() {
		super(Customer.class);
	}

	static private CustomerModel self_ = new CustomerModel();

	// the built in special needs types
	static final public String AISLE = "Aisle";
	static final public String FRONT = "Front";
	static final public String FRONT_ONLY = "Front Row Only";
	static final public String REAR = "Rear";
	static final public String NONE = "None";

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static CustomerModel getReference() {
		return (self_);
	}

	/**
	 * create a new customer.
	 * 
	 * @return the customer
	 */
	public Customer newCustomer() {
		return (super.newRecord());
	}

	/**
	 * Gets a customer by key.
	 * 
	 * @param num
	 *            the key
	 * 
	 * @return the customer
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Customer getCustomer(int num) throws Exception {
		return (super.getRecord(num));
	}

	/**
	 * Bulk change the number of allowed tickets for all customers.
	 * 
	 * @param num
	 *            the new number of allowed tickets
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void bulkChgAllowedTickets(int num) throws Exception {
		CustomerJdbcDB db = (CustomerJdbcDB) db_;
		db.bulkChgAllowedTickets(num);
		refresh();
	}

	@Override
	public void cascadeDelete(Customer entity) {
		TicketModel.getReference().setNotifyListeners(false);
		TicketRequestModel.getReference().setNotifyListeners(false);
		ReservationModel.getReference().setNotifyListeners(false);

		try {
			// delete the customer and all children in a single transaction
			JdbcDB.startTransaction();
			TicketRequestModel.getReference().deleteRequestsForCustomer(entity.getKey());
			TicketModel.getReference().deleteRequestsForCustomer(entity.getKey());
			ReservationModel.getReference().deleteRequestsForCustomer(entity.getKey());
			this.delete(entity);
			JdbcDB.commitTransaction();
		} catch (Exception ex) {
			try {
				JdbcDB.rollbackTransaction();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Errmsg.getErrorHandler().errmsg(ex);
		} finally {

			TicketModel.getReference().setNotifyListeners(true);
			TicketRequestModel.getReference().setNotifyListeners(true);
			ReservationModel.getReference().setNotifyListeners(true);
		}

	}

	@Override
	public String getCascadeDeleteWarning() {
		return "This will delete the selected Customer and ALL Records associated with it, such as tickets, requests, and reservations.\n"
				+ "Are you sure you want to proceed?";
	}

	/**
	 * add the quality values for a ticket to a customer
	 * 
	 * @param cust
	 * @param ticket
	 */
	public void addTicketQuality(Ticket ticket) throws Exception {
		Seat s = SeatModel.getReference().getSeat(ticket.getSeatId().intValue());
		Customer cust = getRecord(ticket.getCustomerId().intValue());
		cust.setTotalTickets(Integer.valueOf(1 + cust.getTotalTickets().intValue()));
		cust.setTotalQuality(Integer.valueOf(s.getWeight().intValue() + cust.getTotalQuality().intValue()));
		saveRecord(cust);
	}

	/**
	 * subtract the quality values for a ticket from a customer
	 * 
	 * @param cust
	 * @param ticket
	 */
	public void subtractTicketQuality(Ticket ticket) throws Exception {
		Seat s = SeatModel.getReference().getSeat(ticket.getSeatId().intValue());
		Customer cust = getRecord(ticket.getCustomerId().intValue());
		cust.setTotalTickets(Integer.valueOf(cust.getTotalTickets().intValue() - 1));
		cust.setTotalQuality(Integer.valueOf(cust.getTotalQuality().intValue() - s.getWeight().intValue()));
		if( cust.getTotalTickets() < 0) cust.setTotalTickets(0);
		if( cust.getTotalQuality() < 0) cust.setTotalQuality(0);
		saveRecord(cust);
	}

	/**
	 * recalculate all quality values based on existing tickets
	 */
	public void adjustQualityValues() throws Exception {
		Collection<Customer> custs = CustomerModel.getReference().getRecords();
		for (Customer c : custs) {
			Collection<Ticket> tickets = TicketModel.getReference().getTicketsForCustomer(c.getKey());
			if (tickets.size() == c.getTotalTickets().intValue())
				continue;

			System.out.println(
					c.getFirstName() + " " + c.getLastName() + " " + (tickets.size() - c.getTotalTickets().intValue()));
			System.out.println();

			c.setTotalTickets(tickets.size());
			int q = 0;
			for (Ticket t : tickets) {
				Seat s = SeatModel.getReference().getRecord(t.getSeatId().intValue());
				q += s.getWeight().intValue();
			}
			c.setTotalQuality(q);
			CustomerModel.getReference().saveRecord(c);
		}

	}

	/**
	 * Erase all total tickets and total quality values
	 * 
	 * @throws Exception
	 */
	public void eraseAllQualityValues() throws Exception {
		CustomerJdbcDB db = (CustomerJdbcDB) db_;
		db.eraseAllQualityValues();
		refresh();
	}

	public void importCSV(Reader r) {
		String fn = "";
		String ln = "";
		String ad = "";
		String ph = "";

		boolean hadError = false;

		boolean inquote = false;
		boolean indata = false;

		int field = 0;
		while (true) {
			int ch;
			try {
				ch = r.read();
			} catch (IOException e1) {
				Errmsg.getErrorHandler().errmsg(e1);
				return;
			}
			if (ch == -1)
				break;
			
			if ((ch == '\r') || (ch == '\n')) {

				if( !indata ) continue;
				
				CustomerModel cm = CustomerModel.getReference();
				Customer c = cm.newCustomer();
				c.setAddress(ad);
				c.setFirstName(fn);
				c.setLastName(ln);
				c.setEmail("");
				c.setPhone(ph);
				c.setAllowedTickets(Integer.valueOf(4));
				c.setSpecialNeedsType(CustomerModel.NONE);
				c.setResident("Y");

				try {
					cm.saveRecord(c);
				} catch (Exception e) {
					log.severe(e.toString());
					log.severe(
							"INSERT INTO customers (FIRST_NAME,LAST_NAME,ALLOWED_TICKETS,SPECIAL_NEEDS,TOTAL_TICKETS,TOTAL_QUALITY,ADDRESS,RESIDENT) "
									+ "VALUES ('" + fn + "','" + ln + "',2,'None',0,0,'" + ad + "','Y');\n");
					hadError = true;
				}
				fn = "";
				ln = "";
				ad = "";
				ph = "";
				field = 0;
				indata = false;
				continue;

			}

			indata = true;

			if (ch == '"') {
				if (inquote) {
					inquote = false;
				} else {
					inquote = true;
				}
				continue;
			}

			if (ch == ',') {
				if (inquote) {
					continue;
				}

				field++;
				continue;

			}

			if (field == 0) {
				ln += (char) ch;
			} else if (field == 1) {
				if (fn.equals("") && ch == ' ')
					continue;
				fn += (char) ch;
			} else if (field == 2) {
				ad += (char) ch;
			} else if (field == 3) {
				ph += (char) ch;
			}

		}

		if (hadError == true)
			Errmsg.getErrorHandler().notice("Import completed with errors. Please check the log");

	}

}
