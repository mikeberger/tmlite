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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.mbb.TicketMaven.ui.MainView;
import com.mbb.TicketMaven.ui.options.OptionsView.OptionsPanel;
import com.mbb.TicketMaven.ui.util.FontChooser;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * UI for the appearance options tab
 *
 */
public class AppearanceOptionsPanel extends OptionsPanel {

	private static final long serialVersionUID = 1L;

	private JTextField defaultFontText = new JTextField();
	private JComboBox<String> lnfBox = new JComboBox<String>();
	private JComboBox<String> tabBox = new JComboBox<String>();

	/**
	 * Instantiates a new appearance options panel.
	 */
	public AppearanceOptionsPanel() {
		this.setLayout(new GridBagLayout());
		JLabel lnfLabel = new JLabel();
		lnfLabel.setText("Look and Feel:");
		lnfBox.setEditable(false);
		lnfBox.setAutoscrolls(true);

		this.add(lnfLabel, GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH));
		this.add(lnfBox, GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));

		JButton defFontButton = new JButton();
		defFontButton.setText("Set Font");
		defFontButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fontActionPerformed(defaultFontText);
			}
		});
		this.add(defFontButton, GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH));
		defaultFontText.setEditable(false);
		this.add(defaultFontText, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));

		JLabel tabl = new JLabel();
		tabl.setText("Main Window Tab Placement:");
		this.add(tabl, GridBagConstraintsFactory.create(0, 2,
				GridBagConstraints.BOTH));

		tabBox.addItem("Top");
		tabBox.addItem("Left");
		this.add(tabBox, GridBagConstraintsFactory.create(1, 2,
				GridBagConstraints.BOTH));

	}

	@Override
	public void applyChanges() {
		String newlnf = (String) lnfBox.getSelectedItem();
		String oldlnf = Prefs.getPref(PrefName.LNF);

		// try to switch the look and feel while the program is running -
		// doesn't always work well
		if (!newlnf.equals(oldlnf)) {
			try {
				UIManager.getLookAndFeelDefaults().put("ClassLoader",
						getClass().getClassLoader());
				UIManager.setLookAndFeel(newlnf);
				SwingUtilities.updateComponentTreeUI(MainView.getReference());
				Prefs.putPref(PrefName.LNF, newlnf);
			} catch (Exception e) {
				// Errmsg.getErrorHandler().notice( "Could not find look and feel: " + newlnf );
				Errmsg.getErrorHandler().notice(e.toString());
				return;
			}
		}
		Prefs.putPref(PrefName.TABSIDE, tabBox.getSelectedItem());

		Prefs.putPref(PrefName.DEFFONT, defaultFontText.getText());
		FontChooser.setDefaultFont(Font.decode(defaultFontText.getText()));

		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(MainView.getReference());

	}

	/**
	 * bring up a font chooser UI and let the user change a font
	 * 
	 * @param fontname
	 *            the preference name associated with the font
	 */
	private void fontActionPerformed(JTextField fontText) {

		// get font from pref name
		Font pf = Font.decode(fontText.getText());

		// choose a new font
		Font f = FontChooser.showDialog(null, null, pf);
		if (f == null) {
			return;
		}

		// get the font name and store
		String s = FontChooser.fontString(f);
		fontText.setText(s);

	}

	@Override
	public void loadOptions() {
		// add installed look and feels to lnfBox
		lnfBox.removeAllItems();
		TreeSet<String> lnfs = new TreeSet<String>();
		String curlnf = Prefs.getPref(PrefName.LNF);
		LookAndFeelInfo lnfinfo[] = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < lnfinfo.length; i++) {
			String name = lnfinfo[i].getClassName();
			lnfs.add(name);
		}
		try {
			// add jgoodies LNF if it's in the classpath
			Class.forName("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
			lnfs.add("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (Exception e) {
			// empty
		}

		lnfs.add(curlnf);

		for (String s : lnfs)
			lnfBox.addItem(s);

		lnfBox.setSelectedItem(curlnf);
		lnfBox.setEditable(false);
		tabBox.setSelectedItem(Prefs.getPref(PrefName.TABSIDE));

		defaultFontText.setText(Prefs.getPref(PrefName.DEFFONT));

	}

}
