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

package com.mbb.TicketMaven.ui.ticketprint;

import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.filter.AvailableSeatsForShowFilter;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.model.filter.TicketsForShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;

/**
 * provides the ticket printing this
 */
public class TicketMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public TicketMenu() {
		this.setText("Print Tickets");
		this.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Print16.gif")));

		JMenuItem printMI = new JMenuItem();
		printMI.setText("Print Sold Tickets For Show");
		printMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								null, true));
				if (s == null)
					return;
				try {
					TicketPrinter.printShow(s.getKey(), TicketPrinter.SOLD);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(printMI);

		JMenuItem unsoldMI = new JMenuItem();
		unsoldMI.setText("Print Unsold Tickets For Show");
		unsoldMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								LayoutModel.AUDITORIUM, true));
				if (s == null)
					return;
				try {
					TicketPrinter.printShow(s.getKey(), TicketPrinter.UNSOLD);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(unsoldMI);

		JMenuItem blankMI = new JMenuItem();
		blankMI.setText("Print Blank Sheet of Tickets For Show");
		blankMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								null, true));
				if (s == null)
					return;
				try {
					TicketPrinter.printShow(s.getKey(), TicketPrinter.BLANK);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(blankMI);

		JMenuItem chooseMI = new JMenuItem();
		chooseMI.setText("Print Selected Sold Tickets For Show");
		chooseMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								LayoutModel.AUDITORIUM, true));
				if (s == null)
					return;
				Collection<Ticket> tickets = BeanSelector.selectBeans(
						TicketModel.getReference(), new TableSorter(
								new String[] { "Name", "Seat" }, new Class[] {
										java.lang.String.class,
										java.lang.String.class }),
						new String[] { "CustomerName", "RowAisle" },
						new TicketsForShowFilter(s.getKey()));
				try {
					TicketPrinter.printSelectedTickets(tickets);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(chooseMI);

		JMenuItem chooseUAMI = new JMenuItem();
		chooseUAMI.setText("Print Selected Unsold Tickets For Show");
		chooseUAMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								LayoutModel.AUDITORIUM, true));
				if (s == null)
					return;
				Collection<Seat> seats = BeanSelector.selectBeans(SeatModel
						.getReference(), new TableSorter(new String[] { "Row",
						"Seat" }, new Class[] { java.lang.String.class,
						java.lang.Integer.class }), new String[] { "Row",
						"Number" }, new AvailableSeatsForShowFilter(s.getKey()));
				try {
					TicketPrinter.printSelectedAvailableTickets(s.getKey(),
							seats);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(chooseUAMI);

		JMenuItem printByNameMI = new JMenuItem();
		printByNameMI
				.setText("Print Sold Tickets For Selected Shows (Sorted by Name)");
		printByNameMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Collection<Show> coll = BeanSelector.selectBeans(ShowModel
						.getReference(), new TableSorter(new String[] {
						"Show Name", "Show Date/Time" }, new Class[] {
						java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								null, true));
				if (coll == null)
					return;
				try {
					TicketPrinter.printByName(coll);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(printByNameMI);

		JMenuItem printByTableMI = new JMenuItem();
		printByTableMI
				.setText("Print Sold Tickets For Show (Sorted by Seat or Table)");
		printByTableMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Show s = BeanSelector.selectBean(
						ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name",
								"Show Date/Time" }, new Class[] {
								java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								null, true));
				if (s == null)
					return;
				try {
					TicketPrinter.printBySeatOrTable(s.getKey());
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(printByTableMI);

		JMenuItem printSelForCust = new JMenuItem();
		printSelForCust
				.setText("Print Sold Tickets For Selected Customers in Selected Shows");
		printSelForCust.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Collection<Show> shows = BeanSelector.selectBeans(ShowModel
						.getReference(), new TableSorter(new String[] {
						"Show Name", "Show Date/Time" }, new Class[] {
						java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(
								null, true));
				if (shows == null)
					return;

				TableSorter ts = new TableSorter(new String[] { "First",
						"Last", "Phone" }, new Class[] {
						java.lang.String.class, java.lang.String.class,
						java.lang.String.class });
				ts.sortByColumn(1);
				Collection<Customer> custs = BeanSelector.selectBeans(
						CustomerModel.getReference(), ts, new String[] {
								"FirstName", "LastName", "Phone" }, null);
				
				if( custs == null ) return;

				try {
					TicketPrinter.printByShowForCust(shows, custs);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});
		this.add(printSelForCust);

	}

}
