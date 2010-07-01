/*
 * SDDL_ACEFrame.java		0.1		26/05/2006
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


package net.aepik.casl.ui.sddl;

import net.aepik.casl.core.sddl.SDDL_ACEString;
import net.aepik.casl.ui.util.TableCellJRadioButtonEditor;
import net.aepik.casl.ui.util.TableCellJRadioButtonRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.UUID;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

public class SDDL_ACEFrame extends JDialog implements ActionListener, WindowListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Fenetre mère **/
	private JDialog owner ;
	/** L'ace courante **/
	private SDDL_ACEString currentAce ;
	/** La liste des Aces **/
	private Vector<SDDL_ACEString> currentAceList ;
	/** La Jlist a rafraichir pour toute modif de cette ace **/
	private JList currentAceListIHM ;
	/** Indique si on a créé automatiquement une nouvelle ace **/
	private boolean isNewAce ;

	/** Les modèles des tables **/
	private SDDL_ACETableModel[] modeles ;
	/** Le sid de l'objet, comme définit dans une Ace **/
	private JTextField objectSid ;
	/** Le sid de l'objet dont cet objet hérite **/
	private JTextField inheritedObjectSid ;

	/** Bouton ok **/
	private JButton boutonOk ;
	/** Bouton annuler **/
	private JButton boutonAnnuler ;

	/** Le composant qui gère les différents panels **/
	private JTabbedPane onglets ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SDDL_ACEFrame( SDDL_ACLFrame o,
			SDDL_ACEString ace,
			Vector<SDDL_ACEString> aceList,
			JList aceListIHM ) {

		super( o, "Configuration d'une ACE", true );
		setSize( 400, 450 );
		setResizable( false );
		addWindowListener( this );

		// On modifie la localisation de la fenêtre
		// par rapport à la fenêtre parente.

		Point p = o.getLocation();
		setLocation( (int) p.getX()+20, (int) p.getY()+20 );

		// On créer les modèles de tables.

		modeles = new SDDL_ACETableModel[4];
		modeles[0] = new SDDL_ACETableModel( SDDL_ACEString.ACEType ) ;
		modeles[1] = new SDDL_ACETableModel( SDDL_ACEString.ACEFlags ) ;
		modeles[2] = new SDDL_ACETableModel( SDDL_ACEString.ACEPermissions ) ;
		modeles[3] = new SDDL_ACETableModel( SDDL_ACEString.ACETrustee ) ;

		if( ace!=null ) {
			currentAce = ace ;
			modeles[0].setFromGlobalString( ace.getType() );
			modeles[1].setFromGlobalString( ace.getFlags() );
			modeles[2].setFromGlobalString( ace.getPermissions() );
			modeles[3].setFromGlobalString( ace.getTrustee() );
			isNewAce = false ;
		} else {
			currentAce = new SDDL_ACEString() ;
			isNewAce = true ;
		}

		owner = o ;
		currentAceList = aceList ;
		currentAceListIHM = aceListIHM ;
		boutonOk = new JButton( "Valider" );
		boutonAnnuler = new JButton( "Annuler" );
		objectSid = new JTextField( currentAce.getObject() );
		inheritedObjectSid = new JTextField( currentAce.getInheritedObject() );
		onglets = new JTabbedPane();

		initFrame();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		if( o==boutonAnnuler ) {
			windowClosing( null );

		} else if( o==boutonOk ) {

			boolean ok = true ;

			// On regarde si les SID sont corrects.
			// Ils doivent être de la forme d'un UUID.

			if( !currentAce.setObject( objectSid.getText() ) ) {
				JOptionPane.showMessageDialog( this, "Le SID unique de cet objet n'est pas correct.", "Erreur", JOptionPane.ERROR_MESSAGE );
				//onglets.setSelectedIndex(0);
				objectSid.requestFocusInWindow();
				objectSid.selectAll();

			} else if( !currentAce.setInheritedObject( inheritedObjectSid.getText() ) ) {
				JOptionPane.showMessageDialog( this, "Le SID unique de l'objet parent n'est pas correct.", "Erreur", JOptionPane.ERROR_MESSAGE );
				//onglets.setSelectedIndex(0);
				inheritedObjectSid.requestFocusInWindow();
				inheritedObjectSid.selectAll();

			} else {

				// On récupère chaque champs de chaque tableau
				// et on modifie la valeur de l'ace courante.

				currentAce.setType( modeles[0].getGlobalStringValue() );
				currentAce.setFlags( modeles[1].getGlobalStringValue() );
				currentAce.setPermissions( modeles[2].getGlobalStringValue() );
				currentAce.setTrustee( modeles[3].getGlobalStringValue() );
	
				// On l'enregistre dans la fenêtre mère.
				// et on ferme la fenêtre.
	
				if( isNewAce
						&& currentAceList!=null
						&& currentAceListIHM!=null
						&& !currentAceList.contains( currentAce ) ) {
					currentAceList.add( currentAce );
					currentAceListIHM.setListData( currentAceList );
				}

				windowClosing( null );
				owner.repaint();
			}

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

		// Les boutons

		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );

		// Le premier onglet concerne le sid de Object et InheritedObject

		JPanel panelObjectSid = new JPanel( new GridLayout( 2, 1 ) );
		panelObjectSid.add( new JLabel( "Identifiant unique de cet objet pour cette règle :" ) );
		panelObjectSid.add( objectSid );
		panelObjectSid.setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 0 ) );

		JPanel panelInheritedObjectSid = new JPanel( new GridLayout( 2, 1 ) );
		panelInheritedObjectSid.add( new JLabel( "Identifiant unique de l'objet parent pour cette règle :" ) );
		panelInheritedObjectSid.add( inheritedObjectSid );

		JPanel panelGeneralConfigIn = new JPanel( new GridLayout( 2, 1 ) );
		JPanel panelGeneralConfig = new JPanel( new BorderLayout() );
		panelGeneralConfigIn.add( panelObjectSid );
		panelGeneralConfigIn.add( panelInheritedObjectSid );
		panelGeneralConfig.add( panelGeneralConfigIn, BorderLayout.NORTH );
		panelGeneralConfig.setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );

		onglets.add( "Général", panelGeneralConfig );

		// - Autres onglets -
		// On va gérer tous les onglets, on créé chaque
		// éléments, qui sont en fait chaque tableau.

		String[] titles = new String[4] ;
		titles[0] = "Types";
		titles[1] = "Paramêtres";
		titles[2] = "Permissions";
		titles[3] = "Administrateurs";

		JTable[] tables = new JTable[4];
		JScrollPane[] scrollers = new JScrollPane[4];

		for( int i=0; i<tables.length; i++ ) {

			tables[i] = new JTable( modeles[i] );
			tables[i].setTableHeader( null );
			tables[i].removeEditor();
			//tables[i].setBackground( (new JLabel()).getBackground() );
			tables[i].setGridColor( tables[i].getBackground() );
			tables[i].setRowSelectionAllowed( false );

			// La première table concerne les types.
			// On impose un choix à l'aide de bouton radio.
			if( i==0 ) {
				tables[0].setDefaultRenderer( Boolean.class, new TableCellJRadioButtonRenderer() );
				tables[0].setDefaultEditor( Boolean.class, new TableCellJRadioButtonEditor() );
			}

			tables[i].getColumnModel().getColumn(0).setPreferredWidth( 20 );
			tables[i].getColumnModel().getColumn(1).setPreferredWidth( 300 );

			JPanel panelTemp = new JPanel();
			panelTemp.setBackground( tables[i].getBackground() );
			panelTemp.add( tables[i] );

			scrollers[i] = new JScrollPane( panelTemp );
			scrollers[i].setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
			scrollers[i].setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
			scrollers[i].setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder( 11, 11, 11, 11 ),
						BorderFactory.createBevelBorder( BevelBorder.LOWERED, Color.lightGray, Color.darkGray ) ) );
			//scrollers[i].setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );

			onglets.add( titles[i], scrollers[i] );
		}

		// - Design -

		onglets.setBorder( BorderFactory.createEmptyBorder( 5, 5, 1, 5 ) );
		onglets.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

		JPanel panelBoutons = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		panelBoutons.add( boutonOk );
		panelBoutons.add( boutonAnnuler );

		JPanel panelMain = new JPanel( new BorderLayout() );
		panelMain.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );
		panelMain.add( onglets );
		panelMain.add( panelBoutons, BorderLayout.SOUTH );

		getContentPane().add( panelMain );
	}

}
