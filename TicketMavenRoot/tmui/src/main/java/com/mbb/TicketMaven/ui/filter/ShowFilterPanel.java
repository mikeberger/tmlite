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

package com.mbb.TicketMaven.ui.filter;

import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * This class filters shows by name and date
 */
public class ShowFilterPanel extends FilterPanel<Show> {

	
	private static final long serialVersionUID = 1L;

	private JCheckBox oldShows = null;

	private JButton showButton = null;

	private JTextField showtext = null;

	/**
	 * Instantiates a new show filter panel.
	 */
	public ShowFilterPanel() {
		super();

		initialize(); // init the GUI widgets

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.filter.KeyedEntityFilter#getMatchingBeans()
	 */
	@Override
	public Collection<Show> getMatchingEntities() {
		Collection<Show> matchlist = new ArrayList<Show>();
		try {
			Collection<Show> beans = ShowModel.getReference().getRecords();

			for (Show b : beans) {
				if (matches(b))
					matchlist.add(b);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

	/**
	 * Gets the old shows.
	 * 
	 * @return the old shows
	 */
	private JCheckBox getOldShows() {
		if (oldShows == null) {
			oldShows = new JCheckBox();
			oldShows.setText("Show Past Shows");
			oldShows.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					selOldShows();
				}
			});
		}
		return oldShows;
	}

	/**
	 * This method initializes showButton.
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Search Text:");
			showButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showSelect();
				}
			});
		}
		return showButton;
	}

	/**
	 * Gets the show text.
	 * 
	 * @return the show text
	 */
	private JTextField getShowText() {
		if (showtext == null) {
			showtext = new JTextField();

		}
		return showtext;
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());

		this.add(getShowButton(), GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH));
		this.add(getShowText(), GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));
		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH);
		gbc.gridwidth = 2;
		this.add(getOldShows(), gbc);

	}

	/**
	 * Matches.
	 * 
	 * @param b
	 *            the b
	 * 
	 * @return true, if successful
	 */
	public boolean matches(Show b) {
		Show s = b;

		String text = getShowText().getText();
		if (text != null && !text.equals("") && s.getName().indexOf(text) == -1)
			return false;

		if (!getOldShows().isSelected()) {
			Date yesterday = new Date(new Date().getTime() - 1000 * 60 * 60
					* 24);
			if (s.getDateTime() != null && s.getDateTime().before(yesterday))
				return false;
		}

		return true;
	}

	/**
	 * Sel old shows.
	 */
	private void selOldShows() {
		notifyParent();
	}

	/**
	 * Show select.
	 */
	private void showSelect() {
		notifyParent();
	}

} 