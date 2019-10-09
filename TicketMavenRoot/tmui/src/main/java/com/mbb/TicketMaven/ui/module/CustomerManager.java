/*
 * #%L
 * tmui
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

package com.mbb.TicketMaven.ui.module;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.ui.detail.CustomerView;
import com.mbb.TicketMaven.ui.util.TableSorter;

/**
 * The Class CustomerManager provides the customer manager UI
 */
public class CustomerManager extends ViewListPanel<Customer> implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon = null;

	/**
	 * Instantiates a new customer manager.
	 */
	public CustomerManager() {
		super(CustomerModel.getReference(),
				new CustomerView(), null, new TableSorter(
						new String[] { "First Name", "Last Name",
								"Phone", "Tickets",
								"Documented Special Needs" },
						new Class[] { java.lang.String.class,
								java.lang.String.class,
								java.lang.String.class,
								java.lang.Integer.class,
								java.lang.String.class }),
				new String[] { "FirstName", "LastName", "Phone",
						"AllowedTickets", "SpecialNeedsType" });
		icon = new ImageIcon(getClass().getResource("/resource/addr16.jpg"));

	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Customer Manager";
	}


	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

}
