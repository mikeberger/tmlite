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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.util.Errmsg;

public class SeatSelector extends JDialog {

	private static final long serialVersionUID = 1L;
	private static List<Seat> selection_ = new ArrayList<Seat>();
	private int show = -1;
	private int lid = 0; // layout

	public static List<Seat> selectSeat(int show_id, SeatGridPanel.SelectionMode mode) {
		selection_.clear();
		if (show_id != -1)
			new SeatSelector(show_id, mode).setVisible(true);
		return selection_;
	}

	private SeatSelector(int show_id, SeatGridPanel.SelectionMode mode) {

		super();
		setModal(true);
		show = show_id;
		// init the gui components
		initComponents(mode);

		try {
			Show s = ShowModel.getReference().getShow(show_id);
			lid = s.getLayout().intValue();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		this.setTitle("Select Seat");
		refresh();

		pack();

	}

	public void refresh() {
		//empty
	}

	private void initComponents(SeatGridPanel.SelectionMode mode)// GEN-BEGIN:initComponents
	{

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0; // Generated
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH; // Generated
		gridBagConstraints.gridy = 1; // Generated
		this.getContentPane().setLayout(new GridBagLayout()); // Generated

		jPanel1 = new javax.swing.JPanel();

		sgp = new SeatGridPanel( show, lid, true);
		sgp.setSelectionMode(mode);

		JButton selectButton = new JButton();
		selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Save16.gif")));
		selectButton.setText("Save Selection");
		selectButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectSeats();
			}
		});

		jPanel1.add(selectButton);

		JButton clearButton = new JButton();
		clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Undo16.gif")));
		clearButton.setText("Clear Selection");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearbuttonActionPerformed(evt);
			}
		});

		jPanel1.add(clearButton);

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		this.getContentPane().add(sgp, gridBagConstraints1);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;

		this.getContentPane().add(jPanel1, gridBagConstraints2); // Generated

	}

	private void clearbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		selection_.clear();
		this.dispose();
	}
	
	private void selectSeats()
	{
		// get seats into selection_ from sgp
		selection_.addAll(sgp.getSelectedSeats());
		this.dispose();
	}

	private javax.swing.JPanel jPanel1;

	private SeatGridPanel sgp;

}
