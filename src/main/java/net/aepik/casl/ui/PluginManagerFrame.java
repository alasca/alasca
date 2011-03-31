/*
 * PluginsManager.java		0.1		20/06/2006
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
import net.aepik.casl.ui.util.DescriptiveInternalFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * Fenêtre pour lister les extentions disponibles, avec leurs
 * descriptions respectives.
**/

public class PluginManagerFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Attributs
////////////////////////////////

	/** La fen$etre parente **/
	private JFrame mainFrame ;
	/** Le manager de plugins **/
	private PluginManager pluginManager ;

	/** Le bouton fermer **/
	private JButton closeButton = new JButton( "Fermer" );

////////////////////////////////
// Constructeurs
////////////////////////////////

	public PluginManagerFrame( JFrame owner, PluginManager pM ) {

		super();
		setSize( 500, 400 );
		setResizable( false );
		setLocationRelativeTo( owner );
		setTitle( "Liste des extentions" );
		setIconImage( owner.getIconImage() );
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

		mainFrame = owner ;
		pluginManager = pM ;

		for( int i=0; pluginManager.getPlugins()!=null
				&& i<pluginManager.getPlugins().length; i++ ) {
			pluginManager.getPlugins()[i].setRelativeTo( owner );
		}

		initFrame();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o =e.getSource();

		if( o==closeButton ) {
			setVisible( false );
			dispose();
		}
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private void initFrame() {

		JPanel buttonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		buttonsPanel.add( closeButton );

		PluginManagerPanel liste = new PluginManagerPanel( pluginManager );
		JScrollPane listeScroller = new JScrollPane( liste );
		listeScroller.setBackground( Color.white );
		listeScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		JPanel listePanel = new JPanel( new BorderLayout() );
		listePanel.add( listeScroller, BorderLayout.CENTER );
		listePanel.setBorder( BorderFactory.createEmptyBorder( 6, 6, 3, 6 ) );

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( listePanel, BorderLayout.CENTER );
		mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

		getContentPane().add( new DescriptiveInternalFrame(
				mainFrame.getIconImage(),
				"Sélectionner un plugin à éxécuter.\nUn plugin est éxécutable si"
				+ " toutes les conditions de son éxécution sont"
				+ " réunies.",
				mainPanel ) );

		closeButton.addActionListener( this );
	}

}
