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


import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.TMPackage;
import com.mbb.TicketMaven.model.jdbc.PackageJdbcDB;
import com.mbb.TicketMaven.util.Errmsg;

import java.util.Collection;


/**
 * The Package Model
 */
public class PackageModel extends KeyedEntityModel<TMPackage> {

	protected PackageModel() {
		super(TMPackage.class);
	}

	static private PackageModel self_ = new PackageModel();
	
	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.model.KeyedEntityModel#getBeanName()
	 */
	@Override
	public String getEntityName()
	{
		return("Package");
	}

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static PackageModel getReference() {
		return (self_);
	}

	
	/**
	 * create a new package.
	 * 
	 * @return the tM package
	 */
	public TMPackage newPackage() {
		return (super.newRecord());
	}

	/**
	 * Calculate a package discount based on the shows in it and the package price.
	 * 
	 * @param p the package
	 * 
	 * @return the discount percent
	 */
	public static double calcDiscount(TMPackage p)
	{
		try{
			// package price
			int price = (p.getPrice() != null) ? p.getPrice().intValue() : 0;
			
			// total the cost of the shows in the package
			Collection<Integer> c = p.getShows();
			int total = 0;
			for(Integer i : c)
			{
				Show sh = null;
				try {
					sh = ShowModel.getReference().getShow(i.intValue());
					total += sh.getPrice().intValue();
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
					continue;
				}			
			}
			
			if( price > total )
			{
				return 0;
			}
			
			// calculate the discount percent
			double percent = (((double)total - (double)price) * 100.0) / total;
			return percent;
		}
		catch( Exception e)
		{
			return 0;
		}
	}

	public void deletePackagesForShow(int key) throws Exception {
		PackageJdbcDB pdb = (PackageJdbcDB)db_;
		pdb.deletePackagesForShow(key);		
	}

}
