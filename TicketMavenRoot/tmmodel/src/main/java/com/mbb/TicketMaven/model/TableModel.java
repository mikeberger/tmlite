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



import java.util.Collection;

import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.model.jdbc.TableJdbcDB;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * The Table Model.
 */
public class TableModel extends KeyedEntityModel<Table> {

	protected TableModel( ) {
		super(Table.class);
	}

	static private TableModel self_ = new TableModel();
	
	// the types of items that can be shown on a table layout diagram
	public static final String RECT_TABLE = "Rect";
	public static final String CIRC_TABLE = "Circ";
	public static final String RECT_FEAT = "RFeat";
	public static final String CIRC_FEAT = "CFeat";

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static TableModel getReference() {
		return (self_);
	}

	/**
	 * create a new table.
	 * 
	 * @return the table
	 */
	public Table newTable() {
		return (super.newRecord());
	}

	/**
	 * Gets a table by key.
	 * 
	 * @param num the key
	 * 
	 * @return the table
	 * 
	 * @throws Exception the exception
	 */
	public Table getTable(int num) throws Exception {
		return (super.getRecord(num));
	}
	
	/**
	 * Delete tables for a layout.
	 * 
	 * @param layout_id the layout key
	 * 
	 * @throws Exception the exception
	 */
	public void deleteTablesForLayout(int layout_id) throws Exception
	{
		TableJdbcDB sdb = (TableJdbcDB)db_;
		sdb.deleteTablesForLayout(layout_id);
	}
	
	/**
	 * Gets the tables for a layout.
	 * 
	 * @param layout the layout key
	 * 
	 * @return the tables for layout
	 * 
	 * @throws Exception the exception
	 */
	public Collection<Table> getTablesForLayout(int layout) throws Exception
	{
		TableJdbcDB db = (TableJdbcDB)db_;
		return db.getTablesForLayout(layout);
	}
	
	/**
	 * Gets the default number of seats per table.
	 * 
	 * @return the default number of seats per table
	 */
	static public int getDefaultSeats()
	{
		return Prefs.getIntPref(PrefName.TBLSEATS);
	}

}
