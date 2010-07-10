/*
 * Schema.java		0.1		23/05/2006
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


package net.aepik.casl.core.ldap;

import net.aepik.casl.core.History;
import net.aepik.casl.core.ldap.value.QDescription;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.*;

/**
 * Cet objet est un schÃ©ma LDAP au sens large. Il doit contenir des dÃ©finitions
 * de type objectClass ou AttributeType, par l'intermÃ©diaire de l'objet Java
 * SchemaObject.
**/

public class Schema extends Observable {

////////////////////////////////
// Attributs
////////////////////////////////

	/** L'ensemble des objets du schema **/
	private Hashtable<String,SchemaObject> objets;
	/** PropriÃ©tÃ© du schÃ©ma **/
	private Properties proprietes ;
	/** La syntaxe du schema **/
	private SchemaSyntax syntax ;
	/** Indique si la syntaxe a changÃ©e **/
	private boolean isSyntaxChanged ;
	/** L'historique d'ordre des objets **/
	private History objectsOrder;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit un schema vide.
	**/
	public Schema( SchemaSyntax s ) {
		
		objets = new Hashtable<String,SchemaObject>() ;
		proprietes = new Properties();
		syntax = s ;
		isSyntaxChanged = false ;
		objectsOrder = new History();
	}

	/**
	 * Construit un schema en le remplissant
	 * d'objets SchemaObject.
	 * @param objets Des objets du schema.
	**/
	public Schema( SchemaSyntax s, Vector<SchemaObject> objs ) {

		objets = new Hashtable<String,SchemaObject>() ;
		proprietes = new Properties();
		syntax = s ;
		isSyntaxChanged = false ;

		addObjects( objs );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ajoute un objet au schema.
	 * @param o Un objet du schema.
	 * @return True si l'objet n'existe pas dÃ©jÃ , false sinon.
	**/
	public boolean addObject( SchemaObject o ) {

		try {
			if( !contains( o.getId() ) ) {
				objets.put( o.getId(), o );
				objectsOrder.insertElementInLastPosition( o );
				notifyUpdates();
				return true;
			}
		} catch( Exception e ) {}

		return false;
	}

	/**
	 * Ajoute un ensemble d'objets au schema.
	 * @param v Un ensemble d'objets du schema.
	 * @return True si l'opÃ©ration a rÃ©ussi, false sinon.
	 *		Si l'opÃ©ration n'a pas rÃ©ussi, aucun objet n'aura Ã©tÃ© ajoutÃ©.
	**/
	public boolean addObjects( SchemaObject[] v ) {

		boolean ok = true ;

		// On tente d'ajouter tous les objets dans le schema courant.

		for( int i=0; i<v.length && ok; i++ ) {
			ok = addObject( v[i] );
		}

		if( ok ) {
			notifyUpdates();
			return true;
		}

		// Si l'opÃ©ration n'a pas rÃ©ussi, c'est Ã  dire qu'un objet, figurant
		// dans l'ensemble des objets que l'on tente d'insÃ©rer, possÃ¨de un id
		// qui existe dÃ©jÃ  dans le schema courant, alors on effectue
		// l'opÃ©ration inverse: on supprime tout objet insÃ©rer durant
		// l'opÃ©ration prÃ©cÃ©dente.

		for( SchemaObject o : v )
			delObject( o.getId() );

		return false;
	}

	/**
	 * Ajoute un ensemble d'objets au schema.
	 * @param v Un ensemble d'objets du schema.
	 * @return True si l'opÃ©ration a rÃ©ussi, false sinon.
	 *		Si l'opÃ©ration n'a pas rÃ©ussi, aucun objet n'aura Ã©tÃ© ajoutÃ©.
	**/
	public boolean addObjects( Vector<SchemaObject> v ) {

		SchemaObject[] o = new SchemaObject[v.size()];
		Iterator<SchemaObject> it = v.iterator();
		int position = 0;
		while( it.hasNext() ) {
			o[position] = it.next();
			position++;
		}

		return addObjects( o );
	}

	/**
	 * Indique si l'objet identifiÃ© par id existe dans le schema.
	 * @param id Une chaÃ®ne de caractÃ¨res reprÃ©sentant l'id d'un objet du schÃ©ma.
	 * @return True si l'objet d'identifiant id existe dans le schema, false sinon.
	**/
	public boolean contains( String id ) {

		try {
			if( objets.containsKey( id ) )
				return true;
		} catch( Exception e ) { }

		return false;
	}


	/**
	 * Créer un schéma et charge les objets lu à partir d'un fichier dans ce
	 * nouveau schéma.
	 * @param SchemaSyntax syntax
	 * @param String filename
	 * @param boolean load
	 * @return SchemaFile Un objet SchemaFile contenant les objets.
	**/
	public static SchemaFile createAndLoad (SchemaSyntax syntax, String filename, boolean load)
	{
		SchemaFileReader sReader = syntax.createSchemaReader();
		SchemaFile sFile = new SchemaFile(filename,sReader, null);
		if (load)
		{
			sFile.read();
		}
		return sFile;
	}

	/**
	 * Retourne le nombre d'objets que contient le schÃ©ma.
	 * @return int Le nombre d'objets du schÃ©ma.
	**/
	public int countObjects() { System.out.println( objets.size() ); return objets.size(); }

	/**
	 * Supprime un objet du schema.
	 * @param id Une chaÃ®ne de caractÃ¨res reprÃ©sentant l'id d'un objet du schema.
	 * @return True si l'objet d'identifiant id existe dans le schema, false sinon.
	**/
	public boolean delObject( String id ) {
		
		if( contains( id ) ) {
			SchemaObject o = objets.remove( id );
			objectsOrder.removeElement( o );
			notifyUpdates();
			return true;
		}

		return false;
	}

	/**
	 * Retourne l'historique d'ajout du schÃ©ma.
	 * @return History Un historique en fonction des donnÃ©es actuelles du schÃ©ma.
	**/
	public History getHistory() { return objectsOrder; }

	/**
	 * AccÃ¨de Ã  un objet du schema.
	 * @param id Une chaÃ®ne de caractÃ¨res reprÃ©sentant l'id d'un objet du schema.
	 * @return SchemaObject Un objet du schema.
	**/
	public SchemaObject getObject( String id ) {
		
		if( contains( id ) ) {
			return objets.get( id );
		}

		return null;
	}

	/**
	 * Retourne un objet du schÃ©ma dont le nom est name.
	 * @param name Le nom d'un objet, et non son id.
	 * @return SchemaObject Un objet du schÃ©ma.
	**/
	public SchemaObject getObjectByName( String name ) {

		SchemaObject result = null ;
		Enumeration<SchemaObject> it = objets.elements();

		while( result==null && it.hasMoreElements() ) {
			SchemaObject o = it.nextElement();

			if( o.getName().equals( name ) )
				result = o;
		}

		return result ;
	}

	/**
	 * Retourne l'ensemble des objets du schema.
	 * @return SchemaObject[] L'ensemble des objets du schema.
	**/
	public SchemaObject[] getObjects() {

		SchemaObject[] result = new SchemaObject[objets.size()];
		int position = 0;

		for( Enumeration<SchemaObject> e=objets.elements(); e.hasMoreElements();) {
			result[position] = e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Retourne l'ensemble des objets du schema dans l'ordre dans lequel
	 * ils ont Ã©tÃ© ajoutÃ© au schÃ©ma.
	 * @return SchemaObject[] L'ensemble des objets du schema.
	**/
	public SchemaObject[] getObjectsInOrder() {

		SchemaObject[] result = new SchemaObject[objets.size()];
		int position = 0;

		for( Enumeration<Object> e=objectsOrder.elements(); e.hasMoreElements();) {
			result[position] = (SchemaObject) e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Retourne l'ensemble des objets du schema d'un certain type.
	 * @param type Le type des objets Ã  selectionner.
	 * @return SchemaObject[] L'ensemble des objets du schema.
	**/
	public SchemaObject[] getObjects( String type ) {

		// On rÃ©cupÃ¨re tous les objets du type demandÃ©s.

		Vector<SchemaObject> v = new Vector<SchemaObject>();
		for( Enumeration<SchemaObject> e=objets.elements(); e.hasMoreElements();) {
			SchemaObject o = e.nextElement();

			if( type.equals( o.getType() ) )
				v.add( o );
		}

		// Une fois qu'on a tous les objets de ce type, on est en mesure de
		// savoir combien il y en a, et du coup de crÃ©er un tableau de la
		// bonne dimension.

		SchemaObject[] result = new SchemaObject[v.size()];
		int position = 0;

		for( Enumeration<SchemaObject> e=v.elements(); e.hasMoreElements();) {
			result[position] = e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Retourne l'ensemble des objets du schema d'un certain type, dans l'ordre
	 * dans lequel ils ont Ã©tÃ© ajoutÃ© au schÃ©ma.
	 * @param type Le type des objets Ã  selectionner.
	 * @return SchemaObject[] L'ensemble des objets du schema.
	**/
	public SchemaObject[] getObjectsInOrder( String type ) {

		// On rÃ©cupÃ¨re tous les objets du type demandÃ©s.

		Vector<SchemaObject> v = new Vector<SchemaObject>();
		for( Enumeration<Object> e=objectsOrder.elements(); e.hasMoreElements();) {
			SchemaObject o = (SchemaObject) e.nextElement();

			if( type.equals( o.getType() ) )
				v.add( o );
		}

		// Une fois qu'on a tous les objets de ce type, on est en mesure de
		// savoir combien il y en a, et du coup de crÃ©er un tableau de la
		// bonne dimension.

		SchemaObject[] result = new SchemaObject[v.size()];
		int position = 0;

		for( Enumeration<SchemaObject> e=v.elements(); e.hasMoreElements();) {
			result[position] = e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Retourne les propriÃ©tÃ©s du schÃ©ma.
	 * @return Properties L'ensemble des propriÃ©tÃ©s du schÃ©ma.
	**/
	public Properties getProperties() { return proprietes ; }

	/**
	 * Retourne la syntaxe utilisÃ© par le schema.
	 * @return SchemaSyntax Une syntaxe spÃ©cifique.
	**/
	public SchemaSyntax getSyntax() { return syntax; }

	/**
	 * Retourne l'ensemble des syntaxes connues, qui sont
	 * contenues dans le package 'ldap.syntax'.
	 * @return String[] L'ensemble des noms de classes de syntaxes.
	**/
	public static String[] getSyntaxes() {

		String[] result = null ;

		try {

			String packageName = getSyntaxPackageName();
			URL url = Schema.class.getResource(
					"/" + packageName.replace( '.', '/' ) );

			if( url==null )
				return null;

			if( url.getProtocol().equals( "jar" ) ) {

				Vector<String> vectTmp = new Vector<String>();

				int index = url.getPath().indexOf( '!' );
				String path = URLDecoder.decode( url.getPath().substring( index+1 ), "UTF-8" );
				JarFile jarFile = new JarFile( URLDecoder.decode( url.getPath().substring( 5, index ), "UTF-8" ) );
				Enumeration<JarEntry> jarFiles = jarFile.entries();

				if( path.charAt(0)=='/' )
					path = path.substring( 1 );

				while( jarFiles.hasMoreElements() ) {
					JarEntry tmp = jarFiles.nextElement();

					// Pour chaque fichier dans le jar, on regarde si c'est un
					// fichier de classe Java.

					if( !tmp.isDirectory()
							&& tmp.getName().substring( tmp.getName().length()-6 ).equals( ".class" )
							&& tmp.getName().startsWith( path ) ) {
						int i = tmp.getName().lastIndexOf( '/' );
						vectTmp.add( tmp.getName().substring( i+1, tmp.getName().length()-6 ) );
					}
				}

				jarFile.close();

				result = new String[vectTmp.size()];
				for( int i=0; i<vectTmp.size(); i++ )
					result[i] = vectTmp.elementAt( i );

			} else if( url.getProtocol().equals( "file" ) ) {

				// On crÃ©Ã© le fichier associÃ© pour parcourir son contenu.
				// En l'occurence, c'est un dossier.
				File[] files = (new File( url.toURI() )).listFiles();

				// On liste tous les fichiers qui sont dedans.
				// On les stocke dans un vecteur ...
				Vector<File> vectTmp = new Vector<File>();
				for( File f: files ) {
					if( !f.isDirectory() )
						vectTmp.add( f );
				}

			 	// ... pour ensuite les mettres dans le tableau de resultat.
				result = new String[vectTmp.size()];
				for( int i=0; i<vectTmp.size(); i++ ) {
					String name = vectTmp.elementAt( i ).getName();
					int a = name.indexOf( '.' );
					name = name.substring( 0, a );
					result[i] = name;
				}
			}

		// Erreur chargement nom de classe.
		} catch( Exception e ) { }

		if( result!=null )
			Arrays.sort( result );

		return result ;

	}

	/**
	 * Retourne le nom du paquetage contenant toutes les syntaxes.
	 * @return String Un nom de paquetage.
	**/
	public static String getSyntaxPackageName() {

		Schema s = new Schema( null );
		return s.getClass().getPackage().getName() + ".syntax" ;
	}

	/**
	 * Teste si la syntaxe Ã  changer depuis la derniÃ¨re fois qu'on a appelÃ©
	 * cette mÃ©thode.
	**/
	public boolean isSyntaxChangedSinceLastTime() {

		if( isSyntaxChanged ) {
			isSyntaxChanged = false;
			return true ;
		}
		return false ;
	}

	/**
	 * Permet de notifier que les donnÃ©es ont changÃ©es.
	 * Tous les objets observant le schema verront la notification.
	**/
	public void notifyUpdates() {
		setChanged() ;
		notifyObservers() ;
	}

	/**
	 * Modifie la syntaxe du schÃ©ma.
	 * @param newSyntax Une syntaxe spÃ©cifique.
	**/
	public void setSyntax( SchemaSyntax newSyntax ) {
		isSyntaxChanged = true ;
		syntax = newSyntax;
	}

	/**
	 * Modifies les propriÃ©tÃ©s du schÃ©ma.
	 * @param newProp Le nouvel objet Properties.
	**/
	public void setProperties( Properties newProp ) {
		proprietes = newProp ;
	}

}
