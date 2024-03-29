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
/* This code was found posted on a forum without any copyright
 * restrictions. 
 * */

/* I have modified the original code */

package com.mbb.TicketMaven.ui.util;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;


/**
 * The Class FontChooser - displays a dialog for selecting a font
 */
public class FontChooser extends JDialog {
    
    private static final long serialVersionUID = 1L;

	/** The style list. */
    private String[] styleList = new String[] { "Plain", "Bold", "Italic" };
    
    /** The size list. */
    private String[] sizeList =
    new String[] {
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "22",
        "24",
        "27",
        "30",
        "34",
        "39",
        "45",
        "51",
        "60" };
        
        /** The Style list. */
        private NwList StyleList;
        
        /** The Font list. */
        private NwList FontList;
        
        /** The Size list. */
        private NwList SizeList;
        
        /** The Sample. */
        private static JLabel Sample = new JLabel();
        
        /** The ob. */
        private boolean ob = false;
        
        /**
         * Instantiates a new font chooser.
         * 
         * @param parent the parent
         * @param modal the modal
         * @param font_in the font
         */
        private FontChooser(Frame parent, boolean modal, Font font_in) {
        	
            super(parent, modal);
            Font font = font_in;
            initAll();
            setTitle("Font Choosr");
            if (font == null)
                font = Sample.getFont();
            FontList.setSelectedItem(font.getName());
            SizeList.setSelectedItem(font.getSize() + "");
            StyleList.setSelectedItem(styleList[font.getStyle()]);
            
        }
        
        /**
         * Show dialog.
         * 
         * @param parent the parent
         * @param s the s
         * @param font the font
         * 
         * @return the font
         */
        public static Font showDialog(Frame parent, String s, Font font) {
            FontChooser fd = new FontChooser(parent, true, font);
            if (s != null)
                fd.setTitle(s);
            fd.setVisible(true);
            Font fo = null;
            if (fd.ob)
                fo = Sample.getFont();
            fd.dispose();
            return (fo);
        }
        
        /**
         * Inits the all.
         */
        private void initAll() {
            getContentPane().setLayout(null);
            setBounds(50, 50, 450, 450);
            addLists();
            addButtons();
            Sample.setBounds(10, 320, 415, 25);
            Sample.setForeground(Color.black);
            getContentPane().add(Sample);
            addWindowListener(new WindowAdapter() {
                @Override
				public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });
        }
        
        /**
         * Adds the lists.
         */
        private void addLists() {
            FontList =
            new NwList(
            GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames());
            StyleList = new NwList(styleList);
            SizeList = new NwList(sizeList);
            FontList.setBounds(10, 10, 260, 295);
            StyleList.setBounds(280, 10, 80, 295);
            SizeList.setBounds(370, 10, 40, 295);
            getContentPane().add(FontList);
            getContentPane().add(StyleList);
            getContentPane().add(SizeList);
        }
        
        /**
         * Adds the buttons.
         */
        private void addButtons() {
            JButton ok = new JButton("Ok");
            ok.setMargin(new Insets(0, 0, 0, 0));
            JButton ca = new JButton("Cancel");
            ca.setMargin(new Insets(0, 0, 0, 0));
            ok.setBounds(260, 350, 70, 20);
            ok.setFont(new Font(" ", 1, 11));
            ca.setBounds(340, 350, 70, 20);
            ca.setFont(new Font(" ", 1, 12));
            getContentPane().add(ok);
            getContentPane().add(ca);
            ok.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    ob = true;
                }
            });
            ca.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    ob = false;
                }
            });
        }
        
        /**
         * Show sample.
         */
        private void showSample() {
            int g = 0;
            try {
                g = Integer.parseInt(SizeList.getSelectedValue());
            }
            catch (NumberFormatException nfe) {
            	//empty
            }
            String st = StyleList.getSelectedValue();
            int s = Font.PLAIN;
            if (st.equalsIgnoreCase("Bold"))
                s = Font.BOLD;
            if (st.equalsIgnoreCase("Italic"))
                s = Font.ITALIC;
            Sample.setFont(new Font(FontList.getSelectedValue(), s, g));
            Sample.setText("The quick brown fox jumped over the lazy dog.");
        }
        //////////////////////////////////////////////////////////////////////
        /**
         * The Class NwList.
         */
        public class NwList extends JPanel {
            
            private static final long serialVersionUID = 1L;

			/** The jl. */
            private JList<String> jl;
            
            /** The sp. */
            private JScrollPane sp;
            
            /** The jt. */
            private JLabel jt;
            
            /** The si. */
            private String si = " ";
            
            /**
             * Instantiates a new nw list.
             * 
             * @param values the values
             */
            public NwList(String[] values) {
                setLayout(null);
                jl = new JList<String>(values);
                sp = new JScrollPane(jl);
                jt = new JLabel();
                jt.setBackground(Color.white);
                jt.setForeground(Color.black);
                jt.setOpaque(true);
                jt.setBorder(new JTextField().getBorder());
                jt.setFont(getFont());
                jl.setBounds(0, 0, 100, 1000);
                jl.setBackground(Color.white);
                jl.addListSelectionListener(new ListSelectionListener() {
                    @Override
					public void valueChanged(ListSelectionEvent e) {
                        jt.setText((String) jl.getSelectedValue());
                        si = (String) jl.getSelectedValue();
                        showSample();
                    }
                });
                add(sp);
                add(jt);
            }
            
            /**
             * Gets the selected value.
             * 
             * @return the selected value
             */
            public String getSelectedValue() {
                return (si);
            }
            
            /**
             * Sets the selected item.
             * 
             * @param s the new selected item
             */
            public void setSelectedItem(String s) {
                jl.setSelectedValue(s, true);
            }
            
            /* (non-Javadoc)
             * @see java.awt.Component#setBounds(int, int, int, int)
             */
            @Override
			public void setBounds(int x, int y, int w, int h) {
                super.setBounds(x, y, w, h);
                sp.setBounds(0, y + 12, w, h - 23);
                sp.revalidate();
                jt.setBounds(0, 0, w, 20);
            }
        }
        
        /**
         * Font string.
         * 
         * @param font the font
         * 
         * @return the string
         */
        static public String fontString(Font font) {
            String fs = font.getFamily();
            if( !font.isPlain() ) {
                fs += "-";
                if( font.isBold()) {
                    fs += "BOLD";
                }
                if( font.isItalic()) {
                    fs += "ITALIC";
                }
            }
            fs += "-" + font.getSize();
            return(fs);
        }
        
        /**
         * Sets the default font.
         * 
         * @param f the new default font
         */
        static public void setDefaultFont(Font f ) {
            FontUIResource fui = new FontUIResource(f);
            Enumeration<?> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof FontUIResource)
                    UIManager.put(key, fui);
            }
            
        }
        
        /**
         * The main method.
         * 
         * @param args the arguments
         */
        public static void main(String args[]) {
            Font font = null;
            font = FontChooser.showDialog(null, null, null);
            
            System.out.println(fontString(font));
        }
}
