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

package com.mbb.TicketMaven.ui.detail;

import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.util.Warning;

/**
 * ViewDetailPanel is the base class for forms that edit a single KeyedEntity
 * (Entity) and plug into a ViewListPanel
 */
public abstract class ViewDetailPanel<T extends KeyedEntity> extends ViewPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Show an object on the form
	 * 
	 * @param b
	 *            the object
	 */
	public abstract void showData(T b);

	/**
	 * Save the data currently shown on the form
	 * 
	 */
	public abstract void saveData() throws Exception, Warning;

	/**
	 * Gets a String containing the error to give the user in the case of a
	 * duplicate object
	 * 
	 * @return the duplicate error
	 */
	public abstract String getDuplicateError();

	/**
	 * Copy an object
	 * 
	 * @param item
	 *            the object
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void copyItem(T item) throws Exception {
		//empty
	}

	/**
	 * return true if the detailed panel can copy an object.
	 * 
	 * @return true, if the detail pane can copy
	 */
	public boolean canCopy() {
		return false;
	}

	/**
	 * after a save, carryForward is called to see if we want to start editing a
	 * bean with some of the fields in the last saved bean instead of a new bean
	 */
	public T carryForward() {
		return null;
	}

}
