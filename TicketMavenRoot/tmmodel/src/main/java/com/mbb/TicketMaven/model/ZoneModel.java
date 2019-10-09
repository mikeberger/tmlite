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



import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.util.Warning;


/**
 * The Zone Model.
 */
public class ZoneModel extends KeyedEntityModel<Zone> {

	protected ZoneModel() {
		super(Zone.class);
	}

	static private ZoneModel self_ = new ZoneModel();

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static ZoneModel getReference() {
		return (self_);
	}

	/**
	 * Gets a zone by key.
	 * 
	 * @param num the key
	 * 
	 * @return the zone
	 * 
	 * @throws Exception the exception
	 */
	public Zone getZone(int num) throws Exception {
		return (super.getRecord(num));
	}

	// override
	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.KeyedEntityModel#saveRecord(com.mbb.TicketMaven.model.entity.KeyedEntity)
	 */
	@Override
	public int saveRecord(Zone z) throws Warning, Exception {

		if( z.getName().equals(CustomerModel.AISLE) || 
				z.getName().equals(CustomerModel.FRONT) || 
				z.getName().equals(CustomerModel.FRONT_ONLY) || 
				z.getName().equals(CustomerModel.REAR) || 
				z.getName().equals(CustomerModel.NONE))
		{
			throw new Warning("Please use a Different Zone Name. The name " + z.getName() + " is a built-in Zone Name that is already in use.");
		}
		return super.saveRecord(z);
		
	}

}
