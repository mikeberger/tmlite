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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.TicketFormat;
import com.mbb.TicketMaven.ui.ViewFrame;
import com.mbb.TicketMaven.ui.ticketprint.TicketPreview;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * OptionsView provides the UI for editing the TicketMaven Preferences.
 */
public class OptionsView extends ViewFrame {

	/**
	 * 
	 * abstract base class for tabs in the options view
	 * 
	 */
	static public abstract class OptionsPanel extends JPanel {
		private static final long serialVersionUID = -4942616624428977307L;

		/**
		 * set a boolean preference from a checkbox
		 * 
		 * @param box
		 *            the checkbox
		 * @param pn
		 *            the preference name
		 */
		static public void setBooleanPref(JCheckBox box, PrefName pn) {
			if (box.isSelected()) {
				Prefs.putPref(pn, "true");
			} else {
				Prefs.putPref(pn, "false");
			}
		}

		/**
		 * set a check box from a boolean preference
		 * 
		 * @param box
		 *            the checkbox
		 * @param pn
		 *            the preference name
		 */
		static public void setCheckBox(JCheckBox box, PrefName pn) {
			String val = Prefs.getPref(pn);
			if (val.equals("true")) {
				box.setSelected(true);
			} else {
				box.setSelected(false);
			}
		}

		/**
		 * save options from the UI to the preference store
		 */
		public abstract void applyChanges();

		/**
		 * load options from the preference store into the UI
		 */
		public abstract void loadOptions();
	}

	private static final long serialVersionUID = 1L;

	private static OptionsView singleton = null;

	/**
	 * Gets the singleton.
	 * 
	 * @return the singleton
	 */
	public static OptionsView getReference() {
		if (singleton == null || !singleton.isShowing())
			singleton = new OptionsView();
		return (singleton);
	}

	private AppearanceOptionsPanel appearancePanel = new AppearanceOptionsPanel();
	private TicketPreview auditoriumTicketPreview = null;
	private DatabaseOptionsPanel databasePanel = new DatabaseOptionsPanel();
	private GeneralOptionsPanel generalPanel = new GeneralOptionsPanel();
	private SystemOptionsPanel systemPanel = new SystemOptionsPanel();
	private TicketPreview tableTicketPreview = null;

	/**
	 * Instantiates a new options view and loads all preferences into the UI
	 */
	private OptionsView() {
		super();

		initComponents();

		appearancePanel.loadOptions();
		generalPanel.loadOptions();
		databasePanel.loadOptions();
		systemPanel.loadOptions();

		manageMySize(PrefName.OPTIONSVIEWSIZE);
	}

	/**
	 * Save all Preferences based on what's showing on the UI
	 */
	private void applyChanges() {

		appearancePanel.applyChanges();
		generalPanel.applyChanges();
		databasePanel.applyChanges();
		systemPanel.applyChanges();

		auditoriumTicketPreview.applyChanges();
		tableTicketPreview.applyChanges();

		// notify anyone listening for preference changes
		Prefs.notifyListeners();
		
		this.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.ui.ViewFrame#destroy()
	 */
	@Override
	public void destroy() {
		this.dispose();

	}

	/**
	 * Inits the components.
	 */
	private void initComponents() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("General", null, generalPanel, null); // Generated
		tabs.addTab("Appearance", null, appearancePanel, null);
		tabs.addTab("Database", null, databasePanel, null);
		tabs.addTab("System", null, systemPanel, null);

		JTabbedPane ticketTab = new JTabbedPane();

		TicketFormat f = new TicketFormat(LayoutModel.AUDITORIUM);
		f.loadDefault();
		auditoriumTicketPreview = new TicketPreview(f, true);

		ticketTab.addTab("Auditorium Seating", null, auditoriumTicketPreview,
				null);

		f = new TicketFormat(LayoutModel.TABLE);
		f.loadDefault();
		tableTicketPreview = new TicketPreview(f, true);
		ticketTab.addTab("Table Seating", null, tableTicketPreview, null);
		tabs.addTab("Default Ticket Formats", null, ticketTab, null);

		topPanel.add(tabs, GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH, 1.0, 1.0));

		JPanel applyDismissPanel = new JPanel();

		JButton applyButton = new JButton();
		applyButton.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Save16.gif")));
		applyButton.setText("Apply Changes");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				applyChanges();
			}
		});
		applyDismissPanel.add(applyButton, null);

		JButton dismissButton = new JButton();
		dismissButton.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Stop16.gif")));
		dismissButton.setText("Dismiss");
		dismissButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				destroy();
			}
		});
		setDismissButton(dismissButton);
		applyDismissPanel.add(dismissButton, null);
		topPanel.add(applyDismissPanel, GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH));
		this.setContentPane(topPanel);
		this.setTitle("Ticket Maven Options");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(629, 729);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.ui.ViewFrame#refresh()
	 */
	@Override
	public void refresh() {
		// empty
	}
}
