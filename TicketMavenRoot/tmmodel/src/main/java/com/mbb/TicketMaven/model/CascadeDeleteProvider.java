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



import com.mbb.TicketMaven.model.entity.KeyedEntity;

/**
 * The Interface CascadeDeleteProvider.
 */
public interface CascadeDeleteProvider<T extends KeyedEntity> {

	/**
	 * Gets the cascade delete warning.
	 * 
	 * @return the cascade delete warning
	 */
	public String getCascadeDeleteWarning();
	
	/**
	 * Force a Cascade delete.
	 * 
	 * @param entity the root entity to delete
	 */
	public void cascadeDelete(T entity);
}
