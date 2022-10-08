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

import org.jgraph.graph.*;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * The Class CircleView draws a circle in JGraph for the Table layout diagram.
 */
public class CircleView extends VertexView {
	
	private static final long serialVersionUID = 1L;
	/** The renderer. */
	private static MyRenderer myRenderer = new MyRenderer();

	/**
	 * Instantiates a new circle view.
	 */
	public CircleView() {
		//empty
	}

	/**
	 * Instantiates a new circle view.
	 * 
	 * @param object the object
	 */
	public CircleView(Object object) {
		super(object);
	}

	/* (non-Javadoc)
	 * @see org.jgraph.graph.VertexView#getRenderer()
	 */
	@Override
	public CellViewRenderer getRenderer() {
		return myRenderer;
	}

	/* (non-Javadoc)
	 * @see org.jgraph.graph.VertexView#getPerimeterPoint(org.jgraph.graph.EdgeView, java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (getRenderer() instanceof MyRenderer)
			return ((MyRenderer) getRenderer()).getPerimeterPoint(this, source,
					p);
		return super.getPerimeterPoint(edge, source, p);
	}
	
	public static class MyRenderer extends VertexRenderer {
		
		private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getPreferredSize()
		 */
		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width += d.width / 5;
			d.height += d.height * 4;
			return d;
		}

		/* (non-Javadoc)
		 * @see org.jgraph.graph.VertexRenderer#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(Graphics g) {
			int b = borderWidth;
			Dimension d = getSize();
			int height = d.height - b;
			boolean tmp = selected;
			// create 2D by casting g to Graphics2D
			Graphics2D g2 = (Graphics2D) g;

			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g2.fillOval(b, b, height, height);
			}
			try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g2.setColor(Color.gray);
				g2.drawOval(b, b, height, height);
			}
		}
	}
}
