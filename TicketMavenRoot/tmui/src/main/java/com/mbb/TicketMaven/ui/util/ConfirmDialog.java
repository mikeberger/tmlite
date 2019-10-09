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
import javax.swing.JTextArea;



/**
 * Prompts the user to confirm something with a dialog that has OK and Cancel buttons
 */
public class ConfirmDialog extends JDialog {
    
	private static final long serialVersionUID = 1L;

	/** The result_. */
	private static int result_;
	
	/** The Constant OK. */
	public final static int OK = 0;
	
	/** The Constant CANCEL. */
	public final static int CANCEL = 1;
	

    /**
     * Instantiates a new confirm dialog.
     * 
     * @param s the dialog text
     */
    private ConfirmDialog(String s) {
        initComponents();    
        jTextArea.setText(s);
        setModal(true);       
    }
 
    /**
     * Show a confirm notice in a dialog and return the user's choice of Ok or CANCEL.
     * 
     * @param text the text
     * 
     * @return the int
     */
    public static int showNotice(String text)
    {
    	result_ = 0;
    	new ConfirmDialog(text).setVisible(true);
    	return result_;
    }
    
    /**
     * Inits the components. still contains visual editor generated code - ugh - clean up if ever
     * in here in the future
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TICKETMAVEN");
        this.setSize(165, 300);
        this.setContentPane(getJPanel());
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
			public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        pack();
        
        Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
          Dimension labelSize = jScrollPane.getPreferredSize();
          setLocation(screenSize.width/2 - (labelSize.width/2),
                      screenSize.height/2 - (labelSize.height/2));
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
	
	/** The cancel button. */
	private JButton cancelButton = null;
	
	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;  // Generated
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;  // Generated
			gridBagConstraints1.gridy = 1;  // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;  // Generated
			gridBagConstraints.gridy = 0;  // Generated
			gridBagConstraints.weightx = 1.0;  // Generated
			gridBagConstraints.weighty = 1.0;  // Generated
			gridBagConstraints.insets = new java.awt.Insets(4,4,4,4);  // Generated
			gridBagConstraints.gridx = 0;  // Generated
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());  // Generated
			jPanel.add(getJScrollPane(), gridBagConstraints);  // Generated
			jPanel.add(getButtonPanel(), gridBagConstraints1);  // Generated
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
			jScrollPane.setPreferredSize(new java.awt.Dimension(600,200));  // Generated
			jScrollPane.setViewportView(getJTextArea());  // Generated
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
			jTextArea.setEditable(false);  // Generated
			jTextArea.setLineWrap(true);  // Generated
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
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
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
	private void doOk()
	{
		result_ = OK;
		this.dispose();
	}

	/**
	 * This method initializes stackButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					doCancel();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * Do cancel.
	 */
	private void doCancel()
	{
		result_ = CANCEL;
		this.dispose();
	}

}