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

import com.mbb.TicketMaven.model.TableModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.util.Errmsg;

/** filters Tables for a given Layout */
public class TableFilter implements KeyedEntityFilter<Table> {
	
	private int layout_;
	public TableFilter(int layout)
	{
		layout_ = layout;
	}
	
	private boolean matches(KeyedEntity b) {
		Table t = (Table) b;
		if (t.getSeats() != null && t.getSeats().intValue() > 0)
			return true;
		return false;
	}

	@Override
	public Collection<Table> getMatchingEntities() {
		Collection<Table> matchlist = new ArrayList<Table>();
		try {
			Collection<Table> beans = TableModel.getReference().getTablesForLayout(layout_);
			for( Table b : beans ){
				if (matches(b))
					matchlist.add(b);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

}
