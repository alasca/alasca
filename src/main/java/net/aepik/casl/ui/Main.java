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

	private CreditsFrame creditsFrame;

	private Manager manager;

	private ManagerFrame managerFrame;

	private ManagerListener managerListener;

	private String configFile;

	private Image icone;

	private int loadingValue;

	private JProgressBar loadingStatus;

	public Main() throws Exception
	{
		this.configFile = Config.getResourcesPath() + "/config.xml";
		this.icone = Toolkit.getDefaultToolkit().getImage(Config.getResourcesPath() + "/casl.png");

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		};

		loadingStatus = new JProgressBar(0, 3);
		loadingStatus.setValue(0);
		loadingStatus.setStringPainted(true);
		loadingStatus.setOpaque(false);
		loadingStatus.setBorder(null);
		loadingValue = 0;

		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(loadingStatus);
		p1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		Manager tmp = new Manager(configFile);
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
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			};
			manager = new Manager(this.configFile);
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			};
			manager.loadPluginManager();
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			try
			{
				(new Thread()).sleep(200);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			};
			managerFrame = new ManagerFrame(
				manager,
				manager.getProperty("FrameTitle"),
				manager.getProperty("FrameStatus")
			);
			managerListener = new ManagerListener(managerFrame);
			managerFrame.addManagerListener(managerListener);
			managerFrame.setIconImage(icone);
			updateLoadingStatus();
		}
		synchronized(loadingStatus)
		{
			updateLoadingStatus();
			try
			{
				(new Thread()).sleep(400);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			};
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

	private void openLoadingFrame()
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
		ManagerFrame f = null;
		try
		{
			m = new Main();
			m.openLoadingFrame();
			f = (ManagerFrame) m.loadApplication();
			System.out.println("CASL "+f.getManager().getProperty("Version"));
			m.closeLoadingFrame();
			m.disposeLoadingFrame();
			f.setVisible(true);
		}
		catch (Exception e)
		{
			if (m != null)
			{
				m.closeLoadingFrame();
				m.disposeLoadingFrame();
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(
				null,
				"Une erreur est survenue:\n\n" + e.toString() + "\n\n",
				"Erreur critique",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
