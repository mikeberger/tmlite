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

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for letting the user select an Integer value via a Spinner widget
 */
public class IntSelector extends JDialog {
	
	private static final long serialVersionUID = 1L;

	/** The selection_. */
	private static Integer selection_ = null;
	
	/**
	 * This method initializes this.
	 */
	private void initialize() {
        this.setContentPane(getJPanel());  // Generated
			
	}

	/**
	 * creates a new modal IntSelector and returns the selected integer
	 * 
	 * @param owner the owner Frame
	 * @param text the text to show the user
	 * 
	 * @return the integer
	 */
	public static Integer selectInt(Frame owner, String text)
	{
		selection_ = null;
		new IntSelector(owner, text).setVisible(true);
		return selection_;
	}
	
	/**
	 * Instantiates a new int selector.
	 * 
	 * @param owner the owner
	 * @param text the text
	 */
	private IntSelector( Frame owner, String text) {

		super(owner);
		initialize();
		setModal(true);

		this.setTitle("Enter Number");
		jTextField.setText(text);
		spinner.setValue(Integer.valueOf(1));
		this.setLocationRelativeTo(null);
		pack();


	}

	/**
	 * Okbutton action performed.
	 */
	private void okbuttonActionPerformed() {
		// figure out which row is selected.

		Integer i = (Integer)spinner.getValue();
		selection_ = i;
		this.dispose();
	}
	
	/**
	 * Cancel button action performed.
	 */
	private void cancelButtonActionPerformed() {
		selection_ = null;
		this.dispose();
	}


	/** The j panel. */
	private JPanel jPanel = null;
	
	/** The j text field. */
	private JTextField jTextField = null;
	
	/** The spinner. */
	private JSpinner spinner = null;
	
	/** The ok button. */
	private JButton okButton = null;
	
	/** The cancel button. */
	private JButton cancelButton = null;
	
	/** The j panel1. */
	private JPanel jPanel1 = null;
	
	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;  // Generated
			gridBagConstraints11.gridwidth = 3;  // Generated
			gridBagConstraints11.gridy = 3;  // Generated
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;  // Generated
			gridBagConstraints1.gridwidth = 2;  // Generated
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;  // Generated
			gridBagConstraints1.insets = new java.awt.Insets(4,4,4,4);  // Generated
			gridBagConstraints1.gridy = 0;  // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;  // Generated
			gridBagConstraints.gridwidth = 1;  // Generated
			gridBagConstraints.insets = new java.awt.Insets(4,4,4,4);  // Generated
			gridBagConstraints.weightx = 1.0;  // Generated
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());  // Generated
			jPanel.add(getJTextField(), gridBagConstraints);  // Generated
			jPanel.add(getSpinner(), gridBagConstraints1);  // Generated
			jPanel.add(getJPanel1(), gridBagConstraints11);  // Generated
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField.
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setEditable(false);  // Generated
		}
		return jTextField;
	}

	/**
	 * This method initializes spinner.
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner();
		}
		return spinner;
	}

	/**
	 * This method initializes okButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");  // Generated
			okButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					okbuttonActionPerformed(); 
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");  // Generated
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancelButtonActionPerformed();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes jPanel1.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getOkButton(), null);  // Generated
			jPanel1.add(getCancelButton(), null);  // Generated
		}
		return jPanel1;
	}

}
