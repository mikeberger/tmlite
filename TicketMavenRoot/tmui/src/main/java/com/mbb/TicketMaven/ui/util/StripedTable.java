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

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Show a JTable with rows of alternating background colors
 */
public class StripedTable extends JTable {

	private static final long serialVersionUID = 1L;
	// the various default cell renders
	private TableCellRenderer defrend_ = null;
	private TableCellRenderer defDaterend_ = null;
	private TableCellRenderer defBoolRend_ = null;

	/** The default Stripe color (the other rows are the UI look and feel default) */
	private static Color STCOLOR = new Color(240, 240, 250);

	/**
	 * Sets the stripe color.
	 * 
	 * @param c the new stripe color
	 */
	public static void setStripeColor(Color c) {
		STCOLOR = c;
	}

	/**
	 * This class renders the table cells with the proper background colors based on row number
	 */
	private class StripedRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new striped renderer.
		 */
		public StripedRenderer() {
			super();
			setOpaque(true); // MUST do this for background to show up.
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel l = null;

			if (obj instanceof Date) {
				// for Date cells, get the default cell component and muck with it lower down
				l = (JLabel) defDaterend_.getTableCellRendererComponent(table,
						obj, isSelected, hasFocus, row, column);
			} else if (obj instanceof Boolean) {
				// for boolean, get the component, set its background and return it
				// it's a checkbox, so we can't let it drop down into the JLabel code below
				Component c = defBoolRend_.getTableCellRendererComponent(table,
						obj, isSelected, hasFocus, row, column);

				if (isSelected) {
					return c;
				} else if (row % 2 == 0) {
					c.setBackground(STCOLOR);
				} else {
					c.setBackground(Color.WHITE);
				}
				return c;
		
			} else {
				// get the cell component for whatever type this is
				l = (JLabel) defrend_.getTableCellRendererComponent(table, obj,
						isSelected, hasFocus, row, column);
			}
			
			// this renderer class is the JLabel, so copy attributes
			// out of the JLabel that the default renderer returned to set
			// our attributes
			this.setForeground(l.getForeground());
			
			// set the background - selection color overrides any striping
			if (isSelected) {
				this.setBackground(l.getBackground());
			} else if (row % 2 == 0) {
				this.setBackground(STCOLOR);
			} else {
				this.setBackground(Color.WHITE);
			}

			// align our data in the cell
			// this is not part of striping - but this is a goodplace to do this globally
			if (obj instanceof Integer) {
				this.setText(((Integer) obj).toString());
				this.setHorizontalAlignment(CENTER);
			} else if (obj instanceof Date) {
				this.setText(l.getText());
				this.setHorizontalAlignment(CENTER);
			} else {
				this.setText(l.getText());
				this.setHorizontalAlignment(l.getHorizontalAlignment());
			}

			this.setBorder(new EmptyBorder(4, 2, 4, 2));
			return this;
		}
	}

	/**
	 * Instantiates a new striped table.
	 */
	public StripedTable() {
		super();
		
		// save the default renderers and set a new renderer for various types
		defrend_ = this.getDefaultRenderer(String.class);
		defDaterend_ = this.getDefaultRenderer(Date.class);
		defBoolRend_ = this.getDefaultRenderer(Boolean.class);
		this.setDefaultRenderer(Object.class, new StripedRenderer());
		this.setDefaultRenderer(Date.class, new StripedRenderer());
		this.setDefaultRenderer(Integer.class, new StripedRenderer());
		this.setDefaultRenderer(Boolean.class, new StripedRenderer());
	}
}
