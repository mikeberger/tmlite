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

package com.mbb.TicketMaven.ui.filter;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * this class filters tickets by customer, show, and date
 */
public class TicketFilterPanel extends FilterPanel<Ticket> {

	
	private static final long serialVersionUID = 1L;

	private JButton custButton = null;

	private JTextField custtext = null;

	private JCheckBox oldShows = null;

	private Ticket request_;

	private JButton showButton = null;

	private JTextField showtext = null;

	/**
	 * Instantiates a new ticket filter panel.
	 */
	public TicketFilterPanel() {
		super();

		request_ = TicketModel.getReference().newTicket();

		initialize(); // init the GUI widgets

	}

	/**
	 * Customer select.
	 */
	private void customerSelect() {
		Customer c = BeanSelector
				.selectBean(CustomerModel.getReference(), new TableSorter(
						new String[] { "First", "Last", "Phone" }, new Class[] {
								java.lang.String.class, java.lang.String.class,
								java.lang.String.class }), new String[] {
						"FirstName", "LastName", "Phone" }, null);
		if (c == null) {
			request_.setCustomerId(null);
			custtext.setText("");
		} else {
			request_.setCustomerId(Integer.valueOf(c.getKey()));
			custtext.setText(c.getFirstName() + " " + c.getLastName());
		}

		notifyParent();
	}

	/**
	 * This method initializes custButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCustButton() {
		if (custButton == null) {
			custButton = new JButton();
			custButton.setText("Customer:"); // Generated
			custButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					customerSelect();
				}
			});
		}
		return custButton;
	}

	/**
	 * Gets the custtext.
	 * 
	 * @return the custtext
	 */
	private JTextField getCusttext() {
		if (custtext == null) {
			custtext = new JTextField(80);
			custtext.setEditable(false); // Generated
		}
		return custtext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.filter.KeyedEntityFilter#getMatchingBeans()
	 */
	@Override
	public Collection<Ticket> getMatchingEntities() throws MaxRowsException {
		Collection<Ticket> matchlist = new ArrayList<Ticket>();
		try {
			Collection<Ticket> beans = null;
			if (request_.getCustomerId() != null)
				beans = TicketModel.getReference().getTicketsForCustomer(
						request_.getCustomerId().intValue());
			else if (request_.getShowId() != null)
				beans = TicketModel.getReference().getTicketsForShow(
						request_.getShowId().intValue());
			else {
				if (TicketModel.getReference().numRows() > Prefs
						.getIntPref(PrefName.MAXQUERYROWS))
					throw new MaxRowsException();
				beans = TicketModel.getReference().getRecords();
			}

			for (Ticket b : beans) {
				if (matches(b))
					matchlist.add(b);
			}
		} catch (MaxRowsException me) {
			throw me;
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

	/**
	 * Gets the old shows.
	 * 
	 * @return the old shows
	 */
	private JCheckBox getOldShows() {
		if (oldShows == null) {
			oldShows = new JCheckBox();
			oldShows.setText("Show Past Shows");
			oldShows.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					selOldShows();
				}
			});
		}
		return oldShows;
	}

	/**
	 * This method initializes showButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Show:"); // Generated
			showButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showSelect();
				}
			});
		}
		return showButton;
	}

	/**
	 * Gets the show text.
	 * 
	 * @return the show text
	 */
	private JTextField getShowText() {
		if (showtext == null) {
			showtext = new JTextField(100);
			showtext.setEditable(false); // Generated
		}
		return showtext;
	}

	/**
	 * This method initializes this.
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
		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 2,
				GridBagConstraints.BOTH);
		gbc.gridwidth = 2;
		this.add(getOldShows(), gbc);
	}

	/**
	 * Matches.
	 * 
	 * @param b
	 *            the b
	 * 
	 * @return true, if successful
	 */
	public boolean matches(KeyedEntity b) {
		Ticket t = (Ticket) b;

		if (request_.getCustomerId() != null
				&& t.getCustomerId().intValue() != request_.getCustomerId()
						.intValue())
			return false;
		if (request_.getShowId() != null
				&& t.getShowId().intValue() != request_.getShowId().intValue())
			return false;

		if (!getOldShows().isSelected()) {

			if (t.getShowDate() != null && t.getShowDate().before(new Date()))
				return false;
		}

		return true;
	}

	/**
	 * Sel old shows.
	 */
	private void selOldShows() {
		notifyParent();
	}

	/**
	 * Show select.
	 */
	private void showSelect() {
		Show s = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class,
								java.util.Date.class }), new String[] { "Name",
						"DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM,
						!getOldShows().isSelected()));
		if (s == null) {
			request_.setShowId(null);
			showtext.setText("");
		} else {
			request_.setShowId(Integer.valueOf(s.getKey()));
			showtext
					.setText(s.getName() + " -- " + sdf.format(s.getDateTime()));
		}
		notifyParent();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
