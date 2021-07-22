/*
 *	Copyright (c) 2009
 *    Michael Berger
 *	All Rights Reserved
 *
 *	PROPRIETARY 
 *   This Software contains proprietary information that shall not be 
 *    in the possession of, distributed to, or routed to anyone except with written permission of Michael Berger.
 */
package com.mbb.TicketMaven.ui.ticketprint;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ReservationModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.entity.Customer;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Warning;


class TicketPrinterPlugin  {


	public static final int BLANK = 3;

	public static final int SOLD = 1;

	public static final int UNSOLD = 2;
	
	//static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

/*
	public static void initPrinterJobFields(PrinterJob job) {
		job.setJobName("TicketMaven Printout");
		Class<? extends PrinterJob> klass = job.getClass();
		try {
			Class<?> printServiceClass = Class.forName("javax.print.PrintService");
			Method method = klass.getMethod("getPrintService", (Class[]) null);
			Object printService = method.invoke(job, (Object[]) null);
			method = klass.getMethod("setPrintService",
					new Class[] { printServiceClass });
			method.invoke(job, new Object[] { printService });
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

	}
	*/

	static private void printPrintable(TicketPanel p) throws Exception {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		//initPrinterJobFields(printJob);

		PageFormat pageFormat = printJob.defaultPage();
		Paper paper = pageFormat.getPaper();
		paper.setSize(8.5 * 72, 11 * 72);
		paper.setImageableArea(0.875 * 72, 0.625 * 72, 6.75 * 72, 9.75 * 72);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);

		printJob.setPrintable(p, pageFormat);

		if (printJob.printDialog())
			printJob.print();

	}

	public void printSelectedAvailableTickets(int show,
			Collection<Seat> seats)  {
		try {

			Show s = ShowModel.getReference().getShow(show);
			ArrayList<Ticket> tickets = new ArrayList<Ticket>();
			for (Seat seat : seats) {

				Ticket t = TicketModel.getReference().newTicket();
				t.setRow(seat.getRow());
				t.setCustomerName("____________________");
				t.setShowName(s.getName());
				t.setShowDate(s.getDateTime());
				t.setShowId(Integer.valueOf(s.getKey()));
				t.setPrice(s.getPrice());
				t.setSeatId(Integer.valueOf(seat.getKey()));
				tickets.add(t);
			}

			TicketPanel cp = new TicketPanel(tickets);
			printPrintable(cp);
		} catch (Exception w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		}
	}

	public void printSelectedTickets(Collection<Ticket> c)
			 {
		try {
			TicketPanel cp = new TicketPanel(c);
			printPrintable(cp);
		} catch (Exception w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		}
	}

	public void printShow(int show, int which) {

		try {
			Show s = ShowModel.getReference().getShow(show);
			Layout l = LayoutModel.getReference().getLayout(
					s.getLayout().intValue());
			Collection<Ticket> tickets = null;
			if (which == SOLD) {
				if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
					tickets = TicketModel.getReference()
							.getTicketsForShow(show);
				} else {
					tickets = new ArrayList<Ticket>();

					Collection<Reservation> rescol = ReservationModel
							.getReference().getReservationsForShow(show);
					for (Reservation res : rescol) {
						for (int i = 0; i < res.getNum().intValue(); i++) {
							Ticket t = TicketModel.getReference().newTicket();
							t.setCustomerName(res.getCustomerName());
							t.setShowName(res.getShowName());
							t.setShowId(Integer.valueOf(s.getKey()));
							t.setRow("");
							t.setTable(res.getTableName());
							t.setShowDate(res.getShowDate());
							t.setPrice(s.getPrice());
							tickets.add(t);
						}
					}
				}

			} else if (which == UNSOLD) {
				Collection<Seat> seats = SeatModel.getReference()
						.getUnsoldSeatsForShow(show);
				Iterator<Seat> it = seats.iterator();
				tickets = new ArrayList<Ticket>();
				while (it.hasNext()) {

					Seat seat = it.next();
					Ticket t = TicketModel.getReference().newTicket();
					t.setRow(seat.getRow());
					t.setCustomerName("____________________");
					t.setShowName(s.getName());
					t.setShowDate(s.getDateTime());
					t.setShowId(Integer.valueOf(s.getKey()));
					t.setPrice(s.getPrice());
					t.setSeatId(Integer.valueOf(seat.getKey()));
					tickets.add(t);
				}

			} else /*if (which == BLANK)*/ {
				tickets = new ArrayList<Ticket>();

				for (int i = 0; i < 10; i++) {
					Ticket t = TicketModel.getReference().newTicket();
					t.setRow("_____");
					t.setCustomerName("____________________");
					t.setShowName(s.getName());
					t.setShowDate(s.getDateTime());
					t.setShowId(Integer.valueOf(s.getKey()));
					t.setTable("____");
					t.setPrice(s.getPrice());
					tickets.add(t);
				}
			}
			TicketPanel cp = new TicketPanel(tickets);
			printPrintable(cp);
		} catch (Exception w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		}

	}

	public void printByName(Collection<Show> coll) {

		if (coll == null || coll.size() == 0)
			return;
		try {
			TreeSet<Ticket> tickets = new TreeSet<Ticket>(
					new CompareTicketsByName());

			for (Show s : coll) {
				Layout l = LayoutModel.getReference().getLayout(
						s.getLayout().intValue());
				if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
					Collection<Ticket> tkts = TicketModel.getReference()
							.getTicketsForShow(s.getKey());

					if (tkts != null) {
						tickets.addAll(tkts);
					}
				} else {

					Collection<Reservation> reservations = ReservationModel
							.getReference().getReservationsForShow(s.getKey());
					if (reservations != null) {
						for (Reservation res : reservations) {
							for (int i = 0; i < res.getNum().intValue(); i++) {
								Ticket t = TicketModel.getReference().newTicket();
								t.setKey(-1 * (i + 1)); // create phoney keys so that the
								// treeset doesn't remove duplicates
								t.setCustomerName(res.getCustomerName());
								t.setShowName(res.getShowName());
								t.setRow("");
								t.setTable(res.getTableName());
								t.setShowDate(res.getShowDate());
								t.setShowId(res.getShowId());
								t.setPrice(s.getPrice());
								tickets.add(t);
							}
						}
					}
				}

			}

			

			TicketPanel cp = new TicketPanel(tickets);
			printPrintable(cp);

		} catch (Warning w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	public void printBySeatOrTable(int showId) {

		try {
			TreeSet<Ticket> tickets = null;

			Show s = ShowModel.getReference().getShow(showId);
			Layout l = LayoutModel.getReference().getLayout(
					s.getLayout().intValue());

			if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
				tickets = new TreeSet<Ticket>(new CompareTicketsBySeat());
				Collection<Ticket> tkts = TicketModel.getReference()
						.getTicketsForShow(s.getKey());

				if (tkts != null) {
					tickets.addAll(tkts);
				}
			} else {
				tickets = new TreeSet<Ticket>(new CompareTicketsByTable());
				Collection<Reservation> reservations = ReservationModel
						.getReference().getReservationsForShow(s.getKey());
				if (reservations != null) {

					for (Reservation res : reservations) {
						for (int i = 0; i < res.getNum().intValue(); i++) {
							Ticket t = TicketModel.getReference().newTicket();
							t.setKey(-1 * (i + 1)); // create phoney keys so
							// that the
							// treeset doesn't remove duplicates
							t.setCustomerName(res.getCustomerName());
							t.setShowName(res.getShowName());
							t.setRow("");
							t.setTable(res.getTableName());
							t.setShowDate(res.getShowDate());
							t.setShowId(res.getShowId());
							t.setPrice(s.getPrice());
							tickets.add(t);
						}
					}

				}
			}

			TicketPanel cp = new TicketPanel(tickets);
			printPrintable(cp);

		} catch (Warning w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}


	private static class CompareTicketsByName implements Comparator<Ticket> {

		public int compare(Ticket s1, Ticket s2) {

			String r1 = s1.getCustomerName();
			String r2 = s2.getCustomerName();

			int i = r1.compareTo(r2);
			if (i != 0)
				return i;

			return (s1.getKey() - s2.getKey());

		}

	}

	private static class CompareTicketsBySeat implements Comparator<Ticket> {

		public int compare(Ticket s1, Ticket s2) {

			
			String t1 = s1.getRow();
			String t2 = s2.getRow();
			int i = t1.compareTo(t2);
			if (i != 0)
				return i;
			
			// when comparing seat - zero pad for sorting
			String st1 = s1.getSeatInRow();
			String st2 = s2.getSeatInRow();
			if( st1.length() == 1)
				st1 = "0"+st1;
			if( st2.length() == 1)
				st2 = "0"+st2;
			i = st1.compareTo(st2);
			if (i != 0)
				return i;

			String r1 = s1.getCustomerName();
			String r2 = s2.getCustomerName();

			i = r1.compareTo(r2);
			if (i != 0)
				return i;

			return (s1.getKey() - s2.getKey());

		}

	}

	private static class CompareTicketsByTable implements Comparator<Ticket> {

		public int compare(Ticket s1, Ticket s2) {

			String t1 = s1.getTable();
			String t2 = s2.getTable();
			int i = t1.compareTo(t2);
			if (i != 0)
				return i;

			String r1 = s1.getCustomerName();
			String r2 = s2.getCustomerName();

			i = r1.compareTo(r2);
			if (i != 0)
				return i;

			return (s1.getKey() - s2.getKey());

		}

	}

	public void printByShowForCust(Collection<Show> shows,
			Collection<Customer> custs) {
		if (shows == null || shows.size() == 0)
			return;
		if (custs == null || custs.size() == 0)
			return;
		
		Set<Integer> ids = new HashSet<Integer>();
		for(Customer c : custs)
			ids.add(c.getKey());
		
		
		try {
			TreeSet<Ticket> tickets = new TreeSet<Ticket>(
					new CompareTicketsByName());

			for (Show s : shows) {
				Layout l = LayoutModel.getReference().getLayout(
						s.getLayout().intValue());
				if (l.getSeating().equals(LayoutModel.AUDITORIUM)) {
					Collection<Ticket> tkts = TicketModel.getReference()
							.getTicketsForShow(s.getKey());

					if (tkts != null) {
						
						for( Ticket tkt : tkts)
						{
							if( ids.contains(tkt.getCustomerId()))
							{
								tickets.add(tkt);
							}
						}
					}
				} else {

					Collection<Reservation> reservations = ReservationModel
							.getReference().getReservationsForShow(s.getKey());
					if (reservations != null) {
						for (Reservation res : reservations) {
							for (int i = 0; i < res.getNum().intValue(); i++) {
								Ticket t = TicketModel.getReference().newTicket();
								t.setKey(-1 * (i + 1)); // create phoney keys so that the
								// treeset doesn't remove duplicates
								t.setCustomerName(res.getCustomerName());
								t.setShowName(res.getShowName());
								t.setRow("");
								t.setTable(res.getTableName());
								t.setShowDate(res.getShowDate());
								t.setShowId(res.getShowId());
								t.setPrice(s.getPrice());
								tickets.add(t);
							}
						}
					}
				}

			}

			

			TicketPanel cp = new TicketPanel(tickets);
			printPrintable(cp);

		} catch (Warning w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

}
