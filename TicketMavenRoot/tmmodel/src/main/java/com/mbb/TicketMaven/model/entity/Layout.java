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


/**
 * The Seating Layout Entity.
 */

public class Layout extends KeyedEntity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((centerSeat == null) ? 0 : centerSeat.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((numRows == null) ? 0 : numRows.hashCode());
		result = prime * result + ((numSeats == null) ? 0 : numSeats.hashCode());
		result = prime * result + ((seating == null) ? 0 : seating.hashCode());
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
		Layout other = (Layout) obj;
		if (centerSeat == null) {
			if (other.centerSeat != null)
				return false;
		} else if (!centerSeat.equals(other.centerSeat))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numRows == null) {
			if (other.numRows != null)
				return false;
		} else if (!numRows.equals(other.numRows))
			return false;
		if (numSeats == null) {
			if (other.numSeats != null)
				return false;
		} else if (!numSeats.equals(other.numSeats))
			return false;
		if (seating == null) {
			if (other.seating != null)
				return false;
		} else if (!seating.equals(other.seating))
			return false;
		return true;
	}

	public Integer getCenterSeat() {
		return centerSeat;
	}

	public void setCenterSeat(Integer centerSeat) {
		this.centerSeat = centerSeat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumRows() {
		return numRows;
	}

	public void setNumRows(Integer numRows) {
		this.numRows = numRows;
	}

	public Integer getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(Integer numSeats) {
		this.numSeats = numSeats;
	}

	public String getSeating() {
		return seating;
	}

	public void setSeating(String seating) {
		this.seating = seating;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// All seats are assigned from the center outward - the best seat in the
	// row.
	// If this value is left as 0, then the center is assumed to be the middle
	// numbered seat in the row.
	// This value should be manually overridden if a center aisle splits the
	// theater unevenly.
	// If so, the value should be the number of the seat to the left of the
	// center aisle, when facing the stage
	private Integer centerSeat;
	private String name;
	private Integer numRows;
	private Integer numSeats; // seats per row
	private String seating; // Auditorium or Table - see LayoutModel

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		Layout dst = new Layout();
		dst.setKey(getKey());
		dst.setName(getName());
		dst.setCenterSeat(getCenterSeat());
		dst.setNumRows(getNumRows());
		dst.setNumSeats(getNumSeats());
		dst.setSeating(getSeating());
		return (dst);
	}

	
}
