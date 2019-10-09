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

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.ui.detail.ShowView;
import com.mbb.TicketMaven.ui.filter.ShowFilterPanel;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Money;

/**
 * The Class ShowManager provides the UI for managing shows
 */
public class ShowManager extends ViewListPanel<Show> implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon = null;

	/**
	 * Instantiates a new show manager.
	 */
	public ShowManager() {
		super(
				ShowModel.getReference(), // the Show Model, which is the source
				// of the managed objects
				new ShowView(), // the detailed show editing form
				new ShowFilterPanel(), // the filter panel for filtering Shows
				// here's the table related arguments to set up the Show Table
				// first the table model
				new TableSorter(new String[] { "Show Name", "Show Date/Time",
						"Ticket Price" }, // column headings
						new Class[] { java.lang.String.class,
								java.util.Date.class, Money.class }),
				new String[] { "Name", "DateTime", "Price" });
		icon = new ImageIcon(getClass().getResource("/resource/show16.gif"));
		this.addModel(TicketModel.getReference());
		this.addModel(LayoutModel.getReference());

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
		return "Show Manager";
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

}
