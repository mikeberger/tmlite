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

package com.mbb.TicketMaven.ui.report;

import com.mbb.TicketMaven.model.*;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.seatgrid.SeatGrid;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.*;

import javax.swing.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * provides the report menu
 * 
 */
public class ReportMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	private final SimpleDateFormat sdf = new SimpleDateFormat(Prefs.getPref(PrefName.DATEFORMAT));

	public ReportMenu() {
		/*
		 * Most of the options below just populate the parameters for a particular
		 * Jasper Report and then invoke that report. In some cases, the user is
		 * prompted to select certain Entitys first
		 */

		this.setText("Reports");
		this.setIcon(new ImageIcon(getClass().getResource("/resource/Print16.gif")));

		JMenuItem custMI = new JMenuItem();
		custMI.setText("Customer Report");
		custMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				RunReport.runReport("customer", null);
			}
		});

		this.add(custMI);

		JMenuItem tktMI = new JMenuItem();
		tktMI.setText("Ticket Report for a Show");
		tktMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, false));
				if (s == null)
					return;

				parms.put("show_id", Integer.valueOf(s.getKey()));
				parms.put("title", "Tickets for " + s.getName() + "\n" + sdf.format(s.getDateTime()));

				try {
					SeatModel.getReference().loadSeatMappingTable(s.getLayout().intValue());
				} catch (Exception e1) {
					Errmsg.getErrorHandler().errmsg(e1);
					return;
				}

				RunReport.runReport("seatsForShowLeftNumbering", parms);

			}
		});
		this.add(tktMI);

		JMenuItem tktbyNameMI = new JMenuItem();
		tktbyNameMI.setText("Ticket Report for a Show (By Name)");
		tktbyNameMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, false));
				if (s == null)
					return;

				parms.put("show_id", Integer.valueOf(s.getKey()));
				parms.put("title", "Tickets for " + s.getName() + "\n" + sdf.format(s.getDateTime()));

				try {
					SeatModel.getReference().loadSeatMappingTable(s.getLayout().intValue());
				} catch (Exception e1) {
					Errmsg.getErrorHandler().errmsg(e1);
					return;
				}

				RunReport.runReport("seatsForShowByName", parms);

			}
		});
		this.add(tktbyNameMI);

		JMenuItem nresMI = new JMenuItem();
		nresMI.setText("Non-Resident Report for a Show");
		nresMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, false));
				if (s == null)
					return;

				parms.put("show_id", Integer.toString(s.getKey()));
				parms.put("title", "Non-Resident Tickets for " + s.getName() + "\n" + sdf.format(s.getDateTime()));
				RunReport.runReport("nonresSeatsForShow", parms);
			}
		});
		this.add(nresMI);

		JMenuItem rqMI = new JMenuItem();
		rqMI.setText("Request Report for a Show");
		rqMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, false));
				if (s == null)
					return;

				parms.put("show_id", Integer.toString(s.getKey()));
				parms.put("title", "Outstanding Requests for " + s.getName() + " on " + sdf.format(s.getDateTime()));
				RunReport.runReport("requestsForShow", parms);
			}
		});
		this.add(rqMI);

		JMenuItem schartMI = new JMenuItem();
		schartMI.setText("Seating Chart for a Show");
		schartMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, false));
				if (s == null)
					return;

				// the seat grid is not a Jasper Report - just a drawing of the
				// grid
				new SeatGrid(s.getKey(), s.getName() + "   " + sdf.format(s.getDateTime()));
			}
		});
		this.add(schartMI);

		JMenuItem rvMI = new JMenuItem();
		rvMI.setText("Reservations for a Table Event");
		rvMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.TABLE, false));
				if (s == null)
					return;

				parms.put("showid", Integer.valueOf(s.getKey()));
				RunReport.runReport("tblrpt", parms);
			}
		});
		this.add(rvMI);

		JMenuItem rvdMI = new JMenuItem();
		rvdMI.setText("Reservations for a Table Event (detailed)");
		rvdMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Show s = BeanSelector.selectBean(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.TABLE, false));
				if (s == null)
					return;

				parms.put("showid", Integer.valueOf(s.getKey()));
				RunReport.runReport("tblrptdet", parms);
			}
		});
		this.add(rvdMI);

		JMenuItem srMI = new JMenuItem();
		srMI.setText("Sales Report for Show(s)");
		srMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Collection<Show> shows = BeanSelector.selectBeans(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, null);
				if (shows == null || shows.isEmpty())
					return;

				try {
					String showname = "";
					int tickets = 0;
					int sales = 0;
					int cost = 0;
					for (Show s : shows) {
						Layout l = LayoutModel.getReference().getLayout(s.getLayout().intValue());
						showname += s.getName() + " -- " + s.getDateTime() + "\n";
						if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
							Collection<Ticket> t = TicketModel.getReference().getTicketsForShow(s.getKey());
							if (t != null) {
								tickets += t.size();
								for (Ticket tkt : t) {
									if (tkt.getPrice() != null)
										sales += tkt.getPrice().intValue();
								}
							}
						} else {
							Collection<Reservation> rescol = ReservationModel.getReference()
									.getReservationsForShow(s.getKey());
							if (rescol != null) {
								for (Reservation res : rescol) {
									tickets += res.getNum().intValue();
								}
								sales += tickets * s.getPrice().intValue();
							}
						}
						
						cost += s.getCost().intValue();
					}
					parms.put("sold", Integer.toString(tickets));

					// int sales = s.getPrice().intValue() * tickets;
					parms.put("sales", Money.format(sales));
					double rate = Double.parseDouble(Prefs.getPref(PrefName.SALESTAX));
					double tax = (rate / 100.0) * sales;
					int taxcents = (int) tax;
					parms.put("tax", Money.format(taxcents));
					parms.put("cost", Money.format(cost));
					parms.put("profit", Money.format(sales - taxcents - cost));
					parms.put("showname", showname);


				} catch (Exception e1) {
					Errmsg.getErrorHandler().errmsg(e1);
					return;
				}

				RunReport.runReport("salesreport", parms);
			}
		});
		this.add(srMI);

		JMenuItem laMI = new JMenuItem();
		laMI.setText("Address Labels for selected Shows");
		laMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Collection<Show> coll = BeanSelector.selectBeans(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(null, false));
				if (coll == null || coll.isEmpty())
					return;
				java.util.List<Integer> keys = new ArrayList<Integer>();
				for (Show s : coll)
					keys.add(s.getKey());

				parms.put("showid", keys);
				RunReport.runReport("labels", parms);
			}
		});
		this.add(laMI);

		JMenuItem somi = new JMenuItem();
		somi.setText("Sign-off Sheet");
		somi.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				HashMap<String, Object> parms = new HashMap<String, Object>();
				Collection<Show> coll = BeanSelector.selectBeans(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(null, true));
				if (coll == null || coll.isEmpty())
					return;

				String ids = "";
				for (Show s : coll) {
					if (!ids.isEmpty())
						ids += ",";
					ids += Integer.toString(s.getKey());

				}
				parms.put("SHOWS", ids);
				RunReport.runReport("signoff", parms);
			}
		});
		this.add(somi);

		JMenuItem fileRptMI = new JMenuItem();
		fileRptMI.setText("Run Report From a File");
		fileRptMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					InputStream is = FileIO.fileOpen(".", "Select a Report File");
					if (is == null)
						return;
					RunReport.runReport(is, null);
				} catch (Exception ex) {
					Errmsg.getErrorHandler().errmsg(ex);
				}
			}
		});
		this.add(fileRptMI);

	}

}
