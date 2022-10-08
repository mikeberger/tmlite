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


import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.model.jdbc.ShowJdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

import java.util.Collection;

/**
 * The Show Model.
 */
public class ShowModel extends KeyedEntityModel<Show> implements
		CascadeDeleteProvider<Show> {

	protected ShowModel() {
		super(Show.class);
	}

	static private ShowModel self_ = new ShowModel();

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static ShowModel getReference() {
		return (self_);
	}

	/**
	 * create a new show.
	 * 
	 * @return the show
	 */
	public Show newShow() {
		return (super.newRecord());
	}

	/**
	 * Gets a show by key.
	 * 
	 * @param num
	 *            the key
	 * 
	 * @return the show
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Show getShow(int num) throws Exception {
		return (super.getRecord(num));
	}

	/**
	 * Gets the shows with a given seating (from LayoutModel.java).
	 * 
	 * @param seating
	 *            the seating
	 * 
	 * @return the shows
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public Collection<Show> getShows(String seating) throws Exception {
		ShowJdbcDB showdb = (ShowJdbcDB) db_;
		return showdb.getShows(seating);
	}

	@Override
	public void cascadeDelete(Show s) {

		TicketModel.getReference().setNotifyListeners(false);
		TicketRequestModel.getReference().setNotifyListeners(false);
		ShowModel.getReference().setNotifyListeners(false);
		ReservationModel.getReference().setNotifyListeners(false);
		PackageModel.getReference().setNotifyListeners(false);


		try {
			// delete the show and all children in a single transaction
			JdbcDB.startTransaction();
			TicketRequestModel.getReference().deleteRequestsForShow(s.getKey());
			TicketModel.getReference().deleteTicketsForShow(s.getKey());
			ReservationModel.getReference().deleteReservationsForShow(
					s.getKey());
			PackageModel.getReference().deletePackagesForShow(s.getKey());
			ShowModel.getReference().delete(s);
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
			ShowModel.getReference().setNotifyListeners(true);
			ReservationModel.getReference().setNotifyListeners(true);
			PackageModel.getReference().setNotifyListeners(true);
		}
	}
	
	/**
	 * get the shows that reference a particular layout
	 * @param l the layout
	 * @return a collection of shows
	 * @throws Exception 
	 */
	public Collection<Show> getShowsForLayout(Layout l) throws Exception
	{
		ShowJdbcDB showdb = (ShowJdbcDB) db_;
		return showdb.getShowsForLayout(l);
	}

	@Override
	public String getCascadeDeleteWarning() {
		return "This will delete the selected Show and ALL Records associated with it, such as tickets, requests, and reservations.\nAlso" +
				" the show will be removed from any packages.\nAre you sure you want to proceed?";
	}

}
