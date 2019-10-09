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

package com.mbb.TicketMaven.ui.module;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.mbb.TicketMaven.model.PackageModel;
import com.mbb.TicketMaven.model.entity.TMPackage;
import com.mbb.TicketMaven.ui.detail.PackageView;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Money;

/**
 * The Class PackageManager provides the UI for managing packages
 */
public class PackageManager extends ViewListPanel<TMPackage> implements Module {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The icon. */
	private Icon icon = null;

	/**
	 * Instantiates a new package manager.
	 */
	public PackageManager() {
		super(PackageModel.getReference(), new PackageView(), null,
				new TableSorter(new String[] { "Package Name", "Price" },
						new Class[] { java.lang.String.class, Money.class, }),
				new String[] { "Name", "Price" });
		icon = new ImageIcon(getClass().getResource("/resource/pkg16.gif"));

	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Package Manager";
	}


	/* (non-Javadoc)
	 * @see com.mbb.TicketMaven.ui.module.Module#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return icon;
	}

}
