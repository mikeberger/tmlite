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

import com.mbb.TicketMaven.model.jdbc.DumpJdbcDB;
import com.mbb.TicketMaven.ui.util.FontChooser;
import com.mbb.TicketMaven.ui.util.SplashScreen;
import com.mbb.TicketMaven.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class UIControl provides access to the UI from non-UI classes. UIControl
 * provides the main UI entry point.
 *
 *
 */
class UIControl {

	private static Observer shutdownListener = null;

	/**
	 * set a shutdown listener to be called back when the UI shuts down
	 *
	 * @param shutdownListener
	 */
	public static void setShutdownListener(Observer shutdownListener) {
		UIControl.shutdownListener = shutdownListener;
	}

	/**
	 * splash screen
	 */
	private static SplashScreen splashScreen = null;

	/**
	 * flag to indicate if tray icon was started successfully
	 */
	private static boolean trayIcon = false;

	/**
	 * Main UI initialization.
	 *
	 * @param trayname
	 *            - name for the tray icon
	 */
	public static void startUI(String trayname) {

		// Errmsg.setErrorHandler(new UIErrorHandler());

		// set the default program font
		String deffont = Prefs.getPref(PrefName.DEFFONT);
		if (!deffont.equals("")) {
			Font f = Font.decode(deffont);
			FontChooser.setDefaultFont(f);
		}

		// set the look and feel
		String lnf = Prefs.getPref(PrefName.LNF);
		try {

			// set default jgoodies theme
			if (lnf.contains("jgoodies")) {
				String theme = System.getProperty("Plastic.defaultTheme");
				if (theme == null) {
					System.setProperty("Plastic.defaultTheme", "ExperienceBlue");
				}
			}

			UIManager.setLookAndFeel(lnf);
			UIManager.getLookAndFeelDefaults().put("ClassLoader", UIControl.class.getClassLoader());
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		// set the locale from preferences
		String country = Prefs.getPref(PrefName.COUNTRY);
		String language = Prefs.getPref(PrefName.LANGUAGE);

		if (!language.equals("")) {
			Locale.setDefault(new Locale(language, country));
		}

		// make all tooltips stay around a long time
		ToolTipManager.sharedInstance().setDismissDelay(1000 * 60 * 60);

		// pop up the splash
		splashScreen = new SplashScreen();
		splashScreen.setText("Initializing...");
		splashScreen.setVisible(true);
		final String tn = trayname;

		/*
		 * in order for the splash to be seen, we will complete initialization later (in
		 * the swing thread).
		 */
		Timer t = new Timer(3000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				completeUIInitialization(tn);
			}
		});
		t.setRepeats(false);
		t.start();

	}

	/**
	 * complete the parts of the UI initialization that run after the splash screen
	 * has shown for a while
	 *
	 * @param trayname
	 *            name for the tray icon
	 */
	private static void completeUIInitialization(String trayname) {

		try {
			TrayIconProxy tip = TrayIconProxy.getReference();
			tip.init(trayname);
			trayIcon = true;

		} catch (UnsatisfiedLinkError le) {
			Errmsg.getErrorHandler().errmsg(new Exception(le));

		} catch (NoClassDefFoundError ncf) {
			Errmsg.getErrorHandler().errmsg(new Exception(ncf));

		} catch (Warning w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
			;
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			System.exit(0);
		}

		// start the root UI Frame
		MainView.getReference().startApp(trayIcon);

		// destroy the splash screen
		if (splashScreen != null) {
			splashScreen.dispose();
			splashScreen = null;
		}
	}

	/**
	 * raise the UI to the front
	 */
	public static void toFront() {
		MainView mv = MainView.getReference();
		if (!mv.isShowing())
			mv.setVisible(true);
		mv.toFront();
		mv.setState(Frame.NORMAL);
	}

	/**
	 * shuts down the UI, including db backup
	 */
	public static void shutDownUI() {

		// show a splash screen for shutdown
		try {
			SplashScreen ban = new SplashScreen();
			ban.setText("Shutting Down, Please wait....");
			ban.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// automatically backup data if that feature is set
		String backupdir = Prefs.getPref(PrefName.BACKUPDIR);
		if (backupdir != null && !backupdir.equals("")) {
			try {

				int ret = JOptionPane.showConfirmDialog(null, "Write backup data to " + backupdir + "?", "TicketMaven",
						JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.YES_OPTION) {

					// backup by dumping SQL to a timestamped ZIP file
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String uniq = sdf.format(new Date());
					ZipOutputStream out = new ZipOutputStream(
							new FileOutputStream(backupdir + "/tm_backup_" + uniq + ".zip"));
					try (Writer fw = new OutputStreamWriter(out, "UTF8")) {

						out.putNextEntry(new ZipEntry("data.sql"));
						String sql = DumpJdbcDB.dumpData();
						fw.write(sql);
						fw.flush();
						out.closeEntry();

						out.close();
					}
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}

		}
		else {
			JOptionPane.showMessageDialog(null,
				    "Automatic backup is not enabled. It is strongly recommended that it be enabled.",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
		}

		// non-UI shutdown
		if (shutdownListener != null)
			shutdownListener.update(null, null);

	}

}
