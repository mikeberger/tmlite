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

package com.mbb.TicketMaven.ui.detail;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mbb.TicketMaven.model.LayoutModel;
import com.mbb.TicketMaven.model.PackageModel;
import com.mbb.TicketMaven.model.ShowModel;
import com.mbb.TicketMaven.model.entity.Show;
import com.mbb.TicketMaven.model.entity.TMPackage;
import com.mbb.TicketMaven.model.filter.ShowFilter;
import com.mbb.TicketMaven.ui.BeanSelector;
import com.mbb.TicketMaven.ui.util.GridBagConstraintsFactory;
import com.mbb.TicketMaven.ui.util.TableSorter;
import com.mbb.TicketMaven.util.Errmsg;
import com.mbb.TicketMaven.util.Money;
import com.mbb.TicketMaven.util.PrefName;
import com.mbb.TicketMaven.util.Prefs;
import com.mbb.TicketMaven.util.Warning;

public class PackageView extends ViewDetailPanel<TMPackage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TMPackage package_;

	private JTextField nameText = new JTextField();
	private JTextField showtext = new JTextField();
	private JButton showButton = null;

	private JTextField priceText = null;
	
	private ArrayList<Integer> shows_ = new ArrayList<Integer>();

	private JTextField totalText = null;

	private JTextField discountText = null;

	public PackageView() {
		super();

		package_ = null;

		initialize(); // init the GUI widgets

		showData(null);

	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		this.setLayout(new GridBagLayout());  // Generated
		
		this.add(new JLabel("Package Name:"), GridBagConstraintsFactory.create(0, 0));
		this.add(nameText, GridBagConstraintsFactory.create(1, 0, GridBagConstraints.BOTH, 1.0, 0.0));
		
		showtext.setEditable(false);
		this.add(getShowButton(), GridBagConstraintsFactory.create(0, 1));
		this.add(showtext, GridBagConstraintsFactory.create(1, 1, GridBagConstraints.BOTH, 1.0, 0.0));

		this.add(new JLabel("Total Show Price:"), GridBagConstraintsFactory.create(0, 2));
		this.add(getTotalText(), GridBagConstraintsFactory.create(1, 2, GridBagConstraints.BOTH, 1.0, 0.0));
		
		this.add(new JLabel("Price:"), GridBagConstraintsFactory.create(0, 3));
		this.add(getPriceText(), GridBagConstraintsFactory.create(1, 3, GridBagConstraints.BOTH, 1.0, 0.0));
		
		this.add(new JLabel("Discount:"), GridBagConstraintsFactory.create(0, 4));
		this.add(getDiscountText(), GridBagConstraintsFactory.create(1, 4, GridBagConstraints.BOTH, 1.0, 0.0));
	}


	@Override
	public void refresh() {
		//empty
	}

	private void setShowText(Collection<Integer> c)
	{
		int cost = 0;
		String txt = "";
		for( Integer i : c )
		{
			Show sh = null;
			try {
				sh = ShowModel.getReference().getShow(i.intValue());
				cost += sh.getPrice().intValue();
			} catch (Exception e) {
				Errmsg.getErrorHandler().errmsg(e);
				continue;
			}			
			
			if( txt.equals(""))
			{
				txt = sh.getName();
			}
			else
			{
				txt += ";" + sh.getName();
			}
		}
		totalText.setText(Money.format(cost));
		showtext.setText(txt);
		updatePercent();
	}
	
	private void updatePercent()
	{
		try{
			int price = Money.parse(priceText.getText());
			int total = Money.parse(totalText.getText());
			if( price > total )
			{
				discountText.setText("--");
				return;
			}
			double percent = (((double)total - (double)price) * 100.0) / total;
			discountText.setText(Math.round(percent) + "%");
		}
		catch( Exception e)
		{
			discountText.setText("");
		}
	}
	
	@Override
	public void showData(TMPackage s) {
		package_ = s;
		
		if( s == null || s.isNew() || Prefs.is(PrefName.UPDPKG, "true"))
		{
			nameText.setEditable(true);
			showButton.setEnabled(true);
			priceText.setEditable(true);
		}
		else
		{
			nameText.setEditable(false);
			showButton.setEnabled(false);
			priceText.setEditable(false);
		}
		
		if( s == null )
		{
			return;
		}
		nameText.setText(package_.getName());
		if( package_.getPrice() != null)
			priceText.setText(Money.format(package_.getPrice().intValue()));
		else
			priceText.setText(Money.format(0));
		
		shows_.clear();
		shows_.addAll(package_.getShows());
		setShowText(shows_);
		

	}

	@Override
	public void saveData() throws Exception, Warning {
		if (nameText.getText().equals("") 
				|| priceText.getText().equals("")) {
			throw new Warning("Name and Price are Required");
		}
		
		if( package_ == null )
			package_ = PackageModel.getReference().newPackage();
		
		package_.setName(nameText.getText());
		try{
			package_.setPrice(Integer.valueOf(Money.parse(priceText.getText())));
		}
		catch( ParseException e)
		{
			throw new Warning("Invalid format for package price");
		}
		
		package_.setShows(shows_);

		PackageModel.getReference().saveRecord(package_);
		
	}
	
	private void showSelect()
	{
		
		if( package_ == null )
			package_ = PackageModel.getReference().newPackage();
		
		// init with shows in package
		Collection<Show> c = BeanSelector.selectBeans(ShowModel.getReference(),  
				new TableSorter(new String[] { "Show Name", "Show Date/Time" },
				new Class[] { java.lang.String.class,
						java.util.Date.class }), new String[] {
				"Name", "DateTime" }, new ShowFilter(LayoutModel.AUDITORIUM,true), shows_);
		shows_.clear();
		for( Show s : c )
		{
			shows_.add(Integer.valueOf(s.getKey()));
		}
		setShowText(shows_);
		
	}
	
	

	/**
	 * This method initializes showButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setText("Shows:");  // Generated
			showButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showSelect();
				}
			});
		}
		return showButton;
	}

	/**
	 * This method initializes priceText	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPriceText() {
		if (priceText == null) {
			priceText = new JTextField(20);
			priceText.setColumns(0);
			priceText.setText("$0000.00");
			priceText.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
						updatePercent();
					}
			});
		}
		return priceText;
	}

	@Override
	public String getDuplicateError() {		
		return "A Package already exists with the same name.\nCannot store a duplicate. The seat is taken.";
	}

	
	/**
	 * This method initializes totalText	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextField getTotalText() {
		if (totalText == null) {
			totalText = new JTextField();
			totalText.setText("$0000.00");
			totalText.setEditable(false);
		}
		return totalText;
	}

	/**
	 * This method initializes discountText	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDiscountText() {
		if (discountText == null) {
			discountText = new JTextField();
			discountText.setText(" 00%");
			discountText.setHorizontalAlignment(SwingConstants.LEFT);
			discountText.setEditable(false);
		}
		return discountText;
	}


} 
