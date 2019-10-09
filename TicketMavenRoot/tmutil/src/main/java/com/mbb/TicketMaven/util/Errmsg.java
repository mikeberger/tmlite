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


/**
 * standard error handling 
 */
public class Errmsg {

	/**
	 * console error handler
	 *
	 */
	private static class DefaultErrorHandler implements ErrorHandler {
		
		@Override
		public void errmsg(Throwable e) {

			// treat a warning differently - just show its text
			if (e instanceof Warning) {
				notice(e.getMessage());
				return;
			}

			System.out.println(e.toString());
			e.printStackTrace();

		}

		@Override
		public void notice(String s) {

			System.out.println(s);
			return;

		}
	}

	// initialize to only send errors to the console
	private static ErrorHandler errorHandler = new DefaultErrorHandler();

	public static ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public static void setErrorHandler(ErrorHandler errorHandler) {
		Errmsg.errorHandler = errorHandler;
	}

}
