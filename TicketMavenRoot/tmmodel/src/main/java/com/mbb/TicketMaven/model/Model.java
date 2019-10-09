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
import java.util.List;


/**
 * a model is in charge of the data store and presents the data to the
 * rest of the app
 * each model allows Views to register with it for callbacks when the
 * data changes.
 */
public abstract class Model
{

	/**
	 * list of all instantiated models
	 */
	private static List<Model> modelList = new ArrayList<Model>();
	
	/**
	 * get a list of all instantiated models
	 * @return list of models
	 */
	public static List<Model> getExistingModels()
	{
		return modelList;
	}
	
	
    // list of views to notify when the model changes
    private ArrayList<Listener> listeners;
    private boolean notify_listeners = true;
    
    /**
     * Sets the notify listeners.
     * 
     * @param b the new notify listeners
     */
    public void setNotifyListeners(boolean b)
    {
    	notify_listeners = b;
    	refreshListeners();
    }
	
	/**
	 * The Interface Listener.
	 * 
	 * @author mbb
	 * 
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	public interface Listener
	{
		
		/**
		 * Refresh.
		 */
		public abstract void refresh();
		
	}
    
    /**
     * Instantiates a new model.
     */
    public Model()
    {
    	modelList.add(this);
        listeners = new ArrayList<Listener>();
    }
    
    // function to call to register a view with the model
    /**
     * Adds the listener.
     * 
     * @param listener the listener
     */
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }
    
   // function to call to runegister a view from the model
    /**
    * Removes the listener.
    * 
    * @param listener the listener
    */
   public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }
    
    // send a refresh to all registered views
    /**
     * Refresh listeners.
     */
    public void refreshListeners()
    {
    	if( !notify_listeners ) return;
        for( int i = 0; i < listeners.size(); i++ )
        {
            Listener v = listeners.get(i);
            v.refresh();
        }
    }
    
    /**
	 * sync all models
     * @throws Exception 
	 */
	public static void syncModels() throws Exception
	{
		for(Model m : modelList)
		{
			m.sync();
		}
		
		for(Model m : modelList)
		{
			m.refreshListeners();
		}
	}
	
	/**
	 * sync the model when the underlying db changes - includes change of URL/connection
	 */
	abstract protected void sync() throws Exception;
	
    
}
