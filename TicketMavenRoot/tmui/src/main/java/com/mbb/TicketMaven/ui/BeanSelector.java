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

package com.mbb.TicketMaven.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.mbb.TicketMaven.model.KeyedEntityModel;
import com.mbb.TicketMaven.model.entity.KeyedEntity;
import com.mbb.TicketMaven.model.filter.KeyedEntityFilter;
import com.mbb.TicketMaven.ui.util.StripedTable;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

/**
 * A BeanSelector is a generic UI that shows a modal dialog with a table of
 * KeyedEntitys (Entitys) for the user to select from. A filter can be provided to
 * filter the shown rows. There is also the option to select single or multiple
 * rows.
 */
public class BeanSelector<T extends KeyedEntity> extends JDialog implements KeyListener{

	private static final long serialVersionUID = 1L;

	private Collection<T> rows_ = new ArrayList<T>(); // list of rows currently
	// displayed

	private KeyedEntityModel<T> model_; // the model that the Entitys are coming from

	private static ArrayList<KeyedEntity> list_ = new ArrayList<KeyedEntity>(); // list for returning
	// objects to the caller

	private KeyedEntityFilter<T> filter = null; // optional filter that picks only
	// certain beans
	
	// string to filter on
	private String keyFilter = "";

	private boolean multiple_ = false; // multiple selection flag

	// for displaying date/time if necessary
	private final SimpleDateFormat sdf = new SimpleDateFormat(Prefs
			.getPref(PrefName.DATEFORMAT));

	// custom cell renderer ro format date/time in the table with the format
	// above
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
			l.setText(sdf.format(d));
			return l;
		}
	}

	private TableCellRenderer defaultTCR_ = null;

	private String fields_[]; // names of fields in the bean to show

	/**
	 * Display a modal dialog allowing the user to select a single Bean from a
	 * table
	 * 
	 * @param mod
	 *            - the bean model
	 * @param tm
	 *            - the Swing table model
	 * @param fields
	 *            - the names of the bean fields to show
	 * @param f
	 *            the optional filter
	 * @return the selected bean or null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends KeyedEntity> T selectBean(KeyedEntityModel<T> mod,
			TableModel tm, String fields[], KeyedEntityFilter<T> f) {
		new BeanSelector<T>(mod, tm, fields, f, false, null).setVisible(true);
		if (list_.size() != 0) {
			T b = (T) list_.get(0);
			return b;
		}

		return null;
	}

	/**
	 * Display a modal dialog allowing the user to select multiple Beans from a
	 * table
	 * 
	 * @param mod
	 *            - the bean model
	 * @param tm
	 *            - the Swing table model
	 * @param fields
	 *            - the names of the bean fields to show
	 * @param f
	 *            the optional filter
	 * @param init
	 *            - the keys of the beans to mark as selected on initial display
	 * @return the collection of selected beans
	 */
	@SuppressWarnings("unchecked")
	public static <T extends KeyedEntity> Collection<T> selectBeans(
			KeyedEntityModel<T> mod, TableModel tm, String fields[],
			KeyedEntityFilter<T> f, Collection<Integer> init) {
		new BeanSelector<T>(mod, tm, fields, f, true, init).setVisible(true);
		return (Collection<T>) list_;
	}

	/**
	 * Display a modal dialog allowing the user to select multiple Beans from a
	 * table
	 * 
	 * @param mod
	 *            - the bean model
	 * @param tm
	 *            - the Swing table model
	 * @param fields
	 *            - the names of the bean fields to show
	 * @param f
	 *            the optional filter
	 * @return the collection of selected beans
	 */
	@SuppressWarnings("unchecked")
	public static <T extends KeyedEntity> Collection<T> selectBeans(
			KeyedEntityModel<T> mod, TableModel tm, String fields[], KeyedEntityFilter<T> f) {
		new BeanSelector<T>(mod, tm, fields, f, true, null).setVisible(true);
		return (Collection<T>) list_;
	}

	@SuppressWarnings("unchecked")
	private BeanSelector(KeyedEntityModel<T> mod, TableModel tm, String fields[],
			KeyedEntityFilter<T> f, boolean multiple, Collection<Integer> init) {

		super();
		setModal(true);
		setFocusable(true);
		addKeyListener(this);
		filter = f;
		model_ = mod;
		multiple_ = multiple;
		fields_ = fields;
		list_ = new ArrayList<KeyedEntity>();
		// init the gui components
		initComponents();
		
		jTable1.addKeyListener(this);

		// if the table model can sort, then do an initial sort by column 0
		if (tm != null) {
			jTable1.setModel(tm);
			if (tm instanceof TableSorter) {
				TableSorter ts = (TableSorter) tm;
				if (!ts.isSorted()) {
					ts.sortByColumn(0);
				}
			}
			
			
		}

		// install our custom date/time format
		defaultTCR_ = jTable1.getDefaultRenderer(Date.class);
		jTable1
				.setDefaultRenderer(java.util.Date.class,
						new DateTimeRenderer());
		
		// set the selection model
		if (multiple)
			jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		else
			jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// add a mouse listener to allow selection by double click
		jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTable1MouseClicked(evt);
			}
		});

		// create the window title
		if (multiple)
			this.setTitle("Select " + mod.getEntityName() + "s");
		else
			this.setTitle("Select " + mod.getEntityName());
		
		// load the table data
		loadData();

		// if initial selections have been passed in, select those rows
		if (init != null && tm != null) {

			Object[] oa = rows_.toArray();
			for (int r = 0; r < tm.getRowCount(); r++) {
				T b = (T) oa[r];
				Integer key = Integer.valueOf(b.getKey());
				if (init.contains(key)) {
					jTable1.addRowSelectionInterval(r, r);
					list_.add(b);
				}
			}

		}

		pack();

	}

	/**
	 * Load the data.
	 */
	private void loadData() {

		Collection<T> allrows = null;
		if (!model_.is_open())
			return;

		// if no filter, then load all rows from the model
		// if there is a filter, then let it load the rows
		try {
			if (filter != null)
				allrows = filter.getMatchingEntities();
			else
				allrows = model_.getRecords();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
			return;
		}

		// init the table to empty
		TableSorter tm = (TableSorter) jTable1.getModel();
		tm.addMouseListenerToHeaderInTable(jTable1);
		tm.setRowCount(0);

		// load the beans into the table using reflection to 
		// get the data for each column
		Iterator<T> it = allrows.iterator();
		while (it.hasNext()) {
			T r = it.next();

			try {
				Class<? extends KeyedEntity> beanClass = r.getClass();
				Object[] ro = new Object[fields_.length];
				for (int i = 0; i < fields_.length; i++) {
					// find method
					String method = "get" + fields_[i];
					Method m = beanClass.getDeclaredMethod(method,
							(Class[]) null);
					ro[i] = m.invoke(r, (Object[]) null);
				}
				
				// add the table row
				tm.addRow(ro);
				rows_.add(r);
				tm.tableChanged(new TableModelEvent(tm));
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
				return;
			}

		}

	}

	/**
	 * Inits the components.
	 */
	private void initComponents()// GEN-BEGIN:initComponents
	{

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0; // Generated
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH; // Generated
		gridBagConstraints.gridy = 1; // Generated
		this.getContentPane().setLayout(new GridBagLayout()); // Generated

		jTable1 = new StripedTable();
		jPanel1 = new javax.swing.JPanel();
		selectButton = new javax.swing.JButton();

		jTable1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(
				0, 0, 0)));

		DefaultListSelectionModel mylsmodel = new DefaultListSelectionModel();
		mylsmodel
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable1.setSelectionModel(mylsmodel);
		jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTable1MouseClicked(evt);
			}
		});

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setPreferredSize(new java.awt.Dimension(554, 404));
		jScrollPane1.setViewportView(jTable1);

		selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Add16.gif")));
		if (multiple_)
			selectButton.setText("Select " + model_.getEntityName() + "s");
		else
			selectButton.setText("Select " + model_.getEntityName());
		selectButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectbuttonActionPerformed(evt);
			}
		});

		jPanel1.add(selectButton);

		clearButton = new JButton();
		clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Undo16.gif")));
		clearButton.setText("Clear Selection");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearbuttonActionPerformed(evt);
			}
		});

		jPanel1.add(clearButton);
		
		filterLabel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		filterLabel.setVisible(false);
		jPanel1.add(filterLabel);

		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();

		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		this.getContentPane().add(jScrollPane1, gridBagConstraints1);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;

		this.getContentPane().add(jPanel1, gridBagConstraints2); // Generated

	}

	/**
	 * J table1 mouse clicked.
	 * 
	 * @param evt
	 *            the evt
	 */
	private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() > 1) {
			selectbuttonActionPerformed(null);
		}
	}

	/**
	 * Selectbutton action performed.
	 * 
	 * @param evt
	 *            the evt
	 */
	private void selectbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		// figure out which row is selected.
		list_.clear();
		int index[] = jTable1.getSelectedRows();
		if (index.length == 0)
			return;

		for (int i = 0; i < index.length; i++) {
			try {
				// need to ask the table for the original (befor sorting) index
				// of
				// the selected row
				TableSorter tm = (TableSorter) jTable1.getModel();
				int k = tm.getMappedIndex(index[i]); // get original index - not
				// current sorted position in
				// tbl
				Object[] oa = rows_.toArray();
				KeyedEntity b = (KeyedEntity) oa[k];
				list_.add(b);

			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
		this.dispose();
	}

	
	private void clearbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		list_.clear();
		this.dispose();
	}

	private javax.swing.JPanel jPanel1;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.JTable jTable1;

	private javax.swing.JButton selectButton;

	private JButton clearButton;
	
	private JLabel filterLabel = new JLabel();

	@Override
	public void keyPressed(KeyEvent arg0) {
		if( arg0.getKeyChar() == KeyEvent.VK_ESCAPE)
			keyFilter = "";
		
		if( arg0.getKeyChar() == KeyEvent.VK_BACK_SPACE && !keyFilter.isEmpty())
			keyFilter = keyFilter.substring(0, keyFilter.length() - 1);
		
		filterLabel.setText(keyFilter);
		if( keyFilter.isEmpty())
			filterLabel.setVisible(false);

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		//ignore
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
		if( Character.isLetterOrDigit(arg0.getKeyChar()))
			keyFilter += arg0.getKeyChar();
		else
			return;
		
		filterLabel.setText(keyFilter);
		filterLabel.setVisible(true);

		// filter by the filter String
		if( !keyFilter.isEmpty() )
		{
			int filterColumn = -1;
			
			TableModel tm = jTable1.getModel();
			if (tm instanceof TableSorter) {
				TableSorter ts = (TableSorter) tm;
				
				for( int col = 0;col < ts.getColumnCount(); col++)
				{
					if( ts.getSortingStatus(col) != 0 )
					{
						filterColumn = col;
						break;
					}
				}
				
				//System.out.println("filterC=" + filterColumn + " fstr=" + keyFilter  );
				if( filterColumn == -1) return;
				
				for( int row = 0; row < ts.getRowCount(); row++ )
				{
					Object value = ts.getValueAt(row, filterColumn);
					if( value instanceof String)
					{
						if( ((String)value).toUpperCase().startsWith(keyFilter.toUpperCase()))
						{
							//System.out.println("value=" + value);
							jTable1.scrollRectToVisible(jTable1.getCellRect(0, 0, true));
							jTable1.scrollRectToVisible(jTable1.getCellRect(row, filterColumn, true));
							break;
						}
					}
				}
			}
			
		}
	}


}
