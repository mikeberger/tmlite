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

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.ui.detail.LayoutView;
import com.mbb.TicketMaven.ui.detail.ZoneView;
import com.mbb.TicketMaven.ui.seatgrid.SeatGridEditor;
import com.mbb.TicketMaven.ui.tablelayout.TableLayoutEditor;
import com.mbb.TicketMaven.ui.util.TableSorter;

import javax.swing.*;
import java.awt.*;

/**
 * The Class TheaterLayout provides the UI for managing seating and table layouts and special needs zones
 */
public class TheaterLayout extends JTabbedPane implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The icon. */
	private Icon icon;
	
	private SeatGridEditor seatGridEditor = null;
	private TableLayoutEditor tableLayoutEditor = null;
	
	/**
	 * Instantiates a new theater layout.
	 */
	public TheaterLayout()
	{
		icon = new ImageIcon(getClass().getResource("/resource/grid16.gif"));
		// the first tab is a warning message tab that the user has to dismiss to proceed
		// since mucking with the layouts can cause lots of damage if the user is not the person
		// who manages them
		this.addTab("Warning", null, getSeatWarningPane(), null);
		
		/**
		 * normal ViewListPanel showing Layouts
		 */
		this.addTab("Layouts", null,
				new ViewListPanel<Layout>(LayoutModel.getReference(),
						new LayoutView(), null, new TableSorter(
								new String[] { "Name", "Seating Type",
										"Rows", "Seats" }, new Class[] {
										java.lang.String.class,
										java.lang.String.class,
										java.lang.Integer.class,
										java.lang.Integer.class }),
						new String[] { "Name", "Seating", "NumRows",
								"NumSeats" }), null);
		this.setEnabledAt(1, false); // hidden at first

		/*
		 * The seat grid editor is the not a simple ViewListPanel
		 * It is a grid-like UI
		 */
		this.addTab("Seat Grid", null, seatGridEditor = new SeatGridEditor(), null);
		
		/*
		 * ViewListPanel showing the zones 
		 */
		this.addTab("Zones (Special Needs)", null,
				new ViewListPanel<Zone>(ZoneModel.getReference(),
						new ZoneView(), null, new TableSorter(new String[] {
								"Zone Name", "Exclusive", }, new Class[] {
								java.lang.String.class,
								java.lang.String.class }), new String[] {
								"Name", "Exclusive" }), null);
		
		/**
		 * The JGraph-based table layout editor
		 */
		this.addTab("Table Layout Editor", tableLayoutEditor = new TableLayoutEditor());

		// hide the rest of the editing tabs at first
		this.setEnabledAt(2, false);
		this.setEnabledAt(3, false);
		this.setEnabledAt(4, false);
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
		return "Theater Layout";
	}

	/**
	 * Gets the seat warning pane.
	 * 
	 * @return the seat warning pane
	 */
	private JPanel getSeatWarningPane() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JTextArea l = new JTextArea(
				"\n\n\n*** WARNING***\n\n\nThe Theater Layout contains a representation of the theater seating that is used to control the basic operation of this software.\nOnly edit this data if you are sure of what you are doing.\nPress the Proceed button below to continue.");
		l.setEditable(false);
		JButton b = new JButton("Proceed");
		b.setForeground(Color.RED);
		b.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				seatwarnproceed();
			}
		});
		p.add(l);
		p.add(b);

		return p;
	}
	
	/**
	 * When the user dismisses the warning about editing layouts, this method is called
	 * to display the layout editing tabs and destroy the warning tab.
	 */
	private void seatwarnproceed() {
		this.setEnabledAt(1, true);
		this.setEnabledAt(2, true);
		this.setEnabledAt(3, true);
		this.setEnabledAt(4, true);
		this.removeTabAt(0);
	}
	
	public void editSeats(Layout l)
	{
		if( l == null || l.isNew())
			return; // should not happen
		
		if( l.getSeating().equals(LayoutModel.AUDITORIUM))
		{
			seatGridEditor.showLayout(l);
			this.setSelectedComponent(seatGridEditor);
		}
		else
		{
			tableLayoutEditor.showLayout(l);
			this.setSelectedComponent(tableLayoutEditor);
		}
	}

}
