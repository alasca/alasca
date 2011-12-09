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
 * along with Config program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package net.aepik.alasca.util;

import java.util.prefs.Preferences;
import java.util.Vector;

public class Pref
{

	public final static String PREF_VERSION = "version";

	public final static String PREF_LASTOPENFILES = "recentFiles";

	public final static String PREF_LASTOPENSYNTAXES = "recentSyntaxes";

	private static Preferences preferences = null;

	static
	{
		loadPreferences();
	}

	public static String get ( String key )
	{
		return Pref.preferences.get(key, null);
	}

	public static String[] getArray ( String key )
	{
		String str = Pref.get(key);
		if (str == null)
		{
			return new String[0];
		}
		return str.split(";");
	}

	public static Vector<String> getVector ( String key )
	{
		Vector<String> files = new Vector<String>();
		for (String value : Pref.getArray(key))
		{
			files.add(value);
		}
		return files;
	}

	public static void loadPreferences ()
	{
		loadPreferences((new Pref()).getClass());
	}

	public static void loadPreferences ( Class classname )
	{
		Pref.preferences = Preferences.userNodeForPackage(classname);
	}

	public static void set ( String key, String value )
	{
		Pref.preferences.put(key, value);
	}

	public static void set ( String key, String[] values )
	{
		String str = "";
		for (int i = 0; i < values.length; i++)
		{
			str += values[i];
			if (i < values.length - 1)
			{
				str += ";";
			}
		}
		Pref.set(key, str);
	}

}
