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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ReservationModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketFormat;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.ui.ticketprint.TicketPreview;
import com.mbb.TicketMaven.ui.util.DateTimePanel;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.LimitDocument;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Money;
import com.mbb.TicketMaven.util.Warning;

/**
 * 
 * This class edits the details for a Show
 * 
 */
public class ShowView extends ViewDetailPanel<Show> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DateTimePanel dateTimePanel = null;

	private JComboBox<String> layoutBox = null;

	private JTextField nametext = null;

	private JTextField priceText = null;

	private TicketPreview preview = null;
	
	private JLabel soldLabel = new JLabel();
	private JLabel seatLabel = new JLabel();

	private Show show_;

	public ShowView() {
		super();
		show_ = null;
		initialize(); // init the GUI widgets
		showData(null);
	}

	private DateTimePanel getDateTimePanel() {
		if (dateTimePanel == null) {
			dateTimePanel = new DateTimePanel();
		}
		return dateTimePanel;
	}

	@Override
	public String getDuplicateError() {
		return "A Show already exists with the same name and time. Cannot store a duplicate";
	}

	private JComboBox<String> getLayoutBox() {
		if (layoutBox == null) {
			layoutBox = new JComboBox<String>();
		}
		return layoutBox;
	}

	private JTextField getPriceText() {
		if (priceText == null) {
			priceText = new JTextField();
			priceText.setColumns(6);
			priceText.setText("$00.00");
			priceText.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return priceText;
	}

	private JTextField getShowNameText() {
		if (nametext == null) {
			nametext = new JTextField();
			nametext.setColumns(100);
			nametext.setDocument(new LimitDocument(100));
		}
		return nametext;
	}

	private JPanel infoPanel = null;

	private void initialize() {

		this.setSize(new Dimension(600, 246));
		this.setLayout(new GridBagLayout());
		this.add(getJTabbedPane(), GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH, 1.0, 1.0));

	}

	private JPanel getInfoPanel() {

		if (infoPanel == null) {

			infoPanel = new JPanel();
			infoPanel.setLayout(new GridLayout(1,0));
			JPanel leftPanel = new JPanel();
			leftPanel.setLayout(new GridBagLayout());
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new GridBagLayout());

			JLabel showlabel = new JLabel("Show Name:");
			leftPanel.add(showlabel, GridBagConstraintsFactory.create(0, 0,
					GridBagConstraints.BOTH));

			GridBagConstraints gridBagConstraints1 = GridBagConstraintsFactory
					.create(1, 0, GridBagConstraints.BOTH);
			gridBagConstraints1.gridwidth = 2;
			leftPanel.add(getShowNameText(), gridBagConstraints1);

			GridBagConstraints gridBagConstraints11 = GridBagConstraintsFactory
					.create(0, 1, GridBagConstraints.NONE, 1.0, 0.0);
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.insets = new Insets(0, 0, 0, 0);
			leftPanel.add(getDateTimePanel(), gridBagConstraints11);

			JLabel jLabel1 = new JLabel("Seating Layout:");
			leftPanel.add(jLabel1, GridBagConstraintsFactory.create(0, 2,
					GridBagConstraints.HORIZONTAL));
			GridBagConstraints gridBagConstraints22 = GridBagConstraintsFactory
					.create(1, 2, GridBagConstraints.NONE);
			leftPanel.add(getLayoutBox(), gridBagConstraints22);

			JLabel jLabel = new JLabel("Ticket Price:");
			rightPanel.add(jLabel, GridBagConstraintsFactory.create(0, 0,
					GridBagConstraints.BOTH));

			rightPanel.add(getPriceText(), GridBagConstraintsFactory.create(1,
					0, GridBagConstraints.HORIZONTAL, 1.0, 0.0));

			JLabel jLabel2 = new JLabel("Show Cost:");
			rightPanel.add(jLabel2, GridBagConstraintsFactory.create(0, 1,
					GridBagConstraints.BOTH));

			rightPanel.add(getCostField(), GridBagConstraintsFactory.create(1,
					1, GridBagConstraints.HORIZONTAL, 1.0, 0.0));
			
			GridBagConstraints gbc = GridBagConstraintsFactory.create(0,
					-1, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
			gbc.gridwidth = 2;
			rightPanel.add(soldLabel,gbc );
			rightPanel.add(seatLabel,gbc );
			
			infoPanel.add(leftPanel);
			infoPanel.add(rightPanel);
		}
		return infoPanel;
	}

	private int layoutids[] = null;

	private JTextField costField = null;

	private void populateLayoutBox() {
		layoutBox.setEnabled(true);
		layoutBox.removeAllItems();
		layoutids = null;
		Collection<Layout> layouts = null;
		try {
			layouts = LayoutModel.getReference().getAllLayouts();
			layoutids = new int[layouts.size()];
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}
		Iterator<Layout> it = layouts.iterator();
		int i = 0;
		while (it.hasNext()) {
			Layout l = it.next();
			layoutBox.addItem(l.getName());
			layoutids[i] = l.getKey();
			i++;
		}

	}

	@Override
	public void refresh() {
		// empty
	}

	@Override
	public void saveData() throws Exception, Warning {
		if (nametext.getText().equals("")) {
			throw new Warning("Show Name is Required");
		}

		if (layoutids == null || layoutids.length == 0) {
			throw new Warning("A Theater Layout is Required");
		}

		show_.setName(nametext.getText());
		show_.setDateTime(dateTimePanel.getTime());
		show_.setLayout(Integer.valueOf(layoutids[layoutBox.getSelectedIndex()]));
		try {
			show_.setPrice(Integer.valueOf(Money.parse(priceText.getText())));
		} catch (ParseException e) {
			throw new Warning("Invalid format for ticket price");
		}
		try {
			show_.setCost(Integer.valueOf(Money.parse(costField.getText())));
		} catch (ParseException e) {
			throw new Warning("Invalid format for show cost");
		}

		Component c = jTabbedPane.getComponentAt(1);
		if (c instanceof TicketPreview) {
			TicketPreview p = (TicketPreview) c;
			show_.setFormat(new TicketFormat(p.getFormat()));
		} else {
			show_.setFormat(null);
		}
		

		ShowModel.getReference().saveRecord(show_);

	}

	@Override
	public void showData(Show s) {

		populateLayoutBox();

		show_ = s;
		if (s == null) {
			return;
		}

		soldLabel.setText("");
		seatLabel.setText("");
		nametext.setText(show_.getName());
		dateTimePanel.setTime(show_.getDateTime());
		if (show_.getPrice() != null)
			priceText.setText(Money.format(show_.getPrice().intValue()));
		else
			priceText.setText(Money.format(0));

		if (show_.getCost() != null)
			costField.setText(Money.format(show_.getCost().intValue()));
		else
			costField.setText(Money.format(0));

		if (!s.isNew()) {
			try {
				Layout l = LayoutModel.getReference().getLayout(
						show_.getLayout().intValue());
				layoutBox.setSelectedItem(l.getName());

				// disable the layout box if there are tickets or reservations
				// for a show

				if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
					Collection<Ticket> c = TicketModel.getReference()
							.getTicketsForShow(s.getKey());
					if (!c.isEmpty()) {
						layoutBox.setEnabled(false);
					}
					soldLabel.setText("Tickets Sold: " + c.size());
				} else {

					Collection<Reservation> r = ReservationModel.getReference()
							.getReservationsForShow(s.getKey());
					if (!r.isEmpty()) {
						layoutBox.setEnabled(false);
					}
					int seats = 0;
					for( Reservation res : r)
					{
						seats+= res.getNum().intValue();
					}
					soldLabel.setText("Reservations: " + r.size());
					seatLabel.setText("Total Reserved Seats: " + seats);
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

		jTabbedPane.setEnabledAt(1, true);

		if (!s.isNew() && s.getFormat() != null) {

			TicketFormat tf = s.getFormat();
			if (preview == null) {
				preview = new TicketPreview(tf, false);
			} else {
				preview.setFormat(tf);
			}
			preview.setTicket(sampleTicket());
			jTabbedPane.setComponentAt(1, preview);
		} else {
			jTabbedPane.setComponentAt(1, getDefaultPanel());
			if (s.isNew()) {
				jTabbedPane.setSelectedIndex(0);
				jTabbedPane.setEnabledAt(1, false);
			}
		}

	}

	private JTabbedPane jTabbedPane;

	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Show Information", null, getInfoPanel(), null);
			jTabbedPane.addTab("Ticket Format", null, getDefaultPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * if the user wants to create a custom ticket format for a show, this
	 * method is called. A new Ticket Format editor will be displayed for the
	 * user.
	 */
	private void customButtonAction() {
		try {
			Layout l = LayoutModel.getReference().getLayout(
					show_.getLayout().intValue());
			TicketFormat tf = new TicketFormat(l.getSeating());
			tf.loadDefault();
			preview = new TicketPreview(tf, false);
			preview.setTicket(sampleTicket());
			jTabbedPane.setComponentAt(1, preview);
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

		// kludge to resize parent since our size changes drastically
		// not worth coding a fancier generic mechanism
		Container parent = this.getParent();
		Container grandparent = parent.getParent();
		if (grandparent instanceof JSplitPane) {
			((JSplitPane) grandparent).resetToPreferredSizes();
		}

	}

	private JPanel defaultPanel = null;

	private JPanel getDefaultPanel() {
		if (defaultPanel == null) {
			defaultPanel = new JPanel();
			defaultPanel.setLayout(new GridBagLayout());
			JLabel label = new JLabel(
					"This show uses the default ticket format as set in the options");
			defaultPanel.add(label, GridBagConstraintsFactory.create(0, 0));

			JButton button = new JButton("Add a custom format for this show");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					customButtonAction();
				}

			});
			defaultPanel.add(button, GridBagConstraintsFactory.create(0, 2));
		}
		return defaultPanel;
	}

	private JTextField getCostField() {
		if (costField == null) {
			costField = new JTextField();
			costField.setText("$00.00");
		}
		return costField;
	}

	private Ticket sampleTicket() {
		Ticket t = new Ticket();
		t.setRow("A");
		t.setTable("Table 1");
		t.setCustomerName("Customer Name");
		if (show_ != null) {
			t.setShowName(show_.getName());
			t.setShowDate(show_.getDateTime());
			t.setPrice(show_.getPrice());
		} else {
			t.setShowName("Your Show Name");
			t.setShowDate(new Date());
			t.setPrice(Integer.valueOf(1900));
		}
		return t;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
