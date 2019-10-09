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

package com.mbb.TicketMaven.model.entity;

import java.io.Serializable;


/**
 * All TM Entitys extend this class, which provides an integer key. All KeyedEntitys
 * are stored in DB tables with an integer primary key 
 */
public abstract class KeyedEntity implements Serializable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyedEntity other = (KeyedEntity) obj;
		if (key != other.key)
			return false;
		return true;
	}


	public int getKey() {
		return key;
	}


	public void setKey(int key) {
		this.key = key;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int key = -1;

	protected abstract Object clone();
	

	/**
	 * Copy.
	 * 
	 * @return the keyed bean
	 */
	public KeyedEntity copy() {
		return (KeyedEntity) clone();
	}

	
	/**
	 * Checks if is new (never persisted). A negative key is used as the convention for marking new objects
	 * 
	 * @return true, if is new
	 */
	public boolean isNew() {
		if (key < 0)
			return true;

		return false;
	}

}
