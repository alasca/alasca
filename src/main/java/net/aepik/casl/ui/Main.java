/*
 * Copyright (C) 2010 Thomas Chemineau
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

package net.aepik.casl.ui;

import net.aepik.casl.core.Manager;
import net.aepik.casl.core.util.Config;
import net.aepik.casl.ui.CreditsFrame;
import net.aepik.casl.ui.ManagerFrame;
import net.aepik.casl.ui.ManagerListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Runnable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main
{

	private CreditsFrame creditsFrame ;

	private Manager manager ;

	private ManagerFrame managerFrame ;

	private ManagerListener managerListener ;

	private Image icone ;

	private int loadingValue ;

	private JProgressBar loadingStatus ;

	public Main() throws Exception
	{
		loadingStatus = new JProgressBar(0, 4);
		loadingStatus.setValue(0);
		loadingStatus.setStringPainted(true);
		loadingStatus.setOpaque(false);
		loadingStatus.setBorder(null);
		loadingValue = 0;

		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(loadingStatus);
		p1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		Manager tmp = new Manager(Config.getResourcesPath() + "/config.xml");
		creditsFrame = new CreditsFrame(null, tmp, p1, false);
		creditsFrame.setSize(300, 155);
		creditsFrame.setUndecorated(true);

		JPanel p2 = (JPanel) creditsFrame.getContentPane().getComponent(0);
		p2.setBorder( BorderFactory.createLineBorder(Color.darkGray, 1));
	}

	public JFrame loadApplication() throws Exception
	{
		synchronized(loadingStatus)
		{
			loadingStatus.setString("Chargement: configuration");
			try
			{
				(new Thread()).sleep(300);
			}
			catch (Exception e) {};
			manager = new Manager(Config.getResourcesPath() + "/config.xml");
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			loadingStatus.setString("Chargement: plugins");
			try
			{
				(new Thread()).sleep(100);
			}
			catch (Exception e) {};
			manager.loadPluginManager();
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			loadingStatus.setString("Chargement: interface");
			try
			{
				(new Thread()).sleep(100);
			}
			catch (Exception e) {};
			managerFrame = new ManagerFrame(
				manager,
				manager.getProperty("FrameTitle"),
				manager.getProperty("FrameStatus")
			);
			icone = Toolkit.getDefaultToolkit().getImage(Config.getResourcesPath() + "/casl.png");
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			loadingStatus.setString("Chargement: CASL");
			try
			{
				(new Thread()).sleep(100);
			}
			catch (Exception e) {};
			managerListener = new ManagerListener(managerFrame);
			managerFrame.addManagerListener(managerListener);
			managerFrame.setIconImage(icone);
			updateLoadingStatus();
		}
		return managerFrame;
	}

	public void closeLoadingFrame()
	{
		creditsFrame.setVisible(false);
	}

	public void disposeLoadingFrame()
	{
		creditsFrame.dispose();
	}

	public void openLoadingFrame()
	{
		creditsFrame.setVisible(true);
	}

	public void updateLoadingStatus()
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			loadingStatus.setValue(++loadingValue);
		}
		else
		{
			Runnable callMAJ = new Runnable()
			{
				public void run()
				{
					updateLoadingStatus();
				}
	    		};
			SwingUtilities.invokeLater(callMAJ);
		}
	}

	public static void main (String[] args)
	{
		Main m = null;
		try
		{
			// Fixe look and feel.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {}
		try
		{
			// Launch application.
			m = new Main();
			m.openLoadingFrame();
			JFrame f = m.loadApplication();
			try
			{
				(new Thread()).sleep(500);
			}
			catch (Exception e) {};
			m.closeLoadingFrame();
			m.disposeLoadingFrame();
			f.setVisible(true);
		}
		catch (Exception e)
		{
			// Unexpected error.
			if (m != null)
			{
				m.closeLoadingFrame();
				m.disposeLoadingFrame();
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(
				null,
				"Une erreur est survenue pendant l'éxécution de l'application :\n\n"
					+ e.toString() + "\n\n",
				"Erreur critique",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
