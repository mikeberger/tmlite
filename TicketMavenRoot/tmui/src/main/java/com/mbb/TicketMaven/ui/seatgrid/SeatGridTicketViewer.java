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

package com.mbb.TicketMaven.ui.seatgrid;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.util.ComponentPrinter;
import com.mbb.TicketMaven.ui.util.TableSorter;

import javax.swing.*;
import java.awt.*;

public class SeatGridTicketViewer extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private SeatModel model_;

	public SeatGridTicketViewer() {

		super();

		model_ = SeatModel.getReference();
		addModel(model_);

		initComponents();

		refresh();

	}

	@Override
	public void refresh() {
		// empty
	}

	private SeatGridPanel sgp;

	private void initComponents() {

		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 2;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		this.setLayout(new GridBagLayout());

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setPreferredSize(new java.awt.Dimension(554, 404));
		jScrollPane1.setViewportView(sgp = new SeatGridPanel(-1, -1, false));

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);

		this.add(jScrollPane1, gridBagConstraints1);
		this.add(getJPanel(), gridBagConstraints);

		this.add(getJPanel1(), gridBagConstraints11);
	}

	private javax.swing.JScrollPane jScrollPane1;

	private JPanel jPanel = null;

	private JPanel showSelectPanel = null;

	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 0);
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weighty = 1.0D;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints3.gridx = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getShowSelectPanel(), gridBagConstraints3);
		}
		return jPanel;
	}

	private JPanel getShowSelectPanel() {
		if (showSelectPanel == null) {
			showSelectPanel = new JPanel();
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints41.insets = new java.awt.Insets(4, 4, 4, 4);
			gridBagConstraints41.gridy = 1;

			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.weighty = 0.0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(4, 4, 4, 4);
			gridBagConstraints3.gridx = 1;

			showSelectPanel.setLayout(new GridBagLayout());

			showSelectPanel.add(getShowText(), gridBagConstraints3);
			showSelectPanel.add(getShowButton(), gridBagConstraints41);
		}

		return showSelectPanel;
	}

	private JTextField showtext;

	private JTextField getShowText() {
		if (showtext == null) {
			showtext = new JTextField(100);
			showtext.setEditable(false);
		}
		return showtext;
	}

	private void showSelect() {
		Show s = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class,
								java.util.Date.class }), new String[] { "Name",
						"DateTime" },
				new com.mbb.TicketMaven.model.filter.ShowFilter(
						LayoutModel.AUDITORIUM, false));
		if (s == null) {
			// show_ = null;
			showtext.setText("");
			sgp.setShow(-1);
		} else {
			// show_ = s;
			showtext.setText(s.getName() + " -- " + sdf.format(s.getDateTime()));
			sgp.setShow(s.getKey());
		}

	}

	private JButton showButton;

	private JPanel jPanel1 = null;

	private JButton printButton = null;

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
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getPrintButton(), gridBagConstraints2);
		}
		return jPanel1;
	}

	/**
	 * This method initializes printButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPrintButton() {
		if (printButton == null) {
			printButton = new JButton();
			printButton.setText("Print");
			printButton.setIcon(new ImageIcon(getClass().getResource(
					"/resource/Print16.gif")));
			printButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					new ComponentPrinter(sgp).print();
				}
			});
		}
		return printButton;
	}

}
