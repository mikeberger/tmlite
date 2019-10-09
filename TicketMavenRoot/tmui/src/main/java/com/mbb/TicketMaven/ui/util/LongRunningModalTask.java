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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * Executes a long running swing-worker taks while showing a modal
 * dialog to tell the user to wait
 *
 */
abstract public class LongRunningModalTask {

	/**
	 * class that listens for task completion and removes the dialog
	 *
	 */
	static private class SwingWorkerCompletionWaiter implements PropertyChangeListener {
	     private JDialog dialog;
	 
	     public SwingWorkerCompletionWaiter(JDialog dialog) {
	         this.dialog = dialog;
	     }
	 
	     @Override
		public void propertyChange(PropertyChangeEvent event) {
	         if ("state".equals(event.getPropertyName())
	                 && SwingWorker.StateValue.DONE == event.getNewValue()) {
	             dialog.setVisible(false);
	             dialog.dispose();
	         }
	     }
	 }

	/**
	 * the swing worker
	 */
	private SwingWorker<String, Object> worker = null;
	
	/**
	 * the runTask methos contains the actual logic to run in thr worker thread.
	 * this method has to be provided by the sub-class
	 * @return a String result from the task
	 */
	abstract public String runTask();


	public final void start() {

		// create the worker
		worker = new SwingWorker<String, Object>() {

			@Override
			protected String doInBackground() throws Exception {
				return runTask();
			}

		};

		// create the modal dialog
		JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setTitle("TICKETMAVEN");
		dialog.setSize(165, 300);
		dialog.setModal(true);
		dialog.add(new JLabel("Please Wait"));	
		dialog.setResizable(false);
		dialog.pack();

		// place the dialog mid-screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 2,
				screenSize.height / 2 );
		
		dialog.setTitle("Please Wait");
		
		// start the worker
		worker.addPropertyChangeListener(new SwingWorkerCompletionWaiter(
				dialog));
		worker.execute();
		
		// make the dialog visible
		dialog.setVisible(true);

	}

}
