/*
 * Copyright (C) 2006-2010 Thomas Chemineau
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


package net.aepik.casl.plugin.schemaconverter.core;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Properties;
import java.util.Vector;

/**
 * Convertit un schéma d'une certaine syntaxe, dans un schéma
 * d'une autre syntaxe.
 */

public class SchemaConverter
{

	/**
	 * Le schéma
	 */
	private Schema schema;

	/**
	 * Le traducteur
	 */
	private Translator traduc;

	/**
	 * Construit un nouveau convertisseur de schéma.
	 * @param schema Le schéma du schéma.
	 * @param traducteur Le traducteur de syntaxe.
	 */
	public SchemaConverter ( Schema schema, Translator traduc )
	{
		this.schema = schema;
		this.traduc = traduc;
	}

	/**
	 * Effectue la convertion du schéma vers un autre schéma
	 * de la syntaxe spécifiée.
	 * @param dictionaryName Le nom du dictionnaire.
	 * @param syntaxName La nouvelle syntaxe du schéma.
	 */
	public void convertTo ( String dictionnaryName, String syntaxName ) throws Exception
	{
		if (traduc == null || syntaxName == null)
		{
			return;
		}
		if (!traduc.setSelectedDictionnary(dictionnaryName))
		{
			return;
		}

		// Puis on créé l'objet de syntaxe dynamiquement.
		// On est obligé d'ajouter une 'annotation java' pour passer
		// l'avertissement de 'cast' lors de la compilation.
		// On fait confiance à l'utilisateur.

		@SuppressWarnings("unchecked")
		SchemaSyntax syntax = Schema.getSyntax(syntaxName);

		// Impossible de traduire si le traducteur n'est pas chargé.
		// Sinon, l'opération (assez lourde) consiste à récupérer les objets
		// du schéma, modifier leurs types, modifier les noms de leurs valeurs,
		// et enfin modifier leur syntaxe.

		SchemaSyntax oldSyntax = schema.getSyntax();
		SchemaObject[] objets = ArrayUtils.addAll(
			schema.getObjects(oldSyntax.getAttributeType()),
			schema.getObjects(oldSyntax.getObjectClassType())
		);

		for (int i = 0; i < objets.length; i++)
		{
			String newType = null;
			String type = objets[i].getType();
			boolean ok = true;

			if (type.equals(oldSyntax.getObjectClassType()))
			{
				newType = syntax.getObjectClassType();
			}
			else if (type.equals(oldSyntax.getAttributeType()))
			{
				newType = syntax.getAttributeType();
			}
			else
			{
				ok = false ;
			}

			if (ok)
			{
				objets[i].setType(newType);
				objets[i].setSyntax(syntax);

				// On récupère toutes les clefs de l'objet.
				// Puis, nous allons demander au traducteur toutes les
				// correspondances possibles pour la clef en cours. Puis
				// supprimer cette clef de l'objet, et ajouter toutes les
				// nouvelles clefs avec la valeur de l'ancienne clef.

				String[] keys = objets[i].getKeys();

				if (keys != null)
				{
					for (String key : keys)
					{
						// On récupère la valeur de la clef.
						// Et on supprime l'entrée dans l'objet.
						String value = objets[i].getValue( key ).toString();
						objets[i].delValue(key);

						// On teste si cette clef et sa valeur ont une
						// correspondance spécifique dans le traducteur.
						// On récupère toutes les clefs équivalentes.
						String valueSearch = null;
						if (traduc.isKeyExists(key, value))
						{
							valueSearch = value;
						}

						String[] newKeys = traduc.getKeyEquivs(key, valueSearch);

						if (newKeys != null)
						{
							for (String newKey : newKeys)
							{
								String[] tmp = traduc.getKeyEquivValues(key, valueSearch, newKey);
								if (tmp == null || tmp.length == 0)
								{
									SchemaValue v = syntax.createSchemaValue(newType, newKey, value);
									if (v != null)
									{
										objets[i].addValue(newKey, v);
									}
								}

								for (int j = 0; tmp != null && j < tmp.length; j++)
								{
									SchemaValue v = null;
									if (tmp[j] != null)
									{
										v = syntax.createSchemaValue(newType, newKey, tmp[j]);
									}
									else
									{
										v = syntax.createSchemaValue(newType, newKey, value);
									}
									if (v != null)
									{
										objets[i].addValue(newKey, v);
									}
								}
							}
						}
					}
				}
				else
				{
					ok = false ;
				}
			}

			if (!ok)
			{
				System.out.println("Error converting: " + objets[i].getId());
				schema.delObject(objets[i].getId());
			}
		}

		Properties oids = schema.getObjectsIdentifiers();
		schema.setObjectsIdentifiers(new Properties());
		schema.setSyntax(syntax);
		schema.setObjectsIdentifiers(oids);
		schema.notifyUpdates();
	}

	/**
	 * Retourne l'ensemble des dictionnaires possibles pour la syntaxe
	 * du schéma, et en fonction des syntaxes disponibles.
	**/
	public String[] getAvailableDictionnaries() {

		String[] result = null ;
		Vector<String> resultTmp = new Vector<String>();

		// On récupère aussi l'ensemble des dictionnaires disponibles dans le
		// traducteur. On boucle sur chaque entrées de cette liste.

		String[] dictionnaries = traduc.getAvailableDictionnaries();
		for( String currentDictionnary : dictionnaries ) {
			if( getAvailableSyntaxes( currentDictionnary )!=null )
				resultTmp.add( currentDictionnary );
		}

		result = new String[resultTmp.size()];
		Iterator<String> it = resultTmp.iterator();
		int compteur = 0;
		while( it.hasNext() ) {
			result[compteur] = it.next();
			compteur++;
		}

		return result;
	}

	/**
	 * Retourne l'ensemble des syntaxes de conversion possibles pour la
	 * syntaxe du schéma et un dictionnaire donné (tiré du traducteur).
	 * @param dictionnaire Le nom du dictionnaire à prendre en compte.
	**/
	public String[] getAvailableSyntaxes( String dictionnary ) {

		String[] result = null ;

		if( traduc.setSelectedDictionnary( dictionnary ) ) {

			String[] src = traduc.getSourceSyntaxes();
			String[] dst = traduc.getDestinationSyntaxes();
			boolean srcOk = false ;

			// On regarde si la syntaxe du schéma est dans l'ensemble des
			// syntaxes sources.

			String currentSyntax = schema.getSyntax().toString();

			for( int i=0; i<src.length && !srcOk; i++ ) {
				if( src[i].equals( currentSyntax ) )
					srcOk = true ;
			}

			// On regarde si les syntaxes de destinations sont présentes
			// dans le logiciel, dans ce cas on les ajoutes dans le résultat.

			Vector<String> resultTmp = new Vector<String>();
			String[] syntaxes = Schema.getSyntaxes();	// Syntaxes logiciels

			for( int i=0; i<dst.length && srcOk; i++ ) {
				for( int j=0; j<syntaxes.length; j++ ) {
					if( syntaxes[j].equals( dst[i] ) )
						resultTmp.add( dst[i] );
				}
			}

			result = new String[resultTmp.size()];
			Iterator<String> it = resultTmp.iterator();
			int compteur = 0;
			while( it.hasNext() ) {
				result[compteur] = it.next();
				compteur++;
			}
		}

		if( result!=null & result.length==0 )
			result = null ;

		return result ;
	}
}
