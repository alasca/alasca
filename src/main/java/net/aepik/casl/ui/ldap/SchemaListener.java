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


package net.aepik.casl.ui.ldap;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Cette classe écoute tous les événements qui interviennent sur
 * le modèle et sur la vue. C'est lui qui gère l'intéraction entre
 * la vue du schéma et le modèle de données du schéma.
 **/

public class SchemaListener implements ActionListener, MouseListener, Observer, TreeSelectionListener
{

	/**
	 * Le modèle de données du schéma
	 */
	private Schema schema;

	/**
	 * La vue du schéma
	 */
	private SchemaPanel schemaPanel;

	/**
	 * Le listener du manager de schémas
	 */
	private SchemaManagerListener schemaManagerListener;

	/**
	 * Le chemin du dernier objet sélectionné
	 */
	private TreePath lastSelectedPath;

	/**
	 * Créé un nouvel objet SchemaListener.
	 * @param s Le modèle de données du schéma, un objet Schema.
	 * @param p La vue du schéma, un objet SchemaPanel.
	 */
	public SchemaListener (SchemaManagerListener l, Schema s, SchemaPanel p)
	{
		schemaManagerListener = l;
		schema = s;
		schemaPanel = p;
		lastSelectedPath = null;
		schema.addObserver(this);
	}

	/**
	 * Gère les actions de la vue et permet de modifier les données.
	 * @param e L'action soulevée par un élément du panel.
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object o = e.getSource();
		if (o == schemaPanel.item_supprimer)
		{
			if (JOptionPane.showConfirmDialog(schemaPanel,
					"Supprimer cet élément ?", "Confirmation suppression",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
			{
				schemaPanel.removeCurrentSelectedNode();
			}
		}
		if (o == schemaPanel.item_propriete || o == schemaPanel.item_propriete2)
		{
			lastSelectedPath = schemaPanel.getCurrentSelectedPath();
			SchemaObjectEditorFrame sof = new SchemaObjectEditorFrame(
					schemaManagerListener.getManagerFrame(),
					schema,
					schemaPanel.getCurrentSelectedObject());
			sof.setVisible(true);
		}
		if (o == schemaPanel.item_rename)
		{
			lastSelectedPath = schemaPanel.getCurrentSelectedPath();
			String id = lastSelectedPath.getLastPathComponent().toString();
			SchemaObject so = this.schema.getObjectByName(id);
			String result = (String) JOptionPane.showInputDialog(
				schemaPanel,
				"Spécifier le nouveau identifiant d'objet:",
				"Renommer un noeud",
				JOptionPane.QUESTION_MESSAGE,
				UIManager.getIcon("OptionPane.questionIcon"),
				null,
				so.getId()
			);
			if (result != null && result.length() != 0)
			{
				so.setId(result);
				this.schema.addOrReplaceObject(so);
				this.schemaPanel.updateTree();
				this.schemaPanel.setSelectedPath(lastSelectedPath);
			}
		}
	}

	/**
	 * Gère l'ensemble des événements de la souris.
	 * @param e L'événement de la souris.
	 */
	public void mouseAction (MouseEvent e)
	{
		if (e != null && e.isPopupTrigger())
		{
			schemaPanel.showPopupMenu(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Rafraichit les données visuelles quand une notification
	 * de changement est soulevée par le modèle de données.
	 * @param changed L'objet Observable qui soulève la notification
	 *		de changement.
	 * @param arg Les arguments divers pour la mise à jour.
	 */
	public void update (Observable changed, Object arg)
	{
		schemaPanel.refreshParentSelectedNode();
		if (schema.isSyntaxChangedSinceLastTime())
		{
			schemaPanel.updateTree();
		}
		else if (arg instanceof Boolean && ((Boolean) arg).booleanValue())
		{
			schemaPanel.updateTree();
		}
		if (lastSelectedPath != null)
		{
			schemaPanel.setSelectedPath(lastSelectedPath);
			schemaPanel.updateTable();
		}
	}

	/**
	 * Gère les changement intervenus sur l'arbre. Typiquement, les changements
	 * sont des sélections d'élement dans l'arbre.
	 * @param e L'événement soulevée lorsqu'un changement intervient sur l'arbre.
	 */
	public void valueChanged (TreeSelectionEvent e)
	{
		schemaPanel.updateTable();
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

