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

package com.mbb.TicketMaven.ui.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * This class creates a few types of Dialogs with a scrolled Text Area or a JTable. See static methods for details
 */
public class ScrolledDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/** The tbl_. */
	private JTable tbl_ = null;

	/** The error */
	private static Throwable e_;

	/**
	 * Instantiates a new scrolled dialog.
	 * 
	 * @param s the text
	 * @param stack - flag to show a stack trace button
	 */
	private ScrolledDialog(String s, boolean stack) {
		initComponents();
		jTextArea.setText(s);
		stackButton.setVisible(stack);
		setModal(true);
	}

	/**
	 * Instantiates a new scrolled dialog tha shows a table.
	 * 
	 * @param tbl the tbl
	 */
	private ScrolledDialog(JTable tbl) {
		tbl_ = tbl;
		initComponents();
		stackButton.setVisible(false);
		setModal(false);
	}

	/**
	 * Show an error that had been caught. Add a button to show the stack trace if the user preference is set to show that
	 * 
	 * @param e the error
	 * 
	 */
	public static void showError(Throwable e) {
		e_ = e;
		boolean ss = false;
		String showstack = "true"; // Prefs.getPref(PrefName.STACKTRACE);
		if (showstack.equals("true")) {
			ss = true;
		}

		new ScrolledDialog(e.toString(), ss).setVisible(true);
	}

	/**
	 * Show a table in a dialog.
	 * 
	 * @param tbl the tbl
	 */
	public static void showTable(JTable tbl) {
		new ScrolledDialog(tbl).setVisible(true);
	}

	/**
	 * Show a notice - which is just plain text and an OK button to dismiss it
	 * 
	 * @param text the text
	 * 
	 */
	public static void showNotice(String text) {
		new ScrolledDialog(text, false).setVisible(true);
	}

	/**
	 * Inits the components.
	 */
	private void initComponents()
	{
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TICKETMAVEN");
		this.setSize(165, 300);
		this.setContentPane(getJPanel());
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = jScrollPane.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));
	}

	
	private void exitForm(java.awt.event.WindowEvent evt) {
		this.dispose();
	}

	/** The j panel. */
	private JPanel jPanel = null;
	
	/** The j scroll pane. */
	private JScrollPane jScrollPane = null;
	
	/** The j text area. */
	private JTextArea jTextArea = null;
	
	/** The button panel. */
	private JPanel buttonPanel = null;
	
	/** The ok button. */
	private JButton okButton = null;
	
	/** The stack button. */
	private JButton stackButton = null;

	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0; // Generated
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH; // Generated
			gridBagConstraints1.gridy = 1; // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH; // Generated
			gridBagConstraints.gridy = 0; // Generated
			gridBagConstraints.weightx = 1.0; // Generated
			gridBagConstraints.weighty = 1.0; // Generated
			gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4); // Generated
			gridBagConstraints.gridx = 0; // Generated
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout()); // Generated
			jPanel.add(getJScrollPane(), gridBagConstraints); // Generated
			jPanel.add(getButtonPanel(), gridBagConstraints1); // Generated
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setPreferredSize(new java.awt.Dimension(600, 200)); // Generated
			if (tbl_ != null) {
				jScrollPane.setViewportView(tbl_);

			} else {
				jScrollPane.setViewportView(getJTextArea());
			}
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextArea.
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false); // Generated
			jTextArea.setLineWrap(true); // Generated
		}
		return jTextArea;
	}

	/**
	 * This method initializes buttonPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null); // Generated
			buttonPanel.add(getStackButton(), null); // Generated
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doOk();
				}
			});
		}
		return okButton;
	}

	/**
	 * Do ok.
	 */
	private void doOk() {
		this.dispose();
	}

	/**
	 * This method initializes stackButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getStackButton() {
		if (stackButton == null) {
			stackButton = new JButton();
			stackButton.setText("Show Stack Trace");
			stackButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doStack();
				}
			});
		}
		return stackButton;
	}

	/**
	 * called when user has requested to see the stack trace for an error
	 */
	private void doStack() {
		// show the stack trace
		java.io.ByteArrayOutputStream bao = new java.io.ByteArrayOutputStream();
		java.io.PrintStream ps = new java.io.PrintStream(bao);
		e_.printStackTrace(ps);
		ScrolledDialog.showNotice(bao.toString());
		
	}

	
} 