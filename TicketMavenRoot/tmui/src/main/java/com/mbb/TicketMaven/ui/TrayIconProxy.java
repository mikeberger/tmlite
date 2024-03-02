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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mbb.TicketMaven.ui.options.OptionsView;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;

/**
 * Class JDICTrayIconProxy is the link between TicketMaven and the JDIC system
 * tray icon. As soon as all customers go to Java 6 - we need to replace this
 * with the built-in JRE support for the system tray as JDIC is inconsistent on
 * non-windows systems.
 */
class TrayIconProxy {

	static private TrayIconProxy singleton = null;

	static public TrayIconProxy getReference() {
		if (singleton == null)
			singleton = new TrayIconProxy();
		return (singleton);
	}

	/**
	 * Inits the system tray. Adds the menu items and their callbacks
	 *
	 * @param trayname the trayname
	 */
	public void init(String trayname) throws Exception {

		SystemTray systemTray = SystemTray.get();
		if (systemTray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}

		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resource/tm16.jpg"));

		systemTray.setImage(image);

		systemTray.setTooltip(trayname);

		systemTray.getMenu().add(new MenuItem("Open", new OpenListener()));
		systemTray.getMenu().add(new MenuItem("Options", new OptionsListener()));
		systemTray.getMenu().add(new MenuItem("Exit", new ExitListener()));

	}

	// Called when exit option in systray menu is chosen
	static private class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			UIControl.shutDownUI();
		}
	}

	private class OpenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			UIControl.toFront();
		}
	}

	private class OptionsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			OptionsView mv = OptionsView.getReference();
			mv.setVisible(true);
			mv.toFront();
		}
	}

}
