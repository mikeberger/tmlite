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
 * The Customer Entity.
 */

public class Customer extends KeyedEntity {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((Address == null) ? 0 : Address.hashCode());
		result = prime * result + ((AllowedTickets == null) ? 0 : AllowedTickets.hashCode());
		result = prime * result + ((Email == null) ? 0 : Email.hashCode());
		result = prime * result + ((FirstName == null) ? 0 : FirstName.hashCode());
		result = prime * result + ((LastName == null) ? 0 : LastName.hashCode());
		result = prime * result + ((Notes == null) ? 0 : Notes.hashCode());
		result = prime * result + ((Phone == null) ? 0 : Phone.hashCode());
		result = prime * result + ((Resident == null) ? 0 : Resident.hashCode());
		result = prime * result + ((SpecialNeedsType == null) ? 0 : SpecialNeedsType.hashCode());
		result = prime * result + ((TotalQuality == null) ? 0 : TotalQuality.hashCode());
		result = prime * result + ((TotalTickets == null) ? 0 : TotalTickets.hashCode());
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
		Customer other = (Customer) obj;
		if (Address == null) {
			if (other.Address != null)
				return false;
		} else if (!Address.equals(other.Address))
			return false;
		if (AllowedTickets == null) {
			if (other.AllowedTickets != null)
				return false;
		} else if (!AllowedTickets.equals(other.AllowedTickets))
			return false;
		if (Email == null) {
			if (other.Email != null)
				return false;
		} else if (!Email.equals(other.Email))
			return false;
		if (FirstName == null) {
			if (other.FirstName != null)
				return false;
		} else if (!FirstName.equals(other.FirstName))
			return false;
		if (LastName == null) {
			if (other.LastName != null)
				return false;
		} else if (!LastName.equals(other.LastName))
			return false;
		if (Notes == null) {
			if (other.Notes != null)
				return false;
		} else if (!Notes.equals(other.Notes))
			return false;
		if (Phone == null) {
			if (other.Phone != null)
				return false;
		} else if (!Phone.equals(other.Phone))
			return false;
		if (Resident == null) {
			if (other.Resident != null)
				return false;
		} else if (!Resident.equals(other.Resident))
			return false;
		if (SpecialNeedsType == null) {
			if (other.SpecialNeedsType != null)
				return false;
		} else if (!SpecialNeedsType.equals(other.SpecialNeedsType))
			return false;
		if (TotalQuality == null) {
			if (other.TotalQuality != null)
				return false;
		} else if (!TotalQuality.equals(other.TotalQuality))
			return false;
		if (TotalTickets == null) {
			if (other.TotalTickets != null)
				return false;
		} else if (!TotalTickets.equals(other.TotalTickets))
			return false;
		return true;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public Integer getAllowedTickets() {
		return AllowedTickets;
	}

	public void setAllowedTickets(Integer allowedTickets) {
		AllowedTickets = allowedTickets;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getResident() {
		return Resident;
	}

	public void setResident(String resident) {
		Resident = resident;
	}

	public String getSpecialNeedsType() {
		return SpecialNeedsType;
	}

	public void setSpecialNeedsType(String specialNeedsType) {
		SpecialNeedsType = specialNeedsType;
	}

	public Integer getTotalQuality() {
		return TotalQuality;
	}

	public void setTotalQuality(Integer totalQuality) {
		TotalQuality = totalQuality;
	}

	public Integer getTotalTickets() {
		return TotalTickets;
	}

	public void setTotalTickets(Integer totalTickets) {
		TotalTickets = totalTickets;
	}

	private static final long serialVersionUID = 1L;

	private String Address;
	private Integer AllowedTickets; 
	private String Email;
	private String FirstName;
	private String LastName;
	private String Notes;
	private String Phone;
	private String Resident; // flag to indicate if this customer is a resident of the community
	private String SpecialNeedsType; // this string matches the name of a Zone instance
	private Integer TotalQuality; // sum of seat quality values assigned to this customer (ever)
	private Integer TotalTickets; // total tickets assigned to this customer (ever)
	
	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.entity.KeyedEntity#clone()
	 */
	@Override
	protected Object clone() {
		Customer dst = new Customer();
		dst.setKey( getKey());
		dst.setFirstName( getFirstName() );
		dst.setLastName( getLastName() );
		dst.setEmail( getEmail() );
		dst.setPhone( getPhone() );
		dst.setNotes( getNotes() );
		dst.setAllowedTickets( getAllowedTickets() );
		dst.setSpecialNeedsType( getSpecialNeedsType() );
		dst.setTotalTickets( getTotalTickets() );
		dst.setTotalQuality( getTotalQuality() );
		dst.setAddress( getAddress() );
		dst.setResident( getResident() );
		return(dst);
	}

	
}
