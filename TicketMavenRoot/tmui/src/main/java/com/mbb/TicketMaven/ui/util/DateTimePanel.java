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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

/**
 * Creates a JPanel that lets the user select a date and time
 */
public class DateTimePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	/** The j calendar combo box. */
	private JDateChooser dateChooser = new JDateChooser();
	
	/** The hour box. */
	private JComboBox<String> hourBox = null;
	
	/** The minute box. */
	private JComboBox<String> minuteBox = null;
	
	/** The ampm box. */
	private JComboBox<String> ampmBox = null;
	
	/**
	 * This method initializes.
	 */
	public DateTimePanel() {
		super();
		initialize();
	}

	/**
	 * Sets the date and time shown in the panel.
	 * 
	 * @param d the new time
	 */
	public void setTime(Date d)
	{
		Calendar cal = new GregorianCalendar();
		if( d != null )
			cal.setTime(d);
		dateChooser.setCalendar(cal);
		int hr = cal.get(Calendar.HOUR);
		if( hr == 0 ) hr = 12;
		hourBox.setSelectedIndex( hr - 1);
		minuteBox.setSelectedIndex( cal.get(Calendar.MINUTE)/5);
		if( cal.get(Calendar.AM_PM) == Calendar.AM)
			ampmBox.setSelectedItem("AM");
		else
			ampmBox.setSelectedItem("PM");
		
	}
	
	/**
	 * Gets the time shown in the panel.
	 * 
	 * @return the time
	 */
	public Date getTime()
	{
		Calendar cal = dateChooser.getCalendar();
		cal.set(Calendar.HOUR, hourBox.getSelectedIndex()+1);
		cal.set(Calendar.MINUTE, minuteBox.getSelectedIndex() * 5);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		String val = (String)ampmBox.getSelectedItem();
		if( val.equals("AM") )
			cal.set(Calendar.AM_PM, Calendar.AM);
		else
			cal.set(Calendar.AM_PM, Calendar.PM);
		return cal.getTime();
	}
	
	/**
	 * UI init - still contains generated code - cleanup if changing this code
	 */
	private void initialize() {
       
        this.setLayout(new GridBagLayout()); 
        
        this.add(new JLabel("Date:"), GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH) ); 
        this.add(dateChooser, GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH));
        
        this.add(new JLabel("Time:"), GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
        
        GridBagConstraints gbc = GridBagConstraintsFactory.create(1, 1, GridBagConstraints.BOTH, 1.0, 0.0);
        gbc.insets = new Insets(0,0,0,0);
        
        JPanel timePanel = new JPanel();
        timePanel.add(getHourBox(), null);  // Generated
        timePanel.add(getMinuteBox(), null);  // Generated
        timePanel.add(getAmPmBox(), null);  // Generated
        this.add(timePanel, gbc);
			
	}
	
	/**
	 * This method initializes hourBox.
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getHourBox() {
		if (hourBox == null) {
			hourBox = new JComboBox<String>();
			hourBox.addItem("1");
			hourBox.addItem("2");
			hourBox.addItem("3");
			hourBox.addItem("4");
			hourBox.addItem("5");
			hourBox.addItem("6");
			hourBox.addItem("7");
			hourBox.addItem("8");
			hourBox.addItem("9");
			hourBox.addItem("10");
			hourBox.addItem("11");
			hourBox.addItem("12");
		}
		return hourBox;
	}

	/**
	 * This method initializes minuteBox.
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getMinuteBox() {
		if (minuteBox == null) {
			minuteBox = new JComboBox<String>();
			minuteBox.addItem("00");
			minuteBox.addItem("05");
			minuteBox.addItem("10");
			minuteBox.addItem("15");
			minuteBox.addItem("20");
			minuteBox.addItem("25");
			minuteBox.addItem("30");
			minuteBox.addItem("35");
			minuteBox.addItem("40");
			minuteBox.addItem("45");
			minuteBox.addItem("50");
			minuteBox.addItem("55");
			
		}
		return minuteBox;
	}
	
	/**
	 * Gets the am pm box.
	 * 
	 * @return the am pm box
	 */
	private JComboBox<String> getAmPmBox()
	{
		if( ampmBox == null ){
			ampmBox = new JComboBox<String>();
			ampmBox.addItem("AM");
			ampmBox.addItem("PM");
		}
		
		return ampmBox;
	}



}  
