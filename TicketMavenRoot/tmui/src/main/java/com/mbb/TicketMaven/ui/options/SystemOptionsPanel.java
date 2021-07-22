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
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mbb.TicketMaven.ui.options.OptionsView.OptionsPanel;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * provides the UI for the system options tab
 *
 */
class SystemOptionsPanel extends OptionsPanel {
	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	private static final long serialVersionUID = 1L;
	private JTextField socketText = new JTextField();
	private JTextField backupDir = new JTextField();
	private JCheckBox debug = new JCheckBox("Debug Logging");

	public SystemOptionsPanel() {
		this.setLayout(new GridBagLayout());
		this.add(new JLabel("Socket Port:"), GridBagConstraintsFactory.create(
				0, 0, GridBagConstraints.BOTH));
		this.add(socketText, GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));

		JButton but = new JButton("Backup Folder:");
		this.add(but, GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH));
		this.add(backupDir, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));

		but.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backupDirActionPerformed();
			}
		});
		
		this.add(debug, GridBagConstraintsFactory.create(0, 2,
				GridBagConstraints.BOTH) );
	}

	@Override
	public void applyChanges() {
		try {
			Prefs.putPref(PrefName.SOCKETPORT,
					Integer.valueOf(socketText.getText()));
		} catch (Exception e) {
			Errmsg.getErrorHandler().notice("Socket Port must be an Integer");
			return;
		}
		Prefs.putPref(PrefName.BACKUPDIR, backupDir.getText());

		OptionsPanel.setBooleanPref(debug, PrefName.DEBUG);
		boolean debugpref = Prefs.getBoolPref(PrefName.DEBUG);
		if (debugpref == true)
			log.setLevel(Level.ALL);
		else
			log.setLevel(Level.INFO);
	}

	@Override
	public void loadOptions() {
		socketText.setText(Integer.toString(Prefs
				.getIntPref(PrefName.SOCKETPORT)));

		backupDir.setText(Prefs.getPref(PrefName.BACKUPDIR));
		OptionsPanel.setCheckBox(debug, PrefName.DEBUG);
	}

	/**
	 * Backup dir action performed.
	 */
	private void backupDirActionPerformed() {

		String dbdir = chooseBackupDir();
		if (dbdir == null) {
			return;
		}

		backupDir.setText(dbdir);

	}

	/**
	 * prompt the user to select a folder for backup files
	 * 
	 * @return the chosen folder
	 */
	private static String chooseBackupDir() {

		String dirname = null;
		while (true) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Please choose folder for backup files");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				return (null);
			}

			dirname = chooser.getSelectedFile().getAbsolutePath();
			File dir = new File(dirname);
			String err = null;
			if (!dir.exists()) {
				err = "[" + dirname + "] does not exist";
			} else if (!dir.isDirectory()) {
				err = "[" + dirname + "] is not a directory";
			}

			if (err == null) {
				break;
			}

			Errmsg.getErrorHandler().notice(err);
		}

		return (dirname);
	}

}
