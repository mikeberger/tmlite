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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Class ColorChooserButton - a button that is used to popup a color chooser
 * dialog and then sets itself to that color
 */
public class ColorChooserButton extends JButton {
	private static final long serialVersionUID = 1L;
	/** The color property - can be foreground or background */
	private Color colorProperty;
	/**
	 * bg=true means "choosed color is background color" bg=false means
	 * "choosed color is foreground color"
	 */
	private boolean bg;

	/**
	 * Instantiates a new color chooser button.
	 * 
	 * @param p_text
	 *            the button label
	 * @param p_color
	 *            the initial color
	 * @param p_bg
	 *            background flag - if true, choose background color, otherwise foreground
	 */
	public ColorChooserButton(String p_text, Color p_color, boolean p_bg) {
		setText(p_text);
		setBg(p_bg);
		setColorProperty(p_color);

		setColorByProperty();
		super.addActionListener(new ModalListener());

	}

	/**
	 * Sets the color to the color set in the instance
	 */
	public void setColorByProperty() {
		if (isBg()) {
			setBackground(getColorProperty());
		} else {
			setForeground(getColorProperty());
		}
	}

	/**
	 * Gets the color
	 * 
	 * @return Returns the color.
	 */
	public Color getColorProperty() {
		return colorProperty;
	}

	/**
	 * Sets the color property.
	 * 
	 * @param color
	 *            The color to set.
	 */
	public void setColorProperty(Color color) {
		this.colorProperty = color;
		setColorByProperty();
	}

	/**
	 * 
	 * @return Returns the background flag 
	 */
	protected boolean isBg() {
		return bg;
	}

	/**
	 * Sets the background flag.
	 * 
	 * @param bg
	 *            The bg to set.
	 */
	protected void setBg(boolean bg) {
		this.bg = bg;
	}

	/**
	 * button Listener that pops up the color chooser and sets the color after the user chooses one
	 * 
	 *
	 */
	private class ModalListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			Color selected = JColorChooser.showDialog(null,
					isBg() ? "Set background" : "Set foreground",
					getColorProperty());
			if (selected != null)
				setColorProperty(selected);

			// call secondary action listener
			if (myActionListener != null)
				myActionListener.actionPerformed(event);
		}
	}

	/** The action listener. */
	private ActionListener myActionListener = null;

	
	@Override
	public void addActionListener(ActionListener l) {
		this.myActionListener = l;
	}

}
