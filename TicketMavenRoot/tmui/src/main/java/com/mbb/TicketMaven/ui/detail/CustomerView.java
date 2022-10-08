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

package com.mbb.TicketMaven.ui.detail;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.LimitDocument;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Warning;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CustomerView extends ViewDetailPanel<Customer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField addressText = null;
	private JSpinner allowedSpinner = null;
	private Customer cust_;
	private JTextField emtext = null;
	private JTextField fntext = null;
	private JPanel infoPanel = null;
	private JTabbedPane jTabbedPane = null;
	private JTextField lntext = null;
	private JTextField notesText = null;
	private JTextField phtext = null;
	private JCheckBox residentBox = null;
	private JComboBox<String> specialBox = null;
	private JPanel ticketPanel = null;
	
	private JTextField assignedTicketsText = new JTextField();
	private JTextField qualityText = new JTextField();
	private JTextField avgQualityText = new JTextField();


	public CustomerView() {
		super();
		cust_ = null;
		initialize(); // init the GUI widgets
		showData(null);

	}

	/**
	 * This method initializes addressText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getAddressText() {
		if (addressText == null) {
			addressText = new JTextField();
			addressText.setColumns(50); 
			addressText.setDocument(new LimitDocument(50));

		}
		return addressText;
	}

	/**
	 * This method initializes allowedSpinner
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getAllowedSpinner() {
		if (allowedSpinner == null) {
			allowedSpinner = new JSpinner();
		}
		return allowedSpinner;
	}

	@Override
	public String getDuplicateError() {
		return "A Customer already exists with the same name. Cannot store a duplicate";
	}

	/**
	 * This method initializes emtext
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getEmtext() {
		if (emtext == null) {
			emtext = new JTextField();
			emtext.setColumns(50); 
			emtext.setDocument(new LimitDocument(50));
		}
		return emtext;
	}

	/**
	 * This method initializes fntext
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getFntext() {
		if (fntext == null) {
			fntext = new JTextField();
			fntext.setColumns(40); 
			fntext.setDocument(new LimitDocument(40));
		}
		return fntext;
	}

	/**
	 * This method initializes infoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			
			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout()); 
			infoPanel.add(new JLabel("First Name:"), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH)); 
			infoPanel.add(getFntext(), GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(new JLabel("Last Name:"), GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH)); 
			infoPanel.add(getLntext(), GridBagConstraintsFactory.create(1, 1, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(new JLabel("Phone:"), GridBagConstraintsFactory.create(0, 2, GridBagConstraints.BOTH)); 
			infoPanel.add(getPhtext(), GridBagConstraintsFactory.create(1, 2, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(new JLabel("Email:"), GridBagConstraintsFactory.create(0, 3, GridBagConstraints.BOTH)); 
			infoPanel.add(getEmtext(), GridBagConstraintsFactory.create(1, 3, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(new JLabel("Address:"), GridBagConstraintsFactory.create(0, 4, GridBagConstraints.BOTH)); 
			infoPanel.add(getAddressText(), GridBagConstraintsFactory.create(1, 4, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(new JLabel("Notes:"), GridBagConstraintsFactory.create(0, 5, GridBagConstraints.BOTH)); 
			infoPanel.add(getNotesText(), GridBagConstraintsFactory.create(1, 5, GridBagConstraints.BOTH, 1.0, 0.0)); 
			infoPanel.add(getResidentBox(), GridBagConstraintsFactory.create(0, 6, GridBagConstraints.BOTH)); 
		}
		return infoPanel;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Customer Information", null, getInfoPanel(),
					null); 
			jTabbedPane.addTab("Ticketing Information", null, getTicketPanel(),
					null); 
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes lntext
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextField getLntext() {
		if (lntext == null) {
			lntext = new JTextField();
			lntext.setColumns(40); 
			lntext.setDocument(new LimitDocument(40));
		}
		return lntext;
	}

	/**
	 * This method initializes notesText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNotesText() {
		if (notesText == null) {
			notesText = new JTextField();
			notesText.setColumns(50); 
			notesText.setDocument(new LimitDocument(100));
		}
		return notesText;
	}

	/**
	 * This method initializes phtext
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextField getPhtext() {
		if (phtext == null) {
			phtext = new JTextField();
			phtext.setColumns(20); 
			phtext.setDocument(new LimitDocument(20));
		}
		return phtext;
	}

	/**
	 * This method initializes residentBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getResidentBox() {
		if (residentBox == null) {
			residentBox = new JCheckBox();
			residentBox.setText("Resident"); 
		}
		return residentBox;
	}

	private JComboBox<String> getSpecialBox() {
		if (specialBox == null) {
			specialBox = new JComboBox<String>();

		}
		return specialBox;
	}

	/**
	 * This method initializes ticketPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTicketPanel() {
		if (ticketPanel == null) {
			
			ticketPanel = new JPanel();
			ticketPanel.setLayout(new GridBagLayout()); 
			
			ticketPanel.add(new JLabel("Maximum Allowed Tickets:"), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH));
			ticketPanel.add(getAllowedSpinner(), GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH)); 

			ticketPanel.add(new JLabel("Documented Special Needs:"), GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH)); 
			ticketPanel.add(getSpecialBox(), GridBagConstraintsFactory.create(1, 1, GridBagConstraints.BOTH)); 
			
			ticketPanel.add(new JLabel("Assigned Tickets:"), GridBagConstraintsFactory.create(0, 2, GridBagConstraints.BOTH)); 
			assignedTicketsText.setEditable(false);
			ticketPanel.add(assignedTicketsText, GridBagConstraintsFactory.create(1, 2, GridBagConstraints.BOTH)); 

			ticketPanel.add(new JLabel("Total Quality:"), GridBagConstraintsFactory.create(0, 3, GridBagConstraints.BOTH)); 
			qualityText.setEditable(false);
			ticketPanel.add(qualityText, GridBagConstraintsFactory.create(1, 3, GridBagConstraints.BOTH));
			
			ticketPanel.add(new JLabel("Average Quality:"), GridBagConstraintsFactory.create(0, 4, GridBagConstraints.BOTH)); 
			avgQualityText.setEditable(false);
			ticketPanel.add(avgQualityText, GridBagConstraintsFactory.create(1, 4, GridBagConstraints.BOTH));
			

		}
		return ticketPanel;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout()); 
		this.add(getJTabbedPane(), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH, 1.0, 1.0)); 

	}

	private void populateSpecialBox() {
		specialBox.setEnabled(true);
		specialBox.removeAllItems();
		specialBox.addItem(CustomerModel.NONE);
		specialBox.addItem(CustomerModel.FRONT);
		specialBox.addItem(CustomerModel.FRONT_ONLY);
		specialBox.addItem(CustomerModel.AISLE);
		specialBox.addItem(CustomerModel.REAR);

		Collection<Zone> zones = null;
		try {
			zones = ZoneModel.getReference().getRecords();

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}
		for (Zone l : zones) {
			specialBox.addItem(l.getName());
		}

	}

	@Override
	public void refresh() {
		// empty
	}

	@Override
	public void saveData() throws Exception, Warning {
		if (fntext.getText().equals("") || lntext.getText().equals("")) {
			throw new Warning("First and Last name are Required");
		}

		cust_.setFirstName(fntext.getText());
		cust_.setLastName(lntext.getText());
		cust_.setEmail(emtext.getText());
		cust_.setPhone(phtext.getText());
		cust_.setAddress(addressText.getText());
		cust_.setNotes(notesText.getText());
		cust_.setSpecialNeedsType((String) specialBox.getSelectedItem());
		cust_.setAllowedTickets((Integer) allowedSpinner.getValue());
		if (residentBox.isSelected())
			cust_.setResident("Y");
		else
			cust_.setResident("N");
		CustomerModel.getReference().saveRecord(cust_);

	}

	@Override
	public void showData(Customer cust) {

		populateSpecialBox();
		cust_ = cust;

		assignedTicketsText.setText("0");
		qualityText.setText("0");
		avgQualityText.setText("0");

		if (cust == null) {
			fntext.setText("");
			lntext.setText("");
			emtext.setText("");
			phtext.setText("");
			allowedSpinner.setValue(Integer.valueOf(0));
			specialBox.setSelectedItem(CustomerModel.NONE);
			residentBox.setSelected(true);
			return;
		}
		fntext.setText(cust_.getFirstName());
		lntext.setText(cust_.getLastName());
		emtext.setText(cust_.getEmail());
		phtext.setText(cust_.getPhone());
		if (cust_.getAllowedTickets() != null)
			allowedSpinner.setValue(cust_.getAllowedTickets());
		else
			allowedSpinner.setValue(Integer.valueOf(0));
		if (cust_.getSpecialNeedsType() == null
				|| cust_.getSpecialNeedsType().equals(""))
			specialBox.setSelectedItem("None");
		else
			specialBox.setSelectedItem(cust_.getSpecialNeedsType());
		if (cust_.getResident() == null || cust_.getResident().equals("Y"))
			residentBox.setSelected(true);
		else
			residentBox.setSelected(false);
		addressText.setText(cust_.getAddress());
		notesText.setText(cust_.getNotes());
		if( cust_.getTotalTickets() != null )
			assignedTicketsText.setText(cust_.getTotalTickets().toString());
		if( cust_.getTotalQuality() != null )
			qualityText.setText(cust_.getTotalQuality().toString());
		
		if( cust_.getTotalTickets() != null &&  cust_.getTotalQuality() != null )
		{
			if( cust_.getTotalTickets().intValue() != 0)
			{
				float avg = cust_.getTotalQuality().floatValue() / cust_.getTotalTickets().floatValue();
				avgQualityText.setText(Float.toString(avg));
			}
		}

	}

} 
