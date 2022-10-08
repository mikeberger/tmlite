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


import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.jdbc.TicketJdbcDB;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The Ticket Model.
 */
public class TicketModel extends KeyedEntityModel<Ticket> {

	protected TicketModel() {
		super(Ticket.class);
	}

	static private TicketModel self_ = new TicketModel();

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static TicketModel getReference() {
		return (self_);
	}

	/**
	 * create a new ticket.
	 * 
	 * @return the ticket
	 */
	public Ticket newTicket() {
		return (super.newRecord());
	}
	
	/**
	 * Gets the tickets for a show.
	 * 
	 * @param show_id the show key
	 * 
	 * @return the tickets for show
	 * 
	 * @throws Exception the exception
	 */
	public ArrayList<Ticket> getTicketsForShow(int show_id) throws Exception
	{
		TicketJdbcDB db = (TicketJdbcDB)db_;
		return db.getTicketsForShow(show_id);
	}
	
	/**
	 * Gets the tickets for a customer.
	 * 
	 * @param cust_id the customer key
	 * 
	 * @return the tickets for customer
	 * 
	 * @throws Exception the exception
	 */
	public Collection<Ticket> getTicketsForCustomer(int cust_id) throws Exception
	{
		TicketJdbcDB db = (TicketJdbcDB)db_;
		return db.getTicketsForCustomer(cust_id);
	}
	
	/**
	 * Gets the ticket for a seat in a show.
	 * 
	 * @param show_id the show key
	 * @param row the row
	 * @param seat the seat
	 * 
	 * @return the ticket for seat in show
	 * 
	 * @throws Exception the exception
	 */
	public Ticket getTicketForSeatInShow(int show_id, String row, int seat) throws Exception
	{
		TicketJdbcDB db = (TicketJdbcDB)db_;
		return db.getTicketForSeatInShow(show_id, row, seat);
	}

	/**
	 * Delete tickets for a show.
	 * 
	 * @param show_id the show key
	 * 
	 * @throws Exception the exception
	 */
	public void deleteTicketsForShow( int show_id ) throws Exception 
	{
		TicketJdbcDB db = (TicketJdbcDB)db_;
		db.deleteTicketsForShow(show_id);
	}

	public void deleteRequestsForCustomer(int key) throws Exception {
		TicketJdbcDB db = (TicketJdbcDB)db_;
		db.deleteTicketsForCustomer(key);
	}

}
