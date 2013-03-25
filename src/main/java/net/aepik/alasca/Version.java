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

package net.aepik.alasca;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.io.IOException;

public class Version
{

	/**
	 * Project name.
	 */
	public static final String PROJECT_NAME = "Alasca";

	/**
	 * Public URL where to find latest version.
	 */
	public static final String URL_VERSION = "http://alasca.github.com/VERSION.txt";

	/**
	 * Public URL of the website.
	 */
	public static final String URL_WEBSITE = "http://alasca.github.com/";

	/**
	 * Current major number.
	 */
	public static final String MAJOR = "0";

	/**
	 * Current minor number.
	 */
	public static final String MINOR = "4";

	/**
	 * Current revision number.
	 */
	public static final String REVISION = "1";

	/**
	 * Get current version retrieved from website.
	 * @return String The current project version
	 */
	public static String getCurrentVersion ()
	{
		String line;
		try
		{
			URL u = new URL(Version.URL_VERSION);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream stream = connection.getInputStream();
			BufferedReader http = new BufferedReader(new InputStreamReader(stream));
			line = http.readLine();
			http.close();
		}
		catch (MalformedURLException e1)
		{
			return null;
		}
		catch (ProtocolException e2)
		{
			return null;
		}
		catch(IOException e3)
		{
			return null;
		}
		return line;
	}

	/**
	 * Retrieve the project name.
	 * @return String The project name
	 */
	public static String getProjectName ()
	{
		return Version.PROJECT_NAME;
	}

	/**
	 * Get version.
	 * @return String The version
	 */
	public static String getVersion ()
	{
		return Version.MAJOR + "." + Version.MINOR + "." + Version.REVISION;
	}

	/**
	 * Check if update available.
	 * @return boolean If update is available
	 */
	public static boolean isCurrentVersion ( String currentVersion )
	{
		if (currentVersion == null)
		{
			return true;
		}
		if (Version.normalizeVersion(Version.getVersion()) >= Version.normalizeVersion(currentVersion))
		{
			return true;
		}
		return false;
	}

	/**
	 * Normalize version string.
	 * @param v A dotted version
	 * @return int A normalized version number
	 */
	public static int normalizeVersion ( String v )
	{
		String[] s = v.split("\\.");
		if (s.length != 3)
		{
			return 0;
		}
		return Integer.parseInt(String.format("%03d", Integer.parseInt(s[0]))
			+ String.format("%03d", Integer.parseInt(s[1]))
			+ String.format("%03d", Integer.parseInt(s[2]))
		);
	}

}

