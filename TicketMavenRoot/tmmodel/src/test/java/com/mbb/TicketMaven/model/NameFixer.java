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

import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogManager;


public class NameFixer {

	public static void main(String args[]) throws Exception {
		
		LogManager.getLogManager().getLogger("").setLevel(Level.ALL);

		if( args.length != 1) return;
		
		String dbdir = args[0];
		JdbcDB.setDbUrl(dbdir);
		JdbcDB.connect(false);
		
		//JdbcDB.startTransaction();

		Collection<Customer> custs = CustomerModel.getReference().getRecords();
		for( Customer cust : custs)
		{
			if( cust.getFirstName() == null || cust.getFirstName().isEmpty())
			{
				String [] parts = cust.getLastName().split(" ");
				if( parts.length == 2)
				{
					cust.setFirstName(parts[0]);
					cust.setLastName(parts[1]);
					CustomerModel.getReference().saveRecord(cust);
					System.out.println("[" + cust.getFirstName() + "][" + cust.getLastName() + "]");
				}
				else if( parts.length == 4 && parts[1].equals("&"))
				{
					cust.setFirstName(parts[0] + "&" + parts[2]);
					cust.setLastName(parts[3]);
					CustomerModel.getReference().saveRecord(cust);
					System.out.println("[" + cust.getFirstName() + "][" + cust.getLastName() + "]");
				}
				else if( parts.length == 5 && parts[2].equals("&"))
				{
					cust.setFirstName(cust.getLastName());
					cust.setLastName(parts[1]);
					CustomerModel.getReference().saveRecord(cust);
					System.out.println("[" + cust.getFirstName() + "][" + cust.getLastName() + "]");
				}
			}
		}
		
		//JdbcDB.commitTransaction();
		try {
			JdbcDB.cleanup();

		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}



}
