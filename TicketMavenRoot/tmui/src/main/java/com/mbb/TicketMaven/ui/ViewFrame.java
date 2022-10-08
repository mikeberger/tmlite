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

import com.mbb.TicketMaven.model.Model;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * ViewFrame is the base class for frames that show a view of objects from
 * Model(s) Ad view frame registers with Models and refreshes whenever the Model
 * changes. This class also contains ocmmon code used to store and remember a
 * window's size
 */
public abstract class ViewFrame extends javax.swing.JFrame implements
		Model.Listener {
	private static final long serialVersionUID = 1L;

	private static Image image = Toolkit.getDefaultToolkit().getImage(
			ViewFrame.class.getResource("/resource/tm16.jpg"));

	private PrefName prefName_ = null;

	/**
	 * Initialize.
	 */
	private void initialize() {
		setIconImage(image);
	}

	/**
	 * called on Data Model changes to allow the View to refresh itself as
	 * needed
	 * 
	 */
	@Override
	public abstract void refresh();

	/**
	 * Destroy the View
	 */
	public abstract void destroy();

	/**
	 * Instantiates a new view frame.
	 */
	public ViewFrame() {
		initialize();
	}

	/**
	 * Record the size of the View.
	 * 
	 */
	static private void recordSize(Component c) {
		ViewSize vs = new ViewSize();
		vs.setX(c.getBounds().x);
		vs.setY(c.getBounds().y);
		vs.setWidth(c.getBounds().width);
		vs.setHeight(c.getBounds().height);
		ViewFrame v = (ViewFrame) c;
		vs.setMaximized(v.getExtendedState() == Frame.MAXIMIZED_BOTH);

		Prefs.putPref(v.prefName_, vs.toString());

	}

	// function to call to register a view with the model
	/**
	 * Adds this view as a listener to a model.
	 * 
	 * @param m
	 *            the Model
	 */
	protected void addModel(Model m) {
		m.addListener(this);
	}

	/**
	 * called from the subclass to cause the View to use preferences to persist
	 * a View's size and locaiton if the user resizes it
	 * 
	 * @param pname
	 *            the PrefName used to store this Views size
	 */
	public void manageMySize(PrefName pname) {
		prefName_ = pname;

		// set the initial size
		String s = Prefs.getPref(prefName_);
		ViewSize vs = ViewSize.fromString(s);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// get dimensions from pref or use defaults
		int x = (vs.getX() != -1) ? vs.getX() : 0;
		int y = (vs.getY() != -1) ? vs.getY() : 0;
		int w = (vs.getWidth() != -1) ? vs.getWidth() : 800;
		int h = (vs.getHeight() != -1) ? vs.getHeight() : 600;

		// move window onscreen if needed
		if (x < 0 || x > screenSize.width)
			x = 0;
		if (y < 0 || y > screenSize.width)
			y = 0;

		setBounds(new Rectangle(x, y, w, h));

		if (vs.isMaximized()) {
			setExtendedState(Frame.MAXIMIZED_BOTH);
		}


		validate();

		// add listeners to record any changes
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				recordSize(e.getComponent());
			}

			@Override
			public void componentMoved(java.awt.event.ComponentEvent e) {
				recordSize(e.getComponent());
			}
		});
	}

	protected void setDismissButton(final JButton bn) {
		getLayeredPane().registerKeyboardAction(new ActionListener() {
			@Override
			public final void actionPerformed(ActionEvent e) {
				bn.getActionListeners()[0].actionPerformed(e);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
}
