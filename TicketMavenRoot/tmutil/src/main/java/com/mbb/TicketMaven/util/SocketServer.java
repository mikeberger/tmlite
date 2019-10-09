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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A Socket Listener Thread
 */
public class SocketServer extends Thread {
	protected ServerSocket listen_socket;
	private SocketHandler handler_;
	
	// This class is the thread that handles all communication with a client
	static private class Connection extends Thread {
		protected Socket client;
		protected BufferedReader in;
		protected PrintStream out;
		private SocketHandler handler_;

		// Initialize the streams and start the thread
		public Connection(Socket client_socket, SocketHandler handler) {
			client = client_socket;
			handler_ = handler;
			try {
				in = new BufferedReader(new InputStreamReader(client
						.getInputStream()));
				out = new PrintStream(client.getOutputStream());
			} catch (IOException e) {
				try {
					client.close();
				} catch (IOException e2) {
					// empty
				}
				System.err.println("Exception while getting socket streams: " + e);
				return;
			}
			this.start();
		}

		// Provide the service.
		// Read a line, call the handler to process it, output the response
		@Override
		public void run() {

			try {
				for (;;) {
					// read in a line
					String line = in.readLine();
					if (line == null)
						break;
					String output = handler_.processMessage(line);
					out.println(output);
				}
			} catch (IOException e) {
				// empty
			} finally {
				try {
					client.close();
				} catch (IOException e2) {
					// empty
				}
			}
		}
	}


	private static void fail(Exception e, String msg) {
		System.err.println(msg + ": " + e);
	}

	/**
	 * Instantiates a new socket server.
	 * 
	 * @param port
	 *            the port to listen on
	 * @param handler
	 *            the handler to send messages to for processing
	 */
	public SocketServer(int port, SocketHandler handler) {
		this.handler_ = handler;
		try {
			listen_socket = new ServerSocket(port);
		} catch (IOException e) {
			fail(e, "Exception creating server socket");
		}
		System.out.println("Server: listening on port " + port);
		this.start();
	}

	/**
	 * The body of the server thread. Loop forever, listening for and accepting
	 * connections from clients. For each connection, create a Connection object
	 * to handle communication through the new Socket.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				Socket client_socket = listen_socket.accept();
				new Connection(client_socket, handler_);
			}
		} catch (IOException e) {
			fail(e, "Exception while listening for connections");
		}
	}

}

