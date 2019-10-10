package com.mbb.TicketMaven.ui.ticketprint;

import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;

public interface ITicketPrinter {

	public void printBySeatOrTable(int key);

	public void printByShowForCust(Collection<Show> shows, Collection<Customer> custs);

	public void printByName(Collection<Show> coll);
	
	public void printSelectedAvailableTickets(int key, Collection<Seat> seats);

	public void printSelectedTickets(Collection<Ticket> tickets);

	public void printShow(int key, int sold2);
}
