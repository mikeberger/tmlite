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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.ui.options.DbChooser;
import com.mbb.TicketMaven.ui.util.UIErrorHandler;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.FileIO;
import com.mbb.TicketMaven.util.Observable;
import com.mbb.TicketMaven.util.Observer;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;
import com.mbb.TicketMaven.util.SocketClient;
import com.mbb.TicketMaven.util.SocketHandler;
import com.mbb.TicketMaven.util.SocketServer;

/**
 * The Main Application Startup and Shutdown Class. This class initializes the
 * application and its Threads
 */
public class TicketMaven implements SocketHandler, Observer {

	private SocketServer socketServer_ = null; // listens on a socket for window
	static private final Logger log = Logger.getLogger("com.mbb.TicketMaven");

	/**
	 * The main method.
	 *
	 * @param args
	 *            the command arguments (unused)
	 */
	public static void main(String args[]) {
		
		// stop hsqldb from messing up the log config
		System.setProperty("hsqldb.reconfig_logging", "false");

		// on start up open any running instance if there is one instead of
		// starting a new instance
		int port = Prefs.getIntPref(PrefName.SOCKETPORT);
		if (port != -1) {
			String resp;
			try {
				// send an open message to any running instance of ticket maven
				resp = SocketClient.sendMsg("localhost", port, "open");
				if (resp != null && resp.equals("ok")) {

					// exit if we were able to open a running copy of the
					// program
					System.exit(0);
				}
			} catch (IOException e) {
				// empty
			}

		}
		// create a new tm object and call its init routing with the command
		// line args
		TicketMaven tm = new TicketMaven();
		tm.init(args);
	}

	/**
	 * Instantiates a new ticket maven.
	 */
	private TicketMaven() {
		// empty
	}

	/**
	 * init will process the command line args, open and load the database, and
	 * start up the main view
	 *
	 * @param args
	 *            the command line args
	 */
	private void init(String args[]) {

		ConsoleHandler chandler = new ConsoleHandler();
		chandler.setLevel(Level.ALL);
		log.addHandler(chandler);
		log.setUseParentHandlers(false);

		boolean debug = Prefs.getBoolPref(PrefName.DEBUG);
		if (debug == true)
			log.setLevel(Level.ALL);
		else
			log.setLevel(Level.INFO);

		log.fine("Debug logging turned on");
		// process command line args
		boolean testing = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-db")) {
				i++;
				if (i >= args.length) {
					System.out.println("-db_argument_is_missing");
					System.exit(1);
				}
				JdbcDB.setDbUrl(args[i]);
			} else if (args[i].equals("-test")) {
				testing = true;

			}

		}
		if (testing == false) {
			try {
				// redirect stdout and stderr to files
				String home = System.getProperty("user.home", "");
				FileOutputStream outStr = new FileOutputStream(home + "/.tm_out", false);
				PrintStream printStream = new PrintStream(outStr);
				System.setOut(printStream);
				System.setErr(printStream);

				FileHandler fh = new FileHandler("%h/.tm_log");
				fh.setFormatter(new SimpleFormatter());
				fh.setLevel(chandler.getLevel());
				log.removeHandler(chandler);
				log.addHandler(fh);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Errmsg.setErrorHandler(new UIErrorHandler());


		// try to set a valid db folder
		String home = System.getProperty("user.home", "");

		String dbdir = Prefs.getPref(PrefName.DBDIR);
		if (dbdir.isEmpty()) {
			Prefs.putPref(PrefName.DBDIR, home + "/tm_database");
		}

		if (Prefs.getBoolPref(PrefName.DBPROMPT) == true) {
			DbChooser.create();
		}

		dbConnect();

		final String traynm = "TicketMaven";

		UIControl.setShutdownListener(this);

		// start the Swing UI in the swing event handling thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UIControl.startUI(traynm);
			}
		});

		// start a socket listener to listen for any messages
		int port = Prefs.getIntPref(PrefName.SOCKETPORT);
		if (port != -1 && socketServer_ == null) {
			socketServer_ = new SocketServer(port, this);
		}

	}

	public static void dbConnect() {
		try {
			// try to connect to the database, fail if doesn't exist
			JdbcDB.connect(false);
			JdbcDB.execSQL("SELECT * FROM LAYOUTS");

		} catch (SQLException se) {

			// this specific code means the database does not exist
			if (se.getSQLState().equals("08003") || se.getSQLState().equals("S0002") || se.getMessage().contains("not exist")) {
				// need to create the db
				try (InputStream is = TicketMaven.class.getResourceAsStream("/schema/create_hsql.sql");
						InputStreamReader r = new InputStreamReader(is)) {
					// create an hsql database from SQL
					System.out.println("Creating Database");

					JdbcDB.connect(true); // create empty db
					JdbcDB.executeMultiSQL(new BufferedReader(r)); // create the tables
				} catch (Exception e2) {
					Errmsg.getErrorHandler().errmsg(e2);
					handleDbError();
					System.exit(1);
				}
			} else if (se.getMessage().indexOf("locked") != -1) {
				Errmsg.getErrorHandler().notice(
						"The database appears to be in use by another running instance of this software.\nPlease make sure that you only run one instance of the program at a time.");
				System.exit(1);
			} else {
				Errmsg.getErrorHandler().errmsg(se);
				System.err.println(se.getSQLState());
				handleDbError();
				System.exit(1);
			}
		} catch (Exception e1) {
			Errmsg.getErrorHandler().errmsg(e1);
			handleDbError();
			System.exit(1);
		}
	}

	/*
	 * process an incoming socket message. Currently, this is just for the window
	 * open message that we get when the user tries to start a second instance of
	 * the program
	 */
	@Override
	public String processMessage(String msg) {
		if (msg.equals("shutdown")) {
			System.exit(0);
		} else if (msg.equals("open")) {
			// open the main window
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UIControl.toFront();
				}
			});
			return ("ok");
		}

		return ("Unknown msg: " + msg);

	}

	/**
	 * Handle a db error.
	 */
	private static void handleDbError() {
		File ddd = new File(Prefs.getPref(PrefName.DBDIR));
		if (!FileIO.canWriteDir(ddd)) {
			int ret = JOptionPane.showConfirmDialog(null,
					"Cannot open db folder: " + Prefs.getPref(PrefName.DBDIR)
							+ ". Would you like to select a new folder?",
					"DB Folder Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				while (true) {
					JFileChooser chooser = new JFileChooser();

					chooser.setCurrentDirectory(new File("."));
					chooser.setDialogTitle("Please choose your database folder");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					int returnVal = chooser.showOpenDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION)
						return;

					String folder = chooser.getSelectedFile().getAbsolutePath();
					File lf = new File(folder);
					String err = null;
					if (!lf.exists()) {
						err = "Folder " + folder + " does not exist";
					} else if (!FileIO.canWriteDir(lf)) {
						err = "Cannot write in folder " + folder;
					}

					if (err == null) {
						Prefs.putPref(PrefName.DBDIR, folder);
						break;
					}

					Errmsg.getErrorHandler().notice(err);
				}
			}
		}
	}

	/**
	 * do a controlled shutdown. HSQL is pretty indestructible even if you crash and
	 * don't get to this method.
	 */
	static public void shutdown() {

		// close the db
		try {
			JdbcDB.cleanup();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// wait a few secs before shutdown to let the dust settle - probably not
		// needed
		Timer shutdownTimer = new java.util.Timer();
		shutdownTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.exit(0);
			}
		}, 3 * 1000, 28 * 60 * 1000);

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		TicketMaven.shutdown();
	}
}