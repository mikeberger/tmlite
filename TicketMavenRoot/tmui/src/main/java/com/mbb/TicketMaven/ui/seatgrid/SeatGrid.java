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

package com.mbb.TicketMaven.ui.seatgrid;

import com.mbb.TicketMaven.model.*;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.ui.ViewFrame;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public class SeatGrid extends ViewFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static class SeatGridPanel extends JPanel implements Printable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private int show_id = 0;

		private int pages_ = 1;

		private String title_;

		private Ticket rowArray[][];
		private String numArray[][];

		SeatGridPanel(int show, String title) {
			show_id = show;
			title_ = title;
			Ticket noseat = new Ticket();
			noseat.setKey(-999);

			// load data into array
			try {
				int rows = LayoutModel.getNumRows(show_id);
				int seats = LayoutModel.getNumSeats(show_id);
				rowArray = new Ticket[rows][seats];
				numArray = new String[rows][seats];

				Collection<Ticket> tkts = TicketModel.getReference()
						.getTicketsForShow(show_id);

				Iterator<Ticket> it = tkts.iterator();
				while (it.hasNext()) {
					Ticket t = it.next();
					Seat seat = SeatModel.getReference().getSeat(t.getSeatId().intValue());
					int gridx = seat.getSeat().intValue();
					rowArray[SeatModel.rowletters.indexOf(t.getRow())][gridx - 1] = t;
					numArray[SeatModel.rowletters.indexOf(t.getRow())][gridx - 1] = seat.getNumber();

				}

				Show showobj = ShowModel.getReference().getShow(show_id);
				for (int r = 0; r < rows; r++) {
					for (int s = 0; s < seats; s++) {
						if (rowArray[r][s] == null) {
							// check if seat is not avail
							Seat seat = SeatModel.getReference().getSeat(
									SeatModel.rowletters.substring(r, r + 1),
									s + 1, showobj.getLayout().intValue());
							numArray[r][s] = seat.getNumber().toString();
							if (seat.getAvailable().equals("N")) {
								rowArray[r][s] = noseat;
							}
						}
					}
				}

			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

		private int drawIt(Graphics g, double width, double height,
				double pageWidth, double pageHeight, double pagex,
				double pagey, int pageIndex) {

			// set up default and small fonts
			Graphics2D g2 = (Graphics2D) g;

			Font def_font = g2.getFont();
			Font sm_font = def_font.deriveFont(6f);
			Font tiny_font = def_font.deriveFont(5f);

			g2.setColor(Color.white);
			g2.fillRect(0, 0, (int) width, (int) height);

			// set color to black
			g2.setColor(Color.black);

			// translate coordinates based on the amount of the page that
			// is going to be printable on - in other words, set upper right
			// to upper right of printable area - not upper right corner of
			// paper
			g2.translate(pagex, pagey);
			Shape s = g2.getClip();

			// determine placement of title at correct height and centered
			// horizontally on page
			int titlewidth = g2.getFontMetrics().stringWidth(title_);

			g2.drawString(title_, ((int) pageWidth - titlewidth) / 2, g2
					.getFontMetrics().getHeight());

			int rows = 0;
			int seats = 0;
			try {
				rows = LayoutModel.getNumRows(show_id);
				seats = LayoutModel.getNumSeats(show_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int gridRows = rows + 9; // title + heading
			int gridCols = seats + 3; // 2 labels + 1 aisle

			int colwidth = (int) pageWidth / gridCols;
			int rowheight = (int) pageHeight / gridRows;
			// keep it squares
			int min = Math.min(colwidth, rowheight);
			colwidth = min;
			rowheight = min;

			Color sncolor = new Color(255, 153, 153);

			// row labels

			for (int r = 0; r < rows; r++) {
				g2.setFont(sm_font);

				int y = rowheight * (r + 3);
				g2.clipRect(0, y, colwidth, rowheight);
				g2.drawString(SeatModel.rowletters.substring(r, r + 1),
						colwidth / 3, y + g2.getFontMetrics().getHeight()
								+ rowheight / 3);
				g2.setClip(s);

				g2.clipRect((seats + 1) * colwidth, y, colwidth, rowheight);
				g2.drawString(SeatModel.rowletters.substring(r, r + 1),
						(seats + 1) * colwidth + colwidth / 3, y
								+ g2.getFontMetrics().getHeight() + rowheight
								/ 3);
				g2.setClip(s);
			}

			// legend
			int legendy = (rows + 4) * rowheight;
			int legendx = colwidth * 2;

			g2.setFont(def_font);
			g2.drawRect(legendx, legendy, colwidth, rowheight);
			g2.drawString(" = Empty Seat", legendx + colwidth, legendy
					+ g2.getFontMetrics().getHeight());

			g2.setColor(new Color(200, 200, 250));
			g2.fillRect(legendx, legendy + 2 * rowheight, colwidth, rowheight);
			g2.setColor(Color.BLACK);
			g2.drawRect(legendx, legendy + 2 * rowheight, colwidth, rowheight);
			g2.drawString(" = Occupied Seat (non-special needs customer)",
					legendx + colwidth, legendy + 2 * rowheight
							+ g2.getFontMetrics().getHeight());

			g2.setColor(sncolor);
			g2.fillRect(legendx, legendy + 4 * rowheight, colwidth, rowheight);
			g2.setColor(Color.BLACK);
			g2.drawRect(legendx, legendy + 4 * rowheight, colwidth, rowheight);
			g2.drawString(" = Occupied Seat (Special needs customer)", legendx
					+ colwidth, legendy + 4 * rowheight
					+ g2.getFontMetrics().getHeight());

			// print the seats
			int boxpad = colwidth / 5;
			for (int box = 0; box < rows * seats; box++) {

				int boxcol = box % seats;
				int boxrow = box / seats;
				int rowtop = (boxrow + 3) * rowheight + boxpad; // skip title +
				// header
				int colleft = (boxcol + 1) * colwidth + boxpad; // skip row
				// label

				g2.setFont(tiny_font);

				try {
					Ticket t = rowArray[boxrow][boxcol];

					// don't draw box if no seat
					if (t != null && t.getKey() == -999)
						continue;

					if (t != null
							&& !t.getSpecialNeeds().equals(CustomerModel.NONE)) {
						g2.setColor(sncolor);
						g2.fillRect(colleft, rowtop, colwidth - boxpad,
								rowheight - boxpad);

					} else if (t != null
							&& t.getSpecialNeeds().equals(CustomerModel.NONE)) {
						g2.setColor(new Color(200, 200, 250));
						g2.fillRect(colleft, rowtop, colwidth - boxpad,
								rowheight - boxpad);
					}
					g2.setColor(Color.BLACK);

//					String lbl = SeatModel.rowletters.substring(boxrow,
//							boxrow + 1)
//							+ numArray[boxrow][boxcol];
					String lbl = numArray[boxrow][boxcol];
					g2.drawString(lbl, colleft + 1, rowtop
							+ g2.getFontMetrics().getHeight());

					g2.drawRect(colleft, rowtop, colwidth - boxpad, rowheight
							- boxpad);

				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
					continue;
				}

				g2.setClip(s);

			}

			return Printable.PAGE_EXISTS;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			try {
				drawIt(g, getWidth(), getHeight(), getWidth() - 20,
						getHeight() - 20, 10, 10, 0);

			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

		// print does the actual formatting of the printout
		@Override
		public int print(Graphics g, PageFormat pageFormat, int pageIndex)
				throws PrinterException {

			if (pageIndex > pages_ - 1)
				return Printable.NO_SUCH_PAGE;

			return (drawIt(g, pageFormat.getWidth(), pageFormat.getHeight(),
					pageFormat.getImageableWidth(), pageFormat
							.getImageableHeight(), pageFormat.getImageableX(),
					pageFormat.getImageableY(), pageIndex));
		}

		@SuppressWarnings("unused")
		public void setPages(int p) {
			pages_ = p;
		}
	}

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
		} catch (NoSuchMethodException e) {
			//empty
		} catch (IllegalAccessException e) {
			//empty
		} catch (InvocationTargetException e) {
			//empty
		} catch (ClassNotFoundException e) {
			//empty
		}

	}

	static private void printPrintable(SeatGridPanel p) throws Exception {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		initPrinterJobFields(printJob);

		PageFormat pageFormat = printJob.defaultPage();
		Paper paper = pageFormat.getPaper();
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		paper.setSize(8.5 * 72, 11 * 72);
		paper.setImageableArea(0.875 * 72, 0.625 * 72, 6.75 * 72, 9.75 * 72);
		pageFormat.setPaper(paper);

		printJob.setPrintable(p, pageFormat);

		if (printJob.printDialog())
			printJob.print();

	}

	private SeatGridPanel seatGridPanel;

	public SeatGrid(int show, String title) {
		super();

		seatGridPanel = new SeatGridPanel(show, title);
		seatGridPanel.setBackground(Color.WHITE);
		seatGridPanel.setPreferredSize(new Dimension(800, 600));

		// for the preview, create a JFrame with the preview panel and print
		// menubar
		JMenuBar menubar = new JMenuBar();
		JMenu pmenu = new JMenu();
		pmenu.setText("Action");
		JMenuItem mitem = new JMenuItem();
		mitem.setText("Print");
		mitem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				printAction();
			}
		});
		pmenu.add(mitem);
		JMenuItem quititem = new JMenuItem();
		quititem.setText("Dismiss");
		quititem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					destroy();
				} catch (Exception e) {
					//empty
				}
			}
		});
		pmenu.add(quititem);
		menubar.add(pmenu);
		menubar.setBorder(new BevelBorder(BevelBorder.RAISED));

		setJMenuBar(menubar);

		getContentPane().add(seatGridPanel, BorderLayout.CENTER);
		this.setTitle("Seat Layout");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);

		manageMySize(PrefName.SEATGRIDSIZE);
	}

	@Override
	public void destroy() {
		this.dispose();
	}

	private void printAction() {
		try {
			printPrintable(seatGridPanel);
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	@Override
	public void refresh() {
		//empty
	}
}
