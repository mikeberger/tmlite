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

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketRequestModel;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.ui.detail.RequestView;
import com.mbb.TicketMaven.ui.filter.RequestFilterPanel;
import com.mbb.TicketMaven.ui.util.TableSorter;

import javax.swing.*;
import java.awt.*;

/**
 * The Class RequestManager provides the UI for managing ticket requests
 */
public class RequestManager extends ViewListPanel<TicketRequest> implements
		Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon = null;

	/**
	 * Instantiates a new request manager.
	 */
	public RequestManager() {
		super(TicketRequestModel.getReference(), new RequestView(),
				new RequestFilterPanel(), new TableSorter(new String[] {
						"Customer", "Show", "Show Date", "Tickets",
						"Special Needs" }, new Class[] {
						java.lang.String.class, java.lang.String.class,
						java.util.Date.class, java.lang.Integer.class,
						java.lang.String.class }), new String[] {
						"CustomerName", "ShowName", "ShowDate", "Tickets",
						"SpecialNeeds" });
		icon = new ImageIcon(getClass().getResource("/resource/cart16.gif"));
		this.addModel(CustomerModel.getReference());
		this.addModel(ShowModel.getReference());

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
		return "Request Manager";
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

}
