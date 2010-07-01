/*
 * SchemaConverterFrame.java		0.1		09/06/2006
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

package net.aepik.casl.plugin.schemaconverter.ui;

import net.aepik.casl.plugin.schemaconverter.SCPlugin;
import net.aepik.casl.plugin.schemaconverter.core.SchemaConverter;
import net.aepik.casl.plugin.schemaconverter.core.Translator;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.syntax.*;
import net.aepik.casl.ui.ManagerFrame;
import net.aepik.casl.ui.util.DescriptiveInternalFrame;
import org.jdesktop.jdic.desktop.Desktop;

import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
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

public class SchemaConverterFrame extends JFrame {

////////////////////////////////
// Constantes
////////////////////////////////

	/** Le bouton Ok **/
	public JButton boutonOk = new JButton( "Convertir" );
	/** Le bouton Annuler **/
	public JButton boutonAnnuler = new JButton( "Annuler" );
	/** Le bouton précédent **/
	public JButton boutonPrecedent = new JButton( "Préc." );
	/** Le bouton suivant **/
	public JButton boutonSuivant = new JButton( "Suiv." );
	/** Le bouton plus d'infos (=url) **/
	public JButton boutonInfo = new JButton( "Plus d'informations..." );

////////////////////////////////
// Attributs
////////////////////////////////

	/** La fenêtre parente **/
	private JFrame mainFrame ;
	/** Le convertisseur **/
	private SchemaConverter convertisseur ;
	/** La liste contenant l'ensemble des dictionnaires disponibles **/
	private JComboBox dictionnaryList ;
	/** La liste contenant l'ensemble des syntaxes disponibles **/
	private JComboBox syntaxList ;

	/** Le layout qui va afficher tous les panels **/
	private CardLayout boards ;
	/** Le panel qui contient boards **/
	private JPanel boardsPanel ;
	/** L'ensemble des noms de panel **/
	private String[] boardsName = { "dictionnary", "syntax" };
	/** L'index du panel courant **/
	private int currentBoard ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SchemaConverterFrame( JFrame f, SCPlugin p, SchemaConverter c, String nomSchema ) {

		super();
		setTitle( "Convertir le schéma " + nomSchema );
		//setModal( true );
		setSize( 400, 350 );
		setResizable( false );
		setLocationRelativeTo( f );

		if( f!=null )
			setIconImage( f.getIconImage() );

		mainFrame = f ;
		convertisseur = c;
		boards = new CardLayout();
		boardsPanel = new JPanel( boards );
		currentBoard = 0 ;

		initFrame( p );
		updateButtonsStatus();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void addConverterListener( SchemaConverterListener l ) {

		addWindowListener( l );
		boutonOk.addActionListener( l );
		boutonPrecedent.addActionListener( l );
		boutonSuivant.addActionListener( l );
		boutonAnnuler.addActionListener( l );
		boutonInfo.addActionListener( l );
	}

	public String getSelectedDictionnaryName() {
		return dictionnaryList.getSelectedItem().toString();
	}

	public String getSelectedSyntaxName() {
		return syntaxList.getSelectedItem().toString();
	}

	public void removeConverterListener( SchemaConverterListener l ) {

		removeWindowListener( l );
		boutonOk.removeActionListener( l );
		boutonPrecedent.removeActionListener( l );
		boutonSuivant.removeActionListener( l );
		boutonAnnuler.removeActionListener( l );
		boutonInfo.removeActionListener( l );
	}

	public void switchToPreviousPanel() {

		currentBoard--;
		if( currentBoard<0 )
			currentBoard = 0;

		updateBoard();
		updateButtonsStatus();
	}

	public void switchToNextPanel() {

		currentBoard++;
		if( currentBoard>=boardsName.length )
			currentBoard = boardsName.length-1;

		updateBoard();
		updateButtonsStatus();
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private void initFrame( SCPlugin p ) {

		// - Panel description générale -

		JTextArea textAreaDescription = new JTextArea( p.getDescription() );
		textAreaDescription.setEditable( false );
		textAreaDescription.setLineWrap( true );
		textAreaDescription.setWrapStyleWord( true );
		textAreaDescription.setFont( (new JLabel()).getFont() );
		textAreaDescription.setBorder( BorderFactory.createEmptyBorder( 7, 8, 7, 8 ) );
		textAreaDescription.setBackground( (new JLabel()).getBackground() );

		// - Panel bouton du bas -

		JPanel boutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		boutonsPanel.add( boutonPrecedent );
		boutonsPanel.add( boutonSuivant );
		boutonsPanel.add( boutonOk );
		boutonsPanel.add( boutonAnnuler );

		// - Panel plus d'informations -

		boutonInfo.setBorder( BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.blue ) );
		boutonInfo.setForeground( Color.blue );
		boutonInfo.setFocusPainted( false );
		//boutonInfo.setBorderPainted( false );
		boutonInfo.setContentAreaFilled( false );

		JPanel panelInfo = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panelInfo.add( boutonInfo );
		panelInfo.setBorder( BorderFactory.createEmptyBorder( 0, 4, 1, 4 ) );

		// - Panel liste Dictionnaires -

		dictionnaryList = new JComboBox( convertisseur.getAvailableDictionnaries() );

		JTextArea textAreaDictionnary = new JTextArea(
				"Sélectionnez le dictionnaire de traduction. Un dictionnaire"
				+ " de traduction permet d'affiner les conversions de schéma." );
		textAreaDictionnary.setEditable( false );
		textAreaDictionnary.setLineWrap( true );
		textAreaDictionnary.setWrapStyleWord( true );
		textAreaDictionnary.setFont( (new JLabel()).getFont() );
		textAreaDictionnary.setBorder( BorderFactory.createEmptyBorder( 7, 2, 7, 2 ) );
		textAreaDictionnary.setBackground( (new JLabel()).getBackground() );

		JPanel panelDictionnaires = new JPanel( new BorderLayout() );
		panelDictionnaires.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 5, 1, 5 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Liste des dictionnaires " ),
					BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) ) ) );
		panelDictionnaires.add( textAreaDictionnary, BorderLayout.NORTH );

		// Panel liste Syntaxes -

		if( dictionnaryList.getItemCount()==0 ) {

			textAreaDictionnary.setText(
					"Aucune conversion disponible pour la syntaxe de ce schéma."
					+ " Les conversions sont réalisables si des dictionnaires de"
					+ " traduction sont définis pour traduire certaines syntaxes"
					+ " vers d'autres." );
			boutonOk.setEnabled( false );
			syntaxList = new JComboBox();

		} else {

			panelDictionnaires.add( dictionnaryList, BorderLayout.CENTER );
			syntaxList = new JComboBox( convertisseur.getAvailableSyntaxes(
					dictionnaryList.getSelectedItem().toString() ) );
		}

		// Panel liste Syntaxes -
	
		JTextArea textAreaSyntaxes = new JTextArea(
				"Sélectionnez une syntaxe de conversion. Le schéma, après"
				+ " conversion, sera de cette syntaxe." );
		textAreaSyntaxes.setEditable( false );
		textAreaSyntaxes.setLineWrap( true );
		textAreaSyntaxes.setWrapStyleWord( true );
		textAreaSyntaxes.setFont( (new JLabel()).getFont() );
		textAreaSyntaxes.setBorder( BorderFactory.createEmptyBorder( 7, 2, 7, 2 ) );
		textAreaSyntaxes.setBackground( (new JLabel()).getBackground() );

		JPanel panelSyntaxes = new JPanel( new BorderLayout() );
		panelSyntaxes.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 5, 1, 5 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Liste des syntaxes " ),
					BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) ) ) );
		panelSyntaxes.add( textAreaSyntaxes, BorderLayout.NORTH );
		panelSyntaxes.add( syntaxList, BorderLayout.CENTER );

		// - Organisation générale -

		boardsPanel.add( panelDictionnaires, boardsName[0] );
		boardsPanel.add( panelSyntaxes, boardsName[1] );

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( textAreaDescription, BorderLayout.NORTH );
		mainPanel.add( boardsPanel, BorderLayout.CENTER );
		mainPanel.add( panelInfo, BorderLayout.SOUTH );

		JPanel mainPanelContainer = new JPanel( new BorderLayout() );
		mainPanelContainer.add( mainPanel, BorderLayout.NORTH );
		mainPanelContainer.add( boutonsPanel, BorderLayout.SOUTH );
		mainPanelContainer.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );

		getContentPane().add( new DescriptiveInternalFrame(
				( mainFrame!=null ) ? mainFrame.getIconImage() : null,
				"Sélectionner un dictionnaire disponible et la syntaxe de destination.",
				mainPanelContainer ) );
	}

	private void updateBoard() {

		if( currentBoard==0 ) {

			dictionnaryList.removeAllItems();
			String[] tab = convertisseur.getAvailableDictionnaries();
			for( String s : tab )
				dictionnaryList.addItem( s );

		} else if( currentBoard==1 ) {

			syntaxList.removeAllItems();
			String dictionnary = dictionnaryList.getSelectedItem().toString();
			String[] tab = convertisseur.getAvailableSyntaxes( dictionnary );
			for( String s : tab )
				syntaxList.addItem( s );
		}

		boards.show( boardsPanel, boardsName[currentBoard] );
	}

	private void updateButtonsStatus() {

		boutonOk.setEnabled( true );
		boutonPrecedent.setEnabled( true );
		boutonSuivant.setEnabled( true );

		if( currentBoard==0 ) {

			boutonPrecedent.setEnabled( false );
			boutonOk.setEnabled( false );
			if( dictionnaryList.getItemCount()==0 )
				boutonSuivant.setEnabled( false );

		} else if( currentBoard==boardsName.length-1 ) {

			boutonSuivant.setEnabled( false );
			if( syntaxList.getItemCount()==0 )
				boutonOk.setEnabled( false );
		}
	}
}
