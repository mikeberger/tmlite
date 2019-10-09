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
 * Entity for a single PENDING request for one or more tickets by a single customer for a single show
 */

public class TicketRequest extends KeyedEntity {


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((lotteryPosition == null) ? 0 : lotteryPosition.hashCode());
		result = prime * result + ((showDate == null) ? 0 : showDate.hashCode());
		result = prime * result + ((showId == null) ? 0 : showId.hashCode());
		result = prime * result + ((showName == null) ? 0 : showName.hashCode());
		result = prime * result + ((specialNeeds == null) ? 0 : specialNeeds.hashCode());
		result = prime * result + ((tickets == null) ? 0 : tickets.hashCode());
		result = prime * result + ((totalQuality == null) ? 0 : totalQuality.hashCode());
		result = prime * result + ((totalTickets == null) ? 0 : totalTickets.hashCode());
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
		TicketRequest other = (TicketRequest) obj;
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
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (lotteryPosition == null) {
			if (other.lotteryPosition != null)
				return false;
		} else if (!lotteryPosition.equals(other.lotteryPosition))
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
		if (tickets == null) {
			if (other.tickets != null)
				return false;
		} else if (!tickets.equals(other.tickets))
			return false;
		if (totalQuality == null) {
			if (other.totalQuality != null)
				return false;
		} else if (!totalQuality.equals(other.totalQuality))
			return false;
		if (totalTickets == null) {
			if (other.totalTickets != null)
				return false;
		} else if (!totalTickets.equals(other.totalTickets))
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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getLotteryPosition() {
		return lotteryPosition;
	}

	public void setLotteryPosition(Integer lotteryPosition) {
		this.lotteryPosition = lotteryPosition;
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

	public Integer getTickets() {
		return tickets;
	}

	public void setTickets(Integer tickets) {
		this.tickets = tickets;
	}

	public Integer getTotalQuality() {
		return totalQuality;
	}

	public void setTotalQuality(Integer totalQuality) {
		this.totalQuality = totalQuality;
	}

	public Integer getTotalTickets() {
		return totalTickets;
	}

	public void setTotalTickets(Integer totalTickets) {
		this.totalTickets = totalTickets;
	}

	private static final long serialVersionUID = 1L;

	private Integer customerId; // key to the customer record
	private String customerName; // transient
	private Double discount; // discount that this request received due to a package purchase
	private Integer lotteryPosition; // transient - utilized during lottery
	private java.util.Date showDate; // transient
	private Integer showId; // key to the show
	private String showName; // transient
	private String specialNeeds; // special needs of the customer - transient
	private Integer tickets; // number of tickets requested
	private Integer totalQuality; // transient - from customer
	private Integer totalTickets; // transient - from customer
	
	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		TicketRequest dst = new TicketRequest();
		dst.setKey( getKey());
		dst.setCustomerId( getCustomerId() );
		dst.setCustomerName( getCustomerName() );
		dst.setShowId( getShowId() );
		dst.setShowName( getShowName() );
		dst.setShowDate( getShowDate() );
		dst.setTickets( getTickets() );
		dst.setSpecialNeeds( getSpecialNeeds() );
		dst.setTotalTickets( getTotalTickets() );
		dst.setTotalQuality( getTotalQuality() );
		dst.setLotteryPosition( getLotteryPosition() );
		dst.setDiscount( getDiscount() );
		return(dst);
	}


}
