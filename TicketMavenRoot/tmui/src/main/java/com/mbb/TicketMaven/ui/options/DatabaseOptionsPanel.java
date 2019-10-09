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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.mbb.TicketMaven.model.Model;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.ui.TicketMaven;
import com.mbb.TicketMaven.ui.options.OptionsView.OptionsPanel;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.ScrolledDialog;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.FileIO;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * provides the UI for the Database options tab
 *
 */
public class DatabaseOptionsPanel extends OptionsPanel {

	private static final long serialVersionUID = 1L;

	private JTextField dbfolder = new JTextField();

	/**
	 * Instantiates a new database options panel.
	 */
	public DatabaseOptionsPanel() {

		this.setLayout(new GridBagLayout());

		JTextArea dbnotice = new JTextArea(
				"\n\n\n*** IMPORTANT ***\n\n\nYou can change the folder in which the program stores its database using the button below.\n\nNo data is copied from the existing database to the new one.");
		dbnotice.setEditable(false);
		GridBagConstraints gbc = GridBagConstraintsFactory
				.create(0, 0, GridBagConstraints.BOTH, 1.0, 1.0);
		gbc.gridwidth = 2;
		this.add(dbnotice, gbc);

		JButton but = new JButton("Database Folder:");
		this.add(but, GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH));
		this.add(dbfolder, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));
		dbfolder.setEditable(false);
		but.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDBfolder();
			}
		});

	}

	@Override
	public void applyChanges() {
		String dbdir = Prefs.getPref(PrefName.DBDIR);
		Prefs.putPref(PrefName.DBDIR, dbfolder.getText());

		if( !dbdir.equals(dbfolder.getText()))
		{
			ScrolledDialog.showNotice("Changing Database folder...");
			try {
				JdbcDB.cleanup();
				JdbcDB.setDbUrl(JdbcDB.getDbUrl());
				TicketMaven.dbConnect();
				Model.syncModels();

			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

	}

	/**
	 * prompt for new db folder and set text field
	 *
	 */
	private void chooseDBfolder() {

		String folder = null;

		while (true) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Please choose your database folder");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			folder = chooser.getSelectedFile().getAbsolutePath();
			File lf = new File(folder);
			String err = null;
			if (!lf.exists()) {
				err = "Folder " + folder + " does not exist";
			} else if (!FileIO.canWriteDir(lf)) {
				err = "Cannot write in folder " + folder;
			}

			if (err == null)
				break;

			Errmsg.getErrorHandler().notice(err);
		}

		dbfolder.setText(folder);
	}

	@Override
	public void loadOptions() {
		dbfolder.setText(Prefs.getPref(PrefName.DBDIR));
	}

}
