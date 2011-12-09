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


package net.aepik.alasca.gui;

import net.aepik.alasca.core.Manager;
import net.aepik.alasca.core.util.Config;
import net.aepik.alasca.core.util.Pref;
import net.aepik.alasca.gui.ldap.SchemaManagerListener;
import net.aepik.alasca.gui.ldap.SchemaManagerPanel;
import net.aepik.alasca.gui.util.LoadFileFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Cette classe écoute tous les événements qui interviennent sur
 * le modèle et sur la vue. C'est lui qui gère l'intéraction entre
 * la vue du manager des schémas et le manager lui-même.
 */
public class ManagerListener implements ActionListener, MouseListener, WindowListener
{

	/**
	 * Window object.
	 **/
	private ManagerFrame managerFrame;

	/**
	 * Build this listener.
	 * @param mf ManagerFrame object.
	 */
	public ManagerListener ( ManagerFrame mf )
	{
		managerFrame = mf;
	}

	/**
	 * Gère les actions basique de la fenêtre.
	 * @param e Un événement.
	 */
	public void actionPerformed ( ActionEvent e )
	{
		Object o = e.getSource();
		if (o == managerFrame.item_quit)
		{
			this.windowClosing(null);
		}
		if (o == managerFrame.item_plugins)
		{
			PluginManagerFrame f = new PluginManagerFrame(
				managerFrame,
				managerFrame.getManager().getPluginManager()
			);
			this.mouseDescription(null);
			f.setVisible(true);
		}
		if (o == managerFrame.item_authors)
		{
			this.mouseDescription(null);
			new CreditsFrameLauncher(managerFrame, managerFrame.getManager());
		}
		if (o instanceof JMenuItem)
		{
			JMenuItem mi = (JMenuItem) o;
			String[] files = Pref.getArray(Pref.PREF_LASTOPENFILES);
			String[] syntaxes = Pref.getArray(Pref.PREF_LASTOPENSYNTAXES);
			for (int i = 0; i < files.length; i++)
			{
				String file = files[i];
				String syntaxe = syntaxes[i];
				if (mi.getText().endsWith((new File(file)).getName()))
				{
					LoadFileFrame sf = new LoadFileFrame(
						this.managerFrame,
						this.managerFrame.getManager().getSchemaManager()
					);
					if (!sf.loadFile(file, syntaxe))
					{
						JOptionPane.showMessageDialog(
							managerFrame,
							sf.getErrorMessage(),
							"Erreur",
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
			}
		}
	}

	public void mouseClicked ( MouseEvent e )
	{
	}

	/**
	 * Gère la description dans la barre de status.
	 * @param e L'événement de la souris.
	**/
	public void mouseDescription( MouseEvent e )
	{
		if (e != null)
		{
			Object o = e.getSource();
			if (managerFrame != null && o instanceof JMenuItem && e.getComponent().isEnabled())
			{
				managerFrame.setStatusDescription(((JMenuItem) o).getText());
				return;
			}
		}
		managerFrame.setStatusDescription(null);
	}

	public void mouseEntered ( MouseEvent e )
	{
		this.mouseDescription(e);
	}

	public void mouseExited ( MouseEvent e )
	{
		this.mouseDescription(e);
	}

	public void mousePressed ( MouseEvent e )
	{
	}

	public void mouseReleased ( MouseEvent e )
	{
	}

	public void windowActivated ( WindowEvent e )
	{
	}

	public void windowClosed ( WindowEvent e )
	{
	}

 	public void windowClosing ( WindowEvent e )
	{
 		managerFrame.setVisible(false);
 		System.exit(0);
 	}

	public void windowDeactivated ( WindowEvent e )
	{
	}

	public void windowDeiconified ( WindowEvent e )
	{
	}

	public void windowIconified ( WindowEvent e )
	{
	}

	public void windowOpened ( WindowEvent e )
	{
		if (managerFrame.getManager().getUpdateAvailable())
		{
			JEditorPane text = new JEditorPane();
			text.setContentType("text/html; charset=UTF-8");
			text.setText("<p>Une nouvelle version est disponible en ligne<br/>"
			           + "<a href=\"http://alasca.aepik.net\">http://alasca.aepik.net</a></p><br/>");
			JOptionPane.showMessageDialog(
				managerFrame,
				text,
				"Mise à jour disponible",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}

}
