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

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImagePreviewFileChooser extends JPanel implements
		PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private JFileChooser jfc;
	private Image img;
	
	private final int DIM_X = 234;
	private final int DIM_Y = 126;

	public ImagePreviewFileChooser(JFileChooser jfc) {
		this.jfc = jfc;
		Dimension sz = new Dimension(DIM_X, DIM_Y);
		setPreferredSize(sz);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			File file = jfc.getSelectedFile();
			updateImage(file);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void updateImage(File file) throws IOException {
		if (file == null) {
			return;
		}
		img = ImageIO.read(file);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		// fill the background
		g.setColor(Color.gray);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (img != null) {
			// calculate the scaling factor
			int w = img.getWidth(null);
			int h = img.getHeight(null);

			double scalex = DIM_X / (double) w;
			double scaley = DIM_Y /(double) h;
			double scale = Math.min(scalex, scaley);
			w = (int) (scale * w);
			h = (int) (scale * h);
			// draw the image
			g.drawImage(img, 0, 0, w, h, null);

			// draw the image dimensions
			String dim = w + " x " + h;
			g.setColor(Color.black);
			g.drawString(dim, 31, DIM_Y - 5);
			g.setColor(Color.white);
			g.drawString(dim, 30, DIM_Y - 6);
		} else {
			// print a message
			g.setColor(Color.black);
			g.drawString("Not an image", 30, DIM_Y/2);
		}
	}

	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		ImagePreviewFileChooser preview = new ImagePreviewFileChooser(jfc);
		jfc.addPropertyChangeListener(preview);
		jfc.setAccessory(preview);
		jfc.showOpenDialog(null);
	}

}