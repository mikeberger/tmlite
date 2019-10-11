package com.mbb.TicketMaven.ui.ticketprint;

import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.util.Errmsg;

public class TicketPrinter  {

	public static final int BLANK = 3;

	public static final int SOLD = 1;

	public static final int UNSOLD = 2;
	
	static private ITicketPrinter impl = null;
	
	@SuppressWarnings("deprecation")
	static private ITicketPrinter getImpl() {
		if( impl != null) return impl;
		
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class<?> clazz = cl.loadClass("com.mbb.TicketMaven.ui.ticketprint.TicketPrinterPlugin");
			impl = (ITicketPrinter) clazz.newInstance();
			return impl;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return new DummyPrinter();

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
	
	static private class DummyPrinter implements ITicketPrinter {

		@Override
		public void printBySeatOrTable(int key) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}

		@Override
		public void printByShowForCust(Collection<Show> shows, Collection<Customer> custs) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}

		@Override
		public void printByName(Collection<Show> coll) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}

		@Override
		public void printSelectedAvailableTickets(int key, Collection<Seat> seats) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}

		@Override
		public void printSelectedTickets(Collection<Ticket> tickets) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}

		@Override
		public void printShow(int key, int sold2) {
			Errmsg.getErrorHandler().notice("Ticket Printing Plugin is not included in the Lite Version");
		}
		
	}

}
