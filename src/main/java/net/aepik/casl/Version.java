/*
 * Copyright (C) 2011 Thomas Chemineau
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


package net.aepik.casl;


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
	 * Public URL where to find latest version.
	 * @var String
	 */
	public static final String urlVersion = "http://alasca.aepik.net/version/current/";

	/**
	 * Public URL of the website.
	 * @var String
	 */
	public static final String urlWebsite = "http://www.aepik.net/projects/casl/start";

	/**
	 * Current major number.
	 * @var int
	 */
	public static final String major = "0";

	/**
	 * Current minor number.
	 * @var int
	 */
	public static final String minor = "3";

	/**
	 * Current revision number.
	 * @var int
	 */
	public static final String revision = "0";

	/**
	 * Get version.
	 * @return String
	 */
	public static String get ()
	{
		return Version.major + "." + Version.minor + "." + Version.revision;
	}

	/**
	 * Get current version.
	 * @return String
	 */
	public static String getCurrent ()
	{
		String line;
		try
		{
			URL u = new URL(urlVersion);
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
	 * Check if update available.
	 * @return boolean
	 */
	public static boolean isCurrent ( String currentVersion )
	{
		if (currentVersion == null)
		{
			return true;
		}
		if (Version.normalize(Version.get()) >= Version.normalize(currentVersion))
		{
			return true;
		}
		return false;
	}

	/**
	 * Normalize version string.
	 * @param String
	 * @return int
	 */
	public static int normalize ( String v )
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

