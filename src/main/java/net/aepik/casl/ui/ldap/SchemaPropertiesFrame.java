/*
 * SchemaPropertiesFrame.java		0.1		12/06/2006
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

package net.aepik.casl.ui.ldap;

import net.aepik.casl.core.ldap.Schema;
import org.jdesktop.jdic.desktop.Desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Permet d'avoir un accès visuel aux propriétés du schéma.
 * Il est possible d'en rajouter, d'en supprimer, d'en modifier.
 * La lecture des propriétés du schéma dans les fichiers sont réalisées par
 * les parseurs de lecture. C'est eux qui fixent les noms des propriétés.
 * Pour plus d'informations, voir l'aide.
**/

public class SchemaPropertiesFrame extends JDialog
		implements
			ActionListener,
			ListSelectionListener,
			WindowListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le schéma **/
	private Schema schema ;
	/** Les propriétés du schéma **/
	private Properties properties;
	/** La liste des propriétés **/
	private JList propertiesList;

	/** Le bouton Ok **/
	private JButton boutonOk = new JButton( "Valider" );
	/** Le bouton Annuler **/
	private JButton boutonAnnuler = new JButton( "Annuler" );
	/** Le bouton Ajouter **/
	private JButton boutonAjouter = new JButton( "Ajouter" );
	/** Le bouton Supprimer **/
	private JButton boutonSupprimer = new JButton( "Supprimer" );
	/** Le bouton Modifier **/
	private JButton boutonModifier = new JButton( "Modifier" );
	/** Le bouton Plus d'informations **/
	private JButton boutonInfo = new JButton( "Plus d'informations..." );

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SchemaPropertiesFrame( Window parent, Schema s, String sName ) {

		super();
		setTitle( "Propriétés Schéma [" + sName + "]" );
		setModal( true );
		setSize( 400, 330 );
		setResizable( false );
		setLocationRelativeTo( parent );

		schema = s;
		properties = (Properties) schema.getProperties().clone();
		propertiesList = new JList();

		updateList();
		initFrame();
		updateButtons();
	}

////////////////////////////////
// Méthodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		// On souhaite ajouter une nouvelle valeur.
		// On fait appel à une classe interne pour afficher notre
		// boîte de saisie des données.
		if( o==boutonAjouter ) {
			SchemaPropertyEditorFrame f = new SchemaPropertyEditorFrame( this, null, null );
			f.setVisible( true );

		// Ici, nous affichons notre boîte de saisie des données, avec les
		// valeurs pré-remplies.
		} else if( o==boutonModifier ) {

			String str = propertiesList.getSelectedValue().toString();

			if( str!=null ) {
				int index = str.indexOf( ':' );
				SchemaPropertyEditorFrame f = new SchemaPropertyEditorFrame( this,
						str.substring( 0, index ).trim(),
						str.substring( index+1 ).trim() );
				f.setVisible( true );
			}

		// On supprime l'élément en cours de sélection.
		} else if( o==boutonSupprimer ) {

			String str = propertiesList.getSelectedValue().toString();

			if( str!=null ) {
				int index = str.indexOf( ':' );
				properties.remove( str.substring( 0, index ).trim() );
				updateList();
			}

		// Bouton valider, on enregistre les nouvelles valeurs dans le schéma.
		} else if( o==boutonOk ) {
			schema.setProperties( properties );
			windowClosing( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );

		// On demande plus d'informations.
		// On va ouvrir un naviguateur web avec une URL définie.
		} else if( o==boutonInfo ) {

			try {
				String currentDir = System.getProperty( "user.dir" );
				Desktop.browse( new URL( "file://" + currentDir + "/doc/index.html" ) );
			} catch( Exception ex ) {
				//System.out.println( ex );
				ex.printStackTrace();
			}


		// Bouton annuler
		} else if( o==boutonAnnuler ) {
			windowClosing( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
		}

	}

	public void valueChanged( ListSelectionEvent e ) {

		Object o = ( e!=null ) ? e.getSource() : null;
		if( o==propertiesList ) {
			updateButtons();
		}
	}

	// Méthodes liées aux événements fenêtrés.
	public void windowActivated( WindowEvent e ) {}
	public void windowClosed( WindowEvent e ) {}
 	public void windowClosing( WindowEvent e ) { e.getComponent().setVisible( false ); }
	public void windowDeactivated( WindowEvent e ) {}
	public void windowDeiconified( WindowEvent e ) {}
	public void windowIconified( WindowEvent e ) {}
	public void windowOpened( WindowEvent e ) {}

////////////////////////////////
// Méthodes privées
////////////////////////////////

	private void initFrame() {

		// - Panel bouton du bas -

		JPanel boutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		boutonsPanel.add( boutonOk );
		boutonsPanel.add( boutonAnnuler );

		// - Panel de description -

		JTextArea textAreaDescription = new JTextArea(
				"Les propriétés du schéma peuvent être modifiées. Elles ne sont"
				+ " pas pré-définies et sont fonction de la syntaxe employée"
				+ " par ce schéma. Il n'est pas possible de lister toutes les"
				+ " propriétés du schéma, pour cela référez vous à l'aide en"
				+ " cliquant sur le lien plus bas." );
		textAreaDescription.setEditable( false );
		textAreaDescription.setLineWrap( true );
		textAreaDescription.setWrapStyleWord( true );
		textAreaDescription.setFont( (new JLabel()).getFont() );
		textAreaDescription.setBorder( BorderFactory.createEmptyBorder( 7, 8, 7, 8 ) );
		textAreaDescription.setBackground( (new JLabel()).getBackground() );

		// - Panel liste des propriétés -

		JPanel propertiesBoutons = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		propertiesBoutons.add( boutonAjouter );
		propertiesBoutons.add( boutonSupprimer );
		propertiesBoutons.add( boutonModifier );

		JScrollPane propertiesScroller = new JScrollPane( propertiesList );
		propertiesScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		propertiesList.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
		propertiesList.setVisibleRowCount( 4 );
		propertiesList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		JPanel propertiesPanel = new JPanel( new BorderLayout() );
		propertiesPanel.add( propertiesScroller, BorderLayout.CENTER );
		propertiesPanel.add( propertiesBoutons, BorderLayout.SOUTH );
		propertiesPanel.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 5, 1, 5 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Listes des propriétés " ),
					BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) ) );

		// - Panel informations -

		boutonInfo.setBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.blue ) );
		boutonInfo.setForeground( Color.blue );
		boutonInfo.setFocusPainted( false );
		boutonInfo.setContentAreaFilled( false );

		JPanel infoPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		infoPanel.add( boutonInfo );
		infoPanel.setBorder( BorderFactory.createEmptyBorder( 0, 4, 1, 4 ) );

		// - Organisation générales -

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( textAreaDescription, BorderLayout.NORTH );
		mainPanel.add( propertiesPanel, BorderLayout.CENTER );
		mainPanel.add( infoPanel, BorderLayout.SOUTH );

		JPanel mainPanelContainer = new JPanel( new BorderLayout() );
		mainPanelContainer.add( mainPanel, BorderLayout.NORTH );
		mainPanelContainer.add( boutonsPanel, BorderLayout.SOUTH );
		mainPanelContainer.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );

		getContentPane().add( mainPanelContainer );

		// - Listeners -

		addWindowListener( this );
		propertiesList.addListSelectionListener( this );
		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );
		boutonAjouter.addActionListener( this );
		boutonModifier.addActionListener( this );
		boutonSupprimer.addActionListener( this );
		boutonInfo.addActionListener( this );
	}

	private void updateButtons() {

		if( propertiesList.getSelectedIndex()!=-1 ) {
			boutonModifier.setEnabled( true );
			boutonSupprimer.setEnabled( true );

		} else {
			boutonModifier.setEnabled( false );
			boutonSupprimer.setEnabled( false );
		}
	}

	private void updateList() {

		DefaultListModel model = new DefaultListModel();
		for( Enumeration keys = properties.propertyNames();
				keys.hasMoreElements() ; ) {
			String key = (String) keys.nextElement();
			String value = properties.getProperty( key );
			model.addElement( key + ":" + value );
		}

		propertiesList.setModel( model );
		propertiesList.repaint();
	}

////////////////////////////////
// Classes internes
////////////////////////////////

	private class SchemaPropertyEditorFrame extends JDialog implements ActionListener {
		
		private JButton boutonOk = new JButton( "    Ok    " );
		private JButton boutonAnnuler = new JButton( "Annuler" );
		private JTextField varName ;
		private JTextField varValue ;

		public SchemaPropertyEditorFrame( JDialog f, String varname, String varvalue ) {

			super();
			setTitle( "Editer une propriété" );
			setModal( true );
			setResizable( false );
			setLocationRelativeTo( f );

			varName = new JTextField( varname );
			varValue = new JTextField( varvalue );

			initFrame();
		}

		public void actionPerformed( ActionEvent e ) {

			Object o = e.getSource();

			// Bouton annuler boîte de saisie.
			if( o==boutonAnnuler ) {
				SchemaPropertiesFrame.this.windowClosing(
						new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );

			// Bouton de validation. On mets à jour les valeurs de la liste
			// de la classe parente.
			} else if( o==boutonOk ) {
				properties.setProperty( varName.getText(), varValue.getText() );
				SchemaPropertiesFrame.this.updateList();
				SchemaPropertiesFrame.this.windowClosing(
						new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
			}

		}

		private void initFrame() {

			JTextArea inputsDescription = new JTextArea(
					"Veuillez remplir les champs ci-dessous." );
			inputsDescription.setEditable( false );
			inputsDescription.setLineWrap( true );
			inputsDescription.setWrapStyleWord( true );
			inputsDescription.setFont( (new JLabel()).getFont() );
			inputsDescription.setBorder( BorderFactory.createEmptyBorder( 2, 3, 6, 3 ) );
			inputsDescription.setBackground( (new JLabel()).getBackground() );

			JPanel inputsPanel = new JPanel( new BorderLayout() );
			inputsPanel.setBorder( BorderFactory.createEmptyBorder( 0, 2, 0, 2 ) );

			JPanel p1 = new JPanel( new BorderLayout() );
			p1.add( new JLabel( "Nom de la variable :" ), BorderLayout.NORTH );
			p1.add( varName, BorderLayout.CENTER );
			p1.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
			inputsPanel.add( p1, BorderLayout.NORTH );
			JPanel p2 = new JPanel( new BorderLayout() );
			p2.add( new JLabel( "Valeur de la variable :" ), BorderLayout.NORTH );
			p2.add( varValue, BorderLayout.CENTER );
			p2.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
			inputsPanel.add( p2, BorderLayout.CENTER );

			JPanel inputsPanelContainer = new JPanel( new BorderLayout() );
			inputsPanelContainer.add( inputsPanel, BorderLayout.NORTH );

			JPanel boutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			boutonsPanel.add( boutonOk );
			boutonsPanel.add( boutonAnnuler );

			JPanel mainPanel = new JPanel( new BorderLayout() );
			mainPanel.add( inputsDescription, BorderLayout.NORTH );
			mainPanel.add( inputsPanelContainer, BorderLayout.CENTER );
			mainPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 1, 5 ) );

			JPanel mainPanelContainer = new JPanel( new BorderLayout() );
			mainPanelContainer.add( mainPanel, BorderLayout.NORTH );
			mainPanelContainer.add( boutonsPanel, BorderLayout.SOUTH );
			mainPanelContainer.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );

			getContentPane().add( mainPanelContainer );

			// On fixe la taille.
			setSize( 300, 200 );
			setLocationRelativeTo( SchemaPropertiesFrame.this );

			addWindowListener( SchemaPropertiesFrame.this );
			boutonOk.addActionListener( this );
			boutonAnnuler.addActionListener( this );
		}

	}

}
