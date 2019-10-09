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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.util.Errmsg;

/** filter Shows based on 2 criteria:
<br> seating - Auditorium or Table
<br> newOnly - if true, return only "future" shows (actually shows on or after 24 hours ago) 
*/
public class ShowFilter implements KeyedEntityFilter<Show> {
	
	private String seating_ = LayoutModel.AUDITORIUM;
	private boolean newOnly_ = true;

	public ShowFilter(String seating, boolean newOnly)
	{
		seating_ = seating;
		newOnly_ = newOnly;
	}
	
	private boolean matches(KeyedEntity b) {
		Show s = (Show) b;
		Date yesterday = new Date( new Date().getTime() - 1000*60*60*24);
		if (newOnly_ && s.getDateTime() != null && s.getDateTime().before(yesterday))
			return false;
		return true;
	}

	@Override
	public Collection<Show> getMatchingEntities() {
		Collection<Show> matchlist = new ArrayList<Show>();
		try {
			Collection<Show> beans = null;
			if( seating_ == null )
				beans = ShowModel.getReference().getRecords();
			else
				beans = ShowModel.getReference().getShows(seating_);
			for( Show b : beans ) {
				if (matches(b))
					matchlist.add(b);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

}
