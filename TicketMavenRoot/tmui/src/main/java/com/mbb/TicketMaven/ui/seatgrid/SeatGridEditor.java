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

package com.mbb.TicketMaven.ui.seatgrid;

import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.filter.SeatFilterPanel;
import com.mbb.TicketMaven.ui.tablelayout.LayoutChangeListener;
import com.mbb.TicketMaven.ui.util.ComponentPrinter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collection;

public class SeatGridEditor extends ViewPanel implements LayoutChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SeatModel model_;

	private javax.swing.JButton savebutton;

	private javax.swing.JPanel jPanel1;

	private SeatGridPanel grid = null;
	private javax.swing.JScrollPane jScrollPane1;

	private JPanel jPanel = null;

	private SeatGridUpdateDetails updateDetails = null;

	
	private TitledBorder normaltb;

	private SeatFilterPanel seatFilter = null;

	public SeatGridEditor() {

		super();
		updateDetails = new SeatGridUpdateDetails();

		model_ = SeatModel.getReference();
		addModel(model_);
		addModel(ZoneModel.getReference());

		normaltb = javax.swing.BorderFactory.createTitledBorder(null, "Seat Update Options",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null);

		initComponents();

		refresh();

	}

	private JButton printButton = null;

	@Override
	public void layoutChange(Layout data) {
		
		grid.layoutChange(data);
		
		// once shows are assigned, it is read/only
		boolean hasShows = false;
		if( data != null && !data.isNew())
		{
			try {
				Collection<Show> shows  = ShowModel.getReference().getShowsForLayout(data);
				if( !shows.isEmpty() )
					hasShows = true;
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
		
		boolean allowLayoutEdit = Prefs.getBoolPref(PrefName.ALLOWLAYOUTEDIT);
		
		savebutton.setEnabled(allowLayoutEdit || !hasShows);
		
	}

	
	private SeatGridUpdateDetails getDetailView() {
		if (updateDetails != null && !updateDetails.isShowing()) {
			updateDetails.clear();
		}
		return updateDetails;
	}

	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 0); // Generated
			gridBagConstraints3.gridy = 0; // Generated
			gridBagConstraints3.weighty = 1.0D; // Generated
			gridBagConstraints3.weightx = 1.0D; // Generated
			gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH; // Generated
			gridBagConstraints3.gridx = 0; // Generated
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout()); // Generated
			jPanel.setBorder(normaltb);

			SeatGridUpdateDetails dp = getDetailView();
			if (dp != null)
				jPanel.add(dp, gridBagConstraints3); // Generated
		}
		return jPanel;
	}

	/**
	 * This method initializes seatFilter	
	 * 	
	 * @return com.mbb.TicketMaven.ui.SeatFilter	
	 */
	private SeatFilterPanel getSeatFilter() {
		if (seatFilter == null) {
			seatFilter = new SeatFilterPanel();
			seatFilter.setParent(this);
		}
		return seatFilter;
	}

	private void initComponents()// GEN-BEGIN:initComponents
	{

		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 0;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH; // Generated
		gridBagConstraints11.gridy = 0; // Generated
		gridBagConstraints11.insets = new java.awt.Insets(4, 4, 4, 4); // Generated
		gridBagConstraints11.gridx = 0; // Generated
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0; // Generated
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH; // Generated
		gridBagConstraints.gridy = 2; // Generated
		this.setLayout(new GridBagLayout()); // Generated

		jPanel1 = new javax.swing.JPanel();

		savebutton = new javax.swing.JButton();

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setPreferredSize(new java.awt.Dimension(554, 404));
		grid = new SeatGridPanel(-1,-1,false);
		jScrollPane1.setViewportView(grid);

		savebutton.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Save16.gif")));
		savebutton.setText("Update Selected Seats");
		savebutton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savebuttonActionPerformed(evt);
			}
		});

		jPanel1.add(savebutton);

		jPanel1.add(getPrintButton(), null);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;

		this.add(jScrollPane1, gridBagConstraints1); // Generated
		this.add(jPanel1, gridBagConstraints2); // Generated
		this.add(getJPanel(), gridBagConstraints); // Generated

		this.add(getSeatFilter(), gridBagConstraints4);
	}

	@Override
	public void refresh() {
		updateDetails.clear();
	}

	private void savebuttonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			grid.updateSeats(updateDetails.getAvail(), updateDetails.getAisle(), 
					updateDetails.getZone(), updateDetails.getWeight(), updateDetails.getNumber());
			updateDetails.clear();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

	}

	private JButton getPrintButton() {
		if (printButton == null) {
			printButton = new JButton();
			printButton.setText("Print Grid");
			printButton.setIcon(new ImageIcon(getClass().getResource(
			"/resource/Print16.gif")));
			printButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					new ComponentPrinter(grid).print();
				}
			});
		}
		return printButton;
	}


	public void showLayout(Layout l) {
		this.seatFilter.selectLayout(l);		
	}

}
