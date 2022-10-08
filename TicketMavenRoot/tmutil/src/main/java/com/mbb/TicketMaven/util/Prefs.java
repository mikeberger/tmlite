/*
 * #%L
 * tmutil
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

package com.mbb.TicketMaven.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Convenience class for retrieving preferences. Hides the underlying mechanism
 */
public class Prefs {

	/**
	 * interface for classes that want to be notified about preference changes
	 */
	public interface Listener {

		/** called when prefs changed */
		public abstract void prefsChanged();
	}

	/** current set of pref change listeners */
	static private ArrayList<Listener> listeners = new ArrayList<Listener>();

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	static public void addListener(Listener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	static public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify all listeners that prefs have changed.
	 */
	static public void notifyListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			Listener v = listeners.get(i);
			v.prefsChanged();
		}
	}

	// basic Pref CRUD below
	//
	//
	/**
	 * Gets a String pref.
	 * 
	 * @param pn
	 *            the Pref Name
	 * 
	 * @return the pref value
	 */
	public static String getPref(PrefName pn) {
		Preferences prefs = getPrefNode();
		String val = prefs.get(pn.getName(), (String) pn.getDefault());
		return (val);
	}

	/**
	 * Gets an int Pref.
	 * 
	 * @param pn
	 *            the Pref Name
	 * 
	 * @return the int value
	 */
	public static int getIntPref(PrefName pn) {
		Preferences prefs = getPrefNode();
		return (prefs.getInt(pn.getName(), ((Integer) pn.getDefault())
				.intValue()));
	}

	/**
	 * Save a Preference
	 * 
	 * @param pn
	 *            the Pref Name
	 * @param val
	 *            the value
	 */
	public static void putPref(PrefName pn, Object val) {

		Preferences prefs = getPrefNode();
		if (pn.getDefault() instanceof Integer) {
			prefs.putInt(pn.getName(), ((Integer) val).intValue());
		} else {
			prefs.put(pn.getName(), (String) val);
		}
	}

	/**
	 * Instantiates a new prefs.
	 */
	private Prefs() {
		// empty
	}

	/**
	 * Gets a String Preference from a String name. Used outside of the PrefName
	 * mechanism. Only use if the name is generated or derived by the code.
	 * Otherwise, use a PrefName for all Preferences
	 * 
	 * @param name
	 *            the preference name
	 * @param def
	 *            the default value
	 * 
	 * @return the preference value
	 */
	public static String getPref(String name, String def) {
		Preferences prefs = getPrefNode();
		String val = prefs.get(name, def);
		return (val);
	}

	/**
	 * Gets an integer pref by string name
	 * 
	 * @param name
	 *            the pref name
	 * @param def
	 *            the default value
	 * 
	 * @return the int value
	 */
	public static int getIntPref(String name, int def) {
		Preferences prefs = getPrefNode();
		return (prefs.getInt(name, def));
	}

	/**
	 * Save a preference by string name
	 * 
	 * @param name
	 *            the name
	 * @param val
	 *            the value
	 */
	public static void putPref(String name, Object val) {

		Preferences prefs = getPrefNode();
		if (val instanceof Integer) {
			prefs.putInt(name, ((Integer) val).intValue());
		} else {
			prefs.put(name, (String) val);
		}
	}

	/**
	 * here's the actual hook into the Preference implementation. The Java
	 * Preferencs mechanism is used. The node name is hard-coded to guarantee
	 * future compatibility
	 * 
	 * @return the pref node
	 */
	static private Preferences getPrefNode() {
		// hard code to original prefs location for backward compatiblity
		Preferences root = Preferences.userRoot();
		return root.node("com/mbb/TicketMaven/common/util");
	}

	/**
	 * Import prefs from XML
	 * 
	 * @param filename
	 *            the filename
	 */
	public static void importPrefs(String filename) {
		try {
			InputStream istr = new FileInputStream(filename);
			Preferences.importPreferences(istr);
			istr.close();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * Export prefs to XML
	 * 
	 * @param filename
	 *            the filename
	 */
	public static void export(String filename) {
		try {
			OutputStream oostr = new FileOutputStream(filename);
			Preferences prefs = getPrefNode();
			prefs.exportNode(oostr);
			oostr.close();
		} catch (Exception e) {
			Errmsg.getErrorHandler().errmsg(e);
		}
	}

	/**
	 * checks the value of a preference against a string
	 * 
	 * @param pn
	 *            the Pref Name
	 * @param s
	 *            the string
	 * 
	 * @return true, if equal
	 */
	public static boolean is(PrefName pn, String s) {
		String p = getPref(pn);
		if (p != null && p.equals(s))
			return true;
		return false;
	}
	
	public static boolean getBoolPref(PrefName pn) {
		String s = getPref(pn);
		if (s != null && s.equals("true"))
			return true;
		return false;
	}

}
