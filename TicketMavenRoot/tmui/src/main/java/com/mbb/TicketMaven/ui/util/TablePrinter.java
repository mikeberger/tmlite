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
/* this code was loosely based on code obtained from an online forum
 * that did not contain any copyright information */

package com.mbb.TicketMaven.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This class prints a JTable
 */
public class TablePrinter implements Printable
{

    /** The table view. */
    private JTable tableView;
    
	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

    /* (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    @Override
	public int print(Graphics g, PageFormat pageFormat,
    int pageIndex) throws PrinterException
    {
        Graphics2D  g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        int fontHeight=g2.getFontMetrics().getHeight();
        int fontDesent=g2.getFontMetrics().getDescent();
        
        //leave room for page number
        double pageHeight = pageFormat.getImageableHeight()-fontHeight;
        double pageWidth = pageFormat.getImageableWidth();
        double tableWidth = tableView.getColumnModel( ).getTotalColumnWidth();
        double scale = 1;
        if (tableWidth >= pageWidth)
        {
            scale =  pageWidth / tableWidth;
        }
        
        double headerHeightOnPage=tableView.getTableHeader().getHeight()*scale;
        double tableWidthOnPage=tableWidth*scale;
        
        double oneRowHeight=(tableView.getRowHeight()+ tableView.getRowMargin())*scale;
        int numRowsOnAPage=(int)((pageHeight-headerHeightOnPage)/oneRowHeight);
        double pageHeightForTable=oneRowHeight*numRowsOnAPage;
        int totalNumPages=(int)Math.ceil(((double)tableView.getRowCount())/numRowsOnAPage);
        if(pageIndex>=totalNumPages)
        {
            return NO_SUCH_PAGE;
        }
        
        g2.translate(pageFormat.getImageableX(),pageFormat.getImageableY());
        //bottom center
        g2.drawString("Page: "+(pageIndex+1),(int)pageWidth/2-35, (int)(pageHeight+fontHeight-fontDesent));
        
        g2.translate(0f,headerHeightOnPage);
        g2.translate(0f,-pageIndex*pageHeightForTable);
        
        //If this piece of the table is smaller
        //than the size available,
        //clip to the appropriate bounds.
        if (pageIndex + 1 == totalNumPages)
        {
            int lastRowPrinted = numRowsOnAPage * pageIndex;
            int numRowsLeft =tableView.getRowCount()- lastRowPrinted;
            g2.setClip(0,
            (int)(pageHeightForTable * pageIndex),
            (int) Math.ceil(tableWidthOnPage),
            (int) Math.ceil(oneRowHeight *
            numRowsLeft));
        }
        //else clip to the entire area available.
        else
        {
            g2.setClip(0,
            (int)(pageHeightForTable*pageIndex),
            (int) Math.ceil(tableWidthOnPage),
            (int) Math.ceil(pageHeightForTable));
        }
        
        g2.scale(scale,scale);
        tableView.paint(g2);
        g2.scale(1/scale,1/scale);
        g2.translate(0f,pageIndex*pageHeightForTable);
        g2.translate(0f, -headerHeightOnPage);
        g2.setClip(0, 0,
        (int) Math.ceil(tableWidthOnPage),
        (int)Math.ceil(headerHeightOnPage));
        g2.scale(scale,scale);
        tableView.getTableHeader().paint(g2);
        //paint header at top
        
        return Printable.PAGE_EXISTS;
    }
    
	/**
	 * Inits the printer job fields.
	 * 
	 * @param job the job
	 */
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

	
	static private void printPrintable(TablePrinter p) throws Exception {

		PrinterJob printJob = PrinterJob.getPrinterJob();
		initPrinterJobFields(printJob);
		
		PageFormat pageFormat = printJob.defaultPage();
		Paper paper = pageFormat.getPaper();
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		paper.setSize(8.5*72,11*72);
		paper.setImageableArea(0.875*72,0.625*72,6.75*72,9.75*72);
		pageFormat.setPaper(paper);

		printJob.setPrintable(p,pageFormat);
		
		if (printJob.printDialog())
			printJob.print();

	}
    
    
    /**
     * Prints a JTable.
     * 
     * @param tbl the table
     * 
     * @throws Exception the exception
     */
    static public void printTable(JTable tbl) throws Exception
    {
        printPrintable( new TablePrinter(tbl));
    }
    
    /**
     * Instantiates a new table printer.
     * 
     */
    private TablePrinter( JTable c )
    {
        tableView = c;
    }
}



