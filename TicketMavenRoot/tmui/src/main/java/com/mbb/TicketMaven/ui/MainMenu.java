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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.PackageModel;
import com.mbb.TicketMaven.model.ReservationModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TableModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.TicketRequestModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.model.jdbc.DumpJdbcDB;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.ui.options.OptionsView;
import com.mbb.TicketMaven.ui.report.ReportMenu;
import com.mbb.TicketMaven.ui.ticketprint.TicketMenu;
import com.mbb.TicketMaven.ui.util.ConfirmDialog;
import com.mbb.TicketMaven.ui.util.IntSelector;
import com.mbb.TicketMaven.ui.util.LongRunningModalTask;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.FileIO;
import com.mbb.TicketMaven.util.Version;

/**
 * The MainMenu class provides the main application menu bar
 */
class MainMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new main menu.
	 */
	public MainMenu() {

		// File Menu
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		fileMenu.setIcon(new ImageIcon(getClass().getResource("/resource/tm16.jpg")));

		// File/Options Menu
		JMenuItem optMI = new JMenuItem();
		optMI.setText("Options");
		optMI.setIcon(new ImageIcon(getClass().getResource("/resource/Preferences16.gif")));
		optMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				OptionsView.getReference().setVisible(true);
			}
		});

		fileMenu.add(optMI);

		// File/Exit Menu
		JMenuItem exitMenuItem = new JMenuItem();
		exitMenuItem.setText("Exit");
		exitMenuItem.setIcon(new ImageIcon(getClass().getResource("/resource/Stop16.gif")));
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				UIControl.shutDownUI();
			}
		});

		this.add(fileMenu);

		// add Admin Menu
		this.add(getAdminMenu());

		// add Report Menu
		this.add(new ReportMenu());

		// add Ticket Menu
		this.add(new TicketMenu());

		// add space so next menu goes on the right
		this.add(Box.createHorizontalGlue());

		// Help Menu
		JMenu helpmenu = new JMenu();
		JMenuItem helpMI = new JMenuItem();
		helpmenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/Help16.gif")));
		helpmenu.setText("Help");
		helpMI.setText("Help");
		helpMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				helpMIActionPerformed(evt);
			}
		});

		helpmenu.add(helpMI);

		// Help/License Menu
		JMenuItem licsend = new JMenuItem();
		licsend.setText("License");
		licsend.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				licsendActionPerformed(evt);
			}
		});

		helpmenu.add(licsend);

//		// Help/License Mgmt
//		JMenuItem lkMI = new JMenuItem();
//		lkMI.setText("License Key Management");
//		lkMI.addActionListener(new java.awt.event.ActionListener() {
//			@Override
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				//LicenseDialog.showDialog();
//			}
//		});
//
//		helpmenu.add(lkMI);

		JMenuItem logMI = new JMenuItem();
		logMI.setText("Show Log");
		logMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				String home = System.getProperty("user.home", "");

				new FileView(home + "/.tm_log").setVisible(true);
			}
		});

		helpmenu.add(logMI);

		// Help/About
		JMenuItem AboutMI = new JMenuItem();
		AboutMI.setText("About TicketMaven");
		AboutMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AboutMIActionPerformed(evt);
			}
		});

		helpmenu.add(AboutMI);

		this.add(helpmenu);

		fileMenu.add(exitMenuItem);
	}

	/**
	 * About action performed - pops up a window displaying the program version and
	 * other useful info
	 *
	 */
	private void AboutMIActionPerformed(java.awt.event.ActionEvent evt) {

		String build = "";
		String build_time = "";
		try {
			// get the version and build info from a properties file in the
			// jar
			// file
			InputStream is = getClass().getResource("/resource/properties").openStream();
			Properties props = new Properties();
			props.load(is);
			is.close();
			build = props.getProperty("build.number");
			build_time = props.getProperty("build.time");

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

		String version = Version.getVersion();

//		License lic;
//		try {
//			lic = LicenseLogic.getLicense();
//			String who;
//			if (lic.isDemo()) {
//				who = "This is an Unlicensed Demo Version";
//			} else {
//				who = "This software is licensed to " + lic.getCompanyName() + "<" + lic.getEmailAddress() + ">";
//			}
//
			Errmsg.getErrorHandler()
					.notice("TicketMaven (Lite) Version " + version + "\n" 
							+ "\n\nAuthor: Michael Berger<ticketmaven@mbcsoft.com>"
							+ "\n\n" + "Java " + System.getProperty("java.version") + "\nBuild Number: " + build
							+ "\nBuild Time: " + build_time);
		
	}

	/**
	 * Pop up a dialog to allo the user to Bulk change the number of allowed tickets
	 * for all customers.
	 */
	private void bulkChgCustTickets() {
		Integer t = IntSelector.selectInt(null, "Select the number of allowed tickets:");
		if (t == null)
			return;
		int ok = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to change the number of allowed tickets for all customers to " + t.intValue()
						+ "?",
				"Confirm", JOptionPane.YES_NO_OPTION);

		if (ok == JOptionPane.NO_OPTION)
			return;
		try {
			CustomerModel.getReference().bulkChgAllowedTickets(t.intValue());
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * Pop up a dialog to allow the user erase quality history for all customers
	 */
	private void eraseQualityHistory() {

		int ok = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to erase the quality history for all customers?", "Confirm",
				JOptionPane.YES_NO_OPTION);

		if (ok == JOptionPane.NO_OPTION)
			return;
		
		
		try {
			boolean futureTickets = false;
			Collection<Show> futureShows = new ShowFilter(LayoutModel.AUDITORIUM, true).getMatchingEntities();
			for( Show s : futureShows )
			{
				List<Ticket> tkts = TicketModel.getReference().getTicketsForShow(s.getKey());
				if( !tkts.isEmpty())
				{
					futureTickets = true;
					break;
				}
			}
			
			if( futureTickets ) {
				ok = JOptionPane.showConfirmDialog(this,
						"There are tickets assigned for future shows. Erasing Quality totals is probably a mistake, continue anyway?", "Confirm",
						JOptionPane.YES_NO_OPTION);

				if (ok == JOptionPane.NO_OPTION)
					return;
			}
			
			CustomerModel.getReference().eraseAllQualityValues();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * adjust the ticket quality values for each customer based on the existing
	 * tickets
	 */
	private void adjustQuality() {
		int ok = JOptionPane.showConfirmDialog(this,
				"This option will recalculate the ticket quality history for all customers based on tickets currently in the database. Do you want to proceed?",
				"Confirm", JOptionPane.YES_NO_OPTION);

		if (ok == JOptionPane.NO_OPTION)
			return;

		// start a long-running task - it may take a minute or so
		new LongRunningModalTask() {

			@Override
			public String runTask() {
				try {
					CustomerModel.getReference().setNotifyListeners(false);
					CustomerModel.getReference().adjustQualityValues();
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				} finally {
					CustomerModel.getReference().setNotifyListeners(true);
				}
				return null;
			}

		}.start();

	}

	/**
	 * Export all data to a file containing SQL
	 */
	private void exportData() {
		String sql = DumpJdbcDB.dumpData();
		if (sql != null) {
			try {
				FileIO.fileSave(System.getProperty("user.home"), new StringReader(sql));
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
	}

	/**
	 * Gets the admin menu.
	 *
	 * @return the admin menu
	 */
	public JMenu getAdminMenu() {
		JMenu menu = new JMenu();
		menu.setText("Admin");
		menu.setIcon(new ImageIcon(getClass().getResource("/resource/Application16.gif")));

		// Import
		JMenuItem impMI = new JMenuItem();
		impMI.setText("Import Data");
		impMI.setIcon(new ImageIcon(getClass().getResource("/resource/Import16.gif")));
		impMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				importData();
			}
		});
		menu.add(impMI);

		// Export
		JMenuItem expMI = new JMenuItem();
		expMI.setText("Export Data");
		expMI.setIcon(new ImageIcon(getClass().getResource("/resource/Export16.gif")));
		expMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportData();
			}
		});
		menu.add(expMI);

		// Import from CSV
		JMenuItem icMI = new JMenuItem();
		icMI.setText("Import Customer List from CSV Data");
		icMI.setIcon(new ImageIcon(getClass().getResource("/resource/Export16.gif")));
		icMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				importCSVData();
			}
		});
		menu.add(icMI);

		// bulk change allowed tickets
		JMenuItem cust_tickMI = new JMenuItem();
		cust_tickMI.setText("Change Allowed Tickets for All Customers");
		cust_tickMI.setIcon(new ImageIcon(getClass().getResource("/resource/Refresh16.gif")));
		cust_tickMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				bulkChgCustTickets();
			}
		});
		menu.add(cust_tickMI);

		JMenuItem adjustTotalsMI = new JMenuItem();
		adjustTotalsMI.setText("Recalculate Quality Totals for All Customers");
		adjustTotalsMI.setIcon(new ImageIcon(getClass().getResource("/resource/Refresh16.gif")));
		adjustTotalsMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				adjustQuality();
			}
		});
		menu.add(adjustTotalsMI);

		JMenuItem eraseTotalsMI = new JMenuItem();
		eraseTotalsMI.setText("Erase Quality Totals for All Customers");
		eraseTotalsMI.setIcon(new ImageIcon(getClass().getResource("/resource/Delete16.gif")));
		eraseTotalsMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				eraseQualityHistory();
			}
		});
		menu.add(eraseTotalsMI);

		JMenuItem deleteMultipleShows = new JMenuItem();
		deleteMultipleShows.setText("Delete Multiple Shows");
		deleteMultipleShows.setIcon(new ImageIcon(getClass().getResource("/resource/Delete16.gif")));
		deleteMultipleShows.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				Collection<Show> coll = BeanSelector.selectBeans(ShowModel.getReference(),
						new TableSorter(new String[] { "Show Name", "Show Date/Time" },
								new Class[] { java.lang.String.class, java.util.Date.class }),
						new String[] { "Name", "DateTime" }, new ShowFilter(null, false));
				if (coll == null || coll.isEmpty())
					return;

				int ret = JOptionPane.showConfirmDialog(null,
						"Really Delete all of the selected shows? This cannot be undone.", "Delete Shows",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ret != JOptionPane.OK_OPTION)
					return;

				try {
					for (Show s : coll)
						ShowModel.getReference().cascadeDelete(s);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}

			}
		});
		menu.add(deleteMultipleShows);

		// show DB info
		JMenuItem dbinfoMI = new JMenuItem();
		dbinfoMI.setText("Show Database Information");
		dbinfoMI.setIcon(new ImageIcon(getClass().getResource("/resource/Refresh16.gif")));
		dbinfoMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {

					// show misc db info
					String info = "Database URL: " + JdbcDB.getDbUrl() + "\nNumber of Customers = "
							+ CustomerModel.getReference().numRows() + "\nNumber of Shows = "
							+ ShowModel.getReference().numRows() + "\nNumber of Layouts = "
							+ LayoutModel.getReference().numRows() + "\nNumber of Zones = "
							+ ZoneModel.getReference().numRows() + "\nNumber of Seats = "
							+ SeatModel.getReference().numRows() + "\nNumber of Ticket Requests = "
							+ TicketRequestModel.getReference().numRows() + "\nNumber of Tickets = "
							+ TicketModel.getReference().numRows() + "\nNumber of Reservations = "
							+ ReservationModel.getReference().numRows() + "\nNumber of Tables = "
							+ TableModel.getReference().numRows() + "\nNumber of Packages = "
							+ PackageModel.getReference().numRows();
					Errmsg.getErrorHandler().notice(info);
				} catch (Exception ex) {
					Errmsg.getErrorHandler().errmsg(ex);
				}
			}
		});
		menu.add(dbinfoMI);

		// Run SQL
		JMenuItem runsqlMI = new JMenuItem();
		runsqlMI.setText("Run SQL");
		runsqlMI.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// open an SQL dialog
				new SqlRunner().setVisible(true);
			}
		});
		menu.add(runsqlMI);

	
		return menu;
	}

	/**
	 * Help mi action performed.
	 *
	 * @param evt
	 *            the evt
	 */
	private void helpMIActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			// just launch the Help
			HelpProxy.launchHelp();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * Import csv customer data.
	 */
	private void importCSVData() {

		try {

			InputStream is = FileIO.fileOpen(".", "Select Import File");
			if (is == null)
				return;
			InputStreamReader r = new InputStreamReader(is);
			CustomerModel.getReference().importCSV(r);

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

	}

	private static boolean isDbEmpty() throws Exception {

		if (CustomerModel.getReference().numRows() > 0 || ShowModel.getReference().numRows() > 0
				|| LayoutModel.getReference().numRows() > 0 || ZoneModel.getReference().numRows() > 0)
			return false;
		return true;

	}

	private static void importFromStream(InputStream is) {
		try (InputStreamReader r = new InputStreamReader(is)) {


			JdbcDB.startTransaction();
			JdbcDB.executeMultiSQL(new BufferedReader(r));
			JdbcDB.commitTransaction();

		} catch (Exception e) {
			System.out.println(e.toString());
			Errmsg.getErrorHandler().errmsg(e);
			try {
				JdbcDB.rollbackTransaction();
			} catch (Exception e2) {
				// empty
			}
		}
	}

	/**
	 * Import data. This method actually jsut runs SQL from a file. SQL is the
	 * import and export format.
	 */
	private void importData() {

		try {
			if (!isDbEmpty()) {
				int ret = ConfirmDialog.showNotice(
						"WARNING: Database is not empty. Import may corrupt the data!!\n\nPress Cancel to abort import. \n\nPress OK to ignore this warning and proceed.");
				if (ret != ConfirmDialog.OK)
					return;
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		try {
			String s = FileIO.chooseFile(".", "Select Import File");
			if (s.toLowerCase().endsWith(".zip")) {

				try (ZipInputStream in = new ZipInputStream(new FileInputStream(s))) {

					for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {

						if (entry.getName().equals("data.sql")) {
							importFromStream(in);
							break;
						}
					}
				}

			} else {
				try (InputStream is = new FileInputStream(s)) {
					importFromStream(is);
				}
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		TicketModel.getReference().setNotifyListeners(true);
		LayoutModel.getReference().setNotifyListeners(true);
		ZoneModel.getReference().setNotifyListeners(true);
		TicketRequestModel.getReference().setNotifyListeners(true);
		CustomerModel.getReference().setNotifyListeners(true);
		ShowModel.getReference().setNotifyListeners(true);
		SeatModel.getReference().setNotifyListeners(true);
		ReservationModel.getReference().setNotifyListeners(true);
		TableModel.getReference().setNotifyListeners(true);
		PackageModel.getReference().setNotifyListeners(true);
	}

	/**
	 * Show the license HTML
	 *
	 * @param evt
	 *            the evt
	 */
	private void licsendActionPerformed(java.awt.event.ActionEvent evt) {

		new HtmlView("/resource/LICENSE.txt").setVisible(true);
//		License lic;
//		try {
//			lic = LicenseLogic.getLicense();
//			if (lic.isDemo())
//				new HtmlView("/resource/TM_DEMO_LICENSE.html").setVisible(true);
//			else
//				
//		} catch (Exception e) {
//			Errmsg.getErrorHandler().errmsg(e);
//		}

	}


}
