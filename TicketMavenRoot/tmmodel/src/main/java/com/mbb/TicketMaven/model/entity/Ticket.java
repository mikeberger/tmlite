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

/**
 * Entity for a single ticket for a single seat in a single show for a single
 * customer
 */

public class Ticket extends KeyedEntity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((resident == null) ? 0 : resident.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
		result = prime * result + ((seatId == null) ? 0 : seatId.hashCode());
		result = prime * result + ((showDate == null) ? 0 : showDate.hashCode());
		result = prime * result + ((showId == null) ? 0 : showId.hashCode());
		result = prime * result + ((showName == null) ? 0 : showName.hashCode());
		result = prime * result + ((specialNeeds == null) ? 0 : specialNeeds.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		Ticket other = (Ticket) obj;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (resident == null) {
			if (other.resident != null)
				return false;
		} else if (!resident.equals(other.resident))
			return false;
		if (row == null) {
			if (other.row != null)
				return false;
		} else if (!row.equals(other.row))
			return false;
		if (seatId == null) {
			if (other.seatId != null)
				return false;
		} else if (!seatId.equals(other.seatId))
			return false;
		if (showDate == null) {
			if (other.showDate != null)
				return false;
		} else if (!showDate.equals(other.showDate))
			return false;
		if (showId == null) {
			if (other.showId != null)
				return false;
		} else if (!showId.equals(other.showId))
			return false;
		if (showName == null) {
			if (other.showName != null)
				return false;
		} else if (!showName.equals(other.showName))
			return false;
		if (specialNeeds == null) {
			if (other.specialNeeds != null)
				return false;
		} else if (!specialNeeds.equals(other.specialNeeds))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}


	public Integer getCustomerId() {
		return customerId;
	}


	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public Integer getPrice() {
		return price;
	}


	public void setPrice(Integer price) {
		this.price = price;
	}


	public String getResident() {
		return resident;
	}


	public void setResident(String resident) {
		this.resident = resident;
	}


	public String getRow() {
		return row;
	}


	public void setRow(String row) {
		this.row = row;
	}


	public Integer getSeatId() {
		return seatId;
	}


	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}


	public java.util.Date getShowDate() {
		return showDate;
	}


	public void setShowDate(java.util.Date showDate) {
		this.showDate = showDate;
	}


	public Integer getShowId() {
		return showId;
	}


	public void setShowId(Integer showId) {
		this.showId = showId;
	}


	public String getShowName() {
		return showName;
	}


	public void setShowName(String showName) {
		this.showName = showName;
	}


	public String getSpecialNeeds() {
		return specialNeeds;
	}


	public void setSpecialNeeds(String specialNeeds) {
		this.specialNeeds = specialNeeds;
	}


	public String getTable() {
		return table;
	}


	public void setTable(String table) {
		this.table = table;
	}

	private static final long serialVersionUID = 1L;

	private Integer customerId; // key of the customer
	private String customerName; // transient - loaded for easy reporting and
									// ticket printing, but not stored in DB
									// with this record
	private Integer price; // price of the ticket - not always the show price -
							// customer may have bough this ticket at a discount
	private String resident; // transient - from customer
	private String row; // row - transient
	private Integer seatId; // key to the seat record
	private java.util.Date showDate; // transient
	private Integer showId; // key of the show
	private String showName; // transient
	private String specialNeeds; // transient - this comes from the seat record
	private String table; // transient - just filled in to be able to reuse the
							// ticket class to hold reservation info in some

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		Ticket dst = new Ticket();
		dst.setKey(getKey());
		dst.setCustomerId(getCustomerId());
		dst.setCustomerName(getCustomerName());
		dst.setShowId(getShowId());
		dst.setShowName(getShowName());
		dst.setShowDate(getShowDate());
		dst.setSeatId(getSeatId());
		dst.setRow(getRow());
		dst.setSpecialNeeds(getSpecialNeeds());
		dst.setResident(getResident());
		dst.setPrice(getPrice());
		return (dst);
	}


	// convenience - sinlge place where row/aisle is formatted
	/**
	 * Gets the row aisle.
	 * 
	 * @return the row aisle
	 */
	public String getRowAisle() {
		if (getRow() == null)
			return "";
		return getRow() + "/" + getSeat();
	}

	// converts seat integer to string
	/**
	 * Gets the seat.
	 * 
	 * @return the seat
	 */
	public String getSeat() {

		if (getSeatId() == null)
			return "";
		try {
			Seat s = SeatModel.getReference().getSeat(getSeatId().intValue());
			return s.getNumber();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * gets the original positional seat number from the seat
	 * 
	 */
	public String getSeatInRow()
	{
		if (getSeatId() == null)
			return "";
		try {
			Seat s = SeatModel.getReference().getSeat(getSeatId().intValue());
			return Integer.toString(s.getSeat());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


}
