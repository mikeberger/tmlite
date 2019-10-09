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
import javax.swing.JTabbedPane;
import javax.swing.table.TableModel;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.KeyedEntityModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.ui.detail.TicketView;
import com.mbb.TicketMaven.ui.detail.ViewDetailPanel;
import com.mbb.TicketMaven.ui.filter.FilterPanel;
import com.mbb.TicketMaven.ui.filter.TicketFilterPanel;
import com.mbb.TicketMaven.ui.seatgrid.SeatGridTicketViewer;
import com.mbb.TicketMaven.ui.util.TableSorter;

/**
 * The Class AssignedTickets provides the Assigned Tickets manager of the UI
 */
public class AssignedTickets extends JTabbedPane implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon;
	
	/**
	 * subclass ViewListPanel in order to override the delete method
	 *
	 */
	private class TicketListEditor extends ViewListPanel<Ticket>
	{

		private static final long serialVersionUID = 1L;

		public TicketListEditor(KeyedEntityModel<Ticket> mod,
				ViewDetailPanel<Ticket> dp, FilterPanel<Ticket> fp,
				TableModel tm, String[] fields) {
			super(mod, dp, fp, tm, fields);
		}

		@Override
		protected void deleteEntity(Ticket entity) throws Exception {
			
			// subtract quality from the customer before deleting a ticket
			CustomerModel.getReference().subtractTicketQuality(entity);
			super.deleteEntity(entity);
		}

		
		
	}

	/**
	 * Instantiates a new assigned tickets.
	 */
	public AssignedTickets() {
		icon = new ImageIcon(getClass().getResource("/resource/tm16.jpg"));

		TicketListEditor tkpanel = null;

		/**
		 * Ticket ViewListPanel for a tabular view
		 */
		this.addTab("Tickets Editor", null,
				tkpanel = new TicketListEditor(TicketModel.getReference(),
						new TicketView(), new TicketFilterPanel(),
						new TableSorter(new String[] { "Customer", "Show",
								"Show Date", "Row/Seat", "Special Needs" },
								new Class[] { java.lang.String.class,
										java.lang.String.class,
										java.util.Date.class,
										java.lang.String.class,
										java.lang.String.class }),
						new String[] { "CustomerName", "ShowName", "ShowDate",
								"RowAisle", "SpecialNeeds" }), null);
		tkpanel.addModel(CustomerModel.getReference());
		tkpanel.addModel(ShowModel.getReference());
		tkpanel.addModel(SeatModel.getReference());

		/**
		 * The Seat-Grid view of tickets for a Show
		 */
		this.addTab("Seat Grid", null, new SeatGridTicketViewer(), null);

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
		return "Assigned Tickets";
	}


}
