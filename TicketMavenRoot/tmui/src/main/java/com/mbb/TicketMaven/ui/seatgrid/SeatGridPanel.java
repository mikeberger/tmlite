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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.ZoneModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Seat;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.model.entity.Zone;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.tablelayout.LayoutChangeListener;
import com.mbb.TicketMaven.ui.util.FlatButton;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.util.Errmsg;

public class SeatGridPanel extends ViewPanel implements LayoutChangeListener {

	private static final long serialVersionUID = 1L;

	public static enum SelectionMode {
		ANY, ANY_AVAIL
	}

	private static Color zoneColors[] = { Color.PINK, Color.ORANGE,
			new Color(255, 255, 184), new Color(204, 255, 204),
			new Color(255, 204, 153), Color.cyan,
			new Color(153, 204, 255), Color.green, Color.MAGENTA, Color.red, Color.YELLOW, Color.blue };

	private FlatButton buttons[][];

	private int lid = -1; // layout id
	
	private int numRows_ = 0;
	
	private int numSeats_ = 0;
	
	private SelectionMode selectionMode = SelectionMode.ANY;

	private int show_ = -1;
	
	private boolean showQuality_ = false;

	private Ticket tmap[][];

	private HashMap<Integer,Color> zoneColorMap = new HashMap<Integer,Color>();
	
	private JCheckBox showQualityBox = new JCheckBox("Display Seat Quality");
	
	private JPanel gridPanel = new JPanel();


	public SeatGridPanel( int show, int layoutid, boolean modal) {

		lid = layoutid;
		setShow(show);
		showQuality_ = false;
		if( !modal )
		{
			addModel(SeatModel.getReference());
			addModel(TicketModel.getReference());
		}
		
		JPanel buttonPanel = new JPanel();
		this.setLayout( new GridBagLayout());
		this.add(gridPanel, GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH, 1.0, 1.0));
		buttonPanel.add(showQualityBox);
		showQualityBox.setSelected(showQuality_);
		showQualityBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showQuality_ = showQualityBox.isSelected();
				refresh();
			}
			
		});
		this.add(buttonPanel, GridBagConstraintsFactory.create(0, 1));
		
		try {
			refresh();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		//refresh();
	}

	
	public void createGrid() throws Exception {
		Layout l = LayoutModel.getReference().getLayout(lid);
		numSeats_ = l.getNumSeats().intValue();
		numRows_ = l.getNumRows().intValue();
		gridPanel.setLayout(new GridLayout(numRows_ + 1, numSeats_, 2, 2));
		buttons = new FlatButton[numRows_][numSeats_];

		for (int r = 0; r <= numRows_; r++) {
			for (int s = 0; s <= numSeats_; s++) {
				if (s == 0 && r == 0) {
					gridPanel.add(new JLabel());
				} else if (s == 0) {
					JLabel lb = new JLabel(SeatModel.rowletters.substring(
							r - 1, r));
					lb.setHorizontalAlignment(SwingConstants.CENTER);
					gridPanel.add(lb);
				} else if (r == 0) {
					// add heading
					JLabel lb = new JLabel(Integer.toString(s));
					lb.setHorizontalAlignment(SwingConstants.CENTER);
					gridPanel.add(lb);
				} else {
					buttons[r - 1][s - 1] = new FlatButton("X");
					buttons[r - 1][s - 1].setBackground(Color.WHITE);
					gridPanel.add(buttons[r - 1][s - 1]);

				}
			}
		}
	}

	public Collection<Seat> getSelectedSeats()
	{
		Collection<Seat> list = new ArrayList<Seat>();
		for (int r = 0; r < numRows_; r++) {
			for (int s = 0; s < numSeats_; s++) {

				if (!buttons[r][s].isSelected())
					continue;

				Seat seat;
				try {
					seat = SeatModel.getReference().getSeat(
							SeatModel.rowletters.substring(r, r + 1), s + 1,
							lid);
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
					continue;
				}
				if (seat == null)
					continue;

				list.add(seat);

			}
		}
		return list;
	}

	@Override
	public void layoutChange(Layout data) {
		if (data != null)
			setLayout(data.getKey());
		else
			setLayout(-1);
	}

	@Override
	public void refresh() {

		// sanity check
		try {

			gridPanel.removeAll();
			gridPanel.repaint();
			if (lid != -1)
				createGrid();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		zoneColorMap.clear();

		if (show_ != -1) {
			tmap = new Ticket[numRows_][numSeats_];
			try {
				Collection<Ticket> tkts = TicketModel.getReference().getTicketsForShow(
						show_);

				Iterator<Ticket> it = tkts.iterator();
				while (it.hasNext()) {
					Ticket t = it.next();
					Seat seat = SeatModel.getReference().getSeat(t.getSeatId().intValue());
					tmap[SeatModel.rowletters.indexOf(t.getRow())][seat.getSeat().intValue() - 1] = t;
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}

		}
		for (int r = 0; r < numRows_; r++) {
			for (int s = 0; s < numSeats_; s++) {
				try {
					Seat seat = SeatModel.getReference().getSeat(
							SeatModel.rowletters.substring(r, r + 1), s + 1,
							lid);
					if (seat == null) {
						buttons[r][s].setVisible(false);
						continue;
					}
					buttons[r][s].setVisible(true);
					buttons[r][s].setEnabled(true);

					Integer zone_int = seat.getZone();
					Color color = zoneColorMap.get(zone_int);
					if( color == null )
					{
						color = zoneColors[zoneColorMap.size() % zoneColors.length];
						zoneColorMap.put(zone_int, color);
					}

					if (showQuality_) {
						buttons[r][s].setText(seat.getWeight().toString());
					} else {
						buttons[r][s].setText(seat.getNumber());
					}

					if (seat.getZone() == null)
						buttons[r][s].setToolTipText(SeatModel.rowletters
								.substring(r, r + 1)
								+ "/" + seat.getNumber() +
								"-" + seat.getEnd());
					else {
						Zone z = ZoneModel.getReference().getZone(
								seat.getZone().intValue());
						buttons[r][s].setToolTipText(SeatModel.rowletters
								.substring(r, r + 1)
								+ "/"
								+ seat.getNumber()
								+ "-"
								+ z.getName()
								+ "-" + seat.getEnd() );

					}

					// available
					if (seat.getAvailable().equals("N")) {
						buttons[r][s].setBackground(this.getBackground());
						buttons[r][s].setText(" ");
						if( show_ != -1)
							buttons[r][s].setVisible(false);
					} else if (seat.getZone() != null) {
						buttons[r][s].setBackground(color);
					} else {
						buttons[r][s].setBackground(Color.WHITE);
					}

					// show
					if (show_ != -1) {
						buttons[r][s].setBackground(this.getBackground());
						
						Ticket t = tmap[r][s];
						if (t != null) {
							if( selectionMode == SelectionMode.ANY_AVAIL )
							{
								buttons[r][s].setEnabled(false);
							}
							if (t.getSpecialNeeds().equals(CustomerModel.NONE)) {
								buttons[r][s].setBackground(new Color(204,204,255));
							} else if (seat.getZone() != null) {
								buttons[r][s].setBackground(color);
							} else {
								buttons[r][s].setBackground(Color.PINK);
							}
							
							buttons[r][s].setToolTipText(t.getCustomerName());
						}
						
					}

					// aisle
					if (seat.getAvailable().equals("N")) {
						buttons[r][s].setBorderColor(this.getBackground());
					} else if (!seat.getEnd().equals(SeatModel.NONE)) {
						buttons[r][s].setBorderColor(Color.GREEN);
//						if (!showQuality_) {
//							if (seat.getEnd().equals(SeatModel.LEFT)) {
//								buttons[r][s].setText("L");
//							} else if (seat.getEnd().equals(SeatModel.RIGHT)) {
//								buttons[r][s].setText("R");
//							}
//						}
					} else {
						buttons[r][s].setBorderColor(Color.BLACK);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void setLayout(int layoutid) {
		lid = layoutid;
		refresh();
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		refresh();
	}

	public void setShow(int show) {
		show_ = show;
		lid = -1;
		try {
			if (show != -1) {
				Show s = ShowModel.getReference().getShow(show);
				if (s != null)
					lid = s.getLayout().intValue();
			}
		} catch (Exception e) {
				//empty
		}
		refresh();
	}

	public void updateSeats(String avail, String aisle, String zone,
			Object weight, String number) {
		try {
			SeatModel.getReference().setNotifyListeners(false);
			Integer zoneKey = null;
			if (!SeatGridUpdateDetails.NO_CHANGE.equals(zone)) {

				zoneKey = Integer.valueOf(-1);
				Collection<Zone> zones;

				zones = ZoneModel.getReference().getRecords();

				Iterator<Zone> it = zones.iterator();
				while (it.hasNext()) {
					Zone z = it.next();
					if (z.getName().equals(zone)) {
						zoneKey = Integer.valueOf(z.getKey());
						break;
					}
				}
			}

			for (int r = 0; r < numRows_; r++) {
				for (int s = 0; s < numSeats_; s++) {

					if (!buttons[r][s].isSelected())
						continue;

					Seat seat = SeatModel.getReference().getSeat(
							SeatModel.rowletters.substring(r, r + 1), s + 1,
							lid);
					if (seat == null)
						continue;

					if (!SeatGridUpdateDetails.NO_CHANGE.equals(avail))
						seat.setAvailable(avail);
					if (!SeatGridUpdateDetails.NO_CHANGE.equals(aisle))
						seat.setEnd(aisle);
					if( zoneKey != null && zoneKey.intValue() == -1)
						seat.setZone(null);
					else if (zoneKey != null)
						seat.setZone(zoneKey);
					if (weight instanceof Integer)
						seat.setWeight((Integer) weight);
					
					if( number != null && !number.isEmpty())
						seat.setLabel(number);
					
					SeatModel.getReference().saveRecord(seat);

				}
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		} finally {
			SeatModel.getReference().setNotifyListeners(true);
			SeatModel.getReference().refreshListeners();
		}
	}
	
	

}
