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

package com.mbb.TicketMaven.model.filter;

import java.util.Collection;

import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.util.Errmsg;

/** filters out Seats that are available for a particular show */
public class AvailableSeatsForShowFilter implements KeyedEntityFilter<Seat> {

	private int show_id;
	public AvailableSeatsForShowFilter( int s )
	{
		show_id = s;
	}
	
	@Override
	public Collection<Seat> getMatchingEntities() {
		
		try {
			Collection<Seat> beans = SeatModel.getReference().getAvailableSeatsForShow(show_id);			
			return beans;
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return null;
		
	}

}
