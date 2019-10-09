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

package com.mbb.TicketMaven.ui.detail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.ui.MainView;
import com.mbb.TicketMaven.ui.module.Module;
import com.mbb.TicketMaven.ui.module.TheaterLayout;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Warning;

public class LayoutView extends ViewDetailPanel<Layout> {

	private static final long serialVersionUID = 1L;

	private JPanel audPanel = null;

	private int center = 0;

	private JSpinner centerSpinner = new JSpinner();

	private Layout layout_ = null;

	private JTextField nameField = new JTextField();

	private int rows = 0;

	private JSpinner rowSpinner = new JSpinner();

	private JComboBox<String> seatingBox = new JComboBox<String>();

	private int seats = 0;

	private JSpinner seatSpinner = new JSpinner();

	private JButton editorButton = new JButton("Go to Seat Editor");

	public LayoutView() {
		initComponents();
		refresh();
	}

	@Override
	public boolean canCopy() {
		return true;
	}

	@Override
	public void copyItem(Layout oldLayout) throws Exception {

		String newName = JOptionPane.showInputDialog(this,
				"Please enter the name for the new copy:");
		if (newName == null)
			return;
		LayoutModel.getReference().copyLayout(oldLayout, newName);

	}

	private JPanel getAudPanel() {
		if (audPanel == null) {

			audPanel = new JPanel();
			audPanel.setLayout(new GridBagLayout());

			audPanel.add(new JLabel("Number of Rows:"),
					GridBagConstraintsFactory.create(0, 0,
							GridBagConstraints.BOTH));

			rowSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(10),
					Integer.valueOf(1), Integer.valueOf(52), Integer.valueOf(1)));
			audPanel.add(rowSpinner, GridBagConstraintsFactory.create(1, 0,
					GridBagConstraints.BOTH));

			audPanel.add(new JLabel("Maximum Seats Per Row:"),
					GridBagConstraintsFactory.create(0, 1,
							GridBagConstraints.BOTH));
			seatSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(10),
					Integer.valueOf(1), Integer.valueOf(250), Integer.valueOf(1)));
			audPanel.add(seatSpinner, GridBagConstraintsFactory.create(1, 1,
					GridBagConstraints.BOTH));

			audPanel.add(new JLabel("Center Seat:"), GridBagConstraintsFactory
					.create(0, 2, GridBagConstraints.BOTH));
			audPanel.add(centerSpinner, GridBagConstraintsFactory.create(1, 2,
					GridBagConstraints.BOTH));

			JButton centerButton = new JButton("Explain Center Seat");
			centerButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Errmsg.getErrorHandler().notice("All seats are assigned from the center outward - the best seat in the row.\n"
							+ "If this value is left as 0, then the center is assumed to be the middle numbered seat in the row.\n"
							+ "This value should be manually overridden if a center aisle splits the theater unevenly.\n"
							+ "If so, the value should be the number of the seat to the left of the center aisle, when facing the stage");
				}
			});
			audPanel.add(centerButton, GridBagConstraintsFactory.create(2, 2,
					GridBagConstraints.BOTH));
		}
		return audPanel;
	}

	@Override
	public String getDuplicateError() {
		return "A Layout already exists with this name";
	}

	private void initComponents() {

		this.setLayout(new GridBagLayout());

		this.add(new JLabel("Layout Name:"),
				GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH));

		this.add(nameField, GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 0.0));

		this.add(editorButton, GridBagConstraintsFactory.create(2, 0,
				GridBagConstraints.BOTH, 0.0, 0.0));

		editorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				goToSeatEditor();
			}
		});

		this.add(new JLabel("Seating Type:"),
				GridBagConstraintsFactory.create(0, 1, GridBagConstraints.BOTH));
		seatingBox.addItem("Auditorium");
		seatingBox.addItem("Table");
		seatingBox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				seatingTypeChanged();
			}
		});
		this.add(seatingBox, GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.VERTICAL));

		GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 2,
				GridBagConstraints.VERTICAL, 1.0, 0.0);
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 0, 0);
		this.add(getAudPanel(), gbc);
	}

	protected void goToSeatEditor() {
		Module m = MainView.getReference().getModule(TheaterLayout.class);
		if (m != null && m instanceof TheaterLayout) {
			TheaterLayout tl = (TheaterLayout) m;
			tl.editSeats(layout_);
		}
	}

	@Override
	public void refresh() {
		// empty

	}

	public void remove() {
		//
	}

	@Override
	public void saveData() throws Exception, Warning {
		try {
			if (layout_ == null) {
				layout_ = LayoutModel.getReference().newLayout();
			}

			String type = (String) seatingBox.getSelectedItem();
			if (nameField.getText().equals(""))
				throw new Warning("A Layout Name is Required");

			layout_.setCenterSeat((Integer) centerSpinner.getValue());

			layout_.setName(nameField.getText());
			if (type.equals("Auditorium")) {
				layout_.setSeating(LayoutModel.AUDITORIUM);
				layout_.setNumRows((Integer) rowSpinner.getValue());
				layout_.setNumSeats((Integer) seatSpinner.getValue());
			} else {
				layout_.setSeating(LayoutModel.TABLE);
				layout_.setNumRows(Integer.valueOf(0));
				layout_.setNumSeats(Integer.valueOf(0));
			}
			int lid = LayoutModel.getReference().saveRecord(layout_);

			if (type.equals("Auditorium")) {
				SeatModel.getReference().setNotifyListeners(false);
				SeatModel.generateMissingSeats(lid);
			}

		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		SeatModel.getReference().setNotifyListeners(true);
		SeatModel.getReference().refresh();
		refresh();

	}

	private void seatingTypeChanged() {

		String type = (String) seatingBox.getSelectedItem();
		if (type.equals("Table")) {
			audPanel.setVisible(false);
		} else {
			audPanel.setVisible(true);
		}
	}

	@Override
	public void showData(Layout b) {

		layout_ = b;

		if (layout_ == null) {
			rows = 1;
			seats = 1;
			center = 1;
		} else {
			rows = layout_.getNumRows().intValue();
			seats = layout_.getNumSeats().intValue();
			center = layout_.getCenterSeat().intValue();
			nameField.setText(layout_.getName());

		}
		rowSpinner.setValue(Integer.valueOf(rows));
		seatSpinner.setValue(Integer.valueOf(seats));
		centerSpinner.setValue(Integer.valueOf(center));

		if (layout_ == null || layout_.getSeating() == null
				|| layout_.getSeating().equals(LayoutModel.AUDITORIUM))
			seatingBox.setSelectedItem("Auditorium");
		else
			seatingBox.setSelectedItem("Table");

		if (layout_ == null || layout_.isNew()) {
			seatingBox.setEnabled(true);
			editorButton.setEnabled(false);
		} else {
			seatingBox.setEnabled(false);
			editorButton.setEnabled(true);

		}
		// once shows are assigned, it is read/only
		boolean hasShows = false;
		if (layout_ != null && !layout_.isNew()) {
			try {
				Collection<Show> shows = ShowModel.getReference()
						.getShowsForLayout(layout_);
				if (!shows.isEmpty())
					hasShows = true;
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

		if (hasShows == true) {
			rowSpinner.setEnabled(false);
			seatSpinner.setEnabled(false);
			centerSpinner.setEnabled(false);
		} else {
			rowSpinner.setEnabled(true);
			seatSpinner.setEnabled(true);
			centerSpinner.setEnabled(true);
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
