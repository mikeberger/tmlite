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


import com.mbb.TicketMaven.ui.module.Module;
import com.mbb.TicketMaven.ui.module.*;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;
import com.mbb.TicketMaven.util.Version;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

/**
 * MainView is the Main TicketMaven Window
 */
public class MainView extends ViewFrame implements Prefs.Listener {

	private static final long serialVersionUID = 1L;

	private static MainView singleton = null;
	
	private List<Module> modules = new ArrayList<Module>();

	/**
	 * Gets the singleton. There can be only one.
	 * 
	 * @return the singleton
	 */
	public static MainView getReference() {
		if (singleton == null)
			singleton = new MainView();
		return (singleton);
	}

	// top level set of tabs
	private JTabbedPane jTabbedPane = null;

	// flag to indicate if the tray icon was created
	private boolean trayIcon;

	/**
	 * Instantiates a new main view.
	 */
	private MainView() {
		super();
		Prefs.addListener(this); // this window reacts to Preference changes
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				//
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				//
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				destroy(); 				
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// 			
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// 			
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// 			
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				//
			}
			
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.ui.ViewFrame#destroy()
	 */
	@Override
	public void destroy() {
		if (!trayIcon || !Prefs.is(PrefName.SYSTRAY, "true"))
			UIControl.shutDownUI(); // if we close this window and there is no
		// tray icon - shutdown the program
		this.dispose(); // otherwise, leave the program running with only the
		// tray icon showing
	}

	public Module getModule(Class<? extends Module> clazz)
	{
		for( Module m : modules )
			if( clazz.isInstance(m))
				return m;
				
		return null;
	}
	
	private void addModule(Module m) {
		modules.add(m);
		jTabbedPane.addTab(m.getModuleName(), m.getIcon(), m.getComponent());
	}

	/**
	 * Gets the main tabbed pane.
	 * 
	 * @return the main tabbed pane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			setTabSide(); // set where the tabs go - top or side

			/*
			 * show manager
			 */
			addModule(new ShowManager());

			/*
			 * Package Manager
			 */
			addModule(new PackageManager());

			/*
			 * Customer Manager
			 */
			addModule(new CustomerManager());

			/*
			 * the Theater Layout Manager 
			 */
			addModule(new TheaterLayout());

			/**
			 * Request Manager
			 */
			addModule(new RequestManager());

			/**
			 * Ticket Manager
			 */
			addModule(new AssignedTickets());
			
			/**
			 * the reservation manager
			 */
			addModule( new Reservations());
			
			/**
			 * the lottery manager UI
			 */
			addModule( new LotteryPanel());
			
			

		}
		return jTabbedPane;
	}

	private void initialize() {
		this.setTitle("Ticket Maven " + Version.getVersion());
		this.setSize(new java.awt.Dimension(524, 475));

		this.setContentPane(getJTabbedPane()); 
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 
		this.setJMenuBar(new MainMenu());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.ui.ViewFrame#refresh()
	 */
	@Override
	public void refresh() {
		// empty
	}

	/**
	 * Fire up the main TM UI
	 * 
	 * @param trayicon
	 *            if true, there is a system tray running
	 */
	public void startApp(boolean trayicon) {
		trayIcon = trayicon;
		initialize(); // init the GUI widgets

		// display the window
		pack();

		manageMySize(PrefName.MAINVIEWSIZE);
		setVisible(true);
	}

	/**
	 * Sets where to put the top level tabs - side or top
	 */
	private void setTabSide() {

		String side = Prefs.getPref(PrefName.TABSIDE);
		if (side.equals("Left"))
			jTabbedPane.setTabPlacement(SwingConstants.LEFT);
		else
			jTabbedPane.setTabPlacement(SwingConstants.TOP);
		jTabbedPane.revalidate();
		jTabbedPane.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.util.Prefs.Listener#prefsChanged()
	 */
	@Override
	public void prefsChanged() {
		// the only thing affected by prefs changing is the tab position at the
		// moment
		setTabSide();

	}

}
