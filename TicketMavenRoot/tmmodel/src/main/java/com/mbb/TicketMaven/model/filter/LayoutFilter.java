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

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.util.Errmsg;

import java.util.ArrayList;
import java.util.Collection;

/** filter Layouts for a given type (Auditorium or Table) */
public class LayoutFilter implements KeyedEntityFilter<Layout> {

	private String type_;
	
	public LayoutFilter(String type)
	{
		type_ = type;
	}
	
	private boolean matches(KeyedEntity b) {
		Layout l = (Layout) b;
		if (l.getSeating() != null && l.getSeating().equals(type_))
			return true;
		return false;
	}

	@Override
	public Collection<Layout> getMatchingEntities() {
		Collection<Layout> matchlist = new ArrayList<Layout>();
		try {
			Collection<Layout> beans = LayoutModel.getReference().getRecords();
			
			for(Layout b : beans) {
				if (matches(b))
					matchlist.add(b);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

}
