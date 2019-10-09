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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.PackageModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketRequestModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.TMPackage;
import com.mbb.TicketMaven.model.entity.TicketRequest;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Money;
import com.mbb.TicketMaven.util.Warning;

public class RequestView extends ViewDetailPanel<TicketRequest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton custButton = null;

	private Collection<Customer> custs_ = new ArrayList<Customer>();

	private JTextField custtext = null;

	private JTextField discountField = null;

	private TMPackage package_ = null;

	private JButton packageButton = null;

	private JTextField packageText = null;

	private JTextField priceField = null;

	private TicketRequest request_; // @jve:decl-index=0:

	private JButton showButton = null;

	private Collection<Show> shows_ = new ArrayList<Show>(); // @jve:decl-index=0:

	private JTextField showtext = null;

	private JSpinner ticketSpinner = new JSpinner();

	public RequestView() {
		super();
		request_ = null;
		initialize(); // init the GUI widgets
		showData(null);
	}

	

	private void customerSelect() {
		custs_.clear();
		TableSorter ts = new TableSorter(new String[] { "First", "Last",
				"Phone" }, new Class[] { java.lang.String.class,
				java.lang.String.class, java.lang.String.class });
		ts.sortByColumn(1);
		Collection<Customer> c = BeanSelector.selectBeans(CustomerModel
				.getReference(), ts, new String[] { "FirstName", "LastName",
				"Phone" }, null);
		if (c == null) {
			custtext.setText("");
		} else {
			String t = "";
			custs_.addAll(c);
			for (Customer cust : c) {
				if (!t.equals(""))
					t += ";";
				t += cust.getLastName() + "," + cust.getFirstName();
			}
			custtext.setText(t);
		}
	}

	/**
	 * This method initializes custButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCustButton() {
		if (custButton == null) {
			custButton = new JButton();
			custButton.setText("Customer(s):"); 
			custButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					customerSelect();
				}
			});
		}
		return custButton;
	}

	private JTextField getCusttext() {
		if (custtext == null) {
			custtext = new JTextField(80);
			custtext.setEditable(false); 
		}
		return custtext;
	}

	/**
	 * This method initializes discountField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getDiscountField() {
		if (discountField == null) {
			discountField = new JTextField();
			discountField.setColumns(5);
			discountField.setEditable(false);
			discountField.setText(" 00%");
		}
		return discountField;
	}

	@Override
	public String getDuplicateError() {
		return "A Request already exists with the same show and customer. Cannot store a duplicate";
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPackageButton() {
		if (packageButton == null) {
			packageButton = new JButton();
			packageButton.setText("Package:");
			packageButton
					.addActionListener(new java.awt.event.ActionListener() {
						@Override
						public void actionPerformed(java.awt.event.ActionEvent e) {
							selectPackage();
						}
					});
		}
		return packageButton;
	}


	/**
	 * This method initializes packageText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPackageText() {
		if (packageText == null) {
			packageText = new JTextField();
			packageText.setEditable(false);
		}
		return packageText;
	}

	/**
	 * This method initializes priceField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPriceField() {
		if (priceField == null) {
			priceField = new JTextField();
			priceField.setColumns(6);
			priceField.setEditable(false);
		}
		return priceField;
	}

	/**
	 * This method initializes showButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Show(s):"); 
			showButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showSelect();
				}
			});
		}
		return showButton;
	}

	private JTextField getShowText() {
		if (showtext == null) {
			showtext = new JTextField(100);
			showtext.setEditable(false);
		}
		return showtext;
	}

	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		
		this.setLayout(new GridBagLayout()); 
		
		this.add(getCustButton(), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH)); 
		this.add(getCusttext(), GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH, 1.0, 0.0)); 
		this.add(new JLabel("Number of Tickets:"), GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
		this.add(ticketSpinner,  GridBagConstraintsFactory.create(1, 1, GridBagConstraints.VERTICAL));

		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 2, GridBagConstraints.BOTH);
		gbc.gridwidth = 2;
		this.add(new JLabel("Select either Shows or a Discount Package"), gbc);

		this.add(getShowButton(), GridBagConstraintsFactory.create(0, 3, GridBagConstraints.BOTH));
		this.add(getShowText(), GridBagConstraintsFactory.create(1, 3, GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(getPackageButton(), GridBagConstraintsFactory.create(0, 4, GridBagConstraints.BOTH));
		this.add(getPackageText(), GridBagConstraintsFactory.create(1, 4, GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(new JLabel("Package Discount:"), GridBagConstraintsFactory.create(0, 5, GridBagConstraints.BOTH));
		this.add(getDiscountField(), GridBagConstraintsFactory.create(1, 5, GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(new JLabel("Price:"), GridBagConstraintsFactory.create(0, 6, GridBagConstraints.BOTH));
		this.add(getPriceField(), GridBagConstraintsFactory.create(1, 6, GridBagConstraints.BOTH, 1.0, 0.0));
	}

	@Override
	public void refresh() {
		//empty
	}

	@Override
	public void saveData() throws Exception, Warning {

		if (((Integer) ticketSpinner.getValue()).intValue() == 0) {
			throw new Warning("Please select the number of Tickets");

		}

		// check if we are editing an existing request
		if (request_ != null && !request_.isNew()) {
			if (request_.getCustomerId() == null)
				throw new Warning("Customer and Show are Required");
			Customer c = CustomerModel.getReference().getCustomer(
					request_.getCustomerId().intValue());
			if (((Integer) ticketSpinner.getValue()).intValue() > c
					.getAllowedTickets().intValue())
				throw new Warning("This customer can only request "
						+ c.getAllowedTickets().intValue() + " tickets");
			request_.setTickets((Integer) ticketSpinner.getValue());
			TicketRequestModel.getReference().saveRecord(request_);
			return;
		}

		if (custs_.size() == 0) {
			if (request_ == null || request_.getCustomerId() == null)
				throw new Warning("Customer is Required");
			Customer c = CustomerModel.getReference().getCustomer(
					request_.getCustomerId().intValue());
			if (((Integer) ticketSpinner.getValue()).intValue() > c
					.getAllowedTickets().intValue())
				throw new Warning("This customer can only request "
						+ c.getAllowedTickets().intValue() + " tickets");
			custs_.add(c);
		}

		if (shows_.size() == 0 && package_ == null) {
			if (request_ == null || request_.getShowId() == null)
				throw new Warning("Show or Package is Required");
			Show s = ShowModel.getReference().getShow(
					request_.getShowId().intValue());
			shows_.add(s);
		}

		double discount = 0;
		if (package_ != null) {
			Collection<Integer> pshows = package_.getShows();
			for (Integer shid : pshows) {
				Show s = ShowModel.getReference().getShow(shid.intValue());
				shows_.add(s);
			}
			discount = PackageModel.calcDiscount(package_);
		}

		TicketRequestModel.getReference().setNotifyListeners(false);

		for (Customer cust : custs_) {
			for (Show s : shows_) {

				if (((Integer) ticketSpinner.getValue()).intValue() > cust
						.getAllowedTickets().intValue())
				{
					Errmsg.getErrorHandler().notice("Customer " + cust.getFirstName() + " "
							+ cust.getLastName() + " can only request "
							+ cust.getAllowedTickets() + " tickets.");
					continue;
				}

				TicketRequest tr = TicketRequestModel.getReference()
						.newTicketRequest();
				tr.setCustomerId(Integer.valueOf(cust.getKey()));
				tr.setShowId(Integer.valueOf(s.getKey()));
				tr.setTickets((Integer) ticketSpinner.getValue());
				tr.setDiscount(Double.valueOf(discount));

				try {
					TicketRequestModel.getReference().saveRecord(tr);
				} catch (SQLException se) {
					if (se.getSQLState().equalsIgnoreCase("23000")) {
						Errmsg.getErrorHandler().notice(getDuplicateError() + "\nCustomer is "
								+ cust.getLastName() + ","
								+ cust.getFirstName());
					} else {
						TicketRequestModel.getReference().setNotifyListeners(
								true);
						throw se;
					}
				}
			}
		}

		request_.setTickets((Integer) ticketSpinner.getValue());
		TicketRequestModel.getReference().setNotifyListeners(true);
		TicketRequestModel.getReference().refresh();

	}

	private void selectPackage() {

		package_ = BeanSelector.selectBean(PackageModel.getReference(),
				new TableSorter(new String[] { "Package" },
						new Class[] { java.lang.String.class, }),
				new String[] { "Name" }, null);
		if (package_ == null) {
			packageText.setText("");
			showButton.setEnabled(true);
			discountField.setText("--");
		} else {
			packageText.setText(package_.getName());
			showButton.setEnabled(false);
			double d = PackageModel.calcDiscount(package_);
			discountField.setText(Math.round(d) + "%");
		}

	}

	@Override
	public void showData(TicketRequest s) {
		request_ = s;

		if (s == null || s.isNew()) {
			custButton.setEnabled(true);
			showButton.setEnabled(true);
			packageButton.setEnabled(true);
			priceField.setText("--");
		} else {
			custButton.setEnabled(false);
			showButton.setEnabled(false);
			packageButton.setEnabled(false);
			priceField.setText(Money.format(TicketRequestModel
					.requestPrice(request_)));
		}

		if (s == null)
			return;
		custtext.setText(request_.getCustomerName());
		custs_.clear();
		shows_.clear();

		if (request_.getShowDate() != null)
			showtext.setText(request_.getShowName() + " -- "
					+ sdf.format(request_.getShowDate()));
		else
			showtext.setText("");

		if (request_.getTickets() != null)
			ticketSpinner.setValue(request_.getTickets());
		else
			ticketSpinner.setValue(Integer.valueOf(0));

		// never have a package when first editing a request - new or old
		packageText.setText("");
		package_ = null;

		if (request_.getDiscount() == null
				|| request_.getDiscount().doubleValue() == 0)
			discountField.setText("--");
		else
			discountField.setText(Math.round(request_.getDiscount()
					.doubleValue())
					+ "%");

	}

	private void showSelect() {
		shows_.clear();
		TableSorter ts = new TableSorter(new String[] { "Show Name",
				"Show Date/Time" }, new Class[] { java.lang.String.class,
				java.util.Date.class });
		ts.sortByColumn(0);
		Collection<Show> c = BeanSelector.selectBeans(ShowModel.getReference(),
				ts, new String[] { "Name", "DateTime" },
				new com.mbb.TicketMaven.model.filter.ShowFilter(
						LayoutModel.AUDITORIUM, true));
		if (c == null || c.isEmpty()) {
			showtext.setText("");
			packageButton.setEnabled(true);
		} else {
			String t = "";
			Iterator<Show> it = c.iterator();
			shows_.addAll(c);
			while (it.hasNext()) {
				Show s = it.next();
				if (!t.equals(""))
					t += ";";
				t += s.getName() + "," + s.getDateTime();
			}
			showtext.setText(t);
			packageButton.setEnabled(false);
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
