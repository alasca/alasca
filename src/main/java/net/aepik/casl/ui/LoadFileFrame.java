/*
 * SchemaFileFrame.java		0.1		06/06/2006
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

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.ui.util.DescriptiveInternalFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.SuppressWarnings;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;

public class LoadFileFrame extends JDialog implements ActionListener, WindowListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La fenêtre appelante **/
	private JFrame mainFrame ;
	/** Le manager de schéma **/
	private SchemaManager manager ;

	/** Le champs contenant le nom du fichier **/
	private JTextField filename ;
	/** La liste contenant l'ensemble des syntaxes disponibles **/
	private JComboBox syntaxes ;

	/** Le bouton de choix du fichier **/
	private JButton boutonOpenFile ;
	/** Le bouton Ok **/
	private JButton boutonOk ;
	/** Le bouton Annuler **/
	private JButton boutonAnnuler ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public LoadFileFrame( JFrame f, SchemaManager m ) {

		super( f, "Ouvrir un fichier Schema", true );
		setSize( 400, 380 );
		setResizable( false );
		setLocationRelativeTo( f );

		mainFrame = f ;

		manager = m;
		filename = new JTextField();
		syntaxes = new JComboBox();
		boutonOpenFile = new JButton( "..." );
		boutonOk = new JButton( "Charger" );
		boutonAnnuler = new JButton( "Annuler" );

		initFrame();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		// On ouvre un naviguateur de fichiers
		// On récupère le nom du fichier sélectionné.
		if( o==boutonOpenFile ) {

			JFileChooser jfcProgramme = new JFileChooser( "." );
			jfcProgramme.setMultiSelectionEnabled( false );
			jfcProgramme.setDialogTitle( "Selectionner un fichier" );
			jfcProgramme.setApproveButtonText( "Selectionner" );
			jfcProgramme.setApproveButtonToolTipText( "Cliquer apres avoir selectionné un fichier" );

			if( jfcProgramme.showDialog( this, null )==JFileChooser.APPROVE_OPTION ) {

				try {
					filename.setText( jfcProgramme.getSelectedFile().getCanonicalPath() );
				} catch( IOException ioe ) {
					JOptionPane.showMessageDialog( null, "Erreur de nom de fichier.", "Erreur", JOptionPane.ERROR_MESSAGE );
				}
			}

		// On récupère toutes les infos importantes : nom de fichier et syntaxe.
		// On créé dynamiquement une nouvelle instance de la syntaxe.
		// On ouvre le fichier et on le parcourt avec la syntaxe.
		// Si le fichier est chargé correctement, on l'ajoute à la vue
		// principale et on ferme cette fenêtre.
		} else if( o==boutonOk && filename.getText().length()!=0 ) {

			if( (new File( filename.getText() )).exists() ) {

				try {

					// On créer l'objet de la syntaxe dynamiquement.
					String syntaxName = (String) syntaxes.getSelectedItem();
					@SuppressWarnings("unchecked")
					SchemaSyntax syntax = ((Class<SchemaSyntax>) Class.forName(
							Schema.getSyntaxPackageName() + "." + syntaxName )).newInstance();

					// On charge.
					Schema schema = Schema.create( syntax, filename.getText() );

					// Si le chargement du fichier réussi, ne réussi pas, le format est incorrect.
					if( schema==null ) {
						JOptionPane.showMessageDialog( this, "Le format du fichier est incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE );

					// Sinon si l'ajout echoue, le fichier est déjà ouvert.
					} else if( !manager.addSchema( (new File( filename.getText() )).getName(), schema ) ) {
						JOptionPane.showMessageDialog( this, "Le fichier est déjà ouvert.", "Erreur", JOptionPane.ERROR_MESSAGE );

					// Sinon, ok.
					} else {
						windowClosing( null );
					}

				// Erreur lors de la lecture du fichier.					
				} catch( Exception ex ) {
					System.out.println( ex );
					JOptionPane.showMessageDialog( this, "Une erreur est survenue lors de la lecture du fichier.", "Erreur", JOptionPane.ERROR_MESSAGE );
				}
			}

		// On annule, toutes les informations sont perdues.
		} else if( o==boutonAnnuler ) {
			windowClosing( null );
		}

	}

	public void windowActivated( WindowEvent e ) {}
	public void windowClosed( WindowEvent e ) {}
 	public void windowClosing( WindowEvent e ) { setVisible( false ); }
	public void windowDeactivated( WindowEvent e ) {}
	public void windowDeiconified( WindowEvent e ) {}
	public void windowIconified( WindowEvent e ) {}
	public void windowOpened( WindowEvent e ) {}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private void initFrame() {

		// - Panel bouton du bas -

		JPanel boutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		boutonsPanel.add( boutonOk );
		boutonsPanel.add( boutonAnnuler );

		// - Panel nom du fichier -

		JTextArea textAreaFilename = new JTextArea(
				"Indiquez le nom du fichier contenant les définitions du schéma LDAP que vous"
				+ " souhaitez charger." );
		textAreaFilename.setEditable( false );
		textAreaFilename.setLineWrap( true );
		textAreaFilename.setWrapStyleWord( true );
		textAreaFilename.setFont( (new JLabel()).getFont() );
		textAreaFilename.setBorder( BorderFactory.createEmptyBorder( 7, 6, 12, 6 ) );
		textAreaFilename.setBackground( (new JLabel()).getBackground() );

		boutonOpenFile.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder( 0, 5, 0, 0, boutonsPanel.getBackground() ),
				boutonOpenFile.getBorder() ) );

		JPanel filenamePanel = new JPanel( new BorderLayout() );
		filenamePanel.add( textAreaFilename, BorderLayout.NORTH );
		filenamePanel.add( filename, BorderLayout.CENTER );
		filenamePanel.add( boutonOpenFile, BorderLayout.EAST );
		filenamePanel.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 4, 1, 4 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Schéma LDAP " ),
					BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) ) ) );

		// - Panel du selecteur de syntaxes -

		JTextArea textAreaSyntaxes = new JTextArea(
				"Il vous faut appliquer un filtre sur le schéma que vous voulez charger."
				+ " Voici la liste des filtres disponibles :" );
		textAreaSyntaxes.setEditable( false );
		textAreaSyntaxes.setLineWrap( true );
		textAreaSyntaxes.setWrapStyleWord( true );
		textAreaSyntaxes.setFont( (new JLabel()).getFont() );
		textAreaSyntaxes.setBorder( BorderFactory.createEmptyBorder( 7, 6, 12, 6 ) );
		textAreaSyntaxes.setBackground( (new JLabel()).getBackground() );

		JPanel syntaxesPanel = new JPanel( new BorderLayout() );
		syntaxesPanel.add( textAreaSyntaxes, BorderLayout.NORTH );
		syntaxesPanel.add( syntaxes );
		syntaxesPanel.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 4, 1, 4 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Syntaxe LDAP " ),
					BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) ) ) );

		// On injecte le nom des classes de syntaxes possibles
		// dynamiquement.
		String[] syntaxesName = Schema.getSyntaxes();
		for( int i=0; syntaxesName!=null && i<syntaxesName.length; i++ ) {
			syntaxes.addItem( syntaxesName[i] );
		}

		// - Organisation générale -

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( filenamePanel, BorderLayout.NORTH );
		mainPanel.add( syntaxesPanel, BorderLayout.CENTER );

		JPanel mainPanelContainer = new JPanel( new BorderLayout() );
		mainPanelContainer.add( mainPanel, BorderLayout.NORTH );
		mainPanelContainer.add( boutonsPanel, BorderLayout.SOUTH );
		mainPanelContainer.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );

		getContentPane().add( new DescriptiveInternalFrame(
				mainFrame.getIconImage(),
				"Sélectionner un fichier et sa syntaxe.",
				mainPanelContainer ) );

		// - Listeners -

		addWindowListener( this );
		boutonOpenFile.addActionListener( this );
		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );

	}

}
