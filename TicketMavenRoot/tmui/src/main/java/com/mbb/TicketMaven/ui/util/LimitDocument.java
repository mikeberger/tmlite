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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * this class creates a document to be used in a Text field to limit the number fo characters
 * a user can type. If they type more characters than the set limit, the characters are dropped
 */
public class LimitDocument extends PlainDocument {

	private static final long serialVersionUID = 1L;
	/** The limit. */
	private int limit;

	/**
	 * Instantiates a new limit document.
	 * 
	 * @param limit the limit
	 */
	public LimitDocument(int limit) {
		super();
		setLimit(limit); // store the limit
	}

	/**
	 * Gets the limit.
	 * 
	 * @return the limit
	 */
	public final int getLimit() {
		return limit;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(int offset, String s, AttributeSet attributeSet)
			throws BadLocationException {
		if (offset < limit) {
			super.insertString(offset, s, attributeSet);
		} // otherwise, just lose the string
	}

	/**
	 * Sets the limit.
	 * 
	 * @param newValue the new limit
	 */
	public final void setLimit(int newValue) {
		this.limit = newValue;
	}

}
