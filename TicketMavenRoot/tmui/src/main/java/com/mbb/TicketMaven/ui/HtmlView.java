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

import com.mbb.TicketMaven.util.PrefName;

/**
 * This class just displays HTML in a window
 */
class HtmlView extends ViewFrame {

	private static final long serialVersionUID = 1L;


	/**
	 * Creates HtmlView
	 * 
	 * @param file
	 *            the file containing the HTML to show
	 */
	HtmlView(String file) {
		initComponents();
		try {
			jEditorPane1.setPage(getClass().getResource(file));
		} catch (java.io.IOException e1) {
			e1.printStackTrace();
		}

		manageMySize(PrefName.HELPVIEWSIZE);
	}

	private void initComponents()
	{
		jScrollPane1 = new javax.swing.JScrollPane();
		jEditorPane1 = new javax.swing.JEditorPane();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("TicketMaven");
		this.setSize(165, 65);
		this.setContentPane(jScrollPane1);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		jEditorPane1.setEditable(false);
		jEditorPane1.setPreferredSize(new java.awt.Dimension(700, 500));
		jScrollPane1.setViewportView(jEditorPane1);

		pack();
	}

	
	private void exitForm(java.awt.event.WindowEvent evt) {
		this.dispose();
	}

	private javax.swing.JEditorPane jEditorPane1;
	private javax.swing.JScrollPane jScrollPane1;


	@Override
	public void refresh() {
		//empty
	}
	
	@Override
	public void destroy() {
		this.dispose();
	}

} 
