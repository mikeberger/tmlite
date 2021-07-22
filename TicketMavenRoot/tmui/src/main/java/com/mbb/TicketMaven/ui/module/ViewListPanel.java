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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.mbb.TicketMaven.model.CascadeDeleteProvider;
import com.mbb.TicketMaven.model.KeyedEntityModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.filter.KeyedEntityFilter.MaxRowsException;
import com.mbb.TicketMaven.ui.ViewPanel;
import com.mbb.TicketMaven.ui.detail.ViewDetailPanel;
import com.mbb.TicketMaven.ui.filter.FilterPanel;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TablePrinter;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Money;
import com.mbb.TicketMaven.util.Warning;

/**
 * Class ViewListPanel provides a panel that manages a particular Entity - aka
 * KeyedEntity - aka Bean - aka basic TciketMaven object. Within the Panel will
 * be: an optional filter at the top to filter displayed rows in the object
 * table, in the middle - table of Entitys that can sort by columns and is used
 * to select a DOT for edit, a detailed editing form at the bottom, and a common
 * set of buttons at the way bottom to perform actions like Edit New, Delete,
 * Save, etc.... By constructing the ViewListPanel with different arguments, you
 * can set up a wide variety of UIs to manage Entitys.
 */
class ViewListPanel<T extends KeyedEntity> extends ViewPanel {

	/**
	 * A custom TableCellRendererso our Date/Time cells format nicely
	 */
	private class DateTimeRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public DateTimeRenderer() {
			super();
			setOpaque(true); // MUST do this for background to show up.
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel l = (JLabel) defaultTCR_.getTableCellRendererComponent(
					table, obj, isSelected, hasFocus, row, column);
			Date d = (Date) obj;
			if (d != null)
				l.setText(sdf.format(d));
			return l;
		}
	}

	/**
	 * A custom TableCellRendererso our Money cells format like Money
	 */
	private class MoneyRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public MoneyRenderer() {
			super();
			setOpaque(true); // MUST do this for background to show up.
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel l = (JLabel) defaultTCR_.getTableCellRendererComponent(
					table, obj, isSelected, hasFocus, row, column);
			Integer i = (Integer) obj;
			if (i != null)
				l.setText(Money.format(i.intValue()));
			else
				l.setText(Money.format(0));
			return l;
		}
	}

	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel buttonPanel; // the action buttons

	private TableCellRenderer defaultTCR_ = null;

	private ViewDetailPanel<T> detailPanel = null; // the detail single-object
	// editing Form

	private String fields_[];

	private FilterPanel<T> filterPanel = null; // the filter panel at the top

	private JPanel filterParentPanel = null;

	private JButton newbutton = new JButton();

	private JPanel jPanel = null;

	private javax.swing.JScrollPane jScrollPane1; // the scrollpane around the
	// table

	private javax.swing.JTable jTable1; // the table in the middle

	private KeyedEntityModel<T> model_; // the model supplying the objects

	private TitledBorder newtb;

	private TitledBorder normaltb;

	private Collection<T> rows_ = new ArrayList<T>(); // list of rows currently
	// displayed

	private JLabel rowWarningLabel = new JLabel(
			"Too Many Rows to Display. Please filter the results and if necessary, clean up old shows.");
	private javax.swing.JButton savebutton;


	private JSplitPane splitPane = null;

	/**
	 * Instantiates a new view list panel.
	 * 
	 * @param mod
	 *            the model supplying the objects
	 * @param dp
	 *            the ViewDetailPane - i.e. the form for editing a single object
	 *            of the type being managed
	 * @param fp
	 *            the The optional filter panel to filter the rows of the table
	 * @param tm
	 *            the TableModel passed in so that this one object can manage
	 *            all sorts of different data sets
	 * @param fields
	 *            the fields from the managed Entitys that are shown in the
	 *            table
	 */
	public ViewListPanel(KeyedEntityModel<T> mod, ViewDetailPanel<T> dp,
			FilterPanel<T> fp, TableModel tm, String fields[]) {

		super();
		detailPanel = dp;
		filterPanel = fp;
		if (fp != null)
			fp.setParent(this);
		model_ = mod;
		addModel(mod);
		fields_ = fields;
		// init the gui components

		// these are the 2 titled borders that can go around the detailed
		// editing form
		// the first is for when the form is editing an object in the table
		normaltb = javax.swing.BorderFactory.createTitledBorder(null, "Edit "
				+ model_.getEntityName() + " Information",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null);
		// the second is shown when the form is editing a brand new object that
		// is not in the table
		newtb = javax.swing.BorderFactory.createTitledBorder(null, "Enter New "
				+ model_.getEntityName(),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
				Color.RED);

		initComponents();

		if (tm != null) {
			jTable1.setModel(tm);
		}

		// set our custom renderers
		defaultTCR_ = jTable1.getDefaultRenderer(Date.class);
		jTable1.setDefaultRenderer(java.util.Date.class, new DateTimeRenderer());
		jTable1.setDefaultRenderer(Money.class, new MoneyRenderer());

		// only allow single selection in the table
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		refresh();

	}

	/**
	 * create an Object array from the fields in a Bean to show in a table. Use
	 * the passed in array of field names and reflection.
	 * 
	 */
	private Object[] beanToRow(T r) throws Exception {
		Class<? extends KeyedEntity> beanClass = r.getClass();
		Object[] ro = new Object[fields_.length];
		for (int i = 0; i < fields_.length; i++) {
			// find method
			if (fields_[i].equals("Key")) {
				ro[i] = Integer.valueOf(r.getKey());
			} else {
				String method = "get" + fields_[i];
				Method m = beanClass.getDeclaredMethod(method, (Class[]) null);
				ro[i] = m.invoke(r, (Object[]) null);
			}
		}
		return ro;
	}

	/**
	 * Copy button action performed.
	 * 
	 * @param evt
	 *            the evt
	 */
	@SuppressWarnings("unchecked")
	private void copybuttonActionPerformed(java.awt.event.ActionEvent evt) {

		// figure out which row is selected
		int[] indices = jTable1.getSelectedRows();
		if (indices.length != 1) {
			Errmsg.getErrorHandler().notice(
					"Please select a " + model_.getEntityName());
			return;
		}
		int index = indices[0];
		TableSorter tm = (TableSorter) jTable1.getModel();
		int k = tm.getMappedIndex(index);
		Object[] oa = rows_.toArray();
		T item = (T) oa[k];
		try {
			// copy the selected item
			detailPanel.copyItem(item);
		} catch (Warning w) {
			Errmsg.getErrorHandler().notice(w.getMessage());
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

		model_.refresh();
	}

	/**
	 * Delete button action performed.
	 * 
	 * @param evt
	 *            the evt
	 */
	@SuppressWarnings("unchecked")
	private void delbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		// figure out which row is selected
		int[] indices = jTable1.getSelectedRows();
		if (indices.length == 0) {
			Errmsg.getErrorHandler().notice(
					"Please select a " + model_.getEntityName() + " first");
			return;
		}

		// confirm the deletion with the user
		int ret = JOptionPane.showConfirmDialog(null,
				"Really Delete " + model_.getEntityName() + "?", "Delete",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (ret != JOptionPane.OK_OPTION)
			return;

		T entity = null;
		for (int i = 0; i < indices.length; ++i) {
			int index = indices[i];
			try {
				// need to ask the table for the original (befor sorting) index
				// of the selected row
				TableSorter tm = (TableSorter) jTable1.getModel();
				int k = tm.getMappedIndex(index); // get original index - not
				// current sorted position
				// in tbl
				Object[] oa = rows_.toArray();
				entity = (T) oa[k];

				// delete the object
				deleteEntity(entity);
			} catch (SQLException se) {

				/*
				 * 
				 * boy it woul dbe nice if HSQL did a good job of reporting what
				 * foreign key is preventing a delete, but it doesn't. For now,
				 * the user gets a generic error and really doesn't know what
				 * other object depends on the one they are trying to delete
				 */
				if (se.getSQLState().equalsIgnoreCase("23000")) {

					if (model_ instanceof CascadeDeleteProvider) {
						CascadeDeleteProvider<T> cdp = (CascadeDeleteProvider<T>) model_;
						String warn = cdp.getCascadeDeleteWarning();
						if (warn != null) {
							int ok = JOptionPane.showConfirmDialog(null, warn,
									"Confirm", JOptionPane.YES_NO_OPTION);

							if (ok == JOptionPane.NO_OPTION)
								return;
						}
						cdp.cascadeDelete(entity);
					} else {
						Errmsg.getErrorHandler()
								.notice("Cannot delete record. Another record references it");
						se.printStackTrace();
					}
				} else {
					Errmsg.getErrorHandler().errmsg(se);
				}
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}

		// switch back to New Object mode
		newbuttonActionPerformed(evt);
		model_.refresh();
	}

	protected void deleteEntity(T entity) throws Exception {
		model_.delete(entity);
	}

	/**
	 * Sets the UI to be editing a brand new object
	 */
	private void editNew() {

		jTable1.clearSelection();

		// if the detailed form supports it, data on the form from the last save
		// will be kept
		// for editing the next object - to save time when repeating data entry
		// for similar objects
		// like when all requests for a show are entered in a row
		T r = detailPanel.carryForward();
		if (r == null)
			r = model_.newRecord();
		r.setKey(-1);

		// show the new record in the detailed form
		detailPanel.showData(r);

		// switch the border to the neato Edit New border
		jPanel.setBorder(newtb);
		
		newbutton.setEnabled(false);

		jPanel.repaint();

		// whenever switching objects in the detail form - reset the divider of
		// the split pane
		// as the detail form can drastically change size - mainly the show form
		splitPane.resetToPreferredSizes();

	}

	/**
	 * Show an object in the detailed form for editing
	 */
	@SuppressWarnings("unchecked")
	private void editRow() {

		// figure out which row is selected.
		int index = jTable1.getSelectedRow();
		if (index == -1)
			return;
		// ensure only one row is selected.
		jTable1.getSelectionModel().setSelectionInterval(index, index);

		try {
			// need to ask the table for the original (befor sorting) index of
			// the selected row
			TableSorter tm = (TableSorter) jTable1.getModel();
			int k = tm.getMappedIndex(index); // get original index - not
			// current sorted position in
			// tbl
			Object[] oa = rows_.toArray();
			T b = (T) oa[k];
			ViewDetailPanel<T> dp = getDetailView();

			// show the selected object
			if (dp != null)
				dp.showData(b);

			// set the border to the Editing border
			jPanel.setBorder(normaltb);
			newbutton.setEnabled(true);
			jPanel.repaint();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}

		// whenever switching objects in the detail form - reset the divider of
		// the split pane
		// as the detail form can drastically change size - mainly the show form
		splitPane.resetToPreferredSizes();

	}

	/**
	 * Gets the button panel.
	 * 
	 * @return the button panel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new javax.swing.JPanel();
			savebutton = new javax.swing.JButton();
			JButton delbutton = new javax.swing.JButton();

			newbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
					"/resource/Add16.gif")));
			newbutton.setText("Enter New " + model_.getEntityName());
			newbutton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					newbuttonActionPerformed(evt);
				}
			});

			buttonPanel.add(newbutton);

			savebutton.setIcon(new ImageIcon(getClass().getResource(
					"/resource/Save16.gif")));
			savebutton.setText("Save " + model_.getEntityName());
			savebutton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					savebuttonActionPerformed(evt);
				}
			});

			buttonPanel.add(savebutton);

			delbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
					"/resource/Delete16.gif")));
			delbutton.setText("Delete " + model_.getEntityName());
			delbutton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					delbuttonActionPerformed(evt);
				}
			});

			buttonPanel.add(delbutton);

			// only show the copy button for objects that support copy
			if (detailPanel.canCopy()) {
				JButton copyButton = new JButton("Copy "
						+ model_.getEntityName());
				copyButton.setIcon(new javax.swing.ImageIcon(getClass()
						.getResource("/resource/Copy16.gif")));
				copyButton
						.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(
									java.awt.event.ActionEvent evt) {
								copybuttonActionPerformed(evt);
							}
						});

				buttonPanel.add(copyButton);
			}

			JButton printButton = new JButton();
			printButton.setIcon(new javax.swing.ImageIcon(getClass()
					.getResource("/resource/Print16.gif")));
			printButton.setText("Print List");
			printButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					try {
						TablePrinter.printTable(jTable1);
					} catch (Exception e) {
						Errmsg.getErrorHandler().errmsg(e);
					}
				}
			});

			buttonPanel.add(printButton);

		}
		return buttonPanel;
	}

	/**
	 * This method initializes showView.
	 * 
	 * @return com.mbb.TicketMaven.ui.ShowView
	 */
	private ViewDetailPanel<T> getDetailView() {
		if (detailPanel != null && !detailPanel.isShowing()) {
			detailPanel.showData(model_.newRecord());
		}
		return detailPanel;
	}

	/**
	 * This method initializes filterParentPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFilterParentPanel() {
		if (filterParentPanel == null) {
			filterParentPanel = new JPanel();
			if (filterPanel != null) {
				GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
				gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 0);
				gridBagConstraints3.gridy = 0;
				gridBagConstraints3.weighty = 1.0D;
				gridBagConstraints3.weightx = 1.0D;
				gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
				gridBagConstraints3.gridx = 0;
				filterParentPanel.setLayout(new GridBagLayout());
				filterParentPanel
						.setBorder(javax.swing.BorderFactory
								.createTitledBorder(
										null,
										"Filter Criteria",
										javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
										javax.swing.border.TitledBorder.DEFAULT_POSITION,
										null, null));
				filterParentPanel.add(filterPanel, gridBagConstraints3);
			}
			filterParentPanel.setVisible(true);
		}
		return filterParentPanel;
	}

	/**
	 * Gets the j panel.
	 * 
	 * @return the j panel
	 */
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
			jPanel.setBorder(normaltb);

			ViewDetailPanel<T> dp = getDetailView();
			if (dp != null)
				jPanel.add(dp, gridBagConstraints3);
		}
		return jPanel;
	}

	/**
	 * Inits the components.
	 */
	private void initComponents()// GEN-BEGIN:initComponents
	{

		this.setLayout(new GridBagLayout());

		jTable1 = new JTable();

		jTable1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(
				0, 0, 0)));

		DefaultListSelectionModel mylsmodel = new DefaultListSelectionModel();
		mylsmodel
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable1.setSelectionModel(mylsmodel);
		jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				jTable1MouseClicked(evt);
			}
		});

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setPreferredSize(new java.awt.Dimension(554, 404));
		jScrollPane1.setViewportView(jTable1);

		rowWarningLabel.setForeground(Color.BLUE);
		this.add(rowWarningLabel,
				GridBagConstraintsFactory.create(0, 2, GridBagConstraints.BOTH));
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jScrollPane1,
				getJPanel());
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1D);
		this.add(splitPane, GridBagConstraintsFactory.create(0, 3,
				GridBagConstraints.BOTH, 1.0, 1.0));

		this.add(getButtonPanel(),
				GridBagConstraintsFactory.create(0, 4, GridBagConstraints.BOTH));

		/*
		 * only show the filter panel if it is not null
		 */
		if (filterPanel != null) {

			/*
			 * the filter panel can be hidden and it starts out hidden. when not
			 * shown, a button is shown that can unhide it
			 */
			this.add(getFilterParentPanel(), GridBagConstraintsFactory.create(
					0, 1, GridBagConstraints.BOTH));
			
			rowWarningLabel.setVisible(false);

		}
	}

	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
		editRow();
	}

	private void newbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		editNew();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mbb.TicketMaven.ui.ViewPanel#refresh()
	 */
	@Override
	public void refresh() {

		// reload table when the model changes
		TableSorter tm = (TableSorter) jTable1.getModel();

		Collection<T> allrows = null;
		rows_.clear();
		if (!model_.is_open())
			return;
		try {
			// load all rows if no filter, otherwise load just matching rows
			if (filterPanel != null)
				allrows = filterPanel.getMatchingEntities();
			else
				allrows = model_.getRecords();

			rowWarningLabel.setVisible(false);

		} catch (MaxRowsException me) {
			rowWarningLabel.setVisible(true);
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		// init the table to empty
		tm.addMouseListenerToHeaderInTable(jTable1);
		tm.setRowCount(0);

		if (allrows != null) {
			Iterator<T> it = allrows.iterator();
			while (it.hasNext()) {
				T r = it.next();

				try {
					Object ro[] = beanToRow(r);
					tm.addRow(ro);
					rows_.add(r);
					tm.tableChanged(new TableModelEvent(tm));
				} catch (Exception e) {
					Errmsg.getErrorHandler().errmsg(e);
					return;
				}

			}
		}

		// sort the table by column 1 - seems to be good for most tables
		tm.sortByColumn(1);

		// after loading, reset the detail form to edit a new object
		editNew();

		// whenever switching objects in the detail form - reset the divider of
		// the split pane
		// as the detail form can drastically change size - mainly the show form
		splitPane.resetToPreferredSizes();

	}

	private void savebuttonActionPerformed(java.awt.event.ActionEvent evt) {
		try {

			// tell the detail form to save its data
			detailPanel.saveData();

		} catch (SQLException se) {

			if (se.getSQLState().equalsIgnoreCase("23000")) {
				Errmsg.getErrorHandler()
						.notice(detailPanel.getDuplicateError());
			} else {
				Errmsg.getErrorHandler().errmsg(se);
			}
			return;
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		// switch to editing a new object
		newbuttonActionPerformed(evt);

	}

}
