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


import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.model.jdbc.RequestJdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

import java.util.ArrayList;

/**
 * The Ticket Request Model.
 */
public class TicketRequestModel extends KeyedEntityModel<TicketRequest> {

	protected TicketRequestModel() {
		super(TicketRequest.class);
	}

	static private TicketRequestModel self_ = new TicketRequestModel();

	/**
	 * Gets the singleton.
	 *
	 * @return the singleton
	 */
	public static TicketRequestModel getReference() {
		return (self_);
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.KeyedEntityModel#getBeanName()
	 */
	@Override
	public String getEntityName()
	{
		return("Request");
	}

	/**
	 * create a new ticket request.
	 *
	 * @return the ticket request
	 */
	public TicketRequest newTicketRequest() {
		return (super.newRecord());
	}

	/**
	 * Gets the requests for a show.
	 *
	 * @param show_id the show key
	 *
	 * @return the requests for show
	 *
	 * @throws Exception the exception
	 */
	public ArrayList<TicketRequest> getRequestsForShow(int show_id) throws Exception
	{
		RequestJdbcDB db = (RequestJdbcDB)db_;
		return db.getRequestsForShow(show_id);
	}

	/**
	 * Gets the requests for a customer.
	 *
	 * @param cust_id the customer key
	 *
	 * @return the requests for customer
	 *
	 * @throws Exception the exception
	 */
	public ArrayList<TicketRequest> getRequestsForCustomer(int cust_id) throws Exception
	{
		RequestJdbcDB db = (RequestJdbcDB)db_;
		return db.getRequestsForCustomer(cust_id);
	}

	/**
	 * Delete requests for a show.
	 *
	 * @param show_id the show key
	 *
	 * @throws Exception the exception
	 */
	public void deleteRequestsForShow( int show_id ) throws Exception
	{
		RequestJdbcDB db = (RequestJdbcDB)db_;
		db.deleteRequestsForShow(show_id);
	}

	/**
	 * calculate the price of a request based on the show price, any package discount, and nmuber of tickets
	 *
	 * @param r the request
	 *
	 * @return the price in cents
	 */
	static public int requestPrice(TicketRequest r)
	{
		int price = 0;

		try {
			Show sh = ShowModel.getReference().getShow(r.getShowId().intValue());
			double discount = 0.0;
			if( r.getDiscount() != null) discount = r.getDiscount().doubleValue();
			double p = sh.getPrice().doubleValue() * r.getTickets().doubleValue() * (100.0 - discount) / 100.0;
			price = (int)p;
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return 0;
		}


		return price;
	}

	public void deleteRequestsForCustomer(int key) throws Exception {
		RequestJdbcDB db = (RequestJdbcDB)db_;
		db.deleteRequestsForCustomer(key);

	}

	public void moveRequests(int fromShow, int toShow) throws Exception {
		RequestJdbcDB db = (RequestJdbcDB)db_;
		db.moveRequests(fromShow, toShow);
	}
}
