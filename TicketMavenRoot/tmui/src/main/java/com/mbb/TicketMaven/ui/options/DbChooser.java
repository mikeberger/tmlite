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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.FileIO;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * provides the UI for the Database options tab
 *
 */
public class DbChooser extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField dbfolder = new JTextField();

	static public void create() {
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setTitle("Database Selection");
		// dialog.setSize(165, 300);
		dialog.setModal(true);
		dialog.add(new DbChooser(dialog));
		dialog.setResizable(true);
		dialog.pack();

		// place the dialog mid-screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2, screenSize.height / 2);

		dialog.setVisible(true);
	}

	/**
	 * Instantiates a new database options panel.
	 */
	public DbChooser(final JDialog dialog) {

		this.setLayout(new GridBagLayout());

		dbfolder.setText(Prefs.getPref(PrefName.DBDIR));

		JLabel dbnotice = new JLabel("Please select your database folder:");
		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH, 1.0, 1.0);
		gbc.gridwidth = 2;
		this.add(dbnotice, gbc);

		JButton but = new JButton("Database Folder:");
		this.add(but,
				GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
		this.add(dbfolder, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));
		dbfolder.setEditable(false);
		but.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseDBfolder();
				applyChanges();
			}
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}

		});
		gbc = GridBagConstraintsFactory.create(0, 2);
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(okButton, gbc);

	}

	public void applyChanges() {
		// String dbdir = Prefs.getPref(PrefName.DBDIR);
		Prefs.putPref(PrefName.DBDIR, dbfolder.getText());

		// if( !dbdir.equals(dbfolder.getText()))
		// {
		// ScrolledDialog.showNotice("Changing Database folder...");
		// try {
		// JdbcDB.cleanup();
		// JdbcDB.setDbUrl(JdbcDB.getDbUrl());
		// TicketMaven.dbConnect();
		// Model.syncModels();
		//
		// } catch (Exception e) {
		// Errmsg.getErrorHandler().errmsg(e);
		// }
		// }

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

}
