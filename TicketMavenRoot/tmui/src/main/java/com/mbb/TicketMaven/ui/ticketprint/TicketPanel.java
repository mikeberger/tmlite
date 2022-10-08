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

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketFormat;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

class TicketPanel extends JPanel implements Printable {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	private static final long serialVersionUID = 1L;

	static private final double prev_scale = 1.5;

	static private final int DPI = 72;

	private int pages_ = 1;

	private final SimpleDateFormat sdf = new SimpleDateFormat(Prefs.getPref(PrefName.DATEFORMAT));

	private ArrayList<Ticket> tickets;

	private TicketFormat defaultFormat = null; // for ticket preview

	public TicketFormat getDefaultFormat() {
		return defaultFormat;
	}

	public void setDefaultFormat(TicketFormat defaultFormat) {
		this.defaultFormat = defaultFormat;
		repaint();
	}

	public TicketPanel(Collection<Ticket> c, TicketFormat defaultFormat) throws Exception {
		this.defaultFormat = defaultFormat;
		init(c);
	}

	public TicketPanel(Collection<Ticket> c) throws Exception {
		init(c);
	}

	private void init(Collection<Ticket> c) throws Exception {
		tickets = new ArrayList<Ticket>();
		tickets.addAll(c);

		if (tickets == null || tickets.size() == 0) {
			throw new Warning("The report contains no tickets");
		}

		setPages((tickets.size() - 1) / 10 + 1);

	}

	private int drawIt(Graphics g, double width, double height, double pageWidth, double pageHeight, double pagex,
			double pagey, int pageIndex) {

		// set up default and small fonts
		Graphics2D g2 = (Graphics2D) g;

		Font def_font = g2.getFont();
		// Font sm_font = def_font.deriveFont(8f);
		Font reg_font = def_font.deriveFont(11f);
		// Font bold_font = reg_font.deriveFont(Font.BOLD);

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

		double tw = 3.25 * DPI;
		double th = 1.75 * DPI;
		for (int col = 0; col < 2; col++) {
			for (int row = 0; row < 5; row++) {
				double y = row * (2.0 * DPI);
				double x = 0;
				if (col == 1)
					x = 3.5 * DPI;
				double liney = y;

				int tnum = (col * 5 + row) + (pageIndex * 10);
				if (tnum >= tickets.size())
					continue;
				Ticket t = tickets.get(tnum);
				TicketFormat format = defaultFormat;
				if (t.getShowId() != null) {
					try {
						Show show = ShowModel.getReference().getShow(t.getShowId().intValue());
						if (show.getFormat() != null) {
							format = show.getFormat();
						} else {
							Layout l = LayoutModel.getReference().getLayout(show.getLayout().intValue());
							format = new TicketFormat(l.getSeating());
							format.loadDefault();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				if (defaultFormat != null)
					g2.drawRect((int) x, (int) y, (int) tw, (int) th);
				else
					g2.setClip((int) x, (int) y, (int) tw, (int) th);

				// process logo
				String logo = format.getImageFilename();
				if (!logo.equals("")) {

					Toolkit tk = Toolkit.getDefaultToolkit();

					Image img = tk.getImage(logo);
					if (img != null) {
						try {
							MediaTracker mt = new MediaTracker(this);
							mt.addImage(img, 1);
							mt.waitForID(1);

							// scale if needed to fit
							if (img.getHeight(null) > th) {

								Image scaledimg = img.getScaledInstance(-1, (int) th, Image.SCALE_SMOOTH);
								mt.addImage(scaledimg, 2);
								mt.waitForID(2);
								img = scaledimg;
							}

							if (img.getWidth(null) > tw) {
								Image scaledimg = img.getScaledInstance((int) tw, -1, Image.SCALE_SMOOTH);
								mt.addImage(scaledimg, 3);
								mt.waitForID(3);
								img = scaledimg;

							}

						} catch (Exception e) {
							Errmsg.getErrorHandler().errmsg(e);
						}

						try {
							g2.drawImage(img, (int) x, (int) y, Color.WHITE, null);
						} catch (Exception e) {
							log.severe(e.getMessage());
						}
					}
				}

				boolean stub = format.hasStub();
				double leftsidewidth = tw;

				if (stub == true) {
					AffineTransform atorig = g2.getTransform();
					leftsidewidth = 2.75 * DPI;

					Graphics2D g2d = (Graphics2D) g2.create();

					Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
							new float[] { 9 }, 0);
					g2d.setStroke(dashed);

					g2d.drawLine((int) (x + leftsidewidth), (int) y, (int) (x + leftsidewidth), (int) (y + th));
					g2d.dispose();

					AffineTransform at = g2.getTransform();

					at.rotate(-Math.PI / 2.0);
					g2.setTransform(at);
					int fh = g2.getFontMetrics().getHeight();

					for (int i = 0; i < TicketFormat.NUM_STUB_LINES; i++) {

						String fs = format.getStubLine(i).getFont();
						g2.setFont(reg_font);

						if (fs != null && !fs.equals("")) {
							Font f = Font.decode(fs);
							g2.setFont(f);
						}
						String st = format.getStubLine(i).getText();

						if (st != null)
							st = replaceTokens(t, st);
						else
							st = "";
						g2.setColor(format.getStubLine(i).getColor());

						g2.drawString(st, (int) (-1 * (y + th - fh)), (int) (x + leftsidewidth + (i + 1) * fh));

					}

					g2.setTransform(atorig);
					g2.setClip((int) x, (int) y, (int) leftsidewidth, (int) th);
				}

				for (int i = 0; i < 8; i++) {
					g2.setFont(reg_font);
					liney = drawLine(t, i, g2, x, liney, leftsidewidth, format);
				}

				g2.setClip(s);
			}
		}

		return Printable.PAGE_EXISTS;
	}

	private double drawLine(Ticket t, int line, Graphics2D g2, double x, double y, double width, TicketFormat format) {

		String fs = format.getLine(line).getFont();

		if (fs != null && !fs.equals("")) {
			Font f = Font.decode(fs);
			g2.setFont(f);
		}
		String st = format.getLine(line).getText();

		if (st != null)
			st = replaceTokens(t, st);
		else
			st = "";
		g2.setColor(format.getLine(line).getColor());
		int swidth = g2.getFontMetrics().stringWidth(st);
		g2.drawString(st, (int) (x + width / 2 - swidth / 2), (int) (y + g2.getFontMetrics().getHeight()));
		return (y + g2.getFontMetrics().getHeight());
	}

	private String getToken(Ticket t, String token) {
		if (token.equals("club")) {
			return Prefs.getPref(PrefName.COMMUNITY);
		} else if (token.equals("show")) {
			return t.getShowName();
		} else if (token.equals("name")) {
			return t.getCustomerName();
		} else if (token.equals("date")) {
			return sdf.format(t.getShowDate());
		} else if (token.equals("price")) {
			return Money.format(t.getPrice().intValue());
		} else if (token.equals("row")) {
			return t.getRow();
		} else if (token.equals("seat")) {
			if (t.getRow().startsWith("_"))
				return "____";
			String s = t.getSeat();
			if (s.equals("")) {
				return "1";
			}
			return s;

		} else if (token.equals("table")) {
			return t.getTable();
		} else if (token.equals("hp_section")) {

			if (t.getRow().startsWith("_"))
				return "____";
			try {
				String s = t.getSeat();
				int i = Integer.parseInt(s);
				if (i >= 100)
					return "CTR";
				else if (i % 2 == 0)
					return "RGT";
				else
					return "LFT";
			} catch (Exception e) {
				return "CTR";
			}

		}

		return token;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			Graphics2D g2 = (Graphics2D) g;
			g2.scale(prev_scale, prev_scale);
			drawIt(g, getWidth() / prev_scale, getHeight() / prev_scale, getWidth() / prev_scale - 20,
					getHeight() / prev_scale - 20, 10, 10, 0);

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	// print does the actual formatting of the printout
	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

		if (pageIndex > pages_ - 1)
			return Printable.NO_SUCH_PAGE;

		return (drawIt(g, pageFormat.getWidth(), pageFormat.getHeight(), pageFormat.getImageableWidth(),
				pageFormat.getImageableHeight(), pageFormat.getImageableX(), pageFormat.getImageableY(), pageIndex));
	}

	private String replaceTokens(Ticket t, final String s_in) {

		String s = s_in;
		while (true) {
			int i1 = s.indexOf("{");
			if (i1 == -1)
				break;

			int i2 = s.indexOf("}");
			if (i2 == -1)
				break;

			String left = s.substring(0, i1);
			String right = s.substring(i2 + 1);
			s = left + getToken(t, s.substring(i1 + 1, i2)) + right;
		}

		return s;
	}

	public void setPages(int p) {
		pages_ = p;
	}

	public void setTickets(Collection<Ticket> coll) {
		try {
			init(coll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		repaint();
	}
}
