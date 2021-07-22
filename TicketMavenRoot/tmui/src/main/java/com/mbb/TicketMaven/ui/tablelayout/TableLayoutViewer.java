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

package com.mbb.TicketMaven.ui.tablelayout;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.ReservationModel;
import com.mbb.TicketMaven.model.TableModel;
import com.mbb.TicketMaven.model.entity.Reservation;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.filter.TableShowFilterPanel;
import com.mbb.TicketMaven.ui.util.CircleView;
import com.mbb.TicketMaven.ui.util.ComponentPrinter;
import com.mbb.TicketMaven.util.Errmsg;

public class TableLayoutViewer extends ViewPanel {
	private static final long serialVersionUID = 1L;

	private static class CircleCell extends DefaultGraphCell {
		private static final long serialVersionUID = 1L;

		public CircleCell(Object userObject) {
			super(userObject);
		}
	}

	// Defines a Graph that uses the Shift-Button (Instead of the Right
	// Mouse Button, which is Default) to add/remove point to/from an edge.
	private static class MyGraph extends JGraph {

		private static final long serialVersionUID = 1L;
		private Show show_ = null;

		// Construct the Graph using the Model as its Data Source
		public MyGraph(GraphModel model) {
			this(model, null);
			this.setEnabled(false);
			getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
				private static final long serialVersionUID = 1L;

				// CellViews for each type of cell
				@Override
				protected VertexView createVertexView(Object cell) {
					if (cell instanceof CircleCell)
						return new CircleView(cell);
					return new VertexView(cell);
				}
			});
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		// Construct the Graph using the Model as its Data Source
		public MyGraph(GraphModel model, GraphLayoutCache cache) {
			super(model, cache);
		}

		// Display tool tip
		@Override
		public String getToolTipText(MouseEvent event) {
			Object cell = getFirstCellForLocation(event.getX(), event.getY());

			if (cell != null) {
				DefaultGraphCell d = (DefaultGraphCell) cell;
				myUserClass u = (myUserClass) d.getUserObject();
				String hover = "<html>";
				if (u.getNumSeats() == 0) {
					hover += "Room Feature: " + u.getTableLabel();
				} else {
					hover += "Table: " + u.getTableLabel();
					int id = u.getTableNumber();
					if (id != -1) {
						Table t = null;
						try {
							t = TableModel.getReference().getTable(id);
						} catch (Exception e) {
							Errmsg.getErrorHandler().errmsg(e);
							return null;
						}
						
						if( t == null ) return null;
						if ( t.getSeats() != null)
							hover += "<br>Seats: " + t.getSeats().intValue()
									+ " Avail: " + u.getSeatsAvail();

						try {
							Collection<Reservation> res = ReservationModel.getReference()
									.getReservationsForTableInShow(
											show_.getKey(), t.getKey());
							for(Reservation r : res ) {
								hover += "<br>" + r.getCustomerName() + " ("
										+ r.getNum().intValue() + ")";
							}
						} catch (Exception e) {
							//empty
						}
					}
				}
				hover += "</html>";

				return (hover);
			}
			return (null);
		}

		public void setShow(Show s) {
			show_ = s;
		}
	}

	// User Class for Jgraph
	private static class myUserClass {

		private int num_seats;

		private int seatsAvail;

		private String tableLabel;

		private int tableNumber;
		
		private String tblType;

		@SuppressWarnings("unused")
		public String getTblType() {
			return tblType;
		}

		public void setTblType(String tblType) {
			this.tblType = tblType;
		}

		public int getNumSeats() {
			return num_seats;
		}

		public int getSeatsAvail() {
			return seatsAvail;
		}

		public String getTableLabel() {
			return tableLabel;
		}

		public int getTableNumber() {
			return tableNumber;
		}

		public void setNumSeats(int num_seats) {
			this.num_seats = num_seats;
		}

		public void setSeatsAvail(int seatsAvail) {
			this.seatsAvail = seatsAvail;
		}

		public void setTableLabel(String tableLabel) {
			this.tableLabel = tableLabel;
		}

		// Getters and setters follow
		public void setTableNumber(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		@Override
		public String toString() {
			if (num_seats == 0)
				return tableLabel;
			return tableLabel + " (" + (num_seats - seatsAvail) + "/"
					+ num_seats + ")";
		}

	}

	private static int seatsAvail(Show s, Table t) {
		int seats_avail = t.getSeats().intValue();
		try {
			Collection<Reservation> res = ReservationModel.getReference()
					.getReservationsForTableInShow(s.getKey(), t.getKey());
			for(Reservation r : res ) {
				seats_avail = seats_avail - r.getNum().intValue();
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
		return seats_avail;
	}

	// JGraph instance
	private MyGraph graph_;

	private Show show_ = null;

	private TableShowFilterPanel tf_ = null;

	// Construct an Editor Panel
	public TableLayoutViewer() {
		// Construct the Graph
		addModel(TableModel.getReference());
		addModel(ReservationModel.getReference());
		addModel(LayoutModel.getReference());
		graph_ = createGraph();
		initComponents();
	}

	private Map<?, ?> createCellAttributes(Table t) {
		Map<?, ?> map = new Hashtable<Object, Object>();

		// Add a Bounds Attribute to the Map
		GraphConstants.setBounds(map, new Rectangle2D.Double(t.getX()
				.doubleValue(), t.getY().doubleValue(), t.getWidth()
				.doubleValue(), t.getHeight().doubleValue()));

		// Add a nice looking gradient background
		if (t.getSeats() == null || t.getSeats().intValue() == 0) {
			GraphConstants.setGradientColor(map, Color.orange);
		} else {
			int seats_avail = seatsAvail(show_, t);
			if (seats_avail == 0) {
				GraphConstants.setGradientColor(map, Color.red);
			} else {
				GraphConstants.setGradientColor(map, Color.green);
			}
		}

		// Add a Border Color Attribute to the Map
		GraphConstants.setBorderColor(map, Color.black);

		// Add a White Background
		GraphConstants.setBackground(map, Color.white);

		// Make Vertex Opaque
		GraphConstants.setOpaque(map, true);

		// Do not allow double mouse click
		GraphConstants.setEditable(map, false);

		return map;
	}

	// Hook for subclassers
	private MyGraph createGraph() {

		MyGraph lgraph = new MyGraph(new DefaultGraphModel());
		return lgraph;
	}

	private void initComponents() {
		// Use Border Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0; // Generated
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH; // Generated
		gridBagConstraints.gridy = 0; // Generated
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;

		// Add the Graph as Center Component
		tf_ = new TableShowFilterPanel();
		tf_.setParent(this);
		add(tf_, gridBagConstraints);
		add(new JScrollPane(graph_), gridBagConstraints1);
		
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 3;
		gridBagConstraints3.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
		JPanel bpanel = new JPanel();
		JButton printButton = new javax.swing.JButton();

		printButton.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Print16.gif")));
		printButton.setText("Print");
		printButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new ComponentPrinter(graph_).print();
			}
		});

		bpanel.add(printButton);
		add(bpanel,gridBagConstraints3);

	}

	private void insertVertexFromTable(Table t) {

		DefaultGraphCell cell = null;
		myUserClass m = new myUserClass();
		m.setTblType(t.getTblType());

		// Process room feature
		if (t.getTblType().equals(TableModel.CIRC_FEAT) || t.getTblType().equals(TableModel.RECT_FEAT)) {
			m.setNumSeats(0);
			m.setSeatsAvail(0);
		}
		// Process table
		else {
			m.setNumSeats(t.getSeats().intValue());
			m.setSeatsAvail(seatsAvail(show_, t));
		}
		m.setTableLabel(t.getLabel());
		m.setTableNumber(t.getKey());

		if( t.getTblType().equals(TableModel.CIRC_FEAT) || t.getTblType().equals(TableModel.CIRC_TABLE) )
			cell = new CircleCell(m);
		else
			cell = new DefaultGraphCell(m);


		// Create a Map that holds the attributes for the Vertex
		cell.getAttributes().applyMap(createCellAttributes(t));

		// Insert the Vertex (including child port and attributes)
		graph_.getGraphLayoutCache().insert(cell);

		// Move room features to back
		if (m.getNumSeats() == 0) {
			Object c[] = { cell };
			graph_.getGraphLayoutCache().toBack(c);
		}

	}

	@Override
	public void refresh() {
		// Read the data base table layout

		// Collect all cells
		Object[] allCells = graph_.getDescendants(graph_.getRoots());

		// Remove the existing layout
		graph_.getGraphLayoutCache().remove(allCells);

		try {
			Collection<Show> c = tf_.getMatchingEntities();
			if (c.size() != 1) {
				show_ = null;
				graph_.setShow(show_);
				return;
			}
			show_ = c.iterator().next();
			graph_.setShow(show_);
			Collection<Table> tables = TableModel.getReference().getTablesForLayout(
					show_.getLayout().intValue());
			for( Table t : tables) {
				insertVertexFromTable(t);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

	}

}
