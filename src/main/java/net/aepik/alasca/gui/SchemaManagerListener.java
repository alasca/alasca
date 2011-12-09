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
 
 
package net.aepik.alasca.gui.ldap;

import net.aepik.alasca.core.SchemaManager;
import net.aepik.alasca.core.ldap.SchemaFile;
import net.aepik.alasca.core.ldap.SchemaObject;
import net.aepik.alasca.gui.util.LoadFileFrame;
import net.aepik.alasca.gui.ManagerFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Cette classe écoute tous les événements qui interviennent sur
 * le modèle et sur la vue. C'est lui qui gère l'intéraction entre
 * la vue du manager des schémas et le manager lui-même.
 */
public class SchemaManagerListener implements ActionListener, ChangeListener, MouseListener, Observer
{

	/**
	 * La fenêtre principale
	 */
	private ManagerFrame managerFrame;

	/**
	 * Le manager
	 */
	private SchemaManager manager;

	/**
	 * La vue du manager
	 */
	private SchemaManagerPanel managerPanel;

	/**
	 * Créé un nouvel objet ManagerListener.
	 * @param m Le manager de schémas.
	 * @param f La vue du manager de schémas.
	 */
	public SchemaManagerListener (SchemaManager m, SchemaManagerPanel p, ManagerFrame f)
	{
		manager = m ;
		managerPanel = p ;
		managerFrame = f;
		manager.addObserver(this);
		if (managerFrame != null)
		{
			JMenu menu_fichier = managerFrame.getExistingJMenu("Fichier");
			if (menu_fichier != null)
			{
				JMenu menu = new JMenu("Derniers ouverts");
				menu_fichier.add(managerPanel.item_closeAllFiles, 0);
				menu_fichier.add(managerPanel.item_closeFile, 0);
				menu_fichier.add(new JSeparator(), 0);
				menu_fichier.add(managerPanel.item_saveFile, 0);
				menu_fichier.add(menu, 0);
				menu_fichier.add(managerPanel.item_openFile, 0);
			}
			JMenu menu_edition = managerFrame.getExistingJMenu("Edition");
			if (menu_edition != null)
			{
				menu_edition.add(managerPanel.item_propriety, 0);
				menu_edition.add(managerPanel.item_search, 0);
				menu_edition.add(new JSeparator(), 0);
				menu_edition.add(managerPanel.item_renameFile, 0);
			}
		}
	}

	/**
	 * Gère les actions de la vue et permet de modifier les données.
	 * @param e L'action soulevée par un élément de la frame.
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object o = e.getSource();
		this.managerFrame.setStatusDescription(null);
		if (o == managerPanel.item_openFile)
		{
			this.loadFileAction();
		}
		if (o == managerPanel.item_closeFile || o == managerPanel.item_closeFile2)
		{
			this.closeFileAction();
		}
		if (o == managerPanel.item_closeAllFiles)
		{
			this.closeFilesAction();
		}
		if (o == managerPanel.item_saveFile || o == managerPanel.item_saveFile2)
		{
			this.saveFileAction();
		}
		if (o == managerPanel.item_renameFile || o == managerPanel.item_renameFile2)
		{
			this.renameFileAction();
		}
		if (o == managerPanel.item_propriety || o == managerPanel.item_propriety2)
		{
			this.displaySchemaPropertiesAction();
		}
		if (o == managerPanel.item_search || o == managerPanel.item_search2)
		{
			this.searchSchemaObjectAction();
		}
	}

	/**
	 * Action to close a file.
	 */
	private void closeFileAction ()
	{
		int valid = JOptionPane.showConfirmDialog(
			this.managerFrame,
			"Fermer le fichier ?\nLes modifications sur le fichier seront perdues.",
			"Confirmation fermeture",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE
		);
		if (valid == JOptionPane.OK_OPTION)
		{
			String id = this.managerPanel.getSelectedSchemaPanelId();
			this.manager.removeSchema(id);
		}
	}

	/**
	 * Action to close all files.
	 */
	private void closeFilesAction ()
	{
		int valid = JOptionPane.showConfirmDialog(
			this.managerFrame,
			"Fermer tous les fichiers ?\nToutes les modifications seront perdues.",
			"Confirmation fermeture",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE
		);
		if (valid == JOptionPane.OK_OPTION)
		{
			this.manager.removeAll();
		}
	}

	/**
	 * Action to display current schema properties.
	 */
	private void displaySchemaPropertiesAction ()
	{
		String schemaId = managerPanel.getSelectedSchemaPanelId();
		(new SchemaPropertiesFrame(
			managerFrame,
			manager.getSchema(schemaId),
			schemaId
		)).setVisible(true);
	}

	/**
	 * Retourne la fenêtre du manager.
	 * @return ManagerFrame La fenêtre du manager.
	 */
	public ManagerFrame getManagerFrame ()
	{
		return managerFrame;
	}

	/**
	 * Action to load file
	 */
	private void loadFileAction ()
	{
		LoadFileFrame sf = new LoadFileFrame(this.managerFrame, this.manager);
		this.managerFrame.setStatusDescription(null);
		sf.setVisible(true);
	}

	/**
	 * Gère l'ensemble des événements de la souris.
	 * @param e L'événement de la souris.
	 */
	public void mouseAction (MouseEvent e)
	{
		if (e != null && e.isPopupTrigger())
		{
			managerPanel.showPopupMenu(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Action to rename a file.
	 */
	private void renameFileAction ()
	{
		String result = JOptionPane.showInputDialog(
			this.managerFrame,
			"Spécifier le nouveau nom pour ce schéma :",
			"Renommer un schéma",
			JOptionPane.QUESTION_MESSAGE
		);
		if (result == null || result.length() == 0)
		{
			JOptionPane.showMessageDialog(
				this.managerFrame,
				"Aucun nom renseigné.",
				"Erreur",
				JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		if (this.manager.isSchemaIdExists(result))
		{
			JOptionPane.showMessageDialog(
				this.managerFrame,
				"Un schéma du même nom est déjà ouvert.",
				"Erreur",
				JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		String schemaId = managerPanel.getSelectedSchemaPanelId();
		manager.removeSchema(schemaId);
		manager.addSchema(result, manager.getSchema(schemaId));
		managerPanel.selectSchemaPanel(result);
	}

	/**
	 * Action to save a file.
	 */
	private void saveFileAction ()
	{
		JFileChooser jfcProgramme = new JFileChooser(".");
		jfcProgramme.setMultiSelectionEnabled(false);
		jfcProgramme.setDialogTitle("Enregistrer un fichier");
		jfcProgramme.setApproveButtonText("Enregistrer");
		jfcProgramme.setApproveButtonToolTipText("Cliquer apres avoir nommé le fichier");
		jfcProgramme.setAcceptAllFileFilterUsed(false);
		if (jfcProgramme.showDialog(managerFrame, null) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		File file = jfcProgramme.getSelectedFile();
		if (file.exists())
		{
			int result = JOptionPane.showConfirmDialog(
				managerFrame,
				"Le fichier existe déjà, voulez vous l'écraser ?",
				"Confirmation",
				JOptionPane.YES_NO_OPTION
			);
			if (result == JOptionPane.YES_OPTION)
			{
				file.delete();
			}
			else
			{
				return;
			}
		}
		SchemaFile schemaFile = new SchemaFile(
			file.getAbsolutePath(),
			this.manager.getSchema(
				this.managerPanel.getSelectedSchemaPanelId()
			)
		);
		if (!schemaFile.write())
		{
			JOptionPane.showMessageDialog(
				managerFrame,
				"Le format des données est incorrect.",
				"Erreur",
				JOptionPane.ERROR_MESSAGE
			);
			file.delete();
			return;
		}
		JOptionPane.showMessageDialog(
			this.managerFrame,
			"Le schéma a été enregistré avec succès.",
			"Succès",
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	/**
	 * Action to search an object into the current schema.
	 */
	private void searchSchemaObjectAction ()
	{
		String result = JOptionPane.showInputDialog(
			managerFrame,
			"Nom de l'attribut ou de la classe d'objet :",
			"Rechercher un objet",
			JOptionPane.QUESTION_MESSAGE
		);
		if (result == null || result.length() == 0)
		{
			JOptionPane.showMessageDialog(
				this.managerFrame,
				"Aucun nom renseigné.",
				"Erreur",
				JOptionPane.ERROR_MESSAGE
			);
			return;
		}
		SchemaObject object = this.manager.getSchema(
			this.managerPanel.getSelectedSchemaPanelId()
		).getObjectByName(result);
		SchemaPanel panel = this.managerPanel.getSelectedSchemaPanel();
		panel.setSelectedPath(object);
		panel.updateTable();
	}

	/**
	 * Gère les actions du système d'onglets.
	 */
	public void stateChanged (ChangeEvent e)
	{
		managerPanel.selectSchemaPanel(managerPanel.getSelectedSchemaPanelId());
		if (managerFrame != null)
		{
			managerFrame.updateButtons();
		}
	}

	/**
	 * Rafraichit les données visuelles quand une notification
	 * de changement est soulevée par le modèle de données.
	 * @param changed L'objet Observable qui soulève la notification e changement.
	 * @param arg Les arguments divers pour la mise à jour.
	 */
	public void update (Observable changed, Object arg)
	{
		managerPanel.updateTabs();
		managerPanel.updateButtonsStatus();
		managerPanel.selectSchemaPanel(manager.getCurrentSchemaId());	
		if (managerFrame != null)
		{
			managerFrame.setTitle(manager.getCurrentSchemaId());
			managerFrame.updateButtons();
			managerFrame.updateRecentFilesMenu("Fichier/Derniers ouverts");
		}
	}

	public void mouseClicked (MouseEvent e)
	{
		mouseAction(e);
	}

	public void mouseEntered (MouseEvent e)
	{
	}

	public void mouseExited (MouseEvent e)
	{
	}

	public void mousePressed (MouseEvent e)
	{
		mouseAction(e);
	}

	public void mouseReleased (MouseEvent e)
	{
		mouseAction(e);
	}

}
