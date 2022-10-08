/*
 * #%L
 * tmmodel
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
package com.mbb.TicketMaven.model;

import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.awt.*;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * This class holds Ticketing format information and marshalls and unmarshalls
 * it to/from XML for storage in the show Entitys. It also manages the legacy
 * preferences used to store the global ticket formats.
 */
@XmlRootElement(name = "TicketFormat")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketFormat {

	public TicketFormat() {
		super();
	}

	/** available number of lines of text per ticket */
	public static final int NUM_LINES = 8;
	public static final int NUM_STUB_LINES = 2;
	
	public static final PrefName USE_STUB = new PrefName("use_stub","false");
	public static final PrefName RUSE_STUB = new PrefName("ruse_stub","false");

	// default line texts for Tickets
	private static final String lineDefaults[] = { "{club}", "Presents",
			"{show}", "{date}", "{price} per person (Tax Included)", "{name}",
			"Row {row}   Seat {seat}", "No Refunds or Exchanges" };

	// default line texts for Reservations
	private static final String rlineDefaults[] = { "{club}", "Presents",
			"{show}", "{date}", "{price} per person (Tax Included)", "{name}",
			"{table}", "No Refunds or Exchanges" };

	// default line texts for stubs
	private static final String stubLineDefaults[] = { "{name}", "Row {row} Seat {seat}" };

	static {
		// initialize the global ticket formatting Prefs for tickets and
		// reservations
		initPrefs(LayoutModel.AUDITORIUM);
		initPrefs(LayoutModel.TABLE);
	}

	/**
	 * Class Line holds the formatting info for 1 line of a Ticket
	 */
	@XmlRootElement(name = "Line")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Line implements Serializable {

		public Line() {
			super();
		}
		private static final long serialVersionUID = 1L;

		@XmlJavaTypeAdapter(ColorAdapter.class)
		private Color color;
		private String text;
		private String font;
		
		public Color getColor() {
			return color;
		}
		public void setColor(Color color) {
			this.color = color;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getFont() {
			return font;
		}
		public void setFont(String font) {
			this.font = font;
		}

	}

	private static class ColorAdapter extends XmlAdapter<String, Color> {
		@Override
		public Color unmarshal(String s) {
			int ci = Integer.parseInt(s);
			return new Color(ci);
		}

		@Override
		public String marshal(Color c) {
			return Integer.toString(c.getRGB());
		}
	}

	/**
	 * Gets the default line texts for a layout type.
	 * 
	 * @param layoutType
	 *            the layout type
	 * 
	 * @return the line defaults
	 */
	static public String[] getLineDefaults(String layoutType) {
		if (layoutType.equals(LayoutModel.TABLE))
			return rlineDefaults;
		return lineDefaults;
	}

	/**
	 * Gets the internal prefix used in the pref names for a layout type.
	 * 
	 * @param layoutType
	 *            the layout type
	 * 
	 * @return the prefix
	 */
	private static String getPrefix(String layoutType) {
		if (layoutType.equals(LayoutModel.AUDITORIUM))
			return "line";
		return "rline";
	}
	
	private static String getStubPrefix(String layoutType) {
		if (layoutType.equals(LayoutModel.AUDITORIUM))
			return "stub";
		return "rstub";
	}

	/**
	 * Inits the preferences for the global ticket format defaults - the ugly
	 * scheme of pref names is being kept for backwards compatibility with older
	 * versions.
	 * 
	 * @param layoutType
	 *            the layout type
	 */
	static private void initPrefs(String layoutType) {
		String prefix = getPrefix(layoutType);
		String f = Prefs.getPref(prefix + "0font", "not-set");
		if (f.equals("not-set")) {
			String fonts[] = { "Arial-BOLD-11", "", "Arial-BOLD-11", "", "",
					"", "", "" };
			Color colors[] = { Color.RED, Color.BLACK, Color.BLUE, Color.BLACK,
					Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK };
			for (int i = 0; i < NUM_LINES; i++) {
				Prefs.putPref(prefix + i + "color",
						Integer.valueOf(colors[i].getRGB()));
				Prefs.putPref(prefix + i + "text",
						getLineDefaults(layoutType)[i]);
				Prefs.putPref(prefix + i + "font", fonts[i]);

			}
		}
		String stubPrefix = getStubPrefix(layoutType);
		f = Prefs.getPref(stubPrefix + "0font", "not-set");
		if (f.equals("not-set")) {
			for (int i = 0; i < NUM_STUB_LINES; i++) {
				Prefs.putPref(stubPrefix + i + "color",
						Integer.valueOf(Color.BLACK.getRGB()));
				Prefs.putPref(stubPrefix + i + "text",
						stubLineDefaults[i]);
				Prefs.putPref(stubPrefix + i + "font", "");

			}
		}
	}

	// due to legacy XML format and JAXB
	private static class Lines {
		public Line[] line = new Line[NUM_LINES];
		public Line[] stubline = new Line[NUM_STUB_LINES];
	}
	
	/*******************************
	 * End of Statics
	 ********************************/

	// type of layout - implies ticket or reservaion
	private String layoutType = LayoutModel.AUDITORIUM;

	// name of the background image file
	private String imageFilename = null;

	// array of Lines to hold the formatting rules for each line of ticket text
	private Lines lines = new Lines();
	
	private boolean use_stub = false;

	/**
	 * Instantiates a new ticket format.
	 * 
	 * @param layoutType
	 *            the layout type
	 */
	public TicketFormat(String layoutType) {
		this.layoutType = layoutType;
		for (int i = 0; i < NUM_LINES; i++) {
			lines.line[i] = null;
		}
		for (int i = 0; i < NUM_STUB_LINES; i++) {
			lines.stubline[i] = null;
		}
	}

	/**
	 * Gets the image filename.
	 * 
	 * @return the image filename
	 */
	public String getImageFilename() {
		return imageFilename;
	}

	/**
	 * Gets formatting of a line by line number.
	 * 
	 * @param num
	 *            the line number
	 * 
	 * @return the Line object
	 */
	public Line getLine(int num) {
		return lines.line[num];
	}
	
	public Line getStubLine(int num) {
		return lines.stubline[num];
	}

	/**
	 * Load this instance of TicketFormat with the global defaults
	 */
	public void loadDefault() {

		String prefix = getPrefix(layoutType);
		if (layoutType.equals(LayoutModel.AUDITORIUM))
			setImageFilename(Prefs.getPref(PrefName.LOGOFILE));
		else
			setImageFilename(Prefs.getPref(PrefName.RLOGOFILE));

		for (int i = 0; i < NUM_LINES; i++) {
			Line l = new Line();
			l.setText(Prefs.getPref(prefix + i + "text", ""));
			l.setFont(Prefs.getPref(prefix + i + "font", ""));
			int ci = Prefs.getIntPref(prefix + i + "color", 0);
			l.setColor(new Color(ci));
			lines.line[i] = l;
		}
		
		String stubPrefix = getStubPrefix(layoutType);

		for (int i = 0; i < NUM_STUB_LINES; i++) {
			Line l = new Line();
			l.setText(Prefs.getPref(stubPrefix + i + "text", ""));
			l.setFont(Prefs.getPref(stubPrefix + i + "font", ""));
			int ci = Prefs.getIntPref(stubPrefix + i + "color", 0);
			l.setColor(new Color(ci));
			lines.stubline[i] = l;
		}
		
		if( layoutType.equals(LayoutModel.AUDITORIUM))
			use_stub = Prefs.getBoolPref(USE_STUB);
		else
			use_stub = Prefs.getBoolPref(RUSE_STUB);


	}

	/**
	 * Save the contents of this Ticketformat instance to the global defaults
	 */
	public void saveDefault() {

		String prefix = getPrefix(layoutType);
		if (layoutType.equals(LayoutModel.AUDITORIUM))
			Prefs.putPref(PrefName.LOGOFILE, this.imageFilename);
		else
			Prefs.putPref(PrefName.RLOGOFILE, this.imageFilename);

		for (int i = 0; i < NUM_LINES; i++) {
			Line l = lines.line[i];
			Prefs.putPref(prefix + i + "text", l.getText());
			Prefs.putPref(prefix + i + "font", l.getFont());
			Prefs.putPref(prefix + i + "color", Integer.valueOf(l.getColor()
					.getRGB()));
		}
		String stubPrefix = getStubPrefix(layoutType);

		for (int i = 0; i < NUM_STUB_LINES; i++) {
			Line l = lines.stubline[i];
			Prefs.putPref(stubPrefix + i + "text", l.getText());
			Prefs.putPref(stubPrefix + i + "font", l.getFont());
			Prefs.putPref(stubPrefix + i + "color", Integer.valueOf(l.getColor()
					.getRGB()));
		}
		
		if (layoutType.equals(LayoutModel.AUDITORIUM))
			Prefs.putPref(USE_STUB, use_stub ? "true" : "false");
		else
			Prefs.putPref(RUSE_STUB, use_stub ? "true" : "false");
		
	}

	/**
	 * Sets the image filename.
	 * 
	 * @param imageFilename
	 *            the new image filename
	 */
	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	/**
	 * Sets the line format for a given line.
	 * 
	 * @param num
	 *            the line number
	 * @param line
	 *            the Line object
	 */
	void setLine(int num, Line line) {
		this.lines.line[num] = line;
	}

	/**
	 * copy constructor
	 * 
	 * @param orig
	 *            the original
	 */
	public TicketFormat(TicketFormat orig) {
		imageFilename = orig.imageFilename;
		layoutType = orig.layoutType;
		for (int i = 0; i < NUM_LINES; i++) {
			if (orig.lines.line[i] != null) {
				lines.line[i] = new Line();
				lines.line[i].color = new Color(
						orig.lines.line[i].color.getRGB());
				lines.line[i].text = orig.lines.line[i].text;
				lines.line[i].font = orig.lines.line[i].font;
			}
		}
		for (int i = 0; i < NUM_STUB_LINES; i++) {
			if (orig.lines.stubline[i] != null) {
				lines.stubline[i] = new Line();
				lines.stubline[i].color = new Color(
						orig.lines.stubline[i].color.getRGB());
				lines.stubline[i].text = orig.lines.stubline[i].text;
				lines.stubline[i].font = orig.lines.stubline[i].font;
			}
		}
		
		use_stub = orig.use_stub;
	}

	/**
	 * UnMarshall a TicketFormat from XML
	 * 
	 * @param s
	 *            the XML string
	 * 
	 * @return the ticket format
	 * 
	 * @throws Exception
	 *             the exception
	 */
	static public TicketFormat fromXml(String s) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(TicketFormat.class);
		Unmarshaller u = jc.createUnmarshaller();
		TicketFormat tf = (TicketFormat) u.unmarshal(new StringReader(s));
		
		// transition - stub may be missing in db
		if( tf.lines.stubline[0] == null)
		{
			String stubPrefix = getStubPrefix(tf.layoutType);

			for (int i = 0; i < NUM_STUB_LINES; i++) {
				Line l = new Line();
				l.setText(Prefs.getPref(stubPrefix + i + "text", ""));
				l.setFont(Prefs.getPref(stubPrefix + i + "font", ""));
				int ci = Prefs.getIntPref(stubPrefix + i + "color", 0);
				l.setColor(new Color(ci));
				tf.lines.stubline[i] = l;
			}
		}
		return tf;
	}

	/**
	 * Marshall a Ticket Format to XML
	 * 
	 * @return the XML string
	 * @throws Exception
	 */
	public String toXml() throws Exception {
		JAXBContext jc = JAXBContext.newInstance(TicketFormat.class);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(this, sw);
		return sw.toString();
	}

	public static void main(String args[]) throws Exception {
		TicketFormat tf = new TicketFormat(LayoutModel.AUDITORIUM);
		tf.loadDefault();
		System.out.println(tf.toXml());

	}

	public boolean hasStub() {
		return use_stub;
	}

	public void setHasStub(boolean use_stub) {
		this.use_stub = use_stub;
	}

}
