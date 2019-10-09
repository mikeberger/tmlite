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
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.ui.util.LimitDocument;
import com.mbb.TicketMaven.util.Errmsg;

public class SeatGridUpdateDetails extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox<Object> weightBox = null;
	private JComboBox<String> endBox = null;
	private JComboBox<String> availBox = null;
	private JComboBox<String> zoneBox = null;
	private JTextField numberText = null;

	public final static String NO_CHANGE = "No Change";

	public SeatGridUpdateDetails() {
		super();
		initialize();
	}

	public void clear() {

		populateZoneBox();
		weightBox.setSelectedIndex(0);
		endBox.setSelectedIndex(0);
		availBox.setSelectedIndex(0);
		zoneBox.setSelectedIndex(0);
		numberText.setText("");
	}

	private JComboBox<String> getAvailBox() {
		if (availBox == null) {
			availBox = new JComboBox<String>();
			availBox.addItem(NO_CHANGE);
			availBox.addItem("Y");
			availBox.addItem("N");
		}
		return availBox;
	}

	private JComboBox<String> getEndBox() {
		if (endBox == null) {
			endBox = new JComboBox<String>();
			endBox.addItem(NO_CHANGE);
			endBox.addItem(SeatModel.NONE);
			endBox.addItem(SeatModel.LEFT);
			endBox.addItem(SeatModel.RIGHT);
			endBox.addItem(SeatModel.FRONT);
		}
		return endBox;
	}

	private JComboBox<Object> getWeightBox() {
		if (weightBox == null) {
			weightBox = new JComboBox<Object>();
			weightBox.addItem(NO_CHANGE);
			for (int i = 1; i <= SeatModel.MAX_WEIGHT; i++) {
				weightBox.addItem(Integer.valueOf(i));
			}
		}
		return weightBox;
	}

	private JComboBox<String> getZoneBox() {
		if (zoneBox == null) {
			zoneBox = new JComboBox<String>();
		}
		return zoneBox;
	}

	private JTextField getNumberText() {
		if (numberText == null) {
			numberText = new JTextField();
			numberText.setColumns(10); 
			numberText.setDocument(new LimitDocument(10));

		}
		return numberText;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		JLabel zoneLabel = new JLabel("Zone:");
		JLabel endLabel = new JLabel("Aisle:");
		JLabel weightlabel = new JLabel("Quality (1-30):"); // Generated
		JLabel lLabel = new JLabel("Seat Number:");
		JLabel availLabel = new JLabel("Available for Lottery: ");
		this.setLayout(new GridBagLayout()); // Generated

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; 
		gbc.fill = java.awt.GridBagConstraints.NONE; 
		gbc.insets = new java.awt.Insets(4, 4, 4, 4); 
		gbc.anchor = java.awt.GridBagConstraints.EAST; 
		gbc.gridy = 0;
		this.add(availLabel, gbc);
		
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1; 
		gbc2.anchor = java.awt.GridBagConstraints.WEST; 
		gbc2.insets = new java.awt.Insets(4, 4, 4, 4); 
		gbc2.gridy = 0; 
		gbc2.weightx = 1.0;
		this.add(getAvailBox(), gbc2);
		
		gbc.gridx = 2;
		this.add(endLabel, gbc);
		
		gbc2.gridx = 3;
		this.add(getEndBox(), gbc2);
		
		gbc.gridx = 4;
		this.add(zoneLabel, gbc);
		
		gbc2.gridx = 5;
		this.add(getZoneBox(), gbc2);
		
		gbc.gridx = 6;
		this.add(weightlabel, gbc);
		
		gbc2.gridx = 7;
		this.add(getWeightBox(), gbc2);
		
		gbc.gridx = 8;
		this.add(lLabel, gbc);
		
		gbc2.gridx = 9;
		this.add(getNumberText(), gbc2);
		
	}

	private void populateZoneBox() {
		zoneBox.setEnabled(true);
		zoneBox.removeAllItems();
		zoneBox.addItem(NO_CHANGE);
		zoneBox.addItem("");
		Collection<Zone> zones = null;
		try {
			zones = ZoneModel.getReference().getRecords();

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}
		Iterator<Zone> it = zones.iterator();
		while (it.hasNext()) {
			Zone l = it.next();
			zoneBox.addItem(l.getName());
		}

	}
	
	public String getZone()
	{
		return (String)zoneBox.getSelectedItem();
	}
	public String getAisle()
	{
		return (String)endBox.getSelectedItem();
	}
	public String getAvail()
	{
		return (String)availBox.getSelectedItem();
	}
	public Object getWeight()
	{
		return weightBox.getSelectedItem();
	}
	public String getNumber()
	{
		return numberText.getText();
	}

}
