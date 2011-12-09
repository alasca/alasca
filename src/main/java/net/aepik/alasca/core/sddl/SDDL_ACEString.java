/*
 * ACEString.java		0.1		23/05/2006
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


package net.aepik.alasca.core.sddl;

import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.*;

/**
 * Une chaîne de caractères représentant une ACE (access control entrie).
 * Une telle chaîne est composée de 6 différentes parties, séparées l'une
 * de l'autre par le caractère ';'.
 * <br/><br/>
 * La première partie concerne le type, la seconde les flags, la troisième
 * les permission, la quatrième l'objet qui bénéficie de cette ACE, la
 * cinquième l'object parent des fils qui vont bénéficier de cette ACE, et le
 * sixième concerne les accès de confiance.
 * <br/><br/>
 * aceType;aceFlags;Permission;ObjectType;InheritedObject;Trustee
 * <br/><br/>
 * Aucun espace n'est toléré, ni retours à la ligne. Pour plus d'informations
 * concernant les champs qui doivent être renseigné, vous pouvez consulter
 * une page complète à l'adresse
 * http://windows.stanford.edu/Public/Security/ADSecurityOverview.htm.
 * <br/><br/>
 * Concernant la fabrication des GUID, il faut utiliser la classe Uuid. Nous
 * encodons ensuite le résultat en base64, format attendu par Active Directory.
**/

public class SDDL_ACEString {

////////////////////////////////
// Constantes
////////////////////////////////

	public static final String[][] ACEType = {
		{ "A", "Accès autorisé" },
		{ "D", "Accès refusé" },
		{ "OA", "Accès à l'objet autorisé : seulement à un sous-ensemble" },
		{ "OD", "Accès à l'objet refusé : seulement à un sous-ensemble" },
		{ "AU", "Audit du système" },
		{ "AL", "Alarme système" },
		{ "OU", "Audit de l'objet système" },
		{ "OL", "Alarme de l'objet du système" }
	};

	public static final String[][] ACEFlags = {
		{ "CI", "Le conteneur enfant hérite de cet objet" },
		{ "OI", "L'objet enfant hérite de cet objet" },
		{ "NP", "Seul les enfants directs hérite de cet objet" },
		{ "IO", "ACE non appliquée sur cet objet, peut l'être sur ses enfants" },
		{ "ID", "ACE héritée" },
		{ "SA", "Accès audit réussi" },
		{ "FA", "Accès audit échoué" }
	};

	public static final String[][] ACEPermissions = {
	// Generic access rights
		{ "GA", "Générique : tout" },
		{ "GR", "Générique : lecture seule" },
		{ "GW", "Générique : écriture seule" },
		{ "GX", "Générique : éxécution seule" },
	// Directory service access rights
		{ "RC", "Annuaire : droit de lecture" },
		{ "SD", "Annuaire : droit de suppression" },
		{ "WD", "Annuaire : droit de modification" },
		{ "WO", "Annuaire : droit de modification seulement par propriétaire" },
		{ "RP", "Annuaire : lecture de toute propriété" },
		{ "WP", "Annuaire : écriture de toute propriété" },
		{ "CC", "Annuaire : création de tout objet enfant" },
		{ "DC", "Annuaire : suppression de tout objet enfant" },
		{ "LC", "Annuaire : lister contenu" },
		{ "SW", "Annuaire : toute écriture validée" },
		{ "LO", "Annuaire : lister objet" },
		{ "DT", "Annuaire : suppression de sous-ensemble" },
		{ "CR", "Annuaire : tout droit étendu" },
	// File access rights
		{ "FA", "Fichier : tout accès" },
		{ "FR", "Fichier : lecture générique" },
		{ "FW", "Fichier : écriture générique" },
		{ "FX", "Fichier : éxécution générique" },
	// Registry key access rights
		{ "KA", "Clef : tout accès" },
		{ "KR", "Clef : lecture" },
		{ "KW", "Clef : écriture" },
		{ "KX", "Clef : éxécution" }
	};

	public static final String[][] ACETrustee = {
		{ "AO", "Compte opérateurs" },
		{ "RU", "Alias pour compatibilité pré-Windows 2000" },
		{ "AN", "Authentification anonyme" },
		{ "AU", "Authentification utilisateur" },
		{ "BA", "Administrateurs intégrés" },
		{ "BG", "Invités intégrés" },
		{ "BO", "Opérateurs protégés" },
		{ "BU", "Utilisateurs intégrés" },
		{ "CA", "Administrateurs de serveur de certificat" },
		{ "CG", "Groupe de créateur" },
		{ "CO", "Propriétaire de créateur" },
		{ "DA", "Administrateurs de domaine" },
		{ "DC", "Ordinateurs de domaine" },
		{ "DD", "Contrôleurs de domaine" },
		{ "DG", "Invités de domaine" },
		{ "DU", "Utilisateurs de domaine" },
		{ "EA", "Administrateurs d'entreprise" },
		{ "ED", "Contrôleurs de domaine d'entreprise" },
		{ "WD", "Tout le monde" },
		{ "PA", "Administrateurs de politique de groupe" },
		{ "IU", "Authentification intéractive utilisateur" },
		{ "LA", "Administrateur local" },
		{ "LG", "Invité local" },
		{ "LS", "Compte local de service" },
		{ "SY", "Système local" },
		{ "NU", "Authentification utilisateurs réseau" },
		{ "NO", "Configuration opérateurs réseau" },
		{ "NS", "Compte de service réseau" },
		{ "PO", "opérateurs d'impression" },
		{ "PS", "Individu personnel" },
		{ "PU", "Utilisateurs avec pouvoir" },
		{ "RS", "Groupe de serveurs RAS" },
		{ "RD", "Utilisateurs de Terminal serveur" },
		{ "RE", "Replicat" },
		{ "RC", "Code restreint" },
		{ "SA", "Administrateurs de schéma" },
		{ "SO", "Opérateurs de serveur" },
		{ "SU", "Service d'authentification utilisateur" }
	};

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le type **/
	private String type ;
	/** Les drapeaux **/
	private String flags ;
	/** Les permissions **/
	private String permissions ;
	/** Le GUID de l'objet auquel s'applique cette ACE **/
	private String object ;
	/** Le GUID de l'objet parent pour lequel tous les object enfants sont
		touchés par cette ACE **/
	private String inheritedObject ;
	/** Trustee **/
	private String trustee ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Créer une chaîne de caractères ACE vide.
	**/
	public SDDL_ACEString() {
		type = null;
		flags = null;
		permissions = null;
		object = null;
		inheritedObject = null;
		trustee = null;
	}

////////////////////////////////
// Methodes
////////////////////////////////

	/**
	 * Compare tous les attributs de cet objet aux attributs
	 * d'un autre objets SDDL_ACEString.
	 * @param ace Une objet de type SDDL_ACEString.
	 * @return boolean True si les deux objets sont égaux, false sinon.
	**/
	public boolean equals( SDDL_ACEString ace ) {

		return ( type.equals( ace.getType() )
				&& flags.equals( ace.getFlags() )
				&& permissions.equals( ace.getPermissions() )
				&& object.equals( ace.getObject() )
				&& inheritedObject.equals( ace.getInheritedObject() )
				&& trustee.equals( ace.getTrustee() ) );
	}

	/**
	 * Retourne l'attribut 'flags'.
	 * @return String Les flags.
	**/
	public String getFlags() { return flags; }

	/**
	 * Retourne l'attribut 'inheritedObject'.
	 * @return String Les objets enfants concernés par cette ACE.
	**/
	public String getInheritedObject() { return inheritedObject; }

	/**
	 * Retourne l'attribut 'object'.
	 * @return String L'objet concerné par cette ACE.
	**/
	public String getObject() { return object; }

	/**
	 * Retourne l'attribut 'permissions'.
	 * @return String Les permissions.
	**/
	public String getPermissions() { return permissions; }

	/**
	 * Retourne l'attribut 'trustee'.
	 * @return String Le trustee.
	**/
	public String getTrustee() { return trustee; }

	/**
	 * Retourne l'attribut 'type'.
	 * @return String Un type.
	**/
	public String getType() { return type; }

	/**
	 * Initialise cet objet à l'aide d'une chaîne de caractères
	 * correctement formatée.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si l'initialisation a réussie, false sinon.
	**/
	public boolean initFromString( String str ) {

		// On recherche d'abord des espaces, si il y en a au moins un,
		// la chaîne est mal formée, on retourne false.
		if( str.indexOf( (char) 32 )>=0 )
			return false;

		// On récupère toutes les sous-chaînes.
		// Le délimiteur est obligatoirement le caractère ';'.

		String[] strtab = new String[6];

		int i = 0 ;
		int j = 0 ;
		int k = 0 ;
		while( ( i = str.indexOf( ';', j ) )!=-1 ) {
			strtab[k] = str.substring( j, i ) ;
			j = i+1 ;
			k++;
		}

		if( j<=str.length() ) {
			strtab[k] = str.substring( j );
			k++;
		}

		// Si pas le nombre de paramêtres -> false.
		if( k!=6 )
			return false;

		/*boolean a = setType( strtab[0] );
		boolean b = setFlags( strtab[1] );
		boolean c = setPermissions( strtab[2] );
		boolean d = setObject( strtab[3] );
		boolean e = setInheritedObject( strtab[4] );
		boolean f = setTrustee( strtab[5] );

		System.out.println( "type = " + a );
		System.out.println( "flags = " + b );
		System.out.println( "permissions = " + c );
		System.out.println( "object = " + d );
		System.out.println( "inheritedObject = " + e );
		System.out.println( "trustee = " + f + "\n" );

		return a && b && c && d && e && f ;*/

		// Si toutes les mises à jour se font -> true.
		return ( setType( strtab[0] )
				&& setFlags( strtab[1] )
				&& setPermissions( strtab[2] )
				&& setObject( strtab[3] )
				&& setInheritedObject( strtab[4] )
				&& setTrustee( strtab[5] )	);
	}

	/**
	 * Teste si une chaîne de caractères est un 'type' paramêtre.
	 * @param str Une chaîne.
	 * @return boolean True si c'est vrai, false sinon.
	**/
	public static boolean isFlagsParameter( String str ) {

		boolean ok = false ;
		for( int i=0; i<ACEFlags.length && !ok; i++ ) {
			if( str.equals( ACEFlags[i][0] ) )
				ok = true ;
		}

		return ok ;
	}

	/**
	 * Teste si une chaîne de caractères est un 'type' paramêtre.
	 * @param str Une chaîne.
	 * @return boolean True si c'est vrai, false sinon.
	**/
	public static boolean isPermissionsParameter( String str ) {

		boolean ok = false ;
		for( int i=0; i<ACEPermissions.length && !ok; i++ ) {
			if( str.equals( ACEPermissions[i][0] ) )
				ok = true ;
		}

		return ok ;
	}

	/**
	 * Teste si une chaîne de caractères est un 'trustee' paramêtre.
	 * @param str Une chaîne.
	 * @return boolean True si c'est vrai, false sinon.
	**/
	public static boolean isTrusteeParameter( String str ) {

		boolean ok = false ;
		for( int i=0; i<ACETrustee.length && !ok; i++ ) {
			if( str.equals( ACETrustee[i][0] ) )
				ok = true ;
		}

		return ok ;
	}

	/**
	 * Teste si une chaîne de caractères est un 'type' paramêtre.
	 * @param str Une chaîne.
	 * @return boolean True si c'est vrai, false sinon.
	**/
	public static boolean isTypeParameter( String str ) {

		boolean ok = false ;
		for( int i=0; i<ACEType.length && !ok; i++ ) {
			if( str.equals( ACEType[i][0] ) )
				ok = true ;
		}

		return ok ;
	}

	/**
	 * Modifie l'attribut inheritedObject. C'est un GUID.
	 * @param str Une chaîne de caractères identiquant un GUID.
	 * @return boolean True si réussie, false sinon.
	**/
	public boolean setInheritedObject( String objGUID ) {

		if( objGUID!=null && objGUID.length()>0 ) {

			try {
				UUID.fromString( objGUID );
			} catch( Exception ex ) { return false ; }

			this.inheritedObject = objGUID;
			return true ;

		} else if( objGUID!=null && objGUID.length()==0 ) {
			return true;
		}

		return false;
	}

	/**
	 * Modifie l'attribut flags.
	 * La chaîne 'str' doit être formée uniquement de chaînes définies dans
	 * le tableau ACEFlags.
	 * @param str Une chaîne au bon format.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setFlags( String str ) {

		String[] tab = stringToTabOfValues( str, ACEFlags );

		if( tab!=null ) {
			this.flags = str ;
			return true ;

		} else if( str.length()==0 ) {
			this.flags = "";
			return true;
		}

		return false ;
	}

	/**
	 * Modifie l'attribut object. C'est un GUID.
	 * @param str Une chaîne de caractères identiquant un GUID.
	 * @return boolean True si réussie, false sinon.
	**/
	public boolean setObject( String objGUID ) {

		if( objGUID!=null && objGUID.length()>0 ) {

			try {
				UUID.fromString( objGUID );
			} catch( Exception ex ) { return false ; }

			this.object = objGUID;
			return true ;

		} else if( objGUID!=null && objGUID.length()==0 ) {
			return true;
		}

		return false;
	}

	/**
	 * Modifie l'attribut permission.
	 * La chaîne 'str' doit être formée uniquement de chaînes définies dans
	 * le tableau ACEPermissions.
	 * @param str Une chaîne au bon format.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setPermissions( String str ) {

		String[] tab = stringToTabOfValues( str, ACEPermissions );

		if( tab!=null ) {
			this.permissions = str ;
			return true ;

		} else if( str.length()==0 ) {
			this.permissions = "";
			return true;
		}

		return false ;
	}

	/**
	 * Modifie l'attribut trustee.
	 * La chaîne 'str' doit être formée uniquement de chaînes définies dans
	 * le tableau ACETrustee.
	 * @param str Une chaîne au bon format.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setTrustee( String str ) {

		String[] tab = stringToTabOfValues( str, ACETrustee );

		if( tab!=null ) {
			this.trustee = str ;
			return true ;

		} else if( str.length()==0 ) {
			this.trustee = "";
			return true;
		}

		return false ;
	}

	/**
	 * Modifie l'attribut type.
	 * La chaîne 'str' doit être formée uniquement de chaînes définies dans
	 * le tableau ACEType.
	 * @param str Une chaîne au bon format.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setType( String str ) {

		String[] tab = stringToTabOfValues( str, ACEType );

		if( tab!=null ) {
			this.type = str ;
			return true ;

		} else if( str.length()==0 ) {
			this.type = "";
			return true;
		}

		return false ;
	}

	/**
	 * La représentation sous forme de chaîne de caractères de cet ace.
	 * @return String Une chaîne de caractères.
	**/
	public String toString() {

		String str = "";
		
		if( type!=null )
			str += type ;
		str += ";" ;

		if( flags!=null )
			str += flags ;
		str += ";" ;

		if( permissions!=null )
			str += permissions ;
		str += ";" ;

		if( object!=null )
			str += object ;
		str += ";" ;

		if( inheritedObject!=null )
			str += inheritedObject ;
		str += ";" ;

		if( trustee!=null )
			str += trustee ;

		return str ;
	}

////////////////////////////////
// Methodes statiques
////////////////////////////////

	/**
	 * Retourne une chaîne en ensemble de chaînes qui figurent dans le
	 * tableau values. Si c'est chaîne n'est pas correctement formée,
	 * alors null est retourné.
	 * @param str Une chaîne.
	 * @param values L'ensemble des valeurs que peut contenir str.
	 * @return String[][] L'ensemble des valeurs contenus dans str,
	 *		au choix ACEType, ACEFlags, ACEPermissions, ACETrustee
	**/
	public static String[] stringToTabOfValues( String str, String[][] values ) {

		String[] result = null;
		boolean erreur = false ;

		if( str==null || values==null ) {
			erreur = true ;

		} else {
			char[] tab = str.toCharArray();
			Vector<String> vecteur = new Vector<String>();

			// L'algorithme est simple, on va parcourir toute la chaîne
			// en mémorisant chaque caractère dans un buffer. Si le buffer
			// est présent dans le tableau de valeurs, alors, si on lui ajoute
			// le caractère suivant dans la chaîne, on regarde si le buffer
			// est toujours valide. Si ca n'est pas le cas, on a trouvé
			// la bonne valeur, si le buffer est vide, il y a eu une erreur.

			int i = 0;
			while( i<tab.length && !erreur ) {

				boolean ok = true ;
				int previousIndex = -1;
				StringBuffer buffer = new StringBuffer();

				do {
					buffer.append( tab[i] );

					// On cherche dans le tableau de valeurs l'index du mot
					// courant (dans le buffer). Si on le trouve, alors
					// currentIndex vaut cet index.

					int currentIndex = -1;
					for( int j=0; j<values.length && currentIndex==-1; j++ ) {
						if( values[j][0].equals( buffer.toString() ) )
							currentIndex = j;
					}

					// Si le mot courant n'est pas trouvé dans le tableau de
					// valeurs, mais qu'on en a trouvé un au tour précédent,
					// alors on fixe la valeur pour le mot du tour précédent
					// (c'est à dire le mot courant sans sa dernière lettre).

					if( currentIndex==-1 && previousIndex!=-1 ) {
						buffer.deleteCharAt( buffer.length()-1 );
						vecteur.add( buffer.toString() );
						ok = false ;
						i--;

					// Sinon, si le mot a été trouvé, mais qu'on est arrivé
					// au bout de la chaîne (c'est à dire que le tour suivant
					// ne peut pas exister car il n'y a plus de lettres dispos),
					// alors on fixe la valeur pour ce mot.

					} else if( currentIndex!=-1 && i>=tab.length-1 ) {
						vecteur.add( buffer.toString() );
						ok = false ;
					}

					previousIndex = currentIndex ;
					i++;
				} while( ok && i<tab.length );
			}

			result = new String[vecteur.size()];
			Iterator<String> it = vecteur.iterator();
			i = 0;
			while( it.hasNext() ) {
				result[i] = it.next();
				i++;
			}

		}

		return result ;
	}

}
