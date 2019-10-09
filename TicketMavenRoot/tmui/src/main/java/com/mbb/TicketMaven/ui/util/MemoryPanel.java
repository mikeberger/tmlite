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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

public class MemoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton gcButton = new JButton();
	private JProgressBar memoryBar = new JProgressBar();

	public MemoryPanel() {

		memoryBar.setStringPainted(true);
		gcButton = new JButton(new ImageIcon(getClass().getResource(
				"/resource/Delete16.gif")));

		gcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.gc();
			}
		});

		this.setLayout(new GridBagLayout());
		this.add(memoryBar,
				GridBagConstraintsFactory.create(0, 0, GridBagConstraints.BOTH));
		this.add(gcButton, GridBagConstraintsFactory.create(1, 0));

		int to = Prefs.getIntPref(PrefName.MEMBAR_TIMEOUT);
		if (to > 0) {
			new Timer(to, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateBar();
				}
			}).start();
		}
	}

	private void updateBar() {

		long totalMB = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		long freeMB = Runtime.getRuntime().freeMemory() / (1024 * 1024);

		memoryBar.setMinimum(0);
		memoryBar.setMaximum((int) totalMB);
		int used = (int) (totalMB - freeMB);
		memoryBar.setValue(used);

		memoryBar.setString(used + "/" + totalMB + " MB");

	}

}