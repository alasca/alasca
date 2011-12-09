/*
 * Copyright (C) 2006-2011 Thomas Chemineau
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


package net.aepik.alasca.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

public class Node
{

	/**
	 * La valeur du noeud
	 */
	private String value;

	/**
	 * Les références des noeuds que contient ce noeud
	 */
	private LinkedList<Node> nodes;

	/**
	 * Construit un nouveau noeud vide.
	 */
	public Node ()
	{
		value = null;
		nodes = new LinkedList<Node>();
	}

	/**
	 * Construit un nouveau noeud.
	 * @param v La valeur du noeud.
	 */
	public Node ( String v )
	{
		value = v;
		nodes = new LinkedList<Node>();
	}

	/**
	 * Construit un nouveau noeud vide.
	 * @param v La valeur du noeud.
	 * @param n Les sous noeuds de ce noeud.
	 */
	public Node ( String v, Node[] n )
	{
		value = v;
		nodes = new LinkedList<Node>();
		add(n);
	}

	/**
	 * Construit un nouveau noeud vide.
	 * @param v La valeur du noeud.
	 * @param n Les sous noeuds de ce noeud.
	 */
	public Node ( String v, Collection<Node> c )
	{
		value = v;
		nodes = new LinkedList<Node>();
		add(c);
	}

	/**
	 * Ajoute un noeud.
	 * @param n Le nouveau noeud fils.
	 * @return boolean True si le noeud n'est pas déjà présent, false sinon.
	 */
	public boolean add ( Node n )
	{
		if (n != null && !contains(n))
		{
			return nodes.add(n);
		}
		return false;
	}

	/**
	 * Ajoute un ensemble de noeuds.
	 * @param n L'ensemble de noeuds.
	 * @return boolean True si tous les noeuds ont été ajouté, false sinon.
	 *		Si false est retourné, aucun ajout n'aura été effectué.
	 */
	public boolean add ( Node[] n )
	{
		boolean ok = true;
		@SuppressWarnings("unchecked")
		LinkedList<Node> save = (LinkedList<Node>) nodes.clone();
		for (int i = 0; i < n.length && ok; i++)
		{
			if (!add(n[i]))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			nodes = save;
		}
		return ok;
	}

	/**
	 * Ajoute un ensemble de noeuds.
	 * @param c L'ensemble de noeuds.
	 * @return boolean True si tous les noeuds ont été ajouté, false sinon.
	 *		Si false est retourné, aucun ajout n'aura été effectué.
	 */
	public boolean add ( Collection<Node> c )
	{
		boolean ok = true;
		@SuppressWarnings("unchecked")
		LinkedList<Node> save = (LinkedList<Node>) nodes.clone();
		Iterator<Node> it = c.iterator();
		while (ok && it.hasNext())
		{
			if (!add(it.next()))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			nodes = save;
		}
		return ok;
	}

	/**
	 * Indique si un noeud fils est contenu dans ce noeud.
	 * @param n Le noeud fils a testé.
	 * @return boolean True si le noeud fils existe.
	 */
	public boolean contains ( Node n )
	{
		return nodes.contains(n);
	}

	/**
	 * Indique si un noeud fils de valeur v est contenu dans ce noeud.
	 * @param v La valeur du noeud fils recherché.
	 * @return boolean True si le noeud fils existe.
	 */
	public boolean contains ( String v )
	{
		if (getNode(v) != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Retourne le premier noeud fils.
	 * @return Node Le premier noeud, ou null si il n'y a pas de noeuds fils.
	 */
	public synchronized Node getFirstNode ()
	{
		return nodes.getFirst();
	}

	/**
	 * Retourne le dernier noeud fils.
	 * @return Node Le dernier noeud, ou null si il n'y a pas de noeuds fils.
	 */
	public synchronized Node getLastNode ()
	{
		return nodes.getLast();
	}

	/**
	 * Retourne le noeuds fils à la position i.
	 * @param i La position du noeud fils.
	 * @return Node Un noeud fils, ou null si il n'y a pas de noeuds fils.
	 */
	public synchronized Node getNode ( int i )
	{
		return nodes.get(i);
	}

	/**
	 * Retourne le premier noeud fils qui a pour valeur v.
	 * @param v La valeur du noeud fils.
	 * @return Node Le noeuds fils, ou null si il n'y a pas de noeuds fils.
	 */
	public synchronized Node getNode ( String v )
	{
		Node result = null;
		ListIterator<Node> it = nodes.listIterator(0);
		while (it.hasNext() && result == null)
		{
			Node n = it.next();
			if (n.getValue().equals(v))
			{
				result = n;
			}
		}
		return result;
	}

	/**
	 * Retourne les valeurs des noeuds fils du noeud fils dont la valeur est v.
	 * @param v La valeur du noeud fils.
	 * @return String[] Les valeurs des noeuds fils d'un noeud fils de ce noeud,
	 *		ou null si le noeud fils n'existe pas.
	 */
	public String[] getNodeValues ( String v )
	{
		Node n = getNode(v);
		if (n != null)
		{
			return n.getNodesValue();
		}
		return null;
	}

	/**
	 * Retourne la liste des noeuds que contient ce noeud.
	 * @return Node[] La liste des noeuds fils.
	 */
	public synchronized Node[] getNodes ()
	{
		Node[] n = new Node[getNodesCount()];
		ListIterator<Node> it = nodes.listIterator(0);
		for (int i=0; i<n.length; i++)
		{
			n[i] = it.next();
		}
		return n;
	}

	/**
	 * Retourne le ou les noeuds fils qui ont pour valeur v.
	 * @param v La valeur du noeud fils.
	 * @return Node[] Les noeuds fils, ou null si il n'y a pas de noeuds fils.
	 */
	public synchronized Node[] getNodes ( String v )
	{
		Vector<Node> tmp = new Vector<Node>();
		ListIterator<Node> it = nodes.listIterator(0);
		while (it.hasNext())
		{
			Node n = it.next();
			if (n.getValue().equals(v))
			{
				tmp.add(n);
			}
		}
		Node[] result = new Node[tmp.size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = tmp.elementAt(i);
		}
		return result;
	}

	/**
	 * Retourne le nombre de noeuds que contient ce noeud.
	 * @return int Le nombre de noeuds fils.
	 */
	public int getNodesCount ()
	{
		return nodes.size();
	}

	/**
	 * Retourne les valeurs des noeuds fils.
	 * @return String[] Les valeurs des noeuds fils.
	 */
	public String[] getNodesValue ()
	{
		Node[] n = getNodes();
		String[] result = new String[n.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = n[i].getValue();
		}
		return result;
	}

	/**
	 * Retourne la valeur du noeud.
	 * @return String La valeur du noeud.
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Supprime le noeud fils.
	 * @param n Le noeud à supprimer.
	 * @return boolean True si le noeud existe.
	 */
	public synchronized boolean remove ( Node n )
	{
		return nodes.remove(n);
	}

	/**
	 * Supprime le noeud fils à la position i.
	 * @param i La position du noeud fils à supprimer.
	 * @return Node Le noeud supprimé, ou null si echec.
	 */
	public synchronized Node remove ( int i ) throws IndexOutOfBoundsException
	{
		return nodes.remove(i);
	}

	/**
	 * Modifie la valeur du noeud.
	 * @param v La nouvelle valeur du noeud.
	 */
	public void setValue ( String v )
	{
		value = v;
	}

	/**
	 * Retourne la valeur du noeud.
	 * @return String La valeur du noeud.
	 */
	public String toString ()
	{
		return value;
	}

}
