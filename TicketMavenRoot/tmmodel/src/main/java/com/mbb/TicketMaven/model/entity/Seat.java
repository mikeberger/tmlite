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

import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * the Seat Entity. A single seat in a single layout.
 */

public class Seat extends KeyedEntity {


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((layout == null) ? 0 : layout.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
		result = prime * result + ((seat == null) ? 0 : seat.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
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
		Seat other = (Seat) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (layout == null) {
			if (other.layout != null)
				return false;
		} else if (!layout.equals(other.layout))
			return false;
		if (row == null) {
			if (other.row != null)
				return false;
		} else if (!row.equals(other.row))
			return false;
		if (seat == null) {
			if (other.seat != null)
				return false;
		} else if (!seat.equals(other.seat))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (!zone.equals(other.zone))
			return false;
		return true;
	}


	public String getAvailable() {
		return available;
	}


	public void setAvailable(String available) {
		this.available = available;
	}


	public String getEnd() {
		return end;
	}


	public void setEnd(String end) {
		this.end = end;
	}


	public Integer getLayout() {
		return layout;
	}


	public void setLayout(Integer layout) {
		this.layout = layout;
	}


	public String getRow() {
		return row;
	}


	public void setRow(String row) {
		this.row = row;
	}


	public Integer getSeat() {
		return seat;
	}


	public void setSeat(Integer seat) {
		this.seat = seat;
	}


	public Integer getWeight() {
		return weight;
	}


	public void setWeight(Integer weight) {
		this.weight = weight;
	}


	public Integer getZone() {
		return zone;
	}


	public void setZone(Integer zone) {
		this.zone = zone;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	private static final long serialVersionUID = 1L;

	private String available; // Y/N - is seat available - i.e. is a chari
								// there - i.e. can a customer sit at this
								// position in the grid
	private String end; // aisle (Left, Right, None - see SeatModel.java)
	private Integer layout; // parent layout id
	private String row; // row
	private Integer seat; // seat (column in rectangular seat grid) - not a
							// label on the chair!!!
	private Integer weight; // quality value - higher is better
	private Integer zone; // special needs zone (key) for this seat
	private String label; // optional string that overrides the default Seat value

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		Seat dst = new Seat();
		dst.setKey(getKey());
		dst.setRow(getRow());
		dst.setSeat(getSeat());
		dst.setWeight(getWeight());
		dst.setEnd(getEnd());
		dst.setAvailable(getAvailable());
		dst.setLayout(getLayout());
		dst.setZone(getZone());
		dst.setLabel(getLabel());
		return (dst);
	}

	
	/**
	 * get the seat number for display on tickets and the UI. This is normally the column
	 * number in the grid, unless the customer wants this numbered by counting
	 * available seats from the left of the row
	 * 
	 * @return the number
	 */
	public String getNumber() {
		
		// use label first
		if( getLabel() != null && !getLabel().isEmpty())
			return getLabel();
		if (Prefs.is(PrefName.NUMBER_SEATS_FROM_LEFT, "true")) {
			try {
				Integer i = SeatModel.getReference().getSeatNumberFromLeft(this);
				if (i == null)
					return "0";
				return Integer.toString(i);
			} catch (Exception e) {
				e.printStackTrace();
				return Integer.toString(getSeat());
			}
		}

		return Integer.toString(getSeat());

	}


}
