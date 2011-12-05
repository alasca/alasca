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

package net.aepik.casl;

import net.aepik.casl.core.Manager;
import net.aepik.casl.core.util.Config;
import net.aepik.casl.ui.CreditsFrame;
import net.aepik.casl.ui.ManagerFrame;
import net.aepik.casl.ui.ManagerListener;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This class will launch the application.
 */
public class Launcher
{

	/**
	 * Credit frame.
	 */
	private CreditsFrame creditsFrame;

	/**
	 * Core manager.
	 */
	private Manager manager;

	/**
	 * UI manager.
	 */
	private ManagerFrame managerFrame;

	/**
	 * Manager listener.
	 */
	private ManagerListener managerListener;

	/**
	 * Application icon.
	 */
	private Image icon;

	/**
	 * Progress bar.
	 */
	private JProgressBar loadingStatus;

	/**
	 * Progress bar status.
	 */
	private int loadingValue;

	/**
	 * Current version retrieved from network.
	 */
	private String currentVersion;

	/**
	 * Build a new launcher.
	 */
	public Launcher () throws Exception
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(loadingStatus);
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.icon = Toolkit.getDefaultToolkit().getImage(Config.getResourcesPath() + "/alasca.png");
		this.creditsFrame = new CreditsFrame(null, new Manager(), panel, false);
		this.creditsFrame.setSize(300, 155);
		this.creditsFrame.setUndecorated(true);
                this.loadingStatus = new JProgressBar(0, 3);
                this.loadingStatus.setValue(0);
                this.loadingStatus.setStringPainted(true);
                this.loadingStatus.setOpaque(false);
                this.loadingStatus.setBorder(null);
	}

	/**
	 * Load the application.
	 * @return JFrame A frame to display.
	 */
	public JFrame loadApplication () throws Exception
	{
		synchronized (loadingStatus)
		{
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		synchronized (loadingStatus)
		{
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			this.manager = new Manager();
			this.manager.loadPluginManager();
			this.currentVersion = Version.getCurrentVersion();
			this.manager.setUpdateAvailable(!Version.isCurrentVersion(this.currentVersion));
			this.printVersion();
			this.updateLoadingStatus();
		}
		synchronized (loadingStatus)
		{
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			this.managerFrame = new ManagerFrame(this.manager, this.manager.getProperty("FrameTitle"), this.manager.getProperty("FrameStatus"));
			this.managerListener = new ManagerListener(this.managerFrame);
			this.managerFrame.addManagerListener(this.managerListener);
			this.managerFrame.setIconImage(this.icon);
			this.updateLoadingStatus();
		}
		synchronized (loadingStatus)
		{
			this.manager.getSchemaManager().notifyUpdates();
			this.updateLoadingStatus();
			try
			{
				(new Thread()).sleep(400);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return this.managerFrame;
	}

	/**
	 * Close the loading frame.
	 */
	public void closeLoadingFrame ()
	{
		creditsFrame.setVisible(false);
	}

	/**
	 * Dispose the loading frame.
	 */
	public void disposeLoadingFrame ()
	{
		creditsFrame.dispose();
	}

	/**
	 * Open the loading frame.
	 */
	private void openLoadingFrame ()
	{
		creditsFrame.setVisible(true);
	}

	/**
	 * Print version on standard output.
	 */
	private void printVersion ()
	{
		System.out.println(Version.getProjectName() + " " + Version.getVersion());
		if (!Version.isCurrentVersion(this.currentVersion))
		{
			System.out.println("New release " + this.currentVersion + " is available!");
		}
	}

	/**
	 * Update internal loading status.
	 */
	public void updateLoadingStatus ()
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			loadingStatus.setValue(++loadingValue);
		}
		else
		{
			Runnable callMAJ = new Runnable()
			{
				public void run ()
				{
					updateLoadingStatus();
				}
			};
			SwingUtilities.invokeLater(callMAJ);
		}
	}

	/**
	 * Launch the main program.
	 * @param args Arguments taken from command line.
	 */
	public static void main (String[] args)
	{
		Launcher m = null;
		ManagerFrame f = null;
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		try
		{
			m = new Launcher();
			m.openLoadingFrame();
			f = (ManagerFrame) m.loadApplication();
			m.closeLoadingFrame();
			m.disposeLoadingFrame();
			f.setVisible(true);
		}
		catch (Exception e2)
		{
			if (m != null)
			{
				m.closeLoadingFrame();
				m.disposeLoadingFrame();
			}
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null,
				"Une erreur est survenue:\n\n" + e2.toString() + "\n\n",
				"Erreur critique", JOptionPane.ERROR_MESSAGE);
		}
	}
}
