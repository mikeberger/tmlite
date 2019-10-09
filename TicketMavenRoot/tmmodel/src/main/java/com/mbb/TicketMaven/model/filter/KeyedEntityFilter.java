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

import com.mbb.TicketMaven.model.entity.KeyedEntity;

/**
 * a KeyedEntityFilter returns beans of a certain type, filtered by some criteria. A
 * KeyedEntityFilter would normally be initialized and passed to an object that works
 * on a collection of a certain bean type - like a popup that lets the user
 * select any Show. When a Bean filter is passed to the object, the object will
 * use it to filter the list of beans it is working on - for example to make a
 * generic Show selection object only display future shows
 * */

public interface KeyedEntityFilter<T extends KeyedEntity> {

	public class MaxRowsException extends Exception {

		private static final long serialVersionUID = 1L;
	}
	/** 
	 * return a filtered list of beans. getMatchingBeans() must load the Beans form the DB or elsewhere. An initial list is NOT passed in.
	 */
	public Collection<T> getMatchingEntities() throws MaxRowsException;

}
