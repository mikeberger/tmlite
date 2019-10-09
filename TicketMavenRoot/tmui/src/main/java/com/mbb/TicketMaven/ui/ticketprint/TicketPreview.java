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

package com.mbb.TicketMaven.ui.ticketprint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.mbb.TicketMaven.model.TicketFormat;
import com.mbb.TicketMaven.model.entity.Ticket;
import com.mbb.TicketMaven.ui.util.ColorChooserButton;
import com.mbb.TicketMaven.ui.util.FontChooser;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.ImagePreviewFileChooser;
import com.mbb.TicketMaven.util.Errmsg;

public class TicketPreview extends JPanel {

	private static final long serialVersionUID = 1L;

	private static class FontButtonListener implements ActionListener {
		private int line;
		private TicketPreview prev;
		private boolean stub;

		FontButtonListener(int l, boolean stub, TicketPreview p) {
			line = l;
			prev = p;
			this.stub = stub;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Font f = FontChooser.showDialog(null, "Choose Font", null);
			if (f != null) {
				String fs = FontChooser.fontString(f);
				if( stub )
					prev.getFormat().getStubLine(line).setFont(fs);
				else
					prev.getFormat().getLine(line).setFont(fs);
				prev.refresh();
			}
		}
	}

	private static class ColorButtonListener implements ActionListener {
		private int line;
		private TicketPreview prev;
		private boolean stub;

		ColorButtonListener(int l, boolean stub, TicketPreview p) {
			line = l;
			prev = p;
			this.stub = stub;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ColorChooserButton b = (ColorChooserButton) e.getSource();
			if( stub )
				prev.getFormat().getStubLine(line).setColor(b.getColorProperty());
			else
				prev.getFormat().getLine(line).setColor(b.getColorProperty());

			prev.refresh();

		}
	}

	private static class LineTextListener implements KeyListener {
		private int line;
		private TicketPreview prev;
		private boolean stub;

		LineTextListener(int l, boolean stub, TicketPreview p) {
			line = l;
			prev = p;
			this.stub = stub;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// empty
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			JTextField b = (JTextField) arg0.getComponent();
			if( stub )
				prev.getFormat().getStubLine(line).setText(b.getText());
			else
				prev.getFormat().getLine(line).setText(b.getText());

			prev.repaintTicket();
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// empty
		}
	}

	private static class LogoTextListener implements KeyListener {
		private TicketPreview prev;

		LogoTextListener(TicketPreview p) {
			prev = p;

		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// empty
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			JTextField b = (JTextField) arg0.getComponent();
			prev.getFormat().setImageFilename(b.getText());
			prev.repaintTicket();
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// empty
		}
	}

	private TicketFormat format;

	private ColorChooserButton colorButton[];
	private ColorChooserButton stubColorButton[];

	private JTextField lineText[];
	private JTextField stubLineText[];
	private JTextField logoText = new JTextField();

	private TicketPanel ticketPanel = null;
	
	private JCheckBox stubBox = new JCheckBox("Enable Ticket Stub");

	public TicketPreview(TicketFormat format, boolean vertical) {
		super();
		this.format = new TicketFormat(format);

		lineText = new JTextField[TicketFormat.NUM_LINES];
		stubLineText = new JTextField[TicketFormat.NUM_STUB_LINES];
		colorButton = new ColorChooserButton[TicketFormat.NUM_LINES];
		stubColorButton = new ColorChooserButton[TicketFormat.NUM_STUB_LINES];

		initialize(vertical);
		refresh();
	}

	private JPanel getLinesPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		for (int i = 0; i < TicketFormat.NUM_LINES; i++) {
			
			JLabel label = new JLabel("Line " + (i + 1) + ":");
			panel.add(label, GridBagConstraintsFactory
					.create(0, i));

			lineText[i] = new JTextField();
			lineText[i].setText(format.getLine(i).getText());
			lineText[i].addKeyListener(new LineTextListener(i, false, this));
			panel.add(lineText[i], GridBagConstraintsFactory
					.create(1, i, java.awt.GridBagConstraints.BOTH, 1.0,
							0.0));

			colorButton[i] = new ColorChooserButton("Color", format.getLine(i)
					.getColor(), true);
			colorButton[i].addActionListener(new ColorButtonListener(i, false, this));
			panel.add(colorButton[i], GridBagConstraintsFactory
					.create(2, i));

			JButton fontButton = new JButton("font");
			fontButton.addActionListener(new FontButtonListener(i, false, this));

			panel.add(fontButton, GridBagConstraintsFactory
					.create(3, i));
		}

		return panel;
	}

	public void applyChanges() {
		format.saveDefault();
	}

	private JPanel getlogoPanel() {

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());

		JButton logob = new JButton("Logo:");
		jPanel.add(logob, GridBagConstraintsFactory.create(0, 0));
		jPanel.add(logoText, GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH,1.0,0.0));
		
		JButton reset = new JButton("Reload Default Style");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetLineDefaults();
			}
		});

		jPanel.add(reset, GridBagConstraintsFactory.create(2, 0));
		
		logoText.addKeyListener(new LogoTextListener(this));
		logob.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logobrowseActionPerformed(e);
			}
		});

		return jPanel;
	}

	private TicketPanel getTicketPanel() {
		if (ticketPanel == null) {
			Collection<Ticket> tickets = new ArrayList<Ticket>();

			Ticket t = new Ticket();
			t.setRow("A");
			t.setTable("Table 1");

			t.setCustomerName("Customer Name");
			t.setShowName("Your Show Name");
			t.setShowDate(new Date());
			t.setPrice(Integer.valueOf(1900));
			tickets.add(t);
			try {
				ticketPanel = new TicketPanel(tickets, format);
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
			}
		}
		return ticketPanel;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize(boolean vertical) {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new java.awt.Insets(4, 4, 4, 4);
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 1;
		if (!vertical) {
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0D;
		}

		this.setLayout(new GridBagLayout());
		JScrollPane scr = new JScrollPane();
		getTicketPanel().setPreferredSize(new Dimension(375, 205));
		scr.setViewportView(getTicketPanel());
		scr.setMinimumSize(new Dimension(400, 220));

		GridBagConstraints tktCons = GridBagConstraintsFactory.create(0, 0,
				GridBagConstraints.BOTH, 1.0, 1.0);
		this.add(scr, tktCons);

		GridBagConstraints linesCons = GridBagConstraintsFactory.create(1, 0,
				GridBagConstraints.BOTH, 1.0, 1.0);
		if (vertical) {
			linesCons = GridBagConstraintsFactory.create(0, 1,
					GridBagConstraints.BOTH, 1.0, 1.0);
		}
		this.add(getLinesPanel(), linesCons);
		
		GridBagConstraints stubCons = GridBagConstraintsFactory.create(1, 1,
				GridBagConstraints.BOTH, 1.0, 1.0);
		if (vertical) {
			stubCons = GridBagConstraintsFactory.create(0, 2,
					GridBagConstraints.BOTH, 1.0, 1.0);
		}
		this.add(getStubPanel(), stubCons);
		
		GridBagConstraints logoCons = GridBagConstraintsFactory.create(0, 1,
				GridBagConstraints.BOTH, 1.0, 1.0);
		if (vertical) {
			logoCons = GridBagConstraintsFactory.create(0, 3,
					GridBagConstraints.BOTH, 1.0, 1.0);
		}
		this.add(getlogoPanel(), logoCons);

	}
	
	private JPanel getStubPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints sbcons = GridBagConstraintsFactory
				.create(0, 0, java.awt.GridBagConstraints.BOTH);
		sbcons.gridwidth = 4;
		panel.add(stubBox, sbcons);
		stubBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				format.setHasStub(stubBox.isSelected());
				refresh();
			}
			
		});

		for (int i = 0; i < TicketFormat.NUM_STUB_LINES; i++) {
			
			JLabel label = new JLabel("Stub Line " + (i + 1) + ":");
			panel.add(label, GridBagConstraintsFactory
					.create(0, i+1));

			stubLineText[i] = new JTextField();
			stubLineText[i].setText(format.getStubLine(i).getText());
			stubLineText[i].addKeyListener(new LineTextListener(i, true, this));
			panel.add(stubLineText[i], GridBagConstraintsFactory
					.create(1, i+1, java.awt.GridBagConstraints.BOTH, 1.0,
							0.0));

			stubColorButton[i] = new ColorChooserButton("Color", format.getStubLine(i)
					.getColor(), true);
			stubColorButton[i].addActionListener(new ColorButtonListener(i, true, this));
			panel.add(stubColorButton[i], GridBagConstraintsFactory
					.create(2, i+1));

			JButton fontButton = new JButton("font");
			fontButton.addActionListener(new FontButtonListener(i, true, this));

			panel.add(fontButton, GridBagConstraintsFactory
					.create(3, i+1));
		}

		return panel;
	}

	private void repaintTicket() {
		ticketPanel.setDefaultFormat(format);
	}

	private void refresh() {
		for (int i = 0; i < TicketFormat.NUM_LINES; i++) {
			colorButton[i].setColorProperty(format.getLine(i).getColor());
			lineText[i].setText(format.getLine(i).getText());
		}
		logoText.setText(format.getImageFilename());
		stubBox.setSelected(format.hasStub());
		ticketPanel.setDefaultFormat(format);
	}

	private void logobrowseActionPerformed(java.awt.event.ActionEvent evt) {

		String logo = null;
		while (true) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("samples"));
			chooser.setDialogTitle("Please choose the logo file - GIF/JPG/PNG_only");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			ImagePreviewFileChooser preview = new ImagePreviewFileChooser(
					chooser);
			chooser.addPropertyChangeListener(preview);
			chooser.setAccessory(preview);
			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					if (arg0.isDirectory())
						return true;
					String name = arg0.getName().toLowerCase();
					if (name.endsWith(".gif") || name.endsWith(".jpg")
							|| name.endsWith(".jpeg") || name.endsWith(".png"))
						return true;
					return false;
				}

				@Override
				public String getDescription() {
					return "Images";
				}
			};

			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			logo = chooser.getSelectedFile().getAbsolutePath();
			File lf = new File(logo);
			String err = null;
			if (!lf.exists()) {
				err = "File" + logo + "does not exist";
			} else if (!lf.canRead()) {
				err = "Cannot read file " + logo;
			}

			if (err == null)
				break;

			Errmsg.getErrorHandler().notice(err);
		}

		logoText.setText(logo);
		format.setImageFilename(logo);
		refresh();
	}

	private void resetLineDefaults() {

		format.loadDefault();
		getTicketPanel().repaint();
		refresh();
	}

	public TicketFormat getFormat() {
		return format;
	}

	public void setFormat(TicketFormat format) {
		this.format = new TicketFormat(format);
		refresh();
	}

	public void setTicket(Ticket t) {
		Collection<Ticket> coll = new ArrayList<Ticket>();
		coll.add(t);
		ticketPanel.setTickets(coll);
		repaintTicket();
	}

}
