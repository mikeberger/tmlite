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

package com.mbb.TicketMaven.ui;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

/**
 * This class launches the online help. The JavaHelp system is used (javax.help)
 */
class HelpProxy {

	/**
	 * Launch help.
	 * 
	 * @throws Exception 
	 */
	public static void launchHelp() throws Exception {
		String helpHS = "Help.hs";
		ClassLoader cl = HelpProxy.class.getClassLoader();
		URL hsURL = HelpSet.findHelpSet(cl, helpHS);
		HelpSet hs = new HelpSet(null, hsURL);
		HelpBroker hb = hs.createHelpBroker();
		hb.initPresentation();
		hb.setDisplayed(true);
	}
	
	public static void main(String args[]) throws Exception
	{
		launchHelp();
	}
}