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

import java.awt.*;

/**
 * A factory for creating GridBagConstraints objects to remove code bloat in the UI code.
 */
public class GridBagConstraintsFactory {

	/** The Constant defaultInsets. */
	static private final Insets defaultInsets = new Insets(4, 4, 4, 4);

	/**
	 * Creates GridBagconstraints.
	 * 
	 * @param x the x
	 * @param y the y
	 * 
	 * @return the grid bag constraints
	 */
	public static GridBagConstraints create(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.insets = defaultInsets;
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}

	/**
	 * Creates GridBagconstraints.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param fill the fill
	 * 
	 * @return the grid bag constraints
	 */
	public static GridBagConstraints create(int x, int y, int fill) {
		GridBagConstraints gbc = create(x, y);
		gbc.fill = fill;
		return gbc;
	}

	/**
	 * Creates GridBagconstraints.
	 * 
	 * @param x the x
	 * @param y the y
	 * @param fill the fill
	 * @param weightx the weightx
	 * @param weighty the weighty
	 * 
	 * @return the grid bag constraints
	 */
	public static GridBagConstraints create(int x, int y, int fill,
			double weightx, double weighty) {
		GridBagConstraints gbc = create(x, y, fill);
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		return gbc;
	}

}
