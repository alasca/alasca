/*
 * ManagerFrame.java		0.1		20/06/2006
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

import net.aepik.casl.core.Manager;
import net.aepik.casl.core.Plugin;
import net.aepik.casl.ui.ldap.SchemaManagerListener;
import net.aepik.casl.ui.ldap.SchemaManagerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.String;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.MenuElement;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SingleSelectionModel;

public class ManagerFrame extends JFrame {

////////////////////////////////
// Constantes
////////////////////////////////

	/** L'item de menu Liste des plugins **/
	public JMenuItem item_plugins = new JMenuItem( "Liste des extentions" );
	/** L'item de menu quitter **/
	public JMenuItem item_quit = new JMenuItem( "Quitter" );
	/** L'item de menu A Propos **/
	public JMenuItem item_authors = new JMenuItem( "A Propos..." );

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le manager général **/
	private Manager manager ;
	/** le listener général **/
	private ManagerListener listener ;
	/** Le gestionnaire graphique du manager de schémas **/
	private SchemaManagerPanel schemaManagerPanel ;

	/** La barre de menu **/
	private JMenuBar menu ;
	/** Le titre original de la fenêtre **/
	private String titre ;
	/** Le barre de status en bas de la fenêtre **/
	private JLabel statusDescription ;
	/** Le texte par défaut dans la barre de status **/
	private String statusDefaultText ;

	private Vector<JMenuItem> pluginButtons ;

	/** L'ensemble des boutons concernant les plugins **/
	//private JMenuItem[] pluginButtons ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public ManagerFrame( Manager m, String defaultTitre, String defaultStatus ) {

		super( defaultTitre );
		setSize( 750, 550 );
		setLocationRelativeTo( null );

		menu = new JMenuBar();
		titre = defaultTitre ;
		statusDescription = new JLabel( defaultStatus );
		statusDefaultText = defaultStatus ;

		manager = m;
		listener = null ;
		schemaManagerPanel = new SchemaManagerPanel( manager.getSchemaManager() );

		pluginButtons = new Vector<JMenuItem>();

		initFrame();

		SchemaManagerListener l = new SchemaManagerListener(
				manager.getSchemaManager(),
				schemaManagerPanel,
				this );
		schemaManagerPanel.addSchemaManagerListener( l );

		updateButtons();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ajoute un listener pour cet objet.
	 * @param l Un objet ManagerListener.
	**/
	public void addManagerListener( ManagerListener l ) {

		listener = l;

		addWindowListener( l );
		addMouseManagerListener( menu, l );

		item_plugins.addActionListener( l );
		item_quit.addActionListener( l );
		item_authors.addActionListener( l );
	}

	/**
	 * Retourne le menu dont le chemin est path.
	 * Une path est de la forme Unix, c'est à dire qu'il commence par un le
	 * caractère '/', chaque sous-menu est séparé par ce même caractère. Le
	 * path, pour être valide, ne doit pas se terminer par ce caractère.
	 * @param str Le texte du menu.
	 * @return JMenu Un menu ou null si il n'existe pas ou le path incorrect.
	**/
	public JMenu getExistingJMenu( String path ) {

		String[] cats = path.split( "/" );
		JMenu result = null ;

		if( cats.length>0 ) {

			// Tout d'abord, on récupère le premier menu dans la barre de menu.
			// Cette étape est spécifique.

			int nbElements = menu.getMenuCount();

			for( int i=0; result==null && i<nbElements; i++ ) {
				if( menu.getMenu( i ).getText().equals( cats[0] ) ) {
					result = menu.getMenu( i );
				}
			}

			// On a récupéré le premier menu.
			// Si la recherche s'effectue aussi sur des sous-menus, on continue.
			// Sinon, on retourne le menu trouvé.

			if( cats.length==1 )
				return result ;

			// On continue, donc on refabrique le chemin correct.
			// Si le chemin est vide, on s'arrête.

			String subPath = "";
			for( int i=1; i<cats.length; i++ ) {
				subPath += cats[i];
				if( i!=cats.length-1 )
					subPath += "/" ;
			}

			if( subPath.length()==0 )
				return result ;

			// On descends dans l'arborescence jusqu'au bout, si c'est possible.
			// Dans tous les cas, on retourne le menu le plus profond.

			JMenu query = searchMenu( result, subPath );
			if( query!=null )
				result = query;
		}

		return result ;
	}

	/**
	 * Retourne le barre de menu.
	 * @return JMenuBar La barre de menu de la fenêtre.
	**/
	public JMenuBar getJMenuBar() { return menu; }

	/**
	 * Retourne le manager de l'application.
	 * @return Manager Le manager de l'appli.
	**/
	public Manager getManager() { return manager; }

	/**
	 * Supprime un listener pour cet objet.
	 * @param l Un objet ManagerListener.
	**/
	public void removeManagerListener( ManagerListener l ) {

		listener = null ;

		removeWindowListener( l );
		removeMouseManagerListener( menu, l );

		item_plugins.removeActionListener( l );
		item_quit.removeActionListener( l );
		item_authors.removeActionListener( l );
	}

	/**
	 * Modifie la description dans la barre de status.
	 * @param desc Une description sur une ligne.
	**/
	public void setStatusDescription( String desc ) {

		if( desc!=null ) {
			statusDescription.setText( desc );
		} else {
			statusDescription.setText( statusDefaultText );
		}
		statusDescription.repaint();
	}

	/**
	 * Modifie le titre de la fenêtre.
	 * @param titre Le nouveau titre de la fenêtre.
	**/
	public void setTitle( String t ) {

		if( t!=null )
			super.setTitle( titre + " - " + t );
		else
			super.setTitle( titre );
	}

	/**
	 * Mets à jours le status des boutons.
	**/
	public void updateButtons() {

		Plugin[] plugins = manager.getPluginManager().getPlugins();

		for( int i=0; plugins!=null && i<plugins.length; i++ ) {
			if( plugins[i].canRun() )
				pluginButtons.elementAt(i).setEnabled( true );
			else
				pluginButtons.elementAt(i).setEnabled( false );
		}
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	/**
	 * Ajoute un listener de souris récursivement à tous les sous-éléments de
	 * menu de l'élément de menu courant.
	 * @param m L'élément de menu.
	 * @param l Le listener d'événements de la souris.
	**/
	private void addMouseManagerListener( MenuElement m, ManagerListener l ) {

		MenuElement[] elements = m.getSubElements();
		for( int i=0; elements!=null && i<elements.length; i++ ) {
			addMouseManagerListener( elements[i], l );
		}

		m.getComponent().addMouseListener( l );
	}

	/**
	 * Initialise la frame.
	**/
	private void initFrame() {

		// - La barre de menu -

		JMenu menu_fichiers = new JMenu( "Fichier" );
		menu_fichiers.addSeparator();
		menu_fichiers.add( item_quit );
		JMenu menu_edition = new JMenu( "Edition" );
		JMenu menu_plugins = new JMenu( "Outils" );
		JMenu menu_help = new JMenu( "Aide" );
		menu_help.add( item_authors );

		menu.add( menu_fichiers );
		menu.add( menu_edition );
		menu.add( menu_plugins );
		menu.add( menu_help );

		// - Plugins & menu -

		Plugin[] plugins = manager.getPluginManager().getPlugins();

		for( int i=0; plugins!=null && i<plugins.length; i++ ) {

			String path = plugins[i].getCategory();
			if( path.length()!=0 )
				path += "/" ;
			path += plugins[i].getName();

			insertPluginIn( menu_plugins, plugins[i], path );
			plugins[i].setRelativeTo( this );
		}

		if( menu_plugins.getItemCount()==0 )
			menu.remove( menu_plugins );

		menu_plugins.add( new JSeparator(), 0 );
		menu_plugins.add( item_plugins, 0 );

		// - Description -

		statusDescription.setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 0 ) );

		// - Organisation Générale -

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( menu, BorderLayout.NORTH );
		mainPanel.add( schemaManagerPanel, BorderLayout.CENTER );
		mainPanel.add( statusDescription, BorderLayout.SOUTH );
		getContentPane().add( mainPanel );

		// - Raccourcis clavier -

		menu_fichiers.setMnemonic( KeyEvent.VK_F );
		menu_edition.setMnemonic( KeyEvent.VK_E );
		menu_plugins.setMnemonic( KeyEvent.VK_O );
		menu_help.setMnemonic( KeyEvent.VK_A );

		item_authors.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F12, 0 ) );
		item_plugins.setAccelerator( KeyStroke.getKeyStroke(
				KeyEvent.VK_P, KeyEvent.CTRL_MASK ) );
	}

	private void insertPluginIn( JMenu menu, Plugin plugin, String path ) {

		String[] pathTab = path.split( "/" );

		// On est à la fin.
		// On cherche la position dans le menu (ordre alphabétique) et on
		// ajoute le plugin (listener de plugin). L'ajout du plugin se fait
		// après toutes les catégories.
		if( pathTab.length==1 ) {

			Component[] sousMenus = menu.getMenuComponents();
			int position = -1 ;

			for( int i=sousMenus.length-1;
					i>=0 && position==-1; i-- ) {

				Component c = sousMenus[i] ;
				if( c instanceof JMenu )
					position = i+1 ;
				else if( c instanceof JMenuItem
						&& pathTab[0].compareTo( ((JMenuItem) c).getText() )>0 )
					position = i+1 ;
			}

			// Si pas de catégorie trouvée, et que le nom du plugin est
			// inférieur alphabétiquement à tous les autres noms figurant dans
			// le menu, alors on l'ajoute à la première position.

			if( position==-1 )
				position++;

			// On cherche l'éventuelle barre de séparation.

			if( position>0 ) {
				if( sousMenus[position-1] instanceof JMenu ) {
					menu.insertSeparator( position );
					position++;
				}
			}

			// Finalement, au ajoute au menu.
			// On créer le listener de plugin.

			JMenuItem item = new JMenuItem( plugin.getName() );
			item.addActionListener( new PluginListener( plugin ) );
			menu.insert( item, position );
			pluginButtons.add( item );

		// On vaance récursivement dans le sous-menu en fonction du path.
		// On créer les sous-menu, si nécessaire.
		} else if( pathTab.length>1 ) {

			Component[] sousMenus = menu.getMenuComponents();

			// On recherche un sous menu ayant un nom correspondant
			// à pathTab[0]. Si il n'y en a pas, on le créer.

			JMenu menu2 = null ;
			for( int i=0; i<sousMenus.length && menu2==null; i++ ) {
				Component c = sousMenus[i] ;
				if( c instanceof JMenu && ((JMenu) c).getText().equals( pathTab[0] ) )
					menu2 = (JMenu) c ;
			}

			if( menu2==null )
				menu2 = new JMenu( pathTab[0] );

			// On refabrique le path.

			String newPath = "" ;
			for( int i=1; i<pathTab.length; i++ ) {
				newPath += pathTab[i];
				if( i!=pathTab.length-1 )
					newPath += "/" ;
			}

			// On cherche la position d'insertion.

			int position = -1;
			for( int i=0; i<sousMenus.length && position==-1; i++ ) {
				Component c = sousMenus[i] ;
				if( c instanceof JMenuItem )
					position = i ;
				else if( c instanceof JMenu
						&& pathTab[0].compareTo( ((JMenu) c).getText() )<0 )
					position = i ;
			}

			if( position==-1 )
				position = sousMenus.length ;

			// On cherche l'éventuelle barre de séparation.

			if( position<sousMenus.length ) {
				if( !(sousMenus[position] instanceof JMenu)
						&& !(sousMenus[position] instanceof JSeparator) ) {
					menu.insertSeparator( position );
				}
			}

			menu.insert( menu2, position );
			insertPluginIn( menu2, plugin, newPath );
		}
	}

	/**
	 * Supprime un listener de souris récursivement à tous les sous-éléments de
	 * menu de l'élément de menu courant.
	 * @param m L'élément de menu.
	 * @param l Le listener d'événements de la souris.
	**/
	private void removeMouseManagerListener( MenuElement m, ManagerListener l ) {

		MenuElement[] elements = m.getSubElements();
		for( int i=0; elements!=null && i<elements.length; i++ ) {
			removeMouseManagerListener( elements[i], l );
		}

		m.getComponent().removeMouseListener( l );
	}

	/**
	 * Cherche dans un menu le sous-menu le plus profond dans l'arborescence
	 * indiqué par le paramêtre subPath.
	 * @param currentMenu Le menu courant à prendre en compte.
	 * @param subPath Le chemin par rapport au menu courant.
	 * @return JMenu Retourne le menu trouvé, ou null si il n'est pas trouvé.
	**/
	private JMenu searchMenu( JMenu currentMenu, String subPath ) {

		String[] cats = subPath.split( "/" );
		JMenu result = null ;
		Component[] tmp = currentMenu.getMenuComponents();

		// On refabrique le chemin.

		String newPath = "";
		for( int i=1; i<cats.length; i++ ) {
			newPath += cats[i];
			if( i!=cats.length-1 )
				newPath += "/" ;
		}

		// Test d'arrêt.

		if( subPath.length()==0 )
			return currentMenu;

		for( int i=0; result==null && i<tmp.length; i++ ) {
			if( tmp[i] instanceof JMenu && cats[0].equals( ((JMenu) tmp[i]).getText() ) ) {
				result = searchMenu( (JMenu) tmp[i], newPath );
			}
		}

		if( result!=null )
			return result ;
		else
			return currentMenu ;
	}
}
