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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.seatgrid.SeatGridPanel;
import com.mbb.TicketMaven.ui.seatgrid.SeatSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Money;
import com.mbb.TicketMaven.util.Warning;

public class TicketView extends ViewDetailPanel<Ticket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton custButton = null;

	private JTextField custtext = null;
	private JTextField priceText = null;
	private JButton seatButton = null;

	private JTextField seattext = null;

	private List<Seat> selectedSeats = new ArrayList<Seat>();

	private JButton showButton = null;

	private JTextField showtext = null;

	private Ticket ticket_;

	public TicketView() {
		super();
		ticket_ = null;
		initialize(); // init the GUI widgets
		showData(null);

	}

	private void customerSelect() {
		TableSorter ts = new TableSorter(new String[] { "First", "Last",
				"Phone" }, new Class[] { java.lang.String.class,
				java.lang.String.class, java.lang.String.class });
		ts.sortByColumn(1);
		Customer c = BeanSelector.selectBean(CustomerModel.getReference(), ts,
				new String[] { "FirstName", "LastName", "Phone" }, null);
		if (c == null) {
			ticket_.setCustomerId(null);
			custtext.setText("");
		} else {
			ticket_.setCustomerId(Integer.valueOf(c.getKey()));
			custtext.setText(c.getFirstName() + " " + c.getLastName());
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
			custButton.setText("Customer:");
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
		return "A Ticket already exists with the same show and seat.\nCannot store a duplicate. The seat is taken.";
	}

	/**
	 * This method initializes discountText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPriceText() {
		if (priceText == null) {
			priceText = new JTextField();
			priceText.setColumns(7);
			priceText.setEditable(true);
		}
		return priceText;
	}

	/**
	 * This method initializes seatButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSeatButton() {
		if (seatButton == null) {
			seatButton = new JButton();
			seatButton.setText("Seat(s):");
			seatButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					seatSelect();
				}
			});
		}
		return seatButton;
	}

	/**
	 * This method initializes seattext
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSeattext() {
		if (seattext == null) {
			seattext = new JTextField(20);
			seattext.setEditable(false);
			seattext.setText("---------");
		}
		return seattext;
	}

	/**
	 * This method initializes showButton
	 * 
	 * @return javax.swing.JButton
	 */
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
	
	private JCheckBox oldShowBox = new JCheckBox("Include Past Shows");

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());
		this.add(getCustButton(), GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH));
		this.add(getCusttext(), GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(getShowButton(), GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH));
		this.add(getShowText(), GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(oldShowBox, GridBagConstraintsFactory.create(2, 1,
				GridBagConstraints.BOTH));
		this.add(getSeatButton(), GridBagConstraintsFactory.create(0, 2,
				GridBagConstraints.BOTH));
		this.add(getSeattext(), GridBagConstraintsFactory.create(1, 2,
				GridBagConstraints.BOTH, 1.0, 0.0));
		this.add(new JLabel("Price:"), GridBagConstraintsFactory.create(0, 3,
				GridBagConstraints.BOTH));
		this.add(getPriceText(), GridBagConstraintsFactory.create(1, 3,
				GridBagConstraints.BOTH, 1.0, 0.0));
	}

	@Override
	public void refresh() {
		// empty
	}

	@Override
	public void saveData() throws Exception, Warning {
		if (custtext.getText().equals("") || showtext.getText().equals("")
				|| seattext.getText().equals("")) {
			throw new Warning("Customer, Show, and Seat are Required");
		}

		if (ticket_ == null)
			ticket_ = TicketModel.getReference().newTicket();

		int price = 0;
		try {
			price = Money.parse(priceText.getText());
		} catch (ParseException e) {
			throw new Warning("Invalid format for price");
		}

		try {
			JdbcDB.startTransaction();
			SeatModel.getReference().setNotifyListeners(false);
			TicketModel.getReference().setNotifyListeners(false);
			CustomerModel.getReference().setNotifyListeners(false);

			if (!ticket_.isNew())
			{
				Ticket oldTicket = TicketModel.getReference().getRecord(ticket_.getKey());
				CustomerModel.getReference().subtractTicketQuality(oldTicket);
				TicketModel.getReference().delete(oldTicket);
			}
			
			for (Seat s : selectedSeats) {
				Ticket t = TicketModel.getReference().newTicket();
				t.setSeatId(Integer.valueOf(s.getKey()));
				t.setPrice(Integer.valueOf(price));
				t.setShowId(ticket_.getShowId());
				t.setCustomerId(ticket_.getCustomerId());
				TicketModel.getReference().saveRecord(t);
				CustomerModel.getReference().addTicketQuality(t);
			}

			JdbcDB.commitTransaction();
		} catch (Exception e) {
			JdbcDB.rollbackTransaction();
		} finally {
			SeatModel.getReference().setNotifyListeners(true);
			TicketModel.getReference().setNotifyListeners(true);
			CustomerModel.getReference().setNotifyListeners(true);
		}

	}

	private void seatSelect() {
		Integer sh = ticket_.getShowId();
		// if( sh == null ) sh = Integer.valueOf(-1);
		if (sh == null) {
			Errmsg.getErrorHandler().notice("Please select a show before selecting a seat");
			return;
		}
		selectedSeats = SeatSelector.selectSeat(sh.intValue(),
				SeatGridPanel.SelectionMode.ANY_AVAIL);

		String st = "";
		for (Seat s : selectedSeats) {
			st += " " + s.getRow() + "/" + s.getNumber();
		}

		seattext.setText(st);

	}

	@Override
	public void showData(Ticket s) {

		ticket_ = s;
		if (s == null)
			return;

		if (ticket_.isNew())
			showButton.setEnabled(true);
		else
			showButton.setEnabled(false);
		custtext.setText(ticket_.getCustomerName());
		if (ticket_.getShowName() != null)
			showtext.setText(ticket_.getShowName() + " -- "
					+ sdf.format(ticket_.getShowDate()));
		else
			showtext.setText("");
		seattext.setText(ticket_.getRowAisle());
		selectedSeats.clear();
		if (!ticket_.isNew())
			try {
				selectedSeats.add(SeatModel.getReference().getSeat(
						ticket_.getSeatId().intValue()));
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
				return;
			}

		if (ticket_.getPrice() != null)
			priceText.setText(Money.format(ticket_.getPrice().intValue()));
		else {
			priceText.setText(Money.format(0));
		}

	}

	private void showSelect() {
		Show s = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class,
								java.util.Date.class }), new String[] { "Name",
						"DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM,
						!oldShowBox.isSelected()));
		if (s == null) {
			ticket_.setShowId(null);
			showtext.setText("");
			priceText.setText("$0.00");
		} else {
			ticket_.setShowId(Integer.valueOf(s.getKey()));
			showtext
					.setText(s.getName() + " -- " + sdf.format(s.getDateTime()));
			priceText.setText(Money.format(s.getPrice().intValue()));
		}
	}

} 