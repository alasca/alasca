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
 
 
package net.aepik.casl.ui.ldap;

import net.aepik.casl.core.SchemaManager;
import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaFile;
import net.aepik.casl.core.ldap.SchemaFileWriter;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.ui.util.LoadFileFrame;
import net.aepik.casl.ui.ManagerFrame;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Cette classe écoute tous les événements qui interviennent sur
 * le modèle et sur la vue. C'est lui qui gère l'intéraction entre
 * la vue du manager des schémas et le manager lui-même.
**/
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

		// On ouvre une fenêtre pour charger un fichier.
		if (o == managerPanel.item_openFile)
		{
			LoadFileFrame sf = new LoadFileFrame(managerFrame, manager);
			this.managerFrame.setStatusDescription(null);
			sf.setVisible(true);
		}

		// On ferme le fichier sélectionné dans l'onglet.
		// On avertit une fois avant de fermer.
		if (o == managerPanel.item_closeFile || o == managerPanel.item_closeFile2)
		{
			int valid = JOptionPane.showConfirmDialog(
				managerFrame,
				"Fermer le fichier ?\nLes modifications sur le fichier seront perdues.",
				"Confirmation fermeture",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			this.managerFrame.setStatusDescription(null);
			if (valid == JOptionPane.OK_OPTION)
			{
				String id = managerPanel.getSelectedSchemaPanelId();
				manager.removeSchema(id);
			}
		}

		// On fermer tous les fichiers.
		// On avertit tout de même une fois avant de fermer.
		if (o == managerPanel.item_closeAllFiles)
		{
			int valid = JOptionPane.showConfirmDialog(
				managerFrame,
				"Fermer tous les fichiers ?\nToutes les modifications seront perdues.",
				"Confirmation fermeture",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			this.managerFrame.setStatusDescription(null);
			if (valid == JOptionPane.OK_OPTION)
			{
				manager.removeAll();
			}
		}

		// On souhaite sauvergarder le fichier.
		// On lance un mini-explorateur de fichier pour nommer le fichier
		// que l'on souhaite créer.
		if (o == managerPanel.item_saveFile || o == managerPanel.item_saveFile2)
		{
			JFileChooser jfcProgramme = new JFileChooser(".");
			jfcProgramme.setMultiSelectionEnabled(false);
			jfcProgramme.setDialogTitle("Enregistrer un fichier");
			jfcProgramme.setApproveButtonText("Enregistrer");
			jfcProgramme.setApproveButtonToolTipText("Cliquer apres avoir nommé le fichier");
			jfcProgramme.setAcceptAllFileFilterUsed(false);

			this.managerFrame.setStatusDescription(null);

			if (jfcProgramme.showDialog(managerFrame, null) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					boolean ok = true;

					// On récupère le schéma sélectionné.
					String schemaId = managerPanel.getSelectedSchemaPanelId();
					Schema currentSchema = manager.getSchema(schemaId);
					SchemaSyntax syntax = currentSchema.getSyntax();

					// On créer le flux de sortie.
					String filename = jfcProgramme.getSelectedFile().getCanonicalPath();
					SchemaFileWriter schemaWriter = syntax.createSchemaWriter();
					SchemaFile schemaFile = new SchemaFile(filename, null, schemaWriter);
					schemaFile.setSchema(currentSchema);
					File file = new File(filename);

					// Test if file already exists.
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
							ok = false;
						}
					}

					// SAve schema into file.
					if (ok && !schemaFile.write())
					{
						JOptionPane.showMessageDialog(
							managerFrame,
							"Le fichier existe déjà, ou le format des données est incorrect.",
							"Erreur",
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
				catch (IOException ioe)
				{
					JOptionPane.showMessageDialog(
						managerFrame,
						"Impossible d'enregistrer le fichier.",
						"Erreur",
						JOptionPane.ERROR_MESSAGE
					);
				}
				JOptionPane.showMessageDialog(
					managerFrame,
					"Le schéma a été enregistré avec succès.",
					"Succès",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}

		// On renomme le fichier en cours d'utilisation
		if (o == managerPanel.item_renameFile || o == managerPanel.item_renameFile2)
		{
			String result = JOptionPane.showInputDialog(
				managerFrame,
				"Spécifier le nouveau nom pour ce schéma :",
				"Renommer un schéma",
				JOptionPane.QUESTION_MESSAGE
			);
			this.managerFrame.setStatusDescription(null);
			if (result != null && result.length() != 0)
			{
				if (manager.isSchemaIdExists(result))
				{
					JOptionPane.showMessageDialog(
						managerFrame,
						"Un schéma du même nom est déjà ouvert.",
						"Erreur",
						JOptionPane.ERROR_MESSAGE
					);
				}
				else
				{
					String schemaId = managerPanel.getSelectedSchemaPanelId();
					Schema s = manager.getSchema(schemaId);
					manager.removeSchema(schemaId);
					manager.addSchema(result, s);
					managerPanel.selectSchemaPanel(result);
				}
			}
		}

		// On souhaite afficher les propriétés du schéma en cours.
		if (o == managerPanel.item_propriety || o == managerPanel.item_propriety2)
		{
			String schemaId = managerPanel.getSelectedSchemaPanelId();
			Schema currentSchema = manager.getSchema(schemaId);
			SchemaPropertiesFrame spf = new SchemaPropertiesFrame(managerFrame, currentSchema, schemaId);
			this.managerFrame.setStatusDescription(null);
			spf.setVisible(true);
		}

		// Recherche d'un attribut ou classe d'objet
		if (o == managerPanel.item_search || o == managerPanel.item_search2)
		{
			String result = JOptionPane.showInputDialog(
				managerFrame,
				"Nom de l'attribut ou de la classe d'objet :",
				"Rechercher un objet",
				JOptionPane.QUESTION_MESSAGE
			);
			this.managerFrame.setStatusDescription(null);
			if (result != null && result.length() != 0)
			{
				String schemaId = managerPanel.getSelectedSchemaPanelId();
				Schema s = manager.getSchema(schemaId);
				SchemaObject so = s.getObjectByName(result);
				SchemaPanel sp = managerPanel.getSelectedSchemaPanel();
				sp.setSelectedPath(so);
				sp.updateTable();
			}
		}

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
	 * Gère l'ensemble des événements de la souris.
	 * @param e L'événement de la souris.
	 */
	public void mouseAction (MouseEvent e)
	{
		if (e != null)
		{
			Object o = e.getSource();
			if (e.isPopupTrigger())
			{
				managerPanel.showPopupMenu(e.getComponent(), e.getX(), e.getY());
			}
		}
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
