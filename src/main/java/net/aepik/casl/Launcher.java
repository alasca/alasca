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

public class Launcher
{

	private CreditsFrame creditsFrame;

	private Manager manager;

	private ManagerFrame managerFrame;

	private ManagerListener managerListener;

	private String configFile;

	private Image icone;

	private int loadingValue;

	private JProgressBar loadingStatus;

	public Launcher () throws Exception
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(loadingStatus);
		p1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                this.configFile = Config.getResourcesPath() + "/config.xml";
                this.icone = Toolkit.getDefaultToolkit().getImage(Config.getResourcesPath() + "/casl.png");
                this.loadingStatus = new JProgressBar(0, 3);
                this.loadingStatus.setValue(0);
                this.loadingStatus.setStringPainted(true);
                this.loadingStatus.setOpaque(false);
                this.loadingStatus.setBorder(null);
                this.loadingValue = 0;
		this.creditsFrame = new CreditsFrame(null, new Manager(configFile), p1, false);
		this.creditsFrame.setSize(300, 155);
		this.creditsFrame.setUndecorated(true);
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
			}
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
			}
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
			}
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
			manager.getSchemaManager().notifyUpdates();
			updateLoadingStatus();
			try
			{
				(new Thread()).sleep(400);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return managerFrame;
	}

	public void closeLoadingFrame ()
	{
		creditsFrame.setVisible(false);
	}

	public void disposeLoadingFrame ()
	{
		creditsFrame.dispose();
	}

	private void openLoadingFrame ()
	{
		creditsFrame.setVisible(true);
	}

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
				public void run()
				{
					updateLoadingStatus();
				}
	    		};
			SwingUtilities.invokeLater(callMAJ);
		}
	}

	public static void main ( String[] args )
	{
		Launcher m = null;
		ManagerFrame f = null;
		try
		{
			m = new Launcher();
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
