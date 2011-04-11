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


import java.util.Enumeration;
import java.util.Vector;


/**
 * A History object allow to keep in memory a reading order.
 */
public class History
{

	private Vector<Object> datas;

	/**
	 * Build a new History object.
	 */
	public History ()
	{
		datas = new Vector<Object>();
	}

	/**
	 * Get a Enumeration object.
	 * @return Enumeration<Object>
	 */
	public Enumeration<Object> elements ()
	{
		return datas.elements();
	}

	/**
	 * Get the first element.
	 * @return Object
	 */
	public Object getElementInFirstPosition ()
	{
		if (datas.size() != 0)
		{
			return datas.firstElement();
		}
		return null;
	}

	/**
	 * Get the last element.
	 * @return Object
	 */
	public Object getElementInLastPosition ()
	{
		if (datas.size() != 0)
		{
			return datas.lastElement();
		}
		return null;
	}

	/**
	 * Get the position of an element into the history.
	 * @return int
	 */
	public int getIndexOf (Object o)
	{
		return datas.indexOf(o);
	}

	/**
	 * Insert an element at the beginning of the history.
	 * @param Object o The object to insert
	 */
	public void insertElementInFirstPosition (Object o)
	{
		datas.add(0, o);
	}

	/**
	 * Insert an element at the end of the history.
	 * @param Object o The object to insert
	 */
	public void insertElementInLastPosition (Object o)
	{
		datas.add(o);
	}

	/**
	 * Move an element at position i to the position j into the history.
	 * @param int i Initial position of the element
	 * @param int j Final position of the element
	 */
	public void moveElement (int i, int j)
	{
		if (i >= 0 && i < datas.size() && j != i && j >= 0 && j < datas.size())
		{
			Object o = datas.elementAt(i);
			datas.removeElementAt(i);
			datas.insertElementAt(o, j);
		}
	}

	/**
	 * Remove an element.
	 * @param Object o The element to remove.
	 */
	public void removeElement (Object o)
	{
		if (datas.contains(o))
		{
			datas.remove(o);
		}
	}

	/**
	 * Remove the first element of the history, and return it.
	 * @return Object
	 */
	public Object removeElementInFirstPosition ()
	{
		Object result = null;
		if (datas.size() != 0)
		{
			result = datas.firstElement();
			datas.remove(result);
		}
		return result;
	}

	/**
	 * Remove the last element of the history, and return it.
	 * @return Object
	 */
	public Object removeElementInLastPosition ()
	{
		Object result = null;
		if (datas.size() != 0)
		{
			result = datas.lastElement();
			datas.remove(result);
		}
		return result;
	}

}
