/*
 * SchemaPanel.java		0.1		02/06/2006
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
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaValue;
import net.aepik.casl.ui.util.NoEditableTableModel;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Cet objet est un JPanel représentant un schéma.
 * Sur le côté droit se trouve l'arbre pour naviguer dans les éléments
 * du schéma, sur le côté gauche se trouve une table affichant les
 * propriétées d'un élément séléctionné dans l'arbre.
**/

public class SchemaPanel extends JPanel {

////////////////////////////////
// Constantes
////////////////////////////////

	/** L'item de suppression **/
	public JMenuItem item_supprimer = new JMenuItem( "Supprimer" );
	/** L'item propriété **/
	public JMenuItem item_propriete = new JMenuItem( "Modifier..." );
	/** L'item propriété 2 (pour la table) **/
	public JMenuItem item_propriete2 = new JMenuItem( "Modifier..." );

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le schema **/
	private Schema schema ;
	/** L'arbre affiché **/
	private JTree arbre ;
	/** Le model de données de l'arbre **/
	private DefaultTreeModel arbreModel ;
	/** La JTable affichant les informations **/
	private JTable table ;
	/** Le menu Popup pour l'arbre **/
	private JPopupMenu popupMenuArbre ;
	/** Le menu Popup pour la table **/
	private JPopupMenu popupMenuTable ;

	/** Le panel de l'arbre **/
	private SimpleInternalFrame arbrePanel ;
	/** Le panel de la table **/
	private SimpleInternalFrame tablePanel ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SchemaPanel( Schema schema ) {

		super();

		this.schema = schema ;
		this.arbre = new JTree();
		this.arbreModel = new DefaultTreeModel( null );
		this.table = new JTable();
		this.popupMenuArbre = new JPopupMenu();
		this.popupMenuTable = new JPopupMenu();

		initPanel();
		setNewSchema( schema );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ajoute un listener pour cet objet.
	 * @param listener Un objet SchemaListener.
	**/
	public void addSchemaListener( SchemaListener l ) {

		arbre.addTreeSelectionListener( l );
		arbre.addMouseListener( l );
		item_supprimer.addActionListener( l );
		item_supprimer.addMouseListener( l );
		item_propriete.addActionListener( l );
		item_propriete.addMouseListener( l );

		table.addMouseListener( l );
		item_propriete2.addActionListener( l );
		item_propriete2.addMouseListener( l );
	}

	/**
	 * Retourne l'object schema pour le noeud en cours de sélection.
	 * @return SchemaObject L'objet attaché au noeud en cours de séléction.
	**/
	public SchemaObject getCurrentSelectedObject() {

		String id = getCurrentSelectedObjectId();
		return schema.getObject( id );
	}

	/**
	 * Retourne le path de l'abre pour le noeud en cours de sélection.
	 * @return TreePath Le chemin du noeud en cours de sélection.
	**/
	public TreePath getCurrentSelectedPath() {
		return arbre.getSelectionPath();
	}

	/**
	 * Supprime le dernier noeud sélectionné dans l'arbre.
	 * @return boolean True si la suppression a réussi, false sinon.
	**/
	public boolean removeCurrentSelectedNode() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();

		if( node!=null ) {
			arbreModel.removeNodeFromParent( node );
			return true ;
		}

		return false ;
	}

	/**
	 * Rafraichit le noeud parent du noeud sélectionné.
	 * @return boolean True si le rafraichissement a réussi, false sinon.
	**/
	public boolean refreshParentSelectedNode() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();

		if( node!=null ) {
			DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
			arbreModel.reload( nodeParent);
			return true ;
		}

		return false ;
	}

	/**
	 * Supprime un listener pour cet objet.
	 * @param listener Un objet SchemaListener.
	**/
	public void removeSchemaListener( SchemaListener l ) {

		arbre.removeTreeSelectionListener( l );
		arbre.removeMouseListener( l );
		item_supprimer.removeActionListener( l );
		item_supprimer.removeMouseListener( l );
		item_propriete.removeActionListener( l );
		item_propriete.removeMouseListener( l );

		table.removeMouseListener( l );
		item_propriete2.removeActionListener( l );
		item_propriete2.removeMouseListener( l );
	}

	/**
	 * Sélectionne un objet particulier grâce au chemin spécifié
	 * @param path Un objet TreePath qui renseigne le schéma.
	**/
	public void setSelectedPath( TreePath path ) {
		arbre.setSelectionPath( path );
	}

	/**
	 * Modifie les données du panel. On modifie par conséquent les données
	 * de l'arbre et on remet à jour la table.
	 * @param s Le nouveau schéma.
	**/
	public void setNewSchema( Schema s ) {

		schema = s ;
		updateTree();
		updateTable();
	}

	/**
	 * Affiche le menu contextuel pour l'objet indiqué.
	 * @param e Le composant sur lequel afficher le menu popup.
	 * @param x La coordonnée x du menu popup.
	 * @param y La coordonnée y du menu popup.
	**/
	public void showPopupMenu( Component e, int x, int y ) {

		if( e==table ) {
			try {
				if( schema.contains( getCurrentSelectedObjectId() ) ) {
					popupMenuTable.show( table, x, y );
				}
			} catch( NullPointerException ex ) {}

		} else if( e==arbre ) {
			try {
				TreePath objPath = arbre.getPathForLocation( x, y ) ;
				setSelectedPath( objPath );

				if( schema.contains( getCurrentSelectedObjectId() ) ) {
					popupMenuArbre.show( arbre, x, y );
				}
			} catch( NullPointerException ex ) {}
		}
	}

	/**
	 * Met à jour les données de la table en fonction du noeud sélectionné
	 * dans l'arbre.
	**/
	public void updateTable() {

		String[] columnName = { "Variable", "Valeur" };
		String[][] datas = null;
		SchemaObject objet = getCurrentSelectedObject();

		// Aucun noeud sélectionné.
		// On affiche un table vide.
		if( objet==null ) {
			datas = new String[0][2];

			// Modifie le titre du panel de la table.
			tablePanel.setTitle( "Aucun noeud sélectionné" );

		// Un noeud est sélectionné.
		} else {
			if( objet!=null ) {
				String[] keys = objet.getKeys();
				SchemaValue[] values = objet.getValues();
				datas = new String[keys.length][2];
		
				for( int i=0; i<datas.length; i++ ) {
					datas[i][0] = keys[i];
					datas[i][1] = values[i].toString();
				}
			}

			// Modifie le titre du panel de la table.
			tablePanel.setTitle( "Noeud sélectionné : " + objet.getId() );
		}

		NoEditableTableModel model = new NoEditableTableModel( datas, columnName );
		table.setModel( model );
	}

	/**
	 * Met à jour les données de l'arbre.
	**/
	public void updateTree() {

		// On créé le noeud pour les objets.

		String objetTypeName = schema.getSyntax().getObjectDefinitionType() ;
		SchemaObject[] objets = schema.getObjects( objetTypeName );
		DefaultMutableTreeNode objetNodes = new DefaultMutableTreeNode( objetTypeName );

		for( int i=0; i<objets.length; i++ ) {
			String id = objets[i].getId() ;
			String name = objets[i].getName();

			if( name!=null )
				id += " " + name ;

			objetNodes.add( new DefaultMutableTreeNode( id ) );
		}

		// On créé le noeud pour les attributs.

		String attributTypeName = schema.getSyntax().getAttributeDefinitionType() ;
		SchemaObject[] attributs = schema.getObjects( attributTypeName );
		DefaultMutableTreeNode attributNodes = new DefaultMutableTreeNode( attributTypeName );

		for( int i=0; i<attributs.length; i++ ) {
			String id = attributs[i].getId() ;
			String name = attributs[i].getName();

			if( name!=null )
				id += " " + name ;

			attributNodes.add( new DefaultMutableTreeNode( id ) );
		}

		// On met à jour l'arbre

		DefaultMutableTreeNode arbreRootNode = new DefaultMutableTreeNode(
				"Objets du schema" ) ;
		arbreRootNode.add( objetNodes );
		arbreRootNode.add( attributNodes );
		arbreModel = new DefaultTreeModel( arbreRootNode );
		arbre.setModel( arbreModel );

		// On modifie le titre du panel de l'abre.
		arbrePanel.setTitle( "Schéma de syntaxe " + schema.getSyntax().toString() );
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private String getCurrentSelectedObjectId() {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();
		if( node!=null ) {

			// Il se peut qu'on est inséré le nom derrière l'id,
			// on cherche si il existe un espace.
			String str = node.getUserObject().toString();
			int firstSpaceIndex = str.indexOf( ' ' );
			if( firstSpaceIndex!=-1 )
				str = str.substring( 0, firstSpaceIndex );

			return str ;
		}

		return null;
	}

	private void initPanel() {

		// - Panel de droite : la table des valeurs -

		table.removeEditor();
		table.setShowGrid( false );
		table.setRowSelectionAllowed( false );

		JPanel tableContainer = new JPanel( new BorderLayout() );
		tableContainer.add( table.getTableHeader(), BorderLayout.NORTH );
		tableContainer.add( table, BorderLayout.CENTER );
		tableContainer.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
		tableContainer.setBackground( table.getBackground() );

		JScrollPane tableScroller = new JScrollPane( tableContainer );
		tableScroller.setBorder( null );
		tablePanel = new SimpleInternalFrame( "" );
		tablePanel.setContent( tableScroller );
		tablePanel.setBorder( null );
		tablePanel.setOpaque( true );

		// - Panel de gauche : l'arbre -

		arbre.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		arbre.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		arbre.setRootVisible( false );

		JScrollPane arbreScroller = new JScrollPane( arbre );
		arbreScroller.setBorder( null );
		arbrePanel = new SimpleInternalFrame( "" );
		arbrePanel.setContent( arbreScroller );
		arbrePanel.setBorder( null );

		// - Menu popup pour l'arbre -

		popupMenuArbre.add( item_propriete );
		popupMenuArbre.add( item_supprimer );

		// - Menu popup pour la table -

		popupMenuTable.add( item_propriete2 );

		// - Organisation générale -

		JSplitPane frame = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, arbrePanel, tablePanel );
		frame.setContinuousLayout( true );
		frame.setResizeWeight( 0.25 );
		frame.setBorder( null );
		//frame.setBorder( BorderFactory.createLineBorder( (new JLabel()).getBackground() ) );
		frame.setOpaque( true );

		setLayout( new BorderLayout() );
		add( frame, BorderLayout.CENTER );
		setOpaque( true );
	}
}
