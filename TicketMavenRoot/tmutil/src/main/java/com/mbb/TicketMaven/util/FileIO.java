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

import javax.swing.*;
import java.io.*;

/**
 * Some helpers for prompting the users to select files for input and output
 */
public class FileIO {

	/**
	 * Instantiates a new file io.
	 */
	public FileIO() { // empty
	}

	/**
	 * Prompt the user for a File to open.
	 *
	 * @param startDirectory
	 *            the start directory
	 * @param title
	 *            the title
	 *
	 * @return the input stream
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static final InputStream fileOpen(String startDirectory, String title) throws Exception {
		JFileChooser chooser = new JFileChooser();

		chooser.setCurrentDirectory(new File(startDirectory));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;

		String s = chooser.getSelectedFile().getAbsolutePath();
		return new FileInputStream(s);
	}

	/**
	 * Prompt the user for a File 
	 *
	 * @param startDirectory
	 *            the start directory
	 * @param title
	 *            the title
	 *
	 * @return the file path
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static final String chooseFile(String startDirectory, String title) throws Exception {
		JFileChooser chooser = new JFileChooser();

		chooser.setCurrentDirectory(new File(startDirectory));
		chooser.setDialogTitle(title);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;

		String s = chooser.getSelectedFile().getAbsolutePath();
		return s;
	}

	/**
	 * Prompt the user for a file to save.
	 *
	 * @param startDirectory
	 *            the start directory
	 * @param istr
	 *            the Reader containing the data to save
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static final void fileSave(String startDirectory, Reader istr) throws Exception {
		JFileChooser chooser = new JFileChooser();

		chooser.setCurrentDirectory(new File(startDirectory));
		chooser.setDialogTitle("Save");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		String s = chooser.getSelectedFile().getAbsolutePath();
		FileOutputStream ostr = new FileOutputStream(s);

		int b;
		while ((b = istr.read()) != -1)
			ostr.write(b);

		istr.close();
		ostr.close();
	}

	/**
	 * check if a dir is writable
	 * try to create a file because windows will say that some dirs are writable even when they are not - i.e. program files
	 * @param ddd
	 * @return
	 */
	public static final boolean canWriteDir(File ddd){

		if (!ddd.exists() || !ddd.isDirectory() || !ddd.canWrite())
			return false;

		try {
			File f = File.createTempFile("dircheck", ".tmp", ddd);
			f.delete();
			return true;
		}
		catch( Exception e)
		{
			return false;
		}
	}
}
