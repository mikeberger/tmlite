/*
 * #%L
 * tmui
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



package com.mbb.TicketMaven.ui.filter;

import java.text.SimpleDateFormat;

import com.mbb.TicketMaven.model.Model;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.filter.KeyedEntityFilter;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;
 
/**
 * This is the base class for FilterPanels, which are Panels that appear at the top of a ViewListPanel and that are used to filter
 * the rows od the ViewListPanel table. A FilterPanel must call notifyParent() to notifiy any listeners when its filtering information
 * changes
 */
public abstract class FilterPanel<T extends KeyedEntity> extends javax.swing.JPanel implements KeyedEntityFilter<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Model.Listener parent;
	protected final SimpleDateFormat sdf = new SimpleDateFormat(Prefs.getPref(PrefName.DATEFORMAT));
	
    /**
     * Sets the parent.
     * 
     * @param ml the new parent
     */
    public void setParent( Model.Listener ml )
    {
    	parent = ml;
    }
    
    /**
     * Notify the parent that the filter information has changed.
     */
    public void notifyParent()
    {
    	if( parent != null)
    		parent.refresh();
    }

}
