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

import com.mbb.TicketMaven.model.*;
import com.mbb.TicketMaven.model.entity.*;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.util.ConfirmDialog;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * LotteryPanel shows a form that lets users perform a lottery or undo a
 * lottery. Useful info on the sho is also shown
 */
public class LotteryPanel extends ViewPanel implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The show_id. */
	private int show_id = -1; // the key of the show we are viewing

	/** The j panel. */
	private JPanel jPanel = null;

	/** The show button. */
	private JButton showButton = null;

	/** The show text. */
	private JTextField showText = null;

	/** The j panel1. */
	private JPanel jPanel1 = null;

	/** The req text. */
	private JTextField reqText = null;

	/** The tk text. */
	private JTextField tkText = null;

	/** The avail text. */
	private JTextField availText = null;

	/** The run button. */
	private JButton runButton = null;

	/** The undo button. */
	private JButton undoButton = null;

	private JButton moveRequestsButton = new JButton("Move Requests");

	/** The sr text. */
	private JTextField srText = null;

	/** The non res text. */
	private JTextField nonResText = null;

	/** The special text. */
	private JTextField specialText = null;

	/** The avail special. */
	private JTextField availSpecial = null;

	/** The icon. */
	private Icon icon;

	/**
	 * Instantiates a new lottery panel.
	 */
	public LotteryPanel() {

		icon = new ImageIcon(getClass().getResource("/resource/dice16.gif"));

		addModel(TicketModel.getReference());
		addModel(TicketRequestModel.getReference());
		addModel(ShowModel.getReference());
		initialize();
		refresh();
	}

	/**
	 * This method initializes this.
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints2.weighty = 1.0D;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints2.gridx = 0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.add(getShowPanel(), gridBagConstraints1);
		this.add(getMainPanel(), gridBagConstraints2);

	}

	/**
	 * updates the Ui based on the show chosen. the show information is updates
	 * and the run and undo buttons are enabled or disabled appropriately
	 */
	@Override
	public void refresh() {

		if (show_id == -1) {
			runButton.setEnabled(false);
			undoButton.setEnabled(false);
			moveRequestsButton.setEnabled(false);
			reqText.setText("");
			srText.setText("");
			availText.setText("");
			tkText.setText("");
			availSpecial.setText("");
			nonResText.setText("");
			specialText.setText("");
		} else {

			try {

				// rare case where show was just deleted and lottery panel is
				// active
				Show sh = ShowModel.getReference().getShow(show_id);
				if (sh == null) {
					showText.setText("");
					show_id = -1;
					runButton.setEnabled(false);
					undoButton.setEnabled(false);
					moveRequestsButton.setEnabled(false);
					reqText.setText("");
					srText.setText("");
					availText.setText("");
					tkText.setText("");
					availSpecial.setText("");
					nonResText.setText("");
					specialText.setText("");
				} else {

					//
					// calculate the various show statistics
					//
					runButton.setEnabled(true);
					undoButton.setEnabled(true);
					moveRequestsButton.setEnabled(true);

					// number of requests
					Collection<TicketRequest> reqs = TicketRequestModel.getReference().getRequestsForShow(show_id);
					reqText.setText(Integer.toString(reqs.size()));
					if (reqs.isEmpty())
						moveRequestsButton.setEnabled(false);

					// total seats requested
					int s = 0;
					for (TicketRequest tr : reqs) {
						s += tr.getTickets().intValue();

					}
					srText.setText(Integer.toString(s));

					// seats available for show
					Collection<Seat> seats = SeatModel.getReference().getAvailableSeatsForShow(show_id);
					availText.setText(Integer.toString(seats.size()));

					// exclusive seat available
					int availEx = 0;
					Collection<Zone> zones = ZoneModel.getReference().getRecords();
					for (Zone z : zones) {
						if (!"Y".equals(z.getExclusive()))
							continue;
						Collection<Seat> zoneSeats = SeatModel.getReference().getAvailableSpecialSeatsForShow(show_id,
								z.getKey());
						if (zoneSeats != null)
							availEx += zoneSeats.size();
					}
					availSpecial.setText(Integer.toString(availEx));

					// disable undo button if no tickets to undo
					Collection<Ticket> tkts = TicketModel.getReference().getTicketsForShow(show_id);
					if (tkts.isEmpty())
						undoButton.setEnabled(false);

					// number of tickets
					tkText.setText(Integer.toString(tkts.size()));

					// special and non resident tickets
					int special = 0;
					int nonres = 0;
					for (Ticket tk : tkts) {
						if (tk.getResident() != null && tk.getResident().equals("N"))
							nonres += 1;
						if (!tk.getSpecialNeeds().equals(CustomerModel.NONE))
							special += 1;
					}
					specialText.setText(Integer.toString(special));
					nonResText.setText(Integer.toString(nonres));

					// disable run button if no seats for lottery or no requests
					if (reqs.size() == 0 || (seats.size() == 0 && availEx == 0))
						runButton.setEnabled(false);
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}

		}

	}

	/**
	 * This method initializes jPanel.
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getShowPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new java.awt.Insets(4, 4, 4, 4);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new java.awt.Insets(4, 4, 4, 4);
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints7.gridx = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getShowButton(), gridBagConstraints7);
			jPanel.add(getShowText(), gridBagConstraints8);
		}
		return jPanel;
	}

	/**
	 * This method initializes showButton.
	 *
	 * @return javax.swing.JButton
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
	 * This method initializes showText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getShowText() {
		if (showText == null) {
			showText = new JTextField();
		}
		return showText;
	}

	/**
	 * This method initializes jPanel1.
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (jPanel1 == null) {

			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());

			JLabel reqLabel = new JLabel("Outstanding Requests:");
			jPanel1.add(reqLabel, GridBagConstraintsFactory.create(0, 0));
			jPanel1.add(getReqText(), GridBagConstraintsFactory.create(1, 0, GridBagConstraints.NONE, 0.0, 0.0));

			JLabel seatLabel = new JLabel("Seats Requested:");
			jPanel1.add(seatLabel, GridBagConstraintsFactory.create(0, 1));
			jPanel1.add(getSrText(), GridBagConstraintsFactory.create(1, 1, GridBagConstraints.NONE, .0, 0.0));

			JLabel tkLabel = new JLabel("Assigned Tickets:");
			jPanel1.add(tkLabel, GridBagConstraintsFactory.create(0, 2));
			jPanel1.add(getTkText(), GridBagConstraintsFactory.create(1, 2, GridBagConstraints.NONE, 0.0, 0.0));

			JLabel availLabel = new JLabel("Available Non-Exclusive Seats:");
			jPanel1.add(availLabel, GridBagConstraintsFactory.create(0, 3));
			jPanel1.add(getAvailText(), GridBagConstraintsFactory.create(1, 3, GridBagConstraints.NONE, 0.0, 0.0));

			JLabel availExLabel = new JLabel("Available Exclusive Seats:");
			jPanel1.add(availExLabel, GridBagConstraintsFactory.create(0, 4));
			jPanel1.add(getAvailSpecial(), GridBagConstraintsFactory.create(1, 4, GridBagConstraints.NONE, 0.0, 0.0));

			JLabel jLabel = new JLabel("Non-Resident Tickets:");
			jPanel1.add(jLabel, GridBagConstraintsFactory.create(0, 5)); // Generated
			jPanel1.add(getNonResText(), GridBagConstraintsFactory.create(1, 5, GridBagConstraints.NONE, 0.0, 0.0)); // Generated

			JLabel jLabel1 = new JLabel("Special Needs Tickets:");
			jPanel1.add(jLabel1, GridBagConstraintsFactory.create(0, 6)); // Generated
			jPanel1.add(getSpecialText(), GridBagConstraintsFactory.create(1, 6, GridBagConstraints.NONE, 0.0, 0.0)); // Generated

			JPanel buttonPanel = new JPanel();

			buttonPanel.add(getRunButton(), GridBagConstraintsFactory.create(0, 0));

			buttonPanel.add(getUndoButton(), GridBagConstraintsFactory.create(1, 0));

			moveRequestsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					moveRequests();
				}

			});
			buttonPanel.add(moveRequestsButton, GridBagConstraintsFactory.create(2, 0));
			GridBagConstraints gbc = GridBagConstraintsFactory.create(0, 7, GridBagConstraints.NONE, 1.0, 0.0);
			gbc.gridwidth = 2;
			jPanel1.add(buttonPanel, gbc);

		}
		return jPanel1;
	}

	private void moveRequests() {
		int ret = ConfirmDialog
				.showNotice("WARNING: This will move all outstanding requests for this show to another show");
		if (ret != ConfirmDialog.OK)
			return;

		try {
			// disable model change notifications to prevent UI from going
			// haywire and call the model to undo the lottery

			Show s = BeanSelector.selectBean(ShowModel.getReference(),
					new TableSorter(new String[] { "Show Name", "Show Date/Time" },
							new Class[] { java.lang.String.class, java.util.Date.class }),
					new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, true));
			if (s == null) {
				return;
			} else {
				int show = s.getKey();

				if( show_id == show )
				{
					Errmsg.getErrorHandler().notice("You must pick a different show");
					return;
				}
				TicketRequestModel.getReference().setNotifyListeners(false);
				CustomerModel.getReference().setNotifyListeners(false);

				TicketRequestModel.getReference().moveRequests(show_id, show);

			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			TicketRequestModel.getReference().setNotifyListeners(true);
			CustomerModel.getReference().setNotifyListeners(true);
			refresh();
			return;
		}
		TicketRequestModel.getReference().setNotifyListeners(true);
		CustomerModel.getReference().setNotifyListeners(true);

		refresh();
		Errmsg.getErrorHandler().notice("The Requests have been moved successfully");
	}

	/**
	 * This method initializes reqText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getReqText() {
		if (reqText == null) {
			reqText = new JTextField();
			reqText.setColumns(5);
			reqText.setEditable(false);
		}
		return reqText;
	}

	/**
	 * This method initializes tkText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTkText() {
		if (tkText == null) {
			tkText = new JTextField();
			tkText.setColumns(5);
			tkText.setEditable(false);
		}
		return tkText;
	}

	/**
	 * This method initializes availText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getAvailText() {
		if (availText == null) {
			availText = new JTextField();
			availText.setColumns(5);
			availText.setEditable(false);
		}
		return availText;
	}

	/**
	 * Gets the avail special.
	 *
	 * @return the avail special
	 */
	private JTextField getAvailSpecial() {
		if (availSpecial == null) {
			availSpecial = new JTextField();
			availSpecial.setColumns(5);
			availSpecial.setEditable(false);
		}
		return availSpecial;
	}

	/**
	 * Gets the run button.
	 *
	 * @return the run button
	 */
	private JButton getRunButton() {
		if (runButton == null) {
			runButton = new JButton();
			runButton.setText("Run Lottery");
			runButton.setBackground(new java.awt.Color(153, 255, 153));
			runButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					runLottery();
				}
			});
		}
		return runButton;
	}

	/**
	 * Gets the undo button.
	 *
	 * @return the undo button
	 */
	private JButton getUndoButton() {
		if (undoButton == null) {
			undoButton = new JButton();
			undoButton.setText("Undo Lottery");
			undoButton.setBackground(Color.pink);
			undoButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					undoLottery();
				}
			});
		}
		return undoButton;
	}

	/**
	 * Run a lottery.
	 */
	private void runLottery() {
		try {
			// disable model change notifications to prevent UI from going
			// haywire and call the model to run the lottery
			TicketModel.getReference().setNotifyListeners(false);
			TicketRequestModel.getReference().setNotifyListeners(false);
			CustomerModel.getReference().setNotifyListeners(false);

			new LotteryManager(show_id).runLottery();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			Errmsg.getErrorHandler().notice("Lottery has NOT been run");
			TicketModel.getReference().setNotifyListeners(true);
			TicketRequestModel.getReference().setNotifyListeners(true);
			CustomerModel.getReference().setNotifyListeners(true);
			refresh();
			return;
		}
		TicketModel.getReference().setNotifyListeners(true);
		TicketRequestModel.getReference().setNotifyListeners(true);
		CustomerModel.getReference().setNotifyListeners(true);

		refresh();
		Errmsg.getErrorHandler().notice("The Lottery has completed successfully");
	}

	/**
	 * Undo lottery.
	 */
	private void undoLottery() {
		// give the user a chance to abort
		int ret = ConfirmDialog
				.showNotice("WARNING: This will delete all tickets for this show and convert them back into Requests.");
		if (ret != ConfirmDialog.OK)
			return;

		try {
			// disable model change notifications to prevent UI from going
			// haywire and call the model to undo the lottery
			TicketModel.getReference().setNotifyListeners(false);
			TicketRequestModel.getReference().setNotifyListeners(false);
			CustomerModel.getReference().setNotifyListeners(false);

			new LotteryManager(show_id).undoLottery();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			Errmsg.getErrorHandler().notice("Lottery has NOT been undone");
			TicketModel.getReference().setNotifyListeners(true);
			TicketRequestModel.getReference().setNotifyListeners(true);
			CustomerModel.getReference().setNotifyListeners(true);
			refresh();
			return;
		}
		TicketModel.getReference().setNotifyListeners(true);
		TicketRequestModel.getReference().setNotifyListeners(true);
		CustomerModel.getReference().setNotifyListeners(true);

		refresh();
		Errmsg.getErrorHandler().notice("The Lottery has been undone successfully");
	}

	/**
	 * Show select.
	 */
	private void showSelect() {
		Show s = BeanSelector.selectBean(ShowModel.getReference(),
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
						new Class[] { java.lang.String.class, java.util.Date.class }),
				new String[] { "Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM, true));
		if (s == null) {
			showText.setText("");
			show_id = -1;
		} else {
			show_id = s.getKey();
			showText.setText(s.getName() + " -- " + sdf.format(s.getDateTime()));
		}

		refresh();

	}

	/**
	 * This method initializes srText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSrText() {
		if (srText == null) {
			srText = new JTextField();
			srText.setEditable(false);
			srText.setColumns(5);
		}
		return srText;
	}

	/**
	 * This method initializes nonResText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getNonResText() {
		if (nonResText == null) {
			nonResText = new JTextField();
			nonResText.setEditable(false); // Generated
			nonResText.setColumns(5); // Generated
		}
		return nonResText;
	}

	/**
	 * This method initializes specialText.
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getSpecialText() {
		if (specialText == null) {
			specialText = new JTextField();
			specialText.setEditable(false); // Generated
			specialText.setColumns(5); // Generated
		}
		return specialText;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.ui.module.Module#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.mbb.TicketMaven.ui.module.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Lottery Manager";
	}

}
