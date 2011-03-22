/*
 * ACLString.java		0.1		23/05/2006
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


package net.aepik.casl.core.sddl;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.*;

/**
 * Une chaîne de caractères représentant une ACL (access control entrie).
 * Une telle chaîne est composée de 4 différentes parties.
 * <br/><br/>
 * Ces différentes parties peuvent être dans le désordre. Toujours est-il
 * qu'elles sont de format défini:
 * <br/><br/>
 * O:owner_sidG:group_sidD:dacl_flags(ACEString1)(ACEString2)...S:sacl_flags(ACEString1)(ACEString2)...
 * <br/><br/>
 * Il n'y pas de limite pour les ace. Chaque partie peut ne pas être présente.
 * Aucun espace n'est toléré, ni retours à la ligne. Pour plus d'informations
 * concernant les champs qui doivent être renseigné, vous pouvez consulter
 * une page complète à l'adresse
 * l'adresse http://windows.stanford.edu/Public/Security/ADSecurityOverview.htm.
**/

public class SDDL_ACLString {

////////////////////////////////
// Constantes
////////////////////////////////

	public static final String[] keywords = {
		"O:", "G:", "D:", "S:"
	};

	public static final String[] aclFlags = {
		"P", "AR", "AI"
	};

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le SID du possesseur **/
	private String ownersid ;
	/** Le SID du groupe possesseur **/
	private String groupsid ;
	/** Les flags DACL **/
	private String daclflags ;
	/** Les flags SACL **/
	private String saclflags ;
	/** Les ace DACL **/
	private Vector<SDDL_ACEString> daclace ;
	/** Les ace SACL **/
	private Vector<SDDL_ACEString> saclace ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit une nouvelle acl vide.
	**/
	public SDDL_ACLString() {
		ownersid = null ;
		groupsid = null ;
		daclflags = null ;
		saclflags = null ;
		daclace = new Vector<SDDL_ACEString>();
		saclace = new Vector<SDDL_ACEString>();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ajoute une ace propre aux DACL (Discretionary Access Control List).
	 * @param ace Une ace.
	 * @return boolean True si l'ajout a réussi, false sinon.
	**/
	public boolean addACEForDACL( SDDL_ACEString ace ) {

		boolean ok = false ;
		if( !( ok = isACEinDACL( ace ) ) )
			daclace.add( ace );

		return !ok ;
	}

	/**
	 * Ajoute une ace propre aux SACL (System Access Control List).
	 * @param ace Une ace.
	 * @return boolean True si l'ajout a réussi, false sinon.
	**/
	public boolean addACEForSACL( SDDL_ACEString ace ) {

		boolean ok = true ;
		if( !( ok = isACEinSACL( ace ) ) )
			saclace.add( ace );

		return !ok ;
	}

	/**
	 * Supprime une ace propre aux DACL.
	 * @param ace Une ace.
	 * @return boolean True si la suppression a réussi, false sinon.
	**/
	public boolean delACEForDACL( SDDL_ACEString ace ) {

		boolean ok = false ;
		if( ok = isACEinDACL( ace ) )
			daclace.remove( ace );

		return ok ;
	}

	/**
	 * Supprime une ace propre aux SACL.
	 * @param ace Une ace.
	 * @return boolean True si la suppression a réussi, false sinon.
	**/
	public boolean delACEForSACL( SDDL_ACEString ace ) {

		boolean ok = false ;
		if( ok = isACEinSACL( ace ) )
			saclace.remove( ace );

		return ok ;
	}

	/**
	 * Retourne l'ensemble des ACEs DACL.
	 * @return Vector<SDDL_ACEString> L'ensemble des ACEs DACL.
	**/
	public Vector<SDDL_ACEString> getDACLACEs() { return daclace; }

	/**
	 * Retourne les paramêtres pour DACL.
	 * @return String Une chaîne de caractères.
	**/
	public String getDACLFlags() { return daclflags; }

	/**
	 * Retourne le sid groupe.
	 * @return String Identifiant sous forme de chaîne de caractères.
	**/
	public String getGroupSid() { return groupsid; }

	/**
	 * Retourne le sid propriétaire.
	 * @return String Identifiant sous forme de chaîne de caractères.
	**/
	public String getOwnerSid() { return ownersid; }

	/**
	 * Retourne l'ensemble des ACEs SACL.
	 * @return Vector<SDDL_ACEString> L'ensemble des ACEs SACL.
	**/
	public Vector<SDDL_ACEString> getSACLACEs() { return saclace; }

	/**
	 * Retourne les paramêtres pour SACL.
	 * @return String Une chaîne de caractères.
	**/
	public String getSACLFlags() { return saclflags; }

	/**
	 * Initialise cette ACL à partir d'une chaîne de caractères de format
	 * définies plus haut dans la documentation.
	 * @param str Une chaîne correctement formée.
	 * @return boolean True si l'initialisation s'est correctement effectuée,
	 *		false sinon.
	**/
	public boolean initFromString( String str ) {

		if( str==null ) return false ;

		// On parcours la chaîne grâce à une expression régulière.
		// Si la chaîne matche l'expression régulière, tout est OK.

		if( Pattern.matches( "^(O:.*)?(G:.*)?(D:.*\\(.*\\))?(S:.*\\(.*\\))?$", str ) ) {

			// On cherche d'abord le S:
			String[] tab = str.split( "S:" );
			if( tab.length==2 ) {

				String strTmp = tab[1] ;
				boolean ok = true ;
				int firstBracketPosition = strTmp.indexOf( 40 );

				// On regarde si il y a des paramêtres.
				if( firstBracketPosition>0 ) {
					strTmp = tab[1].substring( firstBracketPosition );
					if( !setSACLFlags( tab[1].substring( 0, firstBracketPosition ) ) )
						return false ;
				}

				// On récupère les données entre parenthèses.
				StringTokenizer strtok = new StringTokenizer( strTmp, "()" );
				while( strtok.hasMoreTokens() && ok ) {
					SDDL_ACEString ace = new SDDL_ACEString();
					String initStr = strtok.nextToken();

					boolean a = ace.initFromString( initStr );
					boolean b = addACEForSACL( ace ) ;

					if( !( a && b ) ) {
					//if( !( ace.initFromString( initStr ) && addACEForSACL( ace ) ) ) {
						System.out.println( "!( " + a + " && " + b + " ) == true" );
						ok = false ;
					}
				}

				if( !ok ) return false ;
			}

			// Ensuite le D:
			tab = tab[0].split( "D:" );

			if( tab.length==2 ) {

				String strTmp = tab[1] ;
				boolean ok = true ;
				int firstBracketPosition = strTmp.indexOf( 40 );

				// On regarde si il y a des paramêtres.
				if( firstBracketPosition>0 ) {
					strTmp = tab[1].substring( firstBracketPosition );
					if( !setDACLFlags( tab[1].substring( 0, firstBracketPosition ) ) )
						ok = false ;
				}

				// On récupère les données entre parenthèses.
				StringTokenizer strtok = new StringTokenizer( strTmp, "()" );
				while( strtok.hasMoreTokens() && ok ) {
					SDDL_ACEString ace = new SDDL_ACEString();
					String initStr = strtok.nextToken();

					if( !( ace.initFromString( initStr ) && addACEForDACL( ace ) ) )
						ok = false ;
				}

				if( !ok ) return false ;
			}

			// Ensuite le G:
			tab = tab[0].split( "G:" );
			if( tab.length==2 )
				groupsid = tab[1];

			// Ensuite le O:
			tab = tab[0].split( "O:" );
			if( tab.length==2 )
				ownersid = tab[1];

			/*
			 * Le code ci-dessous est une autre variante de la méthode
			 * d'analyse employé ci-dessus, mais basée sur les expressions
			 * régulières. Il y a encore quelques bugs ...

			Pattern p;
			Matcher m;

			p = Pattern.compile( "O:[^:]*" );
			m = p.matcher( str );

			if( m.find() ) {
				if( str.charAt( m.end()-1 )=='O'
						|| str.charAt( m.end()-1 )=='G'
						|| str.charAt( m.end()-1 )=='D'
						|| str.charAt( m.end()-1 )=='S' ) {
					ownersid = str.substring( m.start()+2, m.end()-1 );
				} else {
					ownersid = str.substring( m.start()+2, m.end() );
				}
			}

			p = Pattern.compile( "G:[^:]*" );
			m = p.matcher( str );

			if( m.find() ) {
				if( str.charAt( m.end()-1 )=='O'
						|| str.charAt( m.end()-1 )=='G'
						|| str.charAt( m.end()-1 )=='D'
						|| str.charAt( m.end()-1 )=='S' ) {
					groupsid = str.substring( m.start()+2, m.end()-1 );
				} else {
					groupsid = str.substring( m.start()+2, m.end() );
				}
			}

			p = Pattern.compile( "D:[^:]*" );
			m = p.matcher( str );

			if( m.find() && ok ) {
				StringTokenizer strtok;
				if( str.charAt( m.end()-1 )=='O'
						|| str.charAt( m.end()-1 )=='G'
						|| str.charAt( m.end()-1 )=='D'
						|| str.charAt( m.end()-1 )=='S' ) {
					strtok = new StringTokenizer( str.substring( m.start()+2, m.end()-1 ), "()" );
				} else {
					strtok = new StringTokenizer( str.substring( m.start()+2, m.end() ), "()" );
				}

				while( strtok.hasMoreTokens() && ok ) {
					SDDL_ACEString ace = new SDDL_ACEString();
					String initStr = strtok.nextToken();

					if( !( ace.initFromString( initStr ) && addACEForDACL( ace ) ) )
						ok = false;
				}
			}
			
			p = Pattern.compile( "S:[^:]*" );
			m = p.matcher( str );

			if( m.find() && ok ) {
				StringTokenizer strtok;
				if( str.charAt( m.end()-1 )=='O'
						|| str.charAt( m.end()-1 )=='G'
						|| str.charAt( m.end()-1 )=='D'
						|| str.charAt( m.end()-1 )=='S' ) {
					strtok = new StringTokenizer( str.substring( m.start()+2, m.end()-1 ), "()" );
				} else {
					strtok = new StringTokenizer( str.substring( m.start()+2, m.end() ), "()" );
				}

				while( strtok.hasMoreTokens() && ok ) {
					SDDL_ACEString ace = new SDDL_ACEString();
					String initStr = strtok.nextToken();

					if( !( ace.initFromString( initStr ) && addACEForSACL( ace ) ) )
						ok = false;
				}
			}
			*/

			return true ;
		}

		return false ;
	}

	/**
	 * Teste si une ace propre aux DACL est déjà présente.
	 * @param ace Une ace.
	 * @return boolean True si l'ace est déjà présente, false sinon.
	**/
	public boolean isACEinDACL( SDDL_ACEString ace ) {
		return daclace.contains( ace ) ;
	}

	/**
	 * Teste si une ace propre aux DACL est déjà présente.
	 * @param ace Une ace.
	 * @return boolean True si l'ace est déjà présente, false sinon.
	**/
	public boolean isACEinSACL( SDDL_ACEString ace ) {
		return saclace.contains( ace ) ;
	}

	/**
	 * Teste si une chaîne de caractères comporte exclusivement des Flags d'acl.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	**/
	public static boolean isAclFlags( String str ) {

		String test = new String( str );
		for( int i=0; i<aclFlags.length && test!=null && test.length()!=0; i++ )
			test = test.replaceAll( aclFlags[i], "" );

		return ( test.length()==0 );
	}

	/**
	 * Teste si un paramêtre est présent ou non.
	 * @param flag Un paramêtre du tableau aclFlags.
	 * @param flags La chaîne de caractères contenant tous les paramêtres.
	 * @return boolean True si c'est le cas, false sinon.
	**/
	public static boolean isPresentInAclFlags( String flag, String flags ) {

		if( flag!=null && flags!=null && isAclFlags( flag ) && isAclFlags( flags ) ) {
			int index = flags.indexOf( flag );

			if( index!=-1 )
				return true ;
		}

		return false;
	}

	/**
	 * Modifie les paramêtres pour DACL.
	 * @param str Une chaîne de caractères vérifiant isAclFlags().
	 * @return boolean True si la chaîne de caractère est correcte, false sinon.
	**/
	public boolean setDACLFlags( String str ) {

		if( isAclFlags( str ) ) {
			daclflags = str ;
			return true ;
		}
		return false ;
	}

	/**
	 * Modifie les paramêtres pour SACL.
	 * @param str Une chaîne de caractères vérifiant isAclFlags().
	 * @return boolean True si la chaîne de caractère est correcte, false sinon.
	**/
	public boolean setSACLFlags( String str ) {

		if( isAclFlags( str ) ) {
			saclflags = str ;
			return true ;
		}

		return false ;
	}

	/**
	 * Modifie le sid groupe.
	 * @value groupsid Identifiant sous forme de chaîne de caractères.
	**/
	public void setGroupSid( String groupsid ) {
		this.groupsid = groupsid;
	}

	/**
	 * Modifie le sid propriétaire.
	 * @return ownersid Identifiant sous forme de chaîne de caractères.
	**/
	public void setOwnerSid( String ownersid ) {
		this.ownersid = ownersid;
	}

	/**
	 * Retourne cette acl sous forme de chaîne de caractères.
	 * @return String Une chaîne de caractères.
	**/
	public String toString() {

		String str = "";

		if( ownersid!=null && ownersid.length()>0 ) {
			str += "O:" + ownersid ;
		}

		if( groupsid!=null && groupsid.length()>0 ) {
			str += "G:" + groupsid ;
		}

		if( daclflags!=null && daclflags.length()>0 ) {
			str += "D:" + daclflags ;
		}

		if( daclace.size()>0 ) {
			if( daclflags==null || daclflags.length()==0 ) {
				str += "D:" ;
			}
			for( int i=0; i<daclace.size(); i++ )
				str += "(" + daclace.elementAt(i) + ")" ;
		}

		if( saclflags!=null && saclflags.length()>0 ) {
			str += "S:" + saclflags ;
		}

		if( saclace.size()>0 ) {
			if( saclflags==null || saclflags.length()==0 ) {
				str += "S:" ;
			}
			for( int i=0; i<saclace.size(); i++ )
				str += "(" + saclace.elementAt(i) + ")" ;
		}

		return str ;
	}
}
