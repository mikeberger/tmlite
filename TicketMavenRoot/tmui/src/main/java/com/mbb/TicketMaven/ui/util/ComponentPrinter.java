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
package com.mbb.TicketMaven.ui.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import com.mbb.TicketMaven.util.Errmsg;

/**
 * This class Prints a Java Component
 */
public class ComponentPrinter {
	
	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	
	/** The component */
	private JComponent c_;

	/**
	 * Instantiates a new component printer.
	 * 
	 * @param c the component
	 */
	public ComponentPrinter(JComponent c) {
		c_ = c;
	}

	/**
	 * Opens a print dialog and then Prints the Component.
	 */
	public void print() {
		if( c_ == null ) return;
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setJobName("TicketMaven Printout");
		Class<? extends PrinterJob> klass = pj.getClass();
		try {
			Class<?> printServiceClass = Class.forName("javax.print.PrintService");
			Method method = klass.getMethod("getPrintService", (Class[]) null);
			Object printService = method.invoke(pj, (Object[]) null);
			method = klass.getMethod("setPrintService",
					new Class[] { printServiceClass });
			method.invoke(pj, new Object[] { printService });
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		PageFormat format = pj.defaultPage();
		format.setOrientation(PageFormat.PORTRAIT);
		Paper paper = format.getPaper();
		paper.setSize(8.5*72,11*72);
		paper.setImageableArea(0.875*72,0.625*72,6.75*72,9.75*72);

		pj.setPrintable(new Printable() {
			@Override
			public int print(Graphics pg, PageFormat pf, int pageNum) {
				if (pageNum > 0) {
					return Printable.NO_SUCH_PAGE;
				}
				Graphics2D g2 = (Graphics2D) pg;
				Dimension d = c_.getSize(); // get size of document
				double panelWidth = d.width; // width in pixels
				double pageWidth = pf.getImageableWidth(); // width of printer
				// page

				double scale = pageWidth / panelWidth;
				g2.translate(pf.getImageableX(), pf.getImageableY());
				g2.scale(scale, scale);
				c_.paint(g2);
				return Printable.PAGE_EXISTS;
			}
		});
		if (pj.printDialog() == false)
			return;

		try {
			pj.print();
		} catch (PrinterException e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

}
