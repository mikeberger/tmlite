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

import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.jdbc.ReservationJdbcDB;

/**
 * the reservation model
 */
public class ReservationModel extends KeyedEntityModel<Reservation> {

	protected ReservationModel() {
		super(Reservation.class);
	}

	static private ReservationModel self_ = new ReservationModel();

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static ReservationModel getReference() {
		return (self_);
	}

	/**
	 * get a new reservation.
	 * 
	 * @return the reservation
	 */
	public Reservation newReservation() {
		return (super.newRecord());
	}

	/**
	 * Gets the reservations for a show.
	 * 
	 * @param show_id the show key
	 * 
	 * @return the reservations for show
	 * 
	 * @throws Exception the exception
	 */
	public ArrayList<Reservation> getReservationsForShow(int show_id) throws Exception
	{
		ReservationJdbcDB db = (ReservationJdbcDB)db_;
		return db.getReservationsForShow(show_id);
	}
	
	/**
	 * Gets the reservations for a customer.
	 * 
	 * @param cust_id the customer key
	 * 
	 * @return the reservations for a customer
	 * 
	 * @throws Exception the exception
	 */
	public Collection<Reservation> getReservationsForCustomer(int cust_id) throws Exception
	{
		ReservationJdbcDB db = (ReservationJdbcDB)db_;
		return db.getReservationsForCustomer(cust_id);
	}
	
	/**
	 * Gets the reservations for a table in a show.
	 * 
	 * @param show_id the show key
	 * @param table_id the table key
	 * 
	 * @return the reservations for table in show
	 * 
	 * @throws Exception the exception
	 */
	public Collection<Reservation> getReservationsForTableInShow(int show_id, int table_id) throws Exception
	{
		ReservationJdbcDB db = (ReservationJdbcDB)db_;
		return db.getReservationsForTableInShow(show_id, table_id);
	}

	/**
	 * Delete reservations for a show.
	 * 
	 * @param show_id the show key
	 * 
	 * @throws Exception the exception
	 */
	public void deleteReservationsForShow( int show_id ) throws Exception 
	{
		ReservationJdbcDB db = (ReservationJdbcDB)db_;
		db.deleteReservationsForShow(show_id);
	}

	public void deleteRequestsForCustomer(int key) throws Exception{
		ReservationJdbcDB db = (ReservationJdbcDB)db_;
		db.deleteReservationsForCustomer(key);
	}

}
