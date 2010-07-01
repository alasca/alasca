/*
 * SDDL_ACLFrame.java		0.1		26/05/2006
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
import net.aepik.casl.core.sddl.SDDL_ACLString;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SDDL_ACLFrame extends JDialog implements ActionListener, ListSelectionListener, WindowListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** ACL courante **/
	private SDDL_ACLString currentAcl ;

	/** Bouton ok **/
	private JButton boutonOk ;
	/** Bouton annuler **/
	private JButton boutonAnnuler ;
	/** Bouton Ajouter DACL **/
	private JButton boutonAjouterDACL ;
	/** Bouton Supprimer DACL **/
	private JButton boutonSupprimerDACL ;
	/** Bouton Modifier DACL **/
	private JButton boutonModifierDACL ;
	/** Bouton Ajouter SACL **/
	private JButton boutonAjouterSACL ;
	/** Bouton Supprimer SACL **/
	private JButton boutonSupprimerSACL ;
	/** Bouton Modifier SACL **/
	private JButton boutonModifierSACL ;

	/** L'identifiant du owner **/
	private JTextField textfieldOwnersid ;
	/** L'identifiant du group **/
	private JTextField textfieldGroupsid ;

	/** Le composant qui gère les différents panels **/
	private JTabbedPane onglets ;
	/** La JList concernant les DACLs **/
	private JList daclList ;
	/** La JList concernant les SACLs **/
	private JList saclList ;
	/** La liste des paramêtres DACL **/
	private JCheckBox[] daclFlags;
	/** La liste des paramêtres SACL **/
	private JCheckBox[] saclFlags;

	/** Le TextField de destination **/
	private JTextField result ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SDDL_ACLFrame( Window owner, SDDL_ACLString acl ) {

		super();
		setTitle( "Configuration d'une ACL" );
		setModal( true );
		setSize( 400, 450 );
		setResizable( false );
		addWindowListener( this );

		// Positionnement Fenêtre

		Point p = owner.getLocation();
		setLocation( (int) p.getX()+20, (int) p.getY()+20 );

		// Initialisation

		if( acl!=null ) {
			this.currentAcl = acl ;
		} else {
			this.currentAcl = new SDDL_ACLString();
		}

		boutonOk = new JButton( "Valider" );
		boutonAnnuler = new JButton( "Annuler" );
		boutonAjouterDACL = new JButton( "Ajouter" );
		boutonSupprimerDACL = new JButton( "Supprimer" );
		boutonModifierDACL = new JButton( "Modifier" );
		boutonAjouterSACL = new JButton( "Ajouter" );
		boutonSupprimerSACL = new JButton( "Supprimer" );
		boutonModifierSACL = new JButton( "Modifier" );

		textfieldGroupsid = new JTextField( currentAcl.getGroupSid() );
		textfieldOwnersid = new JTextField( currentAcl.getOwnerSid() );

		onglets = new JTabbedPane();
		daclList = new JList( currentAcl.getDACLACEs() );
		saclList = new JList( currentAcl.getSACLACEs() );

		daclFlags = new JCheckBox[3];
		daclFlags[0] = new JCheckBox( "Liste d'accès protégée",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[0], currentAcl.getDACLFlags() ) );
		daclFlags[1] = new JCheckBox( "Propagation automatique des permissions aux objets enfants",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[1], currentAcl.getDACLFlags() ) );
		daclFlags[2] = new JCheckBox( "Héritage des permissions autorisées",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[2], currentAcl.getDACLFlags() ) );

		saclFlags = new JCheckBox[3];
		saclFlags[0] = new JCheckBox( "Liste d'accès protégée",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[0], currentAcl.getSACLFlags() ) );
		saclFlags[1] = new JCheckBox( "Propagation automatique des permissions aux objets enfants",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[1], currentAcl.getSACLFlags() ) );
		saclFlags[2] = new JCheckBox( "Héritage des permissions autorisées",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[2], currentAcl.getSACLFlags() ) );

		result = new JTextField();

		initFrame();
	}

	public SDDL_ACLFrame( Window owner, SDDL_ACLString acl, JTextField aclTextField ) {

		super();
		setTitle( "Configuration d'une ACL" );
		setModal( true );
		setSize( 400, 450 );
		setResizable( false );
		addWindowListener( this );

		// Positionnement Fenêtre

		Point p = owner.getLocation();
		setLocation( (int) p.getX()+20, (int) p.getY()+20 );

		// Initialisation

		if( acl!=null ) {
			this.currentAcl = acl ;
		} else {
			this.currentAcl = new SDDL_ACLString();
		}

		boutonOk = new JButton( "Valider" );
		boutonAnnuler = new JButton( "Annuler" );
		boutonAjouterDACL = new JButton( "Ajouter" );
		boutonSupprimerDACL = new JButton( "Supprimer" );
		boutonModifierDACL = new JButton( "Modifier" );
		boutonAjouterSACL = new JButton( "Ajouter" );
		boutonSupprimerSACL = new JButton( "Supprimer" );
		boutonModifierSACL = new JButton( "Modifier" );

		textfieldGroupsid = new JTextField( currentAcl.getGroupSid() );
		textfieldOwnersid = new JTextField( currentAcl.getOwnerSid() );

		onglets = new JTabbedPane();
		daclList = new JList( currentAcl.getDACLACEs() );
		saclList = new JList( currentAcl.getSACLACEs() );

		daclFlags = new JCheckBox[3];
		daclFlags[0] = new JCheckBox( "Liste d'accès protégée",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[0], currentAcl.getDACLFlags() ) );
		daclFlags[1] = new JCheckBox( "Propagation automatique des permissions aux objets enfants",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[1], currentAcl.getDACLFlags() ) );
		daclFlags[2] = new JCheckBox( "Héritage des permissions autorisées",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[2], currentAcl.getDACLFlags() ) );

		saclFlags = new JCheckBox[3];
		saclFlags[0] = new JCheckBox( "Liste d'accès protégée",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[0], currentAcl.getSACLFlags() ) );
		saclFlags[1] = new JCheckBox( "Propagation automatique des permissions aux objets enfants",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[1], currentAcl.getSACLFlags() ) );
		saclFlags[2] = new JCheckBox( "Héritage des permissions autorisées",
				currentAcl.isPresentInAclFlags( SDDL_ACLString.aclFlags[2], currentAcl.getSACLFlags() ) );

		result = aclTextField;

		initFrame();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		// --------------------
		// - Suppression DACL -
		if( o==boutonSupprimerDACL ) {
			if( !daclList.isSelectionEmpty() ) {
				try {
					currentAcl.delACEForDACL( (SDDL_ACEString) daclList.getSelectedValue() );
					daclList.setListData( currentAcl.getDACLACEs() );
					daclList.repaint();
				} catch( Exception ex ) {}
			}

		// - Modification DACL -
		} else if( o==boutonModifierDACL ) {
			SDDL_ACEFrame f = new SDDL_ACEFrame( this,
					(SDDL_ACEString) daclList.getSelectedValue(),
					currentAcl.getDACLACEs(),
					daclList );
			f.setVisible( true );

		// - Ajout DACL -
		} else if( o==boutonAjouterDACL ) {
			SDDL_ACEFrame f = new SDDL_ACEFrame( this,
					null,
					currentAcl.getDACLACEs(),
					daclList );
			f.setVisible( true );
		}

		// --------------------
		// - Suppression SACL -
		if( o==boutonSupprimerSACL ) {
			if( !saclList.isSelectionEmpty() ) {
				try {
					currentAcl.delACEForSACL( (SDDL_ACEString) saclList.getSelectedValue() );
					saclList.setListData( currentAcl.getSACLACEs() );
					saclList.repaint();
				} catch( Exception ex ) {}
			}

		// - Modification SACL -
		} else if( o==boutonModifierSACL ) {
			SDDL_ACEFrame f = new SDDL_ACEFrame( this,
					(SDDL_ACEString) saclList.getSelectedValue(),
					currentAcl.getSACLACEs(),
					saclList );
			f.setVisible( true );

		// - Ajout SACL -
		} else if( o==boutonAjouterSACL ) {
			SDDL_ACEFrame f = new SDDL_ACEFrame( this,
					null,
					currentAcl.getSACLACEs(),
					saclList );
			f.setVisible( true );
		}

		// --------------------
		// - Bouton Ok -
		if( o==boutonOk ) {

			// On récupère les informations générales
			currentAcl.setGroupSid( textfieldGroupsid.getText() );
			currentAcl.setOwnerSid( textfieldOwnersid.getText() );

			// On récupère les paramêtres généraux pour chaque sous-ACL.
			String daclStr = "";
			for( int i=0; i<daclFlags.length; i++ ) {
				if( daclFlags[i].isSelected() )
					daclStr += SDDL_ACLString.aclFlags ;
			}
			String saclStr = "";
			for( int i=0; i<saclFlags.length; i++ ) {
				if( saclFlags[i].isSelected() )
					saclStr += SDDL_ACLString.aclFlags ;
			}

			currentAcl.setDACLFlags( daclStr );
			currentAcl.setSACLFlags( saclStr );

			result.setText( currentAcl.toString() );
			windowClosing( null );

		// - Bouton Annuler -
		} else if( o==boutonAnnuler ) {
			windowClosing( null );
		}
	}

	public void valueChanged( ListSelectionEvent e ) {

		Object o = ( e!=null ) ? e.getSource() : null;

		if( o==daclList ) {
			if( daclList.getSelectedIndex()!=-1 ) {
				boutonModifierDACL.setEnabled( true );
				boutonSupprimerDACL.setEnabled( true );
			} else {
				boutonModifierDACL.setEnabled( false );
				boutonSupprimerDACL.setEnabled( false );
			}
		} else if( o==saclList ) {
			if( saclList.getSelectedIndex()!=-1 ) {
				boutonModifierSACL.setEnabled( true );
				boutonSupprimerSACL.setEnabled( true );
			} else {
				boutonModifierSACL.setEnabled( false );
				boutonSupprimerSACL.setEnabled( false );
			}
		}
	}

	public void windowActivated( WindowEvent e ) {}
	public void windowClosed( WindowEvent e ) { }
 	public void windowClosing( WindowEvent e ) { setVisible( false ); }
	public void windowDeactivated( WindowEvent e ) {}
	public void windowDeiconified( WindowEvent e ) {}
	public void windowIconified( WindowEvent e ) {}
	public void windowOpened( WindowEvent e ) {}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private void initFrame() {

		// - Boutons -

		JPanel panelBoutonsDACL = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		panelBoutonsDACL.add( boutonAjouterDACL );
		panelBoutonsDACL.add( boutonModifierDACL );
		panelBoutonsDACL.add( boutonSupprimerDACL );

		JPanel panelBoutonsSACL = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		panelBoutonsSACL.add( boutonAjouterSACL );
		panelBoutonsSACL.add( boutonModifierSACL );
		panelBoutonsSACL.add( boutonSupprimerSACL );

		JPanel panelBoutons = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		panelBoutons.add( boutonOk );
		panelBoutons.add( boutonAnnuler );

		// - Onglet 1 -

		// Panel affichant des informations.

		JTextArea textAreaGeneralConfig = new JTextArea(
				"La configuration d'une ACL s'applique à un objet. Il est possible de spécifier"
				+ " le propriétaire de cet objet, ainsi que son groupe propriétaire." );
		textAreaGeneralConfig.setEditable( false );
		textAreaGeneralConfig.setLineWrap( true );
		textAreaGeneralConfig.setWrapStyleWord( true );
		textAreaGeneralConfig.setFont( (new JLabel()).getFont() );
		textAreaGeneralConfig.setBorder( BorderFactory.createEmptyBorder( 2, 1, 11, 1 ) );
		textAreaGeneralConfig.setBackground( (new JLabel()).getBackground() );

		JPanel panelOwnersid = new JPanel( new GridLayout( 2, 1 ) );
		panelOwnersid.add( new JLabel( "Identifiant du possesseur :" ) );
		panelOwnersid.add( textfieldOwnersid );
		panelOwnersid.setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 0 ) );
		JPanel panelGroupsid = new JPanel( new GridLayout( 2, 1 ) );
		panelGroupsid.add( new JLabel( "Identifiant du groupe :" ) );
		panelGroupsid.add( textfieldGroupsid );

		JPanel panelGeneralOptions = new JPanel( new GridLayout( 2, 1 ) );
		panelGeneralOptions.add( panelOwnersid );
		panelGeneralOptions.add( panelGroupsid );
		JPanel panelGeneralOptionsIn = new JPanel( new BorderLayout() );
		panelGeneralOptionsIn.add( panelGeneralOptions, BorderLayout.NORTH );

		JPanel panelGeneralConfig = new JPanel( new BorderLayout() );
		panelGeneralConfig.add( textAreaGeneralConfig, BorderLayout.NORTH );
		panelGeneralConfig.add( panelGeneralOptionsIn, BorderLayout.CENTER );
		panelGeneralConfig.setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );

		// - Onglet 2 -

		// Panel affichant les informations concernant les DACLs.

		JTextArea textAreaDACLConfig = new JTextArea(
				"La DACL est la liste de contrôles d'accès relative à l'annuaire."
				+ " Il est possible de spécifier des paramêtres généraux pour cette liste,"
				+ " puis d'affiner sa configuration avec les ACEs." );
		textAreaDACLConfig.setEditable( false );
		textAreaDACLConfig.setLineWrap( true );
		textAreaDACLConfig.setWrapStyleWord( true );
		textAreaDACLConfig.setFont( (new JLabel()).getFont() );
		textAreaDACLConfig.setBorder( BorderFactory.createEmptyBorder( 2, 3, 6, 3 ) );
		textAreaDACLConfig.setBackground( (new JLabel()).getBackground() );

		// Panel concernant les options DACL.

		JPanel panelDACLOptionsIn = new JPanel( new GridLayout( 3, 1 ) );
		for( int i=0; i<daclFlags.length; i++ )
			panelDACLOptionsIn.add( daclFlags[i] );

		JPanel panelDACLOptions = new JPanel( new BorderLayout() );
		panelDACLOptions.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 0, 0, 0 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Paramêtres généraux " ),
					BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) ) );
		panelDACLOptions.add( panelDACLOptionsIn, BorderLayout.CENTER );

		// Panel concernant les DACLs.

		JScrollPane daclListScroller = new JScrollPane( daclList );
		daclListScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		daclList.setVisibleRowCount( 5 );
		daclList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		JPanel panelDACLConfig = new JPanel( new BorderLayout() );
		panelDACLConfig.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 0, 0, 0 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Liste des ACEs " ),
					BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) ) );
		panelDACLConfig.add( daclListScroller, BorderLayout.CENTER );
		panelDACLConfig.add( panelBoutonsDACL, BorderLayout.SOUTH );

		// Panel général de l'onglet 2

		JPanel panelDACL = new JPanel( new BorderLayout() );
		panelDACL.add( textAreaDACLConfig, BorderLayout.NORTH );
		panelDACL.add( panelDACLOptions, BorderLayout.CENTER );
		panelDACL.add( panelDACLConfig, BorderLayout.SOUTH );
		panelDACL.setBorder( BorderFactory.createEmptyBorder( 11, 9, 11, 9 ) );

		JPanel panelDACLContainer = new JPanel( new BorderLayout() );
		panelDACLContainer.add( panelDACL, BorderLayout.NORTH );

		// - Onglet 3 -

		// Panel affichant les informations concernant les DACL.

		JTextArea textAreaSACLConfig = new JTextArea(
				"La SACL est la liste de contrôles d'accès relative au système."
				+ " Il est possible de spécifier des paramêtres généraux pour cette liste,"
				+ " puis d'affiner sa configuration avec les ACEs." );
		textAreaSACLConfig.setEditable( false );
		textAreaSACLConfig.setLineWrap( true );
		textAreaSACLConfig.setWrapStyleWord( true );
		textAreaSACLConfig.setFont( (new JLabel()).getFont() );
		textAreaSACLConfig.setBorder( BorderFactory.createEmptyBorder( 2, 3, 6, 3 ) );
		textAreaSACLConfig.setBackground( (new JLabel()).getBackground() );

		// Panel concernant les options SACL.

		JPanel panelSACLOptionsIn = new JPanel( new GridLayout( 3, 1 ) );
		for( int i=0; i<saclFlags.length; i++ )
			panelSACLOptionsIn.add( saclFlags[i] );

		JPanel panelSACLOptions = new JPanel( new BorderLayout() );
		panelSACLOptions.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 0, 0, 0 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Paramêtres généraux " ),
					BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) ) );
		panelSACLOptions.add( panelSACLOptionsIn );

		// Panel concernant les SACLs.

		JScrollPane saclListScroller = new JScrollPane( saclList );
		saclListScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		saclList.setVisibleRowCount( 5 );
		saclList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		JPanel panelSACLConfig = new JPanel( new BorderLayout() );
		panelSACLConfig.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder( 5, 0, 0, 0 ),
				BorderFactory.createCompoundBorder( 
					BorderFactory.createTitledBorder( " Liste des ACEs " ),
					BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) ) );
		panelSACLConfig.add( saclListScroller, BorderLayout.CENTER );
		panelSACLConfig.add( panelBoutonsSACL, BorderLayout.SOUTH );

		// Panel général de l'onglet 3

		JPanel panelSACL = new JPanel( new BorderLayout() );
		panelSACL.add( textAreaSACLConfig, BorderLayout.NORTH );
		panelSACL.add( panelSACLOptions, BorderLayout.CENTER );
		panelSACL.add( panelSACLConfig, BorderLayout.SOUTH );
		panelSACL.setBorder( BorderFactory.createEmptyBorder( 11, 9, 11, 9 ) );

		JPanel panelSACLContainer = new JPanel( new BorderLayout() );
		panelSACLContainer.add( panelSACL, BorderLayout.NORTH );

		// - Design -

		onglets.add( "Général", panelGeneralConfig );
		onglets.add( "Contrôles d'accès à l'annuaire", panelDACLContainer );
		onglets.add( "Contrôles d'accès du système", panelSACLContainer );
		onglets.setBorder( BorderFactory.createEmptyBorder( 5, 5, 1, 5 ) );
		onglets.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

		JPanel panelMain = new JPanel( new BorderLayout() );
		panelMain.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );
		panelMain.add( onglets );
		panelMain.add( panelBoutons, BorderLayout.SOUTH );

		getContentPane().add( panelMain );

		// - Action Listener -

		boutonModifierDACL.setEnabled( false );
		boutonModifierSACL.setEnabled( false );
		boutonSupprimerDACL.setEnabled( false );
		boutonSupprimerSACL.setEnabled( false );

		// - Ecouteurs -

		saclList.addListSelectionListener( this );
		daclList.addListSelectionListener( this );

		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );
		boutonAjouterDACL.addActionListener( this );
		boutonAjouterSACL.addActionListener( this );
		boutonModifierDACL.addActionListener( this );
		boutonModifierSACL.addActionListener( this );
		boutonSupprimerDACL.addActionListener( this );
		boutonSupprimerSACL.addActionListener( this );
	}

}
