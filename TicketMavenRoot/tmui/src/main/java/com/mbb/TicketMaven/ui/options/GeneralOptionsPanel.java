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

package com.mbb.TicketMaven.ui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.mbb.TicketMaven.ui.options.OptionsView.OptionsPanel;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * provides the UI for the general options tab
 *
 */
public class GeneralOptionsPanel extends OptionsPanel {

	private static final long serialVersionUID = 1L;
	private JTextField communityText = new JTextField();
	private JTextField taxField = new JTextField();
	private JCheckBox systrayBox = new JCheckBox();
	private JRadioButton seatNumberButton1 = new JRadioButton();
	private JRadioButton seatNumberButton2 = new JRadioButton();
	private JCheckBox updpkgBox = new JCheckBox();
	private JCheckBox favorAisleBox = new JCheckBox();
	private JCheckBox dbPromptBox = new JCheckBox();
	private JCheckBox allowLayoutEdit = new JCheckBox();

	public GeneralOptionsPanel() {
		this.setLayout(new GridBagLayout());

		this.add(new JLabel("Community/Club Name:"), GridBagConstraintsFactory
				.create(0, 0));
		this.add(communityText, GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(new JLabel("Sales Tax Rate:"), GridBagConstraintsFactory
				.create(0, 1));
		this.add(taxField, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));
		
		ButtonGroup grp = new ButtonGroup();
		grp.add(seatNumberButton1);
		grp.add(seatNumberButton2);

		seatNumberButton1
				.setText("Number seats by their column in the grid");
		seatNumberButton2
				.setText("Number seats based on actual chairs from the left");
		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 3,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		this.add(seatNumberButton1, gbc);
		gbc = GridBagConstraintsFactory.create(0, 4,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		this.add(seatNumberButton2, gbc);

		gbc = GridBagConstraintsFactory.create(0, 5,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		
		systrayBox.setText("Stay running in system tray when window closed");
		this.add(systrayBox, gbc);
		
		gbc = GridBagConstraintsFactory.create(0, 6,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		
		updpkgBox.setText("Allow Packages to be Updated");
		this.add(updpkgBox, gbc);
		
		gbc = GridBagConstraintsFactory.create(0, 7,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		
		favorAisleBox.setText("Favor Aisle Requests Over Non-Special Needs");
		this.add(favorAisleBox, gbc);
		
		gbc = GridBagConstraintsFactory.create(0, 8,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;
		
		dbPromptBox.setText("Always prompt for Database location at startup");
		this.add(dbPromptBox, gbc);
		
		gbc = GridBagConstraintsFactory.create(0, 9,
				GridBagConstraints.BOTH, 1.0, 0.0);	
		gbc.gridwidth = 2;

		allowLayoutEdit.setText("Allow updates to layouts with shows");
		this.add(allowLayoutEdit, gbc);

	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.options.OptionsView.OptionsPanel#applyChanges()
	 */
	@Override
	public void applyChanges() {
		Prefs.putPref(PrefName.SALESTAX, taxField.getText());
		Prefs.putPref(PrefName.COMMUNITY, communityText.getText());
		if (seatNumberButton1.isSelected())
			Prefs.putPref(PrefName.NUMBER_SEATS_FROM_LEFT, "false");
		else
			Prefs.putPref(PrefName.NUMBER_SEATS_FROM_LEFT, "true");
		OptionsPanel.setBooleanPref(systrayBox, PrefName.SYSTRAY);
		OptionsPanel.setBooleanPref(updpkgBox, PrefName.UPDPKG);
		OptionsPanel.setBooleanPref(favorAisleBox, PrefName.FAVOR_AISLE_REQUESTS);
		OptionsPanel.setBooleanPref(dbPromptBox, PrefName.DBPROMPT);
		OptionsPanel.setBooleanPref(allowLayoutEdit, PrefName.ALLOWLAYOUTEDIT);

	}

	@Override
	public void loadOptions() {
		communityText.setText(Prefs.getPref(PrefName.COMMUNITY));
		taxField.setText(Prefs.getPref(PrefName.SALESTAX));
		String numbering = Prefs.getPref(PrefName.NUMBER_SEATS_FROM_LEFT);
		if ("true".equals(numbering))
			seatNumberButton2.setSelected(true);
		else
			seatNumberButton1.setSelected(true);
		
		OptionsPanel.setCheckBox(systrayBox, PrefName.SYSTRAY);
		OptionsPanel.setCheckBox(updpkgBox, PrefName.UPDPKG);
		OptionsPanel.setCheckBox(favorAisleBox, PrefName.FAVOR_AISLE_REQUESTS);
		OptionsPanel.setCheckBox(dbPromptBox, PrefName.DBPROMPT);
		OptionsPanel.setCheckBox(allowLayoutEdit, PrefName.ALLOWLAYOUTEDIT);

	}

}
