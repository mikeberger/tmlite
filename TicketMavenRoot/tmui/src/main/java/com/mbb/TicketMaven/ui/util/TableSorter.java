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
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;



/* non-copyrighted code updated by Mike Berger 
 * 
 * - lots didn't work out of the box. Plus code was added to make this object
 *   compatible with the old TableSorter so the rest of the code did not have to change
 */
/**
 * Class that adds sorting to a JTable.
 */
@SuppressWarnings("unchecked")

public class TableSorter extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;

	/** The table model. */
    private TableModel tableModel;

    private static final int DESCENDING = -1;
    private static final int NOT_SORTED = 0;
    private static final int ASCENDING = 1;

    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

	/** The Constant COMPARABLE_COMAPRATOR. */
	private static final Comparator<Object> COMPARABLE_COMAPRATOR = new Comparator<Object>() {
        @Override
		public int compare(Object o1, Object o2) {
            return ((Comparable<Object>) o1).compareTo(o2);
        }
    };
    
    /** The Constant LEXICAL_COMPARATOR. */
    private static final Comparator<Object> LEXICAL_COMPARATOR = new Comparator<Object>() {
        @Override
		public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };
    
    

    private Row[] viewToModel;
    
    private int[] modelToView;

    /** The table header. */
    private JTableHeader tableHeader;


    /** The column comparators. */
    private Map<Class<?>, Comparator<?>> columnComparators = new HashMap<Class<?>, Comparator<?>>();
    
    /** The sorting columns. */
    private List<Directive> sortingColumns = new ArrayList<Directive>();

    /**
     * Instantiates a new table sorter.
     */
    public TableSorter() {
    	//empty
    }

    /**
     * Instantiates a new table sorter.
     * 
     * @param tableModel the table model
     */
    public TableSorter(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    /**
     * Instantiates a new table sorter.
     * 
     * @param tableModel the table model
     * @param tableHeader the table header
     */
    public TableSorter(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    /**
     * Clear sorting state.
     */
    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    /**
     * Gets the table model.
     * 
     * @return the table model
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * Sets the table model.
     * 
     * @param tableModel the new table model
     */
    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    /**
     * Gets the table header.
     * 
     * @return the table header
     */
    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    /**
     * Sets the table header.
     * 
     * @param tableHeader the new table header
     */
    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener_);
            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
            }
        }
        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener_);
            this.tableHeader.setDefaultRenderer(
                    new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
        }
    }

    /**
     * Checks if is sorting.
     * 
     * @return true, if is sorting
     */
    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    /**
     * Gets the directive.
     * 
     * @param column the column
     * 
     * @return the directive
     */
    private Directive getDirective(int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            Directive directive = sortingColumns.get(i);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    /**
     * Gets the sorting status.
     * 
     * @param column the column
     * 
     * @return the sorting status
     */
    public int getSortingStatus(int column) {
        return getDirective(column).direction;
    }

    /**
     * Sorting status changed.
     */
    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();
        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    /**
     * Sets the sorting status.
     * 
     * @param column the column
     * @param status the status
     */
    public void setSortingStatus(int column, int status) {
        Directive directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }
        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }
        sortingStatusChanged();
    }

    /**
     * Gets the header renderer icon.
     * 
     * @param column the column
     * @param size the size
     * 
     * @return the header renderer icon
     */
    protected Icon getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    /**
     * Cancel sorting.
     */
    private void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    /**
     * Sets the column comparator.
     * 
     * @param type the type
     * @param comparator the comparator
     */
    public void setColumnComparator(Class<?> type, Comparator<?> comparator) {
        if (comparator == null) {
            columnComparators.remove(type);
        } else {
            columnComparators.put(type, comparator);
        }
    }

    /**
     * Gets the comparator.
     * 
     * @param column the column
     * 
     * @return the comparator
     */
    protected Comparator<Object> getComparator(int column) {
        Class<?> columnType = tableModel.getColumnClass(column);
        Comparator<Object> comparator = (Comparator<Object>) columnComparators.get(columnType);
        if (comparator != null) {
            return comparator;
        }
        if (Comparable.class.isAssignableFrom(columnType)) {
            return COMPARABLE_COMAPRATOR;
        }
        return LEXICAL_COMPARATOR;
    }

    /**
     * Gets the view to model.
     * 
     * @return the view to model
     */
    private Row[] getViewToModel() {
        if (viewToModel == null) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }
        return viewToModel;
    }

    /**
     * Model index.
     * 
     * @param viewIndex the view index
     * 
     * @return the int
     */
    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }

    /**
     * Gets the model to view.
     * 
     * @return the model to view
     */
    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    // TableModel interface methods 

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
	public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
	public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
	public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
	public Class<?> getColumnClass(int column) {
        return tableModel.getColumnClass(column);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
	public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
	public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
	public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    // Helper classes
    
    /**
     * The Class Row.
     */
    private class Row implements Comparable<Object> {
        
        /** The model index. */
        private int modelIndex;

        /**
         * Instantiates a new row.
         * 
         * @param index the index
         */
        public Row(int index) {
            this.modelIndex = index;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
		public int compareTo(Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;

            for (Iterator<Directive> it = sortingColumns.iterator(); it.hasNext();) {
                Directive directive = it.next();
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;
                // Define null less than everything, except null.
                if (o1 == null && o2 == null) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                    comparison = getComparator(column).compare(o1, o2);
                }
                if (comparison != 0) {
                    return directive.direction == DESCENDING ? -comparison : comparison;
                }
            }
            return 0;
        }
    }

    /** The table model listener. */
    private TableModelListener tableModelListener = new TableModelListener (){
        @Override
		public void tableChanged(TableModelEvent e) {
            // If we're not sorting by anything, just pass the event along.             
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);
                return;
            }
                
            // If the table structure has changed, cancel the sorting; the             
            // sorting columns may have been either moved or deleted from             
            // the model. 
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);
                return;
            }

            // We can map a cell event through to the view without widening             
            // when the following conditions apply: 
            // 
            // a) all the changes are on one row (e.getFirstRow() == e.getLastRow()) and, 
            // b) all the changes are in one column (column != TableModelEvent.ALL_COLUMNS) and,
            // c) we are not sorting on that column (getSortingStatus(column) == NOT_SORTED) and, 
            // d) a reverse lookup will not trigger a sort (modelToView != null)
            //
            // Note: INSERT and DELETE events fail this test as they have column == ALL_COLUMNS.
            // 
            // The last check, for (modelToView != null) is to see if modelToView 
            // is already allocated. If we don't do this check; sorting can become 
            // a performance bottleneck for applications where cells  
            // change rapidly in different parts of the table. If cells 
            // change alternately in the sorting column and then outside of             
            // it this class can end up re-sorting on alternate cell updates - 
            // which can be a performance problem for large tables. The last 
            // clause avoids this problem. 
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow()
                    && column != TableModelEvent.ALL_COLUMNS
                    && getSortingStatus(column) == NOT_SORTED
                    && modelToView != null) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(TableSorter.this, 
                                                     viewIndex, viewIndex, 
                                                     column, e.getType()));
                return;
            }

            // Something has happened to the data that may have invalidated the row order. 
            clearSortingState();
            fireTableDataChanged();
            return;
        }
    };

    /** The mouse listener_. */
    private MouseAdapter mouseListener_ = new MouseAdapter (){
        @Override
		public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
                int status = getSortingStatus(column);
                if (!e.isControlDown()) {
                    cancelSorting();
                }
                // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or 
                // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed. 
                status = status + (e.isShiftDown() ? -1 : 1);
                status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
                setSortingStatus(column, status);
            }
        }
    };
    
    /**
     * The Class Arrow.
     */
    private static class Arrow implements Icon {
        
        /** The descending. */
        private boolean descending;
        
        /** The size. */
        private int size;
        
        /** The priority. */
        private int priority;

        /**
         * Instantiates a new arrow.
         * 
         * @param descending the descending
         * @param size the size
         * @param priority the priority
         */
        public Arrow(boolean descending, int size, int priority) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
		public void paintIcon(Component c, Graphics g, int x, int y_in) {
        	int y = y_in;
            Color color = c == null ? Color.GRAY : c.getBackground();             
            // In a compound sort, make each succesive triangle 20% 
            // smaller than the previous one. 
            int dx = (int)(size/2*Math.pow(0.8, priority));
            int dy = descending ? dx : -dx;
            // Align icon (roughly) with font baseline. 
            y = y + 5*size/6 + (descending ? -dy : 0);
            int shift = descending ? 1 : -1;
            g.translate(x, y);

            // Right diagonal. 
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);
            
            // Left diagonal. 
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);
            
            // Horizontal line. 
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }
            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconWidth()
         */
        @Override
		public int getIconWidth() {
            return size;
        }

        /* (non-Javadoc)
         * @see javax.swing.Icon#getIconHeight()
         */
        @Override
		public int getIconHeight() {
            return size;
        }
    }

    /**
     * The Class SortableHeaderRenderer.
     */
    private class SortableHeaderRenderer implements TableCellRenderer {
        
        /** The table cell renderer. */
        private TableCellRenderer tableCellRenderer;

        /**
         * Instantiates a new sortable header renderer.
         * 
         * @param tableCellRenderer the table cell renderer
         */
        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
		public Component getTableCellRendererComponent(JTable table, 
                                                       Object value,
                                                       boolean isSelected, 
                                                       boolean hasFocus,
                                                       int row, 
                                                       int column) {
            Component c = tableCellRenderer.getTableCellRendererComponent(table, 
                    value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setHorizontalTextPosition(SwingConstants.LEFT);
                int modelColumn = table.convertColumnIndexToModel(column);
                l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
            }
            return c;
        }
    }

    /**
     * The Class Directive.
     */
    private static class Directive {
        
        /** The column. */
        private int column;
        
        /** The direction. */
        private int direction;

        /**
         * Instantiates a new directive.
         * 
         * @param column the column
         * @param direction the direction
         */
        public Directive(int column, int direction) {
            this.column = column;
            this.direction = direction;
        }
    }
    
    
    
    // ===============================================================================
    // compatibility with the old SUN TableSorter
    // ===============================================================================
    
    /**
     * The Class NewTableModel.
     */
    private class NewTableModel extends DefaultTableModel{
        
        private static final long serialVersionUID = 1L;

		/** The classes_. */
        Class<?> [] classes_;
        
        /** The editable_. */
        boolean [] editable_;
        
        /**
         * Instantiates a new new table model.
         * 
         * @param cols the cols
         * @param classes the classes
         * @param editable the editable
         */
        public NewTableModel( String cols[], Class<?> classes[], boolean editable[])
        {
            super( cols, 0 );
            classes_ = classes;
            if( editable != null )
        	editable_ = editable;
            else
            {
        	editable_ = new boolean[cols.length];
        	for(int i = 0; i < cols.length; i++)
        	    editable_[i] = false;
            }
        }
 
        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
		public Class<?> getColumnClass(int column) {
            return classes_[column];
        }
        
        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            
            return(editable_[columnIndex]);
        }

    }
    
    /**
     * Instantiates a new table sorter.
     * 
     * @param cols the cols
     * @param classes the classes
     * @param editable the editable
     */
    public TableSorter( String cols[], Class<?> classes[], boolean editable[])
    {
        setTableModel(new NewTableModel(cols,classes, editable));
    }
    
    /**
     * Instantiates a new table sorter.
     * 
     * @param cols the cols
     * @param classes the classes
     */
    public TableSorter( String cols[], Class<?> classes[])
    {
        setTableModel(new NewTableModel(cols,classes, null));
    }

    /**
     * Adds the mouse listener to header in table.
     * 
     * @param table1 the table1
     */
    public void addMouseListenerToHeaderInTable(JTable table1) {
        table1.setColumnSelectionAllowed(false);
        setTableHeader(table1.getTableHeader());       
    }

    /**
     * Adds the row.
     * 
     * @param ro the ro
     */
    public void addRow(Object[] ro) {
        DefaultTableModel tm = (DefaultTableModel) getTableModel();
        tm.addRow(ro);
        fireTableDataChanged();
    }
    
    /**
     * Removes the row.
     * 
     * @param row the row
     */
    public void removeRow(int row) {
        DefaultTableModel tm = (DefaultTableModel) getTableModel();
        tm.removeRow(row);
        fireTableDataChanged();
    }

    /**
     * Table changed.
     * 
     * @param event the event
     */
    public void tableChanged(TableModelEvent event) {
        NewTableModel tm = (NewTableModel) getTableModel();
        tm.fireTableChanged(event);
        fireTableDataChanged();
        
    }

    /**
     * Sets the row count.
     * 
     * @param i the i
     */
    public void setRowCount(int i) {
        DefaultTableModel tm = (DefaultTableModel) getTableModel();
        tm.setRowCount(i);   
        fireTableDataChanged();
    }

    /**
     * Sort by column.
     * 
     * @param i the i
     */
    public void sortByColumn(int i) {
        setSortingStatus(i, ASCENDING);
        
    }

    /**
     * Gets the mapped index.
     * 
     * @param index the index
     * 
     * @return the mapped index
     */
    public int getMappedIndex(int index) {      
        return( modelIndex(index));
    }

    /**
     * Checks if is sorted.
     * 
     * @return true, if is sorted
     */
    public boolean isSorted() {
        
        return isSorting();
    }

    /**
     * Sort.
     */
    public void sort() {
        fireTableDataChanged();
    }
}