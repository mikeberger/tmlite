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


import java.util.ArrayList;
import java.util.Collection;


/**
 * Entity for a Package - a collection of shows sold at a discount. A show can be in any number of packages
 */

public class TMPackage extends KeyedEntity {

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((shows == null) ? 0 : shows.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TMPackage other = (TMPackage) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (shows == null) {
			if (other.shows != null)
				return false;
		} else if (!shows.equals(other.shows))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Collection<Integer> getShows() {
		return shows;
	}

	public void setShows(Collection<Integer> shows) {
		this.shows = shows;
	}

	private static final long serialVersionUID = 1L;

	private String name; // name of this package
	private Integer price; // total price to buy 1 ticket in all shows in package - in cents
	private Collection<Integer> shows = new ArrayList<Integer>(); // the collection of shows in this package

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		TMPackage dst = new TMPackage();
		dst.setKey( getKey());
		dst.setName( getName() );
		dst.setPrice( getPrice() );
		dst.getShows().addAll(shows);
		return(dst);
	}
	
	
}
