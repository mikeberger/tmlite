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

import com.mbb.TicketMaven.util.ErrorHandler;
import com.mbb.TicketMaven.util.Warning;

import java.util.logging.Logger;

/**
 * UI error handling 
 */

public class UIErrorHandler implements ErrorHandler {

	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	/**
	 * Output an exception to the user.
	 * 
	 * @param e
	 *            the e
	 */
	@Override
	public void errmsg(Throwable e) {

		// treat a warning differently - just show its text
		if (e instanceof Warning) {
			notice(e.getMessage());
			return;
		}

		log.severe(e.toString());
		e.printStackTrace();
		ScrolledDialog.showError(e);
		
	}

	/**
	 * output a notice/warning - just shows text
	 * 
	 * @param s
	 *            the text to show
	 */
	@Override
	public void notice(String s) {

		log.info(s);
		ScrolledDialog.showNotice(s);
	}

}
