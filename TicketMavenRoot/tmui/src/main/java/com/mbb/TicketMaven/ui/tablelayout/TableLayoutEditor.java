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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TableModel;
import com.mbb.TicketMaven.model.entity.Layout;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.Table;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.filter.TableLayoutFilterPanel;
import com.mbb.TicketMaven.ui.util.CircleView;
import com.mbb.TicketMaven.ui.util.ComponentPrinter;
import com.mbb.TicketMaven.util.Errmsg;

public class TableLayoutEditor extends ViewPanel implements
		GraphSelectionListener, LayoutChangeListener {

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

		// Construct the Graph using the Model as its Data Source
		public MyGraph(GraphModel model) {
			this(model, null);
			ToolTipManager.sharedInstance().registerComponent(this);
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
			setEnabled(false);
		}

		// Construct the Graph using the Model as its Data Source
		public MyGraph(GraphModel model, GraphLayoutCache cache) {
			super(model, cache);

			setGridEnabled(false);
		}

		// Display tool tip
		@Override
		public String getToolTipText(MouseEvent event) {
			Object cell = getFirstCellForLocation(event.getX(), event.getY());

			if (cell != null) {
				DefaultGraphCell d = (DefaultGraphCell) cell;
				myUserClass u = (myUserClass) d.getUserObject();
				String hover = "<html>";
				if (u.isRoomFeature()) {
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
						}
						if (t != null && t.getSeats() != null)
							hover += "<br>Seats: " + t.getSeats().intValue();
					}
				}
				hover += "</html>";

				return (hover);
			}
			return (null);
		}
	}

	// MarqueeHandler that Connects Vertices and Displays PopupMenus
	private class MyMarqueeHandler extends BasicMarqueeHandler {

		// Override to Gain Control (for PopupMenu and ConnectMode)
		@Override
		public boolean isForceMarqueeEvent(MouseEvent e) {
			if (e.isShiftDown())
				return false;

			// If Right Mouse Button we want to Display the PopupMenu
			if ((SwingUtilities.isRightMouseButton(e) || e.isControlDown())
					&& !e.isShiftDown())
				// Return Immediately
				return true;

			return super.isForceMarqueeEvent(e);
		}

		// Find Port under Mouse and Repaint Connector
		@Override
		public void mouseDragged(MouseEvent e) {
			// Call Superclass
			super.mouseDragged(e);
		}

		// Show Special Cursor if Over Port
		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
		}

		@SuppressWarnings("unused")
		protected boolean isPopupTrigger(MouseEvent e) {
			if (e == null)
				return false;

			return (SwingUtilities.isRightMouseButton(e) || e.isControlDown())
					&& !e.isShiftDown();
		}

		// Display PopupMenu or Remember Start Location and First Port
		@Override
		public void mousePressed(final MouseEvent e) {
			// If Right Mouse Button

			if ((SwingUtilities.isRightMouseButton(e) || e.isControlDown())
					&& !e.isShiftDown()) {
				// Find Cell in Model Coordinates
				Object cell = graph_
						.getFirstCellForLocation(e.getX(), e.getY());

				// Create PopupMenu for the Cell
				JPopupMenu menu = createPopupMenu(e.getPoint(), cell);

				// Display PopupMenu
				menu.show(graph_, e.getX(), e.getY());

				// Else if in ConnectMode and Remembered Port is Valid
			} else {
				// Call Superclass
				super.mousePressed(e);
			}
		}

		// Connect the First Port and the Current Port in the Graph or Repaint
		@Override
		public void mouseReleased(MouseEvent e) {

			graph_.repaint();

			// Call Superclass
			super.mouseReleased(e);
		}

	} // End of Editor.MyMarqueeHandler

	// User Class for Jgraph
	private static class myUserClass {

		private int num_seats;

		private String tableLabel;

		private int tableNumber;

		private String tblType;

		public int getNumSeats() {
			return num_seats;
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
			return tableLabel + " (" + num_seats + ")";
		}

		public String getTblType() {
			return tblType;
		}

		public void setTblType(String tblType) {
			this.tblType = tblType;
		}

		public boolean isRoomFeature() {
			if (tblType.equals(TableModel.CIRC_FEAT)
					|| tblType.equals(TableModel.RECT_FEAT))
				return true;
			return false;
		}
	}

	// JGraph instance
	private JGraph graph_;
	
	private JButton saveButton;

	private int layout_ = -1;

	// cell count that gets put in cell label
	private int tableCount = 1;

	private TableLayoutFilterPanel tf;

	// Construct an Editor Panel
	public TableLayoutEditor() {
		// Construct the Graph
		// addModel(TableModel.getReference());
		// addModel(LayoutModel.getReference());
		graph_ = createGraph();

		// Use a Custom Marquee Handler
		graph_.setMarqueeHandler(createMarqueeHandler());

		// Construct Command History
		//

		initComponents();
		installListeners(graph_);
	}

	// Hook for subclassers
	private Map<Object, Object> createCellAttributes(Point2D point, String tbltype) {
		Map<Object, Object> map = new Hashtable<Object, Object>();

		// Add a Bounds Attribute to the Map
		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 100, 100));

		// Add a nice looking gradient background
		if (tbltype.equals(TableModel.CIRC_TABLE)
				|| tbltype.equals(TableModel.RECT_TABLE)) {
			// Table
			GraphConstants.setGradientColor(map, Color.green);
		} else {
			// Room feature
			GraphConstants.setGradientColor(map, Color.orange);
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

	private Map<Object, Object> createCellAttributes(Table t) {
		Map<Object, Object> map = new Hashtable<Object, Object>();

		// Add a Bounds Attribute to the Map
		GraphConstants.setBounds(map, new Rectangle2D.Double(t.getX()
				.doubleValue(), t.getY().doubleValue(), t.getWidth()
				.doubleValue(), t.getHeight().doubleValue()));

		// Add a nice looking gradient background
		if (t.getTblType().equals(TableModel.CIRC_TABLE)
				|| t.getTblType().equals(TableModel.RECT_TABLE)) {
			// Table
			GraphConstants.setGradientColor(map, Color.green);
		} else {
			// Room feature
			GraphConstants.setGradientColor(map, Color.orange);
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

	private DefaultGraphCell createDefaultGraphCell(String tbltype) {
		DefaultGraphCell cell = null;
		myUserClass m = new myUserClass();
		m.setTableNumber(-1);
		m.setTblType(tbltype);

		// Process room feature
		if (tbltype.equals(TableModel.CIRC_FEAT)
				|| tbltype.equals(TableModel.RECT_FEAT)) {
			m.setTableLabel("");
			m.setNumSeats(0);
		}
		// Process table
		else {
			m.setTableLabel("Table " + Integer.toString(tableCount));
			m.setNumSeats(TableModel.getDefaultSeats());
			tableCount++;
		}

		if (tbltype.equals(TableModel.CIRC_FEAT)
				|| tbltype.equals(TableModel.CIRC_TABLE))
			cell = new CircleCell(m);
		else
			cell = new DefaultGraphCell(m);
		return cell;
	}

	// Hook for subclassers
	private JGraph createGraph() {

		JGraph lgraph = new MyGraph(new DefaultGraphModel());
		return lgraph;
	}

	// Hook for subclassers
	private BasicMarqueeHandler createMarqueeHandler() {
		return new MyMarqueeHandler();
	}

	//
	// PopupMenu
	//
	private JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();

		if (cell != null) {

			menu.add(new AbstractAction("Edit Name") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!graph_.isSelectionEmpty()) {
						Object[] cells = graph_.getSelectionCells();
						cells = graph_.getDescendants(cells);

						DefaultGraphCell c = (DefaultGraphCell) cells[0];
						myUserClass u = (myUserClass) c.getUserObject();

						String name = JOptionPane
								.showInputDialog("Enter New Name:");
						if (name != null)
							u.setTableLabel(name);
						try {
							saveToDB();
						} catch (Exception e1) {
							Errmsg.getErrorHandler().errmsg(e1);
						}
					}
				}
			});

			menu.add(new AbstractAction("Change Number of Seats") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!graph_.isSelectionEmpty()) {
						Object[] cells = graph_.getSelectionCells();
						cells = graph_.getDescendants(cells);

						DefaultGraphCell c = (DefaultGraphCell) cells[0];
						myUserClass u = (myUserClass) c.getUserObject();

						try {
							String num = JOptionPane.showInputDialog(
									"Enter Number of Seats", Integer.valueOf(u
											.getNumSeats()));
							if (num != null) {
								int i = Integer.parseInt(num);
								u.setNumSeats(i);
							}
							saveToDB();
						} catch (NumberFormatException nf) {
							Errmsg.getErrorHandler().notice("Please enter a number");
						} catch (Exception e1) {
							Errmsg.getErrorHandler().errmsg(e1);
						}
					}
				}
			});

			menu.add(new AbstractAction("Remove") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!graph_.isSelectionEmpty()) {
						Object[] cells = graph_.getSelectionCells();
						cells = graph_.getDescendants(cells);
						for (int i = 0; i < cells.length; i++) {
							DefaultGraphCell c = (DefaultGraphCell) cells[i];
							myUserClass u = (myUserClass) c.getUserObject();
							if (u.getTableNumber() != -1) {
								try {
									TableModel.getReference().delete(
											u.getTableNumber());

								} catch (Exception e1) {
									Errmsg.getErrorHandler().errmsg(e1);
								}
							}
						}
						graph_.getGraphLayoutCache().remove(cells);
						// refresh();
					}
				}
			});
		}

		menu.add(new AbstractAction("Insert Rectangular Table") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent ev) {
				insert(pt, TableModel.RECT_TABLE);
			}
		});

		menu.add(new AbstractAction("Insert Round Table") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent ev) {
				insert(pt, TableModel.CIRC_TABLE);
			}
		});

		menu.add(new AbstractAction("Insert Rectangular Room Feature") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent ev) {
				insert(pt, TableModel.RECT_FEAT);
			}
		});

		menu.add(new AbstractAction("Insert Circular Room Feature") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent ev) {
				insert(pt, TableModel.CIRC_FEAT);
			}
		});

		return menu;
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

		tf = new TableLayoutFilterPanel();
		tf.setParent(this);
		add(tf, gridBagConstraints);
		add(new JScrollPane(graph_), gridBagConstraints1);
		

		JPanel jPanel1 = new javax.swing.JPanel();

		saveButton = new javax.swing.JButton();

		saveButton.setIcon(new ImageIcon(getClass().getResource(
				"/resource/Save16.gif")));
		saveButton.setText("Save");
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					saveToDB();
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
				}
			}
		});

		jPanel1.add(saveButton);
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

		jPanel1.add(printButton);
		add(jPanel1, gridBagConstraints2);
	}

	// Insert a new Vertex at point
	private void insert(Point2D point, String tbltype) {

		// Construct Vertex with no Label
		DefaultGraphCell vertex = createDefaultGraphCell(tbltype);

		// Create a Map that holds the attributes for the Vertex
		vertex.getAttributes().applyMap(createCellAttributes(point, tbltype));

		// Insert the Vertex (including child port and attributes)
		graph_.getGraphLayoutCache().insert(vertex);

		// Move room features to back
		if (tbltype.equals(TableModel.CIRC_FEAT)
				|| tbltype.equals(TableModel.RECT_FEAT)) {
			Object c[] = { vertex };
			graph_.getGraphLayoutCache().toBack(c);
		}

	}

	private void insertVertexFromTable(Table t) {

		DefaultGraphCell cell = null;
		myUserClass m = new myUserClass();
		m.setTblType(t.getTblType());

		// Process room feature
		if (t.getTblType().equals(TableModel.CIRC_FEAT)
				|| t.getTblType().equals(TableModel.RECT_FEAT)) {
			m.setNumSeats(0);
		}
		// Process table
		else {
			m.setNumSeats(t.getSeats().intValue());
			tableCount++;
		}
		m.setTableLabel(t.getLabel());
		m.setTableNumber(t.getKey());

		if (t.getTblType().equals(TableModel.CIRC_FEAT)
				|| t.getTblType().equals(TableModel.CIRC_TABLE))
			cell = new CircleCell(m);
		else
			cell = new DefaultGraphCell(m);

		// Create a Map that holds the attributes for the Vertex
		cell.getAttributes().applyMap(createCellAttributes(t));

		// Insert the Vertex (including child port and attributes)
		graph_.getGraphLayoutCache().insert(cell);

		// Move room features to back
		if (m.isRoomFeature()) {
			Object c[] = { cell };
			graph_.getGraphLayoutCache().toBack(c);
		}

	}

	private void installListeners(JGraph graph) {

		// Update ToolBar based on Selection Changes
		graph.getSelectionModel().addGraphSelectionListener(this);
	}

	@Override
	public void layoutChange(Layout l) {
		if (l == null)
			layout_ = -1;
		else
			layout_ = l.getKey();
		refresh();
		
		// once shows are assigned, it is read/only
		boolean hasShows = false;
		if( l != null && !l.isNew())
		{
			try {
				Collection<Show> shows  = ShowModel.getReference().getShowsForLayout(l);
				if( !shows.isEmpty() )
					hasShows = true;
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
		
		saveButton.setEnabled(!hasShows);

	}

	@Override
	public void refresh() {
		// Read the data base table layout

		// Collect all cells
		Object[] allCells = graph_.getDescendants(graph_.getRoots());

		// Remove the existing layout
		graph_.getGraphLayoutCache().remove(allCells);

		// Reset cell count and room feature count
		tableCount = 1;

		if (layout_ == -1) {
			graph_.setEnabled(false);
			return;
		}
		try {
			graph_.setEnabled(true);
			Collection<Table> tables = TableModel.getReference().getTablesForLayout(
					layout_);
			for(Table t : tables ) {
				insertVertexFromTable(t);
			}
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

	}

	private void saveToDB() throws Exception {
		Object[] allCells = graph_.getDescendants(graph_.getRoots());
		TableModel myTableModel = TableModel.getReference();
		for (int i = 0; i < allCells.length; i++) {
			DefaultGraphCell c = (DefaultGraphCell) allCells[i];
			Map<?, ?> map = c.getAttributes();
			Rectangle2D rect = GraphConstants.getBounds(map);
			myUserClass u = (myUserClass) c.getUserObject();

			Table t = null;
			if (u.getTableNumber() == -1)
				t = myTableModel.newTable();
			else
				t = myTableModel.getTable(u.getTableNumber());

			t.setX(Integer.valueOf((int) rect.getX()));
			t.setY(Integer.valueOf((int) rect.getY()));
			t.setWidth(Integer.valueOf((int) rect.getWidth()));
			t.setHeight(Integer.valueOf((int) rect.getHeight()));
			t.setLabel(u.getTableLabel());
			t.setTblType(u.getTblType());
			if (u.isRoomFeature())
				t.setSeats(Integer.valueOf(0));
			else
				t.setSeats(Integer.valueOf(u.getNumSeats()));
			t.setLayout(Integer.valueOf(layout_));

			myTableModel.saveRecord(t);

		}

		refresh();
	}

	// From GraphSelectionListener Interface
	@Override
	public void valueChanged(GraphSelectionEvent e) {
		//empty
	}

	public void showLayout(Layout l) {
		this.tf.selectLayout(l);		
	}

}
