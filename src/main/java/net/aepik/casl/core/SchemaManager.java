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


package net.aepik.casl.core;

import net.aepik.casl.core.ldap.Schema;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

/**
 * Gère un ensemble de schéma.
 */
public class SchemaManager extends Observable implements Observer
{

	/**
	 * Le manager général de l'application
	 */
	private Manager manager;

	/**
	 * Une hastable pour stocker l'ensemble des schémas
	 */
	private Hashtable<String,Schema> schemas;

	/**
	 * L'identifiant du dernier schéma ajouté
	 */
	private String lastSchemaId;

	/**
	 * L'identifiant schéma sélectionné
	 */
	private String currentSchemaId;

	/**
	 * Build a new SchemaManager object.
	 * @param m A Manager object.
	 */
	public SchemaManager ( Manager m )
	{
		manager = m;
		schemas = new Hashtable<String,Schema>();
		lastSchemaId = null;
		currentSchemaId = null;
	}

	/**
	 * Ajoute un schéma dans l'ensemble.
	 * @param id L'identifiant du schéma.
	 * @param s Un objet Schema.
	 * @return boolean True si l'ajout réussi, false sinon.
	 */
	public boolean addSchema ( String id, Schema s )
	{
		try
		{
			if (!isSchemaIdExists(id))
			{
				schemas.put(id, s);
				s.addObserver(this);
				lastSchemaId = id;
				notifyUpdates();
				return true;
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retourne le manager de l'application.
	 * @return Manager Le manager de l'application.
	 */
	public Manager getManager ()
	{
		return manager;
	}

	/**
	 * Retourne le schéma correspondant à l'identifiant id.
	 * @param id Un identifiant.
	 * @return Schema Le schéma correspondant à l'id ou null.
	 */
	public Schema getSchema ( String id )
	{
		try
		{
			if (isSchemaIdExists(id))
			{
				return schemas.get(id);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retourne les identifiants des schémas.
	 * @return String[] Les identifiants des schémas, ou null.
	 */
	public String[] getSchemaIds ()
	{
		String[] result = new String[schemas.size()];
		int position = 0;
		for (Enumeration<String> e = schemas.keys(); e.hasMoreElements();)
		{
			result[position] = e.nextElement();
			position++;
		}
		return result;
	}

	/**
	 * Retourne l'ensemble des schémas.
	 * @return Schema[] Un ensemble de schémas.
	 */
	public Schema[] getSchemas ()
	{
		Schema[] result = new Schema[schemas.size()];
		int position = 0;
		for (Enumeration<Schema> e = schemas.elements(); e.hasMoreElements();)
		{
			result[position] = e.nextElement();
			position++;
		}
		return result;
	}

	/**
	 * Retourne le schéma sélectionné.
	 * @return Schema Un schéma.
	 */
	public Schema getCurrentSchema ()
	{
		return getSchema(currentSchemaId);
	}

	/**
	 * Retourne l'identifiant du schéma sélectionné.
	 * @return String Un identifiant.
	 */
	public String getCurrentSchemaId ()
	{
		return currentSchemaId;
	}

	/**
	 * Retourne le dernier schéma ajouté.
	 * @return Schema Un schéma.
	 */
	public Schema getLastSchema ()
	{
		return getSchema(lastSchemaId);
	}

	/**
	 * Retourne l'identifiant du dernier schéma ajouté.
	 * @return String Un identifiant.
	 */
	public String getLastSchemaId ()
	{
		return lastSchemaId;
	}

	/**
	 * Retourne le nombre de schémas ajoutés.
	 * @return int Un entier.
	 */
	public int getNbSchemas ()
	{
		return schemas.size();
	}

	/**
	 * Teste si un schéma d'identifiant id existe.
	 * @param id L'identifiant d'un schéma.
	 * @return boolean True si l'id existe, false sinon.
	 */
	public boolean isSchemaIdExists ( String id )
	{
		try
		{
			if (id != null)
			{
				return schemas.containsKey(id);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Permet de notifier que les données ont changées.
	 * Tous les objets observant le Manager verront la notification.
	 */
	public void notifyUpdates ()
	{
		setChanged();
		notifyObservers();
	}

	/**
	 * Supprime tous les schémas.
	 */
	public void removeAll ()
	{
		schemas.clear();
		lastSchemaId = null;
		notifyUpdates();
	}

	/**
	 * Supprime un schéma d'identifiant id.
	 * @param id L'identifiant du schéma à supprimer.
	 * @return boolean True si l'id n'existe pas déjà, false sinon.
	 */
	public boolean removeSchema ( String id )
	{
		try
		{
			if (isSchemaIdExists(id))
			{
				lastSchemaId = null;
				currentSchemaId = null;
				int compteur = 0;
				boolean ok = false;
				boolean isNextElement = false;
				String previousSchemaId = null;
				Enumeration<String> keys = schemas.keys();
				while (keys.hasMoreElements() && !ok)
				{
					String idTmp = keys.nextElement();
					if (currentSchemaId == null)
					{
						if (isNextElement)
						{
							currentSchemaId = idTmp;
							isNextElement = false;
						}
						else if (idTmp.equals(id))
						{
							if (compteur > 0)
							{
								currentSchemaId = previousSchemaId;
							}
							else if (keys.hasMoreElements())
							{
								isNextElement = true;
							}
						}
						else
						{
							previousSchemaId = idTmp;
						}
					}
					if (!keys.hasMoreElements())
					{
						lastSchemaId = idTmp;
					}
				}
				getSchema(id).deleteObserver(this);
				schemas.remove(id);
				notifyUpdates();
				return true;
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Modifie le schéma marqué comme sélectionné.
	 * @param id Un indentifiant valide de schéma.
	 */
	public void setCurrentSchema ( String id )
	{
		if (isSchemaIdExists(id))
		{
			currentSchemaId = id;
		}
	}

	/**
	 * Rafraichit les données visuelles quand une notification
	 * de changement est soulevée par les données contenues dans ce manager.
	 * @param changed L'objet Observable qui soulève la notification
	 *		de changement.
	 * @param arg Les arguments divers pour la mise à jour.
	 */
	public void update ( Observable changed, Object arg )
	{
		notifyUpdates();
	}

}
