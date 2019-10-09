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



package com.mbb.TicketMaven.ui;

import java.text.SimpleDateFormat;

import com.mbb.TicketMaven.model.Model;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;
 
/**
 * A ViewPanel is an abstract base class representing a JPanel that listens for changes to a Model
 */
public abstract class ViewPanel extends javax.swing.JPanel implements Model.Listener
{
	private static final long serialVersionUID = 1L;
	protected final SimpleDateFormat sdf = new SimpleDateFormat(Prefs.getPref(PrefName.DATEFORMAT));
    
    /* (non-Javadoc)
     * @see com.mbb.TicketMaven.model.Model.Listener#refresh()
     */
    @Override
	public abstract void refresh();
      
   
    /**
     * Adds the ViewPanel as a listener to a given Model.
     * param m the Model
     */
    public void addModel(Model m)
    {
        m.addListener(this);
    }
     
    
}
