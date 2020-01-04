
package com.mbb.TicketMaven.ui.ticketprint;

import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.util.Errmsg;


public class TicketPrinterPlugin {

	public void printBySeatOrTable(int key) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}

	public void printByShowForCust(Collection<Show> shows, Collection<Customer> custs) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}

	public void printByName(Collection<Show> coll) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}

	public void printSelectedAvailableTickets(int key, Collection<Seat> seats) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}

	public void printSelectedTickets(Collection<Ticket> tickets) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}

	public void printShow(int key, int sold2) {
		Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
	}
	
}