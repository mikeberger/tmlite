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

package com.mbb.TicketMaven.ui.detail;

import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.LimitDocument;
import com.mbb.TicketMaven.util.Warning;

import javax.swing.*;
import java.awt.*;

public class ZoneView extends ViewDetailPanel<Zone> {

	
	private static final long serialVersionUID = 1L;
	private JComboBox<String> exclBox = null;
	private JTextField nametext = null;

	private Zone zone_; // @jve:decl-index=0:

	public ZoneView() {
		super();

		zone_ = null;

		initialize(); // init the GUI widgets

		showData(null);

	}

	@Override
	public String getDuplicateError() {
		return "A Zone already exists with the same name. Cannot store a duplicate";
	}

	/**
	 * This method initializes exclBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getExclBox() {
		if (exclBox == null) {

			exclBox = new JComboBox<String>();
			exclBox.setEnabled(true);
			exclBox.setEditable(false);
			exclBox.addItem("Y");
			exclBox.addItem("N");

		}
		return exclBox;
	}

	/**
	 * This method initializes fntext
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getZoneNameText() {
		if (nametext == null) {
			nametext = new JTextField();
			nametext.setColumns(100);
			nametext.setDocument(new LimitDocument(100));
		}
		return nametext;
	}

	private void initialize() {

		this.setLayout(new GridBagLayout());

		this.add(new JLabel("Zone Name:"), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH));
		this.add(getZoneNameText(), GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(new JLabel("Exclusive:"), GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
		this.add(getExclBox(), GridBagConstraintsFactory.create(1, 1, GridBagConstraints.VERTICAL));
	}

	@Override
	public void refresh() {
		// empty
	}

	@Override
	public void saveData() throws Exception, Warning {
		if (nametext.getText().equals("")) {
			throw new Warning("Zone Name is Required");
		}

		zone_.setName(nametext.getText());
		zone_.setExclusive((String) exclBox.getSelectedItem());

		ZoneModel.getReference().saveRecord(zone_);

	}

	@Override
	public void showData(Zone s) {

		zone_ = s;
		if (s == null || s.isNew()) {
			nametext.setText("");
			exclBox.setSelectedItem("N");
			return;
		}

		nametext.setText(zone_.getName());
		exclBox.setSelectedItem(zone_.getExclusive());

	}

}
