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

package com.mbb.TicketMaven.ui.module;

import javax.swing.*;
import java.awt.*;

/**
 * interface implemented by all UI Modules. The MainView manages a set of UI
 * Modules. Each Module is responsible for providing a component to show in
 * a multiview tab, responding to print requests, and requesting its own
 * toolbar and menu items
 */
public interface Module {

	/**
	 * get the module's name.
	 * 
	 * @return the name
	 */
	public String getModuleName();

	/**
	 * get the Component for this Module.
	 * 
	 * @return the Component or null if none to show
	 */
	public Component getComponent();

	/**
	 * get the Icon for the module.
	 * 
	 * @return the icon
	 */
	public Icon getIcon();


}