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

import com.mbb.TicketMaven.model.*;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.LimitDocument;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Warning;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ReservationView extends ViewDetailPanel<Reservation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton custButton = null;

	private Collection<Customer> custs_ = new ArrayList<Customer>();

	private JTextField custtext = null;

	private JSpinner numSpinner = null;

	private Reservation reservation_;

	private JButton showButton = null;

	private JTextField showtext = null;

	private SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 999, 1);

	private JButton tableButton = null;

	private JTextField tableText = null;

	private JTextField paymentText = null;

	private JTextField notesText = null;

	private JTextField amountText = null;

	public ReservationView() {
		super();
		reservation_ = null;
		initialize(); // init the GUI widgets
		showData(null);

	}

	private void customerSelect() {
		custs_.clear();
		TableSorter ts = new TableSorter(new String[] { "First", "Last",
				"Phone" }, new Class[] { java.lang.String.class,
				java.lang.String.class, java.lang.String.class });
		ts.sortByColumn(1);
		Collection<Customer> c = BeanSelector.selectBeans(
				CustomerModel.getReference(), ts, new String[] { "FirstName",
						"LastName", "Phone" }, null);
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

	@Override
	public String getDuplicateError() {
		return "A Reservation already exists with the same customer, show and table.\nCannot store a duplicate. Please edit the existing reservation.";
	}

	/**
	 * This method initializes numSpinner
	 * 
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getNumSpinner() {
		if (numSpinner == null) {
			numSpinner = new JSpinner();
			numSpinner.setModel(spinModel);
		}
		return numSpinner;
	}

	/**
	 * This method initializes seatButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSeatButton() {
		if (tableButton == null) {
			tableButton = new JButton();
			tableButton.setText("Table:");
			tableButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					tableSelect();
				}
			});
		}
		return tableButton;
	}

	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Show:");
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
	 * This method initializes seattext
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTableText() {
		if (tableText == null) {
			tableText = new JTextField(20);
			tableText.setEditable(false);
		}
		return tableText;
	}

	private JTextField getPaymentText() {
		if (paymentText == null) {
			paymentText = new JTextField(50);
			paymentText.setDocument(new LimitDocument(50));
			paymentText.setMinimumSize(new Dimension(400, 0));

		}
		return paymentText;
	}

	private JTextField getNotesText() {
		if (notesText == null) {
			notesText = new JTextField(400);
			notesText.setDocument(new LimitDocument(400));
		}
		return notesText;
	}

	private JTextField getAmountText() {
		if (amountText == null) {
			amountText = new JTextField(10);
			amountText.setDocument(new LimitDocument(10));
			amountText.setMinimumSize(new Dimension(100, 0));

		}
		return amountText;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());

		this.add(getCustButton(),
				GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH));
		this.add(getCusttext(), GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));

		this.add(getShowButton(),
				GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
		this.add(getShowText(), GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));

		this.add(getSeatButton(),
				GridBagConstraintsFactory.create(0, 2, GridBagConstraints.BOTH));
		this.add(getTableText(), GridBagConstraintsFactory.create(1, 2,
				GridBagConstraints.BOTH, 1.0, 0.0));

		JPanel pp = new JPanel();
		pp.setLayout(new GridBagLayout());

		GridBagConstraints pgbc = GridBagConstraintsFactory.create(-1, 0,
				GridBagConstraints.NONE, 0.0, 0.0);
		GridBagConstraints pgbc2 = GridBagConstraintsFactory.create(-1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0);
		pgbc.anchor = GridBagConstraints.WEST;

		pp.add(new JLabel("Number Of Seats:"), pgbc);
		pp.add(getNumSpinner(), pgbc);

		pp.add(new JLabel("Payment:"), pgbc);
		pp.add(getPaymentText(), pgbc2);

		pp.add(new JLabel("Amount:"), pgbc);
		pp.add(getAmountText(), pgbc2);

		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 3,
				GridBagConstraints.NONE, 1.0, 0.0);
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		this.add(pp, gbc);

		this.add(new JLabel("Notes:"),
				GridBagConstraintsFactory.create(0, 4, GridBagConstraints.BOTH));
		this.add(getNotesText(), GridBagConstraintsFactory.create(1, 4,
				GridBagConstraints.BOTH, 1.0, 0.0));

	}

	@Override
	public void refresh() {
		// empty
	}

	@Override
	public void saveData() throws Exception, Warning {
		if (custtext.getText().equals("") || showtext.getText().equals("")
				|| tableText.getText().equals("")) {
			throw new Warning("Customer, Show, and Table are Required");
		}

		Table tbl = TableModel.getReference().getTable(
				reservation_.getTableId().intValue());
		Collection<Reservation> res = ReservationModel.getReference()
				.getReservationsForTableInShow(
						reservation_.getShowId().intValue(),
						reservation_.getTableId().intValue());
		int seats_taken = 0;
		Iterator<Reservation> it = res.iterator();
		while (it.hasNext()) {
			Reservation r = it.next();
			seats_taken += r.getNum().intValue();
		}

		if (!reservation_.isNew()) {
			int desired_seats = ((Integer) getNumSpinner().getValue())
					.intValue();
			if (tbl.getSeats().intValue() - seats_taken
					+ reservation_.getNum().intValue() < desired_seats) {
				throw new Warning("Cannot increase this reservation to "
						+ desired_seats + " seats as there are only "
						+ (tbl.getSeats().intValue() - seats_taken)
						+ " seats available at the selected table");

			}
			reservation_.setPayment(getPaymentText().getText());
			reservation_.setNotes(getNotesText().getText());
			reservation_.setAmount(getAmountText().getText());
			reservation_.setNum((Integer) getNumSpinner().getValue());
			ReservationModel.getReference().saveRecord(reservation_);

			return;
		}

		if (custs_.size() == 0) {
			if ( reservation_.getCustomerId() == null)
				throw new Warning("Customer is Required");
			Customer c = CustomerModel.getReference().getCustomer(
					reservation_.getCustomerId().intValue());
			custs_.add(c);
		}

		int desired_seats = ((Integer) getNumSpinner().getValue()).intValue()
				* custs_.size();

		if (tbl.getSeats().intValue() - seats_taken < desired_seats) {
			throw new Warning("Cannot reserve " + desired_seats
					+ " seats as there are only "
					+ (tbl.getSeats().intValue() - seats_taken)
					+ " seats available at the selected table");

		}

		// make sure we don't refresh during the updates
		// this would cause a showData(null) call and a concurrent
		// update error on custs_
		ReservationModel.getReference().setNotifyListeners(false);
		for (Customer c : custs_) {
			Reservation r = ReservationModel.getReference().newReservation();
			r.setShowId(reservation_.getShowId());
			r.setCustomerId(Integer.valueOf(c.getKey()));
			r.setNum((Integer) getNumSpinner().getValue());
			r.setTableId(reservation_.getTableId());
			r.setPayment(getPaymentText().getText());
			r.setAmount(getAmountText().getText());
			r.setNotes(getNotesText().getText());
			try {
				ReservationModel.getReference().saveRecord(r);
			} catch (SQLException se) {
				if (se.getSQLState().equalsIgnoreCase("23000")) {
					Errmsg.getErrorHandler().notice(
							getDuplicateError() + "\nCustomer is "
									+ c.getLastName() + ","
									+ c.getFirstName());
				} else {
					ReservationModel.getReference().setNotifyListeners(true);
					ReservationModel.getReference().refresh();
					throw se;
				}
			}

		}
		ReservationModel.getReference().setNotifyListeners(true);
		ReservationModel.getReference().refresh();

	}

	@Override
	public void showData(Reservation s) {
		reservation_ = s;
		if (s == null)
			return;
		tableButton.setEnabled(true);

		if (s.isNew()) {
			custButton.setEnabled(true);
			showButton.setEnabled(true);
		} else {
			custButton.setEnabled(false);
			showButton.setEnabled(false);

			try {
				Show sh = ShowModel.getReference().getShow(s.getShowId());
				Collection<Table> tbls = TableModel.getReference()
						.getTablesForLayout(sh.getLayout());
				if (tbls.size() == 1) {
					tableButton.setEnabled(false);
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}

		}
		custtext.setText(reservation_.getCustomerName());
		custs_.clear();

		if (reservation_.getShowName() != null)
			showtext.setText(reservation_.getShowName() + " -- "
					+ sdf.format(reservation_.getShowDate()));
		else
			showtext.setText("");
		getTableText().setText(reservation_.getTableName());
		if (reservation_.getNum() == null)
			getNumSpinner().setValue(Integer.valueOf(1));
		else
			getNumSpinner().setValue(reservation_.getNum());

		getPaymentText().setText(reservation_.getPayment());
		getNotesText().setText(reservation_.getNotes());
		getAmountText().setText(reservation_.getAmount());

	}

	private void showSelect() {

		tableText.setText("");
		tableButton.setEnabled(true);

		Show s = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class,
								java.util.Date.class }), new String[] { "Name",
						"DateTime" }, new ShowFilter(LayoutModel.TABLE, true));
		if (s == null) {
			reservation_.setShowId(null);
			showtext.setText("");

		} else {
			reservation_.setShowId(Integer.valueOf(s.getKey()));
			showtext.setText(s.getName() + " -- " + sdf.format(s.getDateTime()));
			try {
				// if only one table, no need to prompt
				Collection<Table> tbls = TableModel.getReference()
						.getTablesForLayout(s.getLayout());
				if (tbls.size() == 1) {
					Table t = (Table) tbls.toArray()[0];
					tableText.setText(t.getLabel());
					reservation_.setTableId(t.getKey());
					tableButton.setEnabled(false);
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
	}

	private void tableSelect() {
		if (reservation_.getShowId() == null) {
			Errmsg.getErrorHandler().notice("Please select a show first");
			return;
		}
		int layout = 0;
		try {
			Show s = ShowModel.getReference().getShow(
					reservation_.getShowId().intValue());
			layout = s.getLayout().intValue();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}
		Table tbl = BeanSelector.selectBean(TableModel.getReference(),
				new TableSorter(new String[] { "Table" },
						new Class[] { java.lang.String.class, }),
				new String[] { "Label" },
				new com.mbb.TicketMaven.model.filter.TableFilter(layout));
		if (tbl == null) {
			reservation_.setTableId(null);
			tableText.setText("");
		} else {
			reservation_.setTableId(Integer.valueOf(tbl.getKey()));
			tableText.setText(tbl.getLabel());
		}
	}

}
