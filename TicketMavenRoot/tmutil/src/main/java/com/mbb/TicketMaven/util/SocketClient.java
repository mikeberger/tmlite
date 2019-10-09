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

import java.io.*;
import java.net.*;

/**
 * generic Socket client for sending text over a socket
 */
public class SocketClient {


	
	/**
	 * Send a text msg to a socket.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param msg
	 *            the msg
	 * 
	 * @return the string
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String sendMsg(String host, int port, String msg)
			throws IOException {
		Socket s = null;
		String line = null;
		try {
			s = new Socket(host, port);
			BufferedReader sin = new BufferedReader(new InputStreamReader(s
					.getInputStream()));
			PrintStream sout = new PrintStream(s.getOutputStream());
			sout.println(msg);
			line = sin.readLine();
			// Check if connection is closed (i.e. for EOF)
			if (line == null) {
				System.out.println("Connection closed by server.");
			}
		} catch (IOException e) {
			if (s != null)
				s.close();
			throw e;
		}
		// Always be sure to close the socket
		finally {
			try {
				if (s != null)
					s.close();
			} catch (IOException e2) {
				// empty
			}
		}

		return line;
	}

	
}
