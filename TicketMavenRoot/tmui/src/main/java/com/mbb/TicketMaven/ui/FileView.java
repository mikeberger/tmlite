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
/*
This file is part of BORG.

    BORG is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BORG is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BORG; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Copyright 2003 by Mike Berger
 */
/*
 * helpscrn.java
 *
 * Created on October 5, 2003, 8:55 AM
 */

package com.mbb.TicketMaven.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;

/**
 * dockable view that shows information from a file
 */
class FileView extends ViewFrame {

	private static final long serialVersionUID = 1L;
	private JEditorPane jEditorPane;
	private JScrollPane jScrollPane1;
	private JPanel panel;

	static private final PrefName FILEVIEWSIZE = new PrefName("fileviewsize",
			"-1,-1,-1,-1,N");

	private String file_;

	FileView(String file) {
		file_ = file;
		initComponents();
		try {
			jEditorPane.setPage("file:///" + file_);
		} catch (java.io.IOException e1) {
			Errmsg.getErrorHandler().errmsg(e1);
		}

		manageMySize(FILEVIEWSIZE);
	}

	/**
	 * initialize the UI
	 */
	private void initComponents() {
		panel = new JPanel();

		panel.setLayout(new GridBagLayout());
		jScrollPane1 = new javax.swing.JScrollPane();
		jEditorPane = new javax.swing.JEditorPane();
		jEditorPane.setEditable(false);
		jScrollPane1.setViewportView(jEditorPane);
		panel.add(jScrollPane1, GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH, 1.0, 1.0));

		JPanel bPanel = new JPanel();
		JButton reloadButton = new JButton("Reload");
		reloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Document doc = jEditorPane.getDocument();
				doc.putProperty(Document.StreamDescriptionProperty, null);
				try {
					jEditorPane.setPage("file:///" + file_);
				} catch (IOException e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}

		});
		bPanel.add(reloadButton);
		panel.add(bPanel, GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));

		jEditorPane.setPreferredSize(new java.awt.Dimension(700, 500));
		setTitle("TicketMaven");
		this.setSize(165, 65);
		this.setContentPane(panel);

		pack();
	}

	public void print() {
		try {
			jEditorPane.print();
		} catch (PrinterException e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	@Override
	public void refresh() {
		// ignore

	}

	@Override
	public void destroy() {
		this.dispose();
	}

}
