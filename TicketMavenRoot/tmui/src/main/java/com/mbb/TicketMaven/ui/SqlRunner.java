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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.mbb.TicketMaven.model.CustomerModel;
import com.mbb.TicketMaven.model.SeatModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.TicketModel;
import com.mbb.TicketMaven.model.TicketRequestModel;
import com.mbb.TicketMaven.model.jdbc.JdbcDB;
import com.mbb.TicketMaven.ui.util.ScrolledDialog;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;

/**
 * Class SqlRunner opens a dialog that allows the suer to enter ans run SQL. If the SQL results in 
 * a ResultSet its rows are displayed in a Tabular format
 */
class SqlRunner extends JDialog {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new sql runner.
	 */
	public SqlRunner() {

		super();
		setModal(false);
	
		// init the gui components
		initComponents();	
		this.setTitle("Run SQL");
		pack();

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

		editor = new javax.swing.JEditorPane();
		jPanel1 = new javax.swing.JPanel();
		runButton = new javax.swing.JButton();

		editor.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(
				0, 0, 0)));

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setPreferredSize(new java.awt.Dimension(554, 404));
		jScrollPane1.setViewportView(editor);

		runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Forward16.gif")));
		
		runButton.setText("Run");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				runbuttonActionPerformed(evt);
			}
		});

		jPanel1.add(runButton);
		

		clearButton = new JButton();
		clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/resource/Undo16.gif")));
		clearButton.setText("Clear");
		clearButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearbuttonActionPerformed(evt);
			}
		});

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

		jPanel1.add(clearButton, clearButton.getName());
		this.getContentPane().add(jPanel1, gridBagConstraints2); // Generated
		

	}


	/**
	 * Runbutton action performed.
	 * 
	 * @param evt the evt
	 */
	private void runbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			
			JdbcDB.startTransaction();
			
			// run the SQL
			ResultSet r = JdbcDB.execSQL(editor.getText());
			
			JdbcDB.commitTransaction();
			
			
			if( r != null && r.next())
			{
				
				// load the result set data into a table
				JTable tbl = new JTable();
				tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

				int cols = r.getMetaData().getColumnCount();
				String colnames[] = new String[cols];
				Class<?> classes[] = new Class[cols];
				for( int c = 0; c < cols; c++)
				{
					colnames[c] = r.getMetaData().getColumnName(c+1);
					classes[c] = String.class;
				}
				TableSorter ts = new TableSorter(colnames, classes);
				ts.addMouseListenerToHeaderInTable(tbl);
				tbl.setModel(ts);
				Object row[] = new Object[cols];
				for( ;!r.isAfterLast();r.next())
				{
					for( int i = 1; i <= cols; i++)
					{
						row[i-1] = r.getString(i);
					}
					ts.addRow(row);
				}
				
				// show the results
				ScrolledDialog.showTable(tbl);
			}
			else
				ScrolledDialog.showNotice("No Output");
		
			CustomerModel.getReference().refresh();
			ShowModel.getReference().refresh();
			SeatModel.getReference().refresh();
			TicketModel.getReference().refresh();
			TicketRequestModel.getReference().refresh();
			
		
		} catch (Exception e) {
			System.out.println(e.toString());
			try {
			    JdbcDB.rollbackTransaction();
			} catch (Exception e2) {
				//empty
			}
			Errmsg.getErrorHandler().errmsg(e);
		}
		
		
		
	}

	/**
	 * Clearbutton action performed.
	 * 
	 * @param evt the evt
	 */
	private void clearbuttonActionPerformed(java.awt.event.ActionEvent evt) {
		editor.setText("");
	}

	private javax.swing.JPanel jPanel1;

	private javax.swing.JScrollPane jScrollPane1;

	private JEditorPane editor;

	private javax.swing.JButton runButton;

	private JButton clearButton;


}
