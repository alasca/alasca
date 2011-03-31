/*
 * PluginsManagerPanel.java		0.1		20/07/2006
 * 
 * Copyright (C) 2006 Thomas Chemineau
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package net.aepik.casl.ui;

import net.aepik.casl.core.Plugin;
import net.aepik.casl.core.PluginManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Panel qui liste toutes les extentions.
**/

public class PluginManagerPanel extends JPanel {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Attributs
////////////////////////////////

	private PluginManager manager ;
	private PluginManagerCell selectedValue ;

	private Color selectionBackground ;
	private Color selectionForeground ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public PluginManagerPanel( PluginManager m ) {

		super( new BorderLayout() );
		setBackground( (new JList()).getBackground() );
		setForeground( (new JList()).getForeground() );

		manager = m;
		selectedValue = null ;

		selectionBackground = (new JList()).getSelectionBackground();
		selectionForeground = (new JList()).getSelectionForeground();

		initPanel();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public Color getSelectionBackground() { return selectionBackground; }
	public Color getSelectionForeground() { return selectionForeground; }

////////////////////////////////
// Methodes privées
////////////////////////////////

	public void initPanel() {

		Plugin[] plugins = manager.getPlugins();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.PAGE_AXIS ) );

		for( int i=0; plugins!=null && i<plugins.length; i++ ) {
			PluginManagerCell c = new PluginManagerCell( plugins[i] );
			mainPanel.add( c );
		}

		//if( plugins!=null && plugins.length>0 ) {
		//	PluginManagerCell tmp = (PluginManagerCell) mainPanel.getComponent(0);
		//	selectedValue = tmp ;
		//	tmp.setSelected( true );
		//	tmp.update();
		//}

		mainPanel.setOpaque( false );
		add( mainPanel, BorderLayout.NORTH );
	}

////////////////////////////////
// Classes privées
////////////////////////////////

	/**
	 * Objet représentant un Plugin dans une liste de sélection.
	 * C'est un JPanel affichant le nom, la catégorie, la description et la
	 * version du plugin.
	**/
	
	private class PluginManagerCell extends JPanel
			implements MouseListener {

		private static final long serialVersionUID = 0;

		private boolean isSelected ;
		private Plugin plugin ;

		private JLabel name ;
		private JLabel category ;
		private JLabel version ;
		private JTextArea description ;
		private JButton boutonExecuter ;
		private JButton boutonSupprimer ;
		private JPanel container ;

		public PluginManagerCell( Plugin p ) {
	
			super( new BorderLayout() );
			isSelected = false ;
			plugin = p ;

			Font styleSimple = (new JLabel( "font" )).getFont();
			Font styleGras = new Font( styleSimple.getName(),
					Font.BOLD, styleSimple.getSize() );

			////////

			name = new JLabel( plugin.getName() );
			name.setFont( styleGras );
			name.setOpaque( false );

			if( plugin.getCategory()!=null && plugin.getCategory().length()>0 ) {
				category = new JLabel( " Catégorie : " + plugin.getCategory() );
			} else {
				category = new JLabel( " Catégorie : Aucune" );
			}
			category.setFont( styleSimple );
			category.setOpaque( false );

			version = new JLabel( " Version : " + plugin.getVersion() );
			version.setFont( styleSimple );
			version.setOpaque( false );

			description = new JTextArea( plugin.getDescription() );
			description.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
			description.setEditable( false );
			description.setFocusable( false );
			description.setFont( (new JLabel()).getFont() );
			description.setOpaque( false );
			description.setLineWrap( true );
			description.setWrapStyleWord( true );

			JPanel p11 = new JPanel( new GridLayout( 2, 1 ) );
			p11.add( category );
			p11.add( version );
			p11.setOpaque( false );

			JPanel p1 = new JPanel( new BorderLayout() );
			p1.add( name, BorderLayout.NORTH );
			p1.add( p11, BorderLayout.CENTER );
			p1.add( description, BorderLayout.SOUTH );
			p1.setOpaque( false );

			////////

			boutonExecuter = new JButton( "Executer" );
			boutonExecuter.addActionListener( new PluginListener( plugin ) );

			JPanel p2 = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			p2.add( boutonExecuter );
			p2.setOpaque( false );

			////////

			//Dimension d = p1.getPreferredSize();
			//if( p2.getPreferredSize().getHeight()>d.getHeight() )
			//	d = p2.getPreferredSize() ;

			container = new JPanel( new BorderLayout() );
			//container.setPreferredSize( d );
			container.add( p1, BorderLayout.CENTER );
			container.add( p2, BorderLayout.SOUTH );

			//container = new JPanel( new BorderLayout() );
			//container.add( p1, BorderLayout.NORTH );
			//container.add( boutonPanel, BorderLayout.SOUTH );
			container.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 5 ) );

			add( container, BorderLayout.CENTER );
			addMouseListener( this );
			setBorder( BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.lightGray ),
					BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) ) );
			setOpaque( false );

			update();
		}

		public boolean isSelected() { return isSelected ; }

		public void mousePressed( MouseEvent e ) {

			if( !isSelected ) {
				setSelected( true );
				update();

				PluginManagerCell c = PluginManagerPanel.this.selectedValue ;

				if( c!=null ) {
					c.setSelected( false );
					c.update();
				}

				PluginManagerPanel.this.selectedValue = this ;
			}
		}

		public void mouseClicked( MouseEvent e ) {}

		public void mouseEntered( MouseEvent e ) {}

		public void mouseExited( MouseEvent e ) {}

		public void mouseReleased( MouseEvent e ) {}

		public void setSelected( boolean selected ) { isSelected = selected ; }

		public void update() {

			if( isSelected ) {
				name.setForeground( PluginManagerPanel.this.getSelectionForeground() );
				category.setForeground( PluginManagerPanel.this.getSelectionForeground() );
				version.setForeground( PluginManagerPanel.this.getSelectionForeground() );
				description.setForeground( PluginManagerPanel.this.getSelectionForeground() );
				container.setBackground( PluginManagerPanel.this.getSelectionBackground() );

				description.setVisible( true );
				boutonExecuter.setVisible( true );

			} else {
				name.setForeground( (new JLabel()).getForeground() );
				category.setForeground( (new JLabel()).getForeground() );
				version.setForeground( (new JLabel()).getForeground() );
				description.setForeground( (new JLabel()).getForeground() );
				container.setBackground( PluginManagerPanel.this.getBackground() );

				description.setVisible( false );
				boutonExecuter.setVisible( false );
			}

			if( !plugin.canRun() )
				boutonExecuter.setEnabled( false );
			else
				boutonExecuter.setEnabled( true );

		}

	}

}
