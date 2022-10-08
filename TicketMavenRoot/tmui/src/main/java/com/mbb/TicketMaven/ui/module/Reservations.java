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
import com.mbb.TicketMaven.model.ReservationModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.ui.detail.ReservationView;
import com.mbb.TicketMaven.ui.filter.ReservationFilterPanel;
import com.mbb.TicketMaven.ui.tablelayout.TableLayoutViewer;
import com.mbb.TicketMaven.ui.util.TableSorter;

import javax.swing.*;
import java.awt.*;

/**
 * The Class Reservations provides the UI for managing reservations
 */
public class Reservations extends JTabbedPane implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon;

	/**
	 * Instantiates a new reservations.
	 */
	public Reservations() {
		icon = new ImageIcon(getClass().getResource("/resource/tm16.jpg"));

		ViewListPanel<Reservation> respanel = null;

		/*
		 * the tabular reservation editor
		 */
		this.addTab("Table Reservations Editor", null,
				respanel = new ViewListPanel<Reservation>(ReservationModel
						.getReference(), new ReservationView(),
						new ReservationFilterPanel(),
						new TableSorter(new String[] { "Customer", "Show",
								"Show Date", "Table", "Seats", "Payment","Amount" }, new Class[] {
								java.lang.String.class, java.lang.String.class,
								java.util.Date.class, java.lang.String.class,
								Integer.class, java.lang.String.class,java.lang.String.class }), new String[] {
								"CustomerName", "ShowName", "ShowDate",
								"TableName", "Num", "Payment", "Amount"}), null);

		/**
		 * a JGraph based viewer to view resevations for a Show - read-only
		 */
		this.addTab("Table View", new TableLayoutViewer());
		respanel.addModel(CustomerModel.getReference());
		respanel.addModel(ShowModel.getReference());

	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Table Reservations";
	}

}
