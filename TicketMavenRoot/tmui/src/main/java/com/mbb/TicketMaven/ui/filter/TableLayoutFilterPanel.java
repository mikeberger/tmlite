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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JTextField;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.TableModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.model.filter.LayoutFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.tablelayout.LayoutChangeListener;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;

/**
 * This class filters tables by layout
 */
public class TableLayoutFilterPanel extends FilterPanel<Table> {

	private static final long serialVersionUID = 1L;

	private Layout layout_ = null;

	private JButton layoutButton = null;

	private JTextField layoutText = null;

	/**
	 * Instantiates a new table layout filter panel.
	 */
	public TableLayoutFilterPanel() {
		super();
		initialize(); // init the GUI widgets
	}

	/**
	 * Gets the cust button.
	 * 
	 * @return the cust button
	 */
	private JButton geLayoutButton() {
		if (layoutButton == null) {
			layoutButton = new JButton();
			layoutButton.setText("Select Layout:");
			layoutButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					layoutSelect();
				}
			});
		}
		return layoutButton;
	}

	/**
	 * Gets the custtext.
	 * 
	 * @return the custtext
	 */
	private JTextField getLayoutText() {
		if (layoutText == null) {
			layoutText = new JTextField(80);
			layoutText.setEditable(false);
		}
		return layoutText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mbb.TicketMaven.model.filter.KeyedEntityFilter#getMatchingBeans()
	 */
	@Override
	public Collection<Table> getMatchingEntities() {
		Collection<Table> matchlist = new ArrayList<Table>();
		if (layout_ == null)
			return matchlist;
		try {
			Collection<Table> beans = null;
			beans = TableModel.getReference().getTablesForLayout(
					layout_.getKey());

			for (Table b : beans) {
				if (matches(b))
					matchlist.add(b);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return matchlist;
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {

		this.setLayout(new GridBagLayout());
		this.add(geLayoutButton(), GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH));
		this.add(getLayoutText(), GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));
	}

	/**
	 * Layout select.
	 */
	private void layoutSelect() {
		layout_ = BeanSelector.selectBean(LayoutModel.getReference(),
				new TableSorter(new String[] { "Name" },
						new Class[] { java.lang.String.class }),
				new String[] { "Name" }, new LayoutFilter(LayoutModel.TABLE));
		if (layout_ == null) {
			layoutText.setText("");
		} else {
			layoutText.setText(layout_.getName());
		}

		Object p = this.parent;

		// send the layout object to the view, which needs to update itself
		if (p instanceof LayoutChangeListener) {
			LayoutChangeListener cl = (LayoutChangeListener) p;
			cl.layoutChange(layout_);
		}
		notifyParent();
	}

	/**
	 * Matches.
	 * 
	 * @param b
	 *            the b
	 * 
	 * @return true, if successful
	 */
	public boolean matches(KeyedEntity b) {

		return true;

	}

	public void selectLayout(Layout l) {
		layout_ = l;
		layoutText.setText(layout_.getName());
		Object p = this.parent;

		// send the layout object to the view, which needs to update itself
		if (p instanceof LayoutChangeListener) {
			LayoutChangeListener cl = (LayoutChangeListener) p;
			cl.layoutChange(layout_);
		}
		notifyParent();
	}
	

} 
