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

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class filters shows by name for table layouts only
 */
public class TableShowFilterPanel extends FilterPanel<Show> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Show show_ = null;

	private JButton showButton = null;

	private JTextField showText = null;

	/**
	 * Instantiates a new table show filter panel.
	 */
	public TableShowFilterPanel() {
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
		if (show_ != null)
			matchlist.add(show_);

		return matchlist;
	}

	/**
	 * Gets the show button.
	 * 
	 * @return the show button
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Select Show:");
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
		if (showText == null) {
			showText = new JTextField(80);
			showText.setEditable(false);
		}
		return showText;
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
	}


	/**
	 * Show select.
	 */
	private void showSelect() {
		show_ = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class,
								java.util.Date.class }), new String[] { "Name",
						"DateTime" }, new ShowFilter(LayoutModel.TABLE, false));
		if (show_ == null) {
			showText.setText("");
		} else {

			showText.setText(show_.getName() + " -- "
					+ sdf.format(show_.getDateTime()));
		}

		notifyParent();
	}

} 
