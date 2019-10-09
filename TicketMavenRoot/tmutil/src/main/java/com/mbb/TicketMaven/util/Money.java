/*
 * #%L
 * tmutil
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

package com.mbb.TicketMaven.util;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * a single place to format cents into dollars and vice versa.
 */
public class Money {
	
	/**
	 * Format cents into a dollar string.
	 * 
	 * @param cents the cents
	 * 
	 * @return the string
	 */
	public static String format(int cents)
	{
		return NumberFormat.getCurrencyInstance().format(cents/100.0);
	}
	
	/**
	 * Parses a dollar String into cents
	 * 
	 * @param s the dollar string (i.e. $5.99)
	 * 
	 * @return the int
	 * 
	 * @throws ParseException the parse exception
	 */
	public static int parse(final String s) throws ParseException 
	{		
			String ss = s;
			if( ss.indexOf("$") != 0)
				ss = "$" + ss;
			Number n = NumberFormat.getCurrencyInstance().parse(ss);
			return Double.valueOf(n.doubleValue()*100.0).intValue();
	}
}
