package com.mbb.TicketMaven.ui.ticketprint;

import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;

public class TicketPrinter  {

	public static final int BLANK = 3;

	public static final int SOLD = 1;

	public static final int UNSOLD = 2;
	
	
	static private TicketPrinterPlugin getImpl() {
		
		return new TicketPrinterPlugin();

	}

	public static void printBySeatOrTable(int key) {
		getImpl().printBySeatOrTable(key);
	}

	public static void printByShowForCust(Collection<Show> shows, Collection<Customer> custs) {
		getImpl().printByShowForCust(shows, custs);
	}

	public static void printByName(Collection<Show> coll) {
		getImpl().printByName(coll);
	}

	public static void printSelectedAvailableTickets(int key, Collection<Seat> seats) {
		getImpl().printSelectedAvailableTickets(key, seats);
	}

	public static void printSelectedTickets(Collection<Ticket> tickets) {
		getImpl().printSelectedTickets(tickets);
	}

	public static void printShow(int key, int sold2) {
		getImpl().printShow(key, sold2);
	}

}
