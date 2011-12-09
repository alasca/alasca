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

import net.aepik.alasca.core.Plugin;
import net.aepik.alasca.core.PluginManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Panel qui liste toutes les extentions.
 */

public class PluginManagerPanel extends JPanel
{

	private static final long serialVersionUID = 0;

	private PluginManager manager;

	private PluginManagerCell selectedValue;

	private Color selectionBackground;

	private Color selectionForeground;

	/**
	 * Build a new PluginManagerPanel object.
	 * @param m A PluginManager object.
	 */
	public PluginManagerPanel (PluginManager m)
	{
		super(new BorderLayout());
		this.setBackground((new JList()).getBackground());
		this.setForeground((new JList()).getForeground());
		this.manager = m;
		this.selectedValue = null;
		this.selectionBackground = (new JList()).getSelectionBackground();
		this.selectionForeground = (new JList()).getSelectionForeground();
		this.initPanel();
	}

	/**
	 * Return the background color.
	 * @return Color The background color.
	 */
	public Color getSelectionBackground ()
	{
		return selectionBackground;
	}

	/**
	 * Return the foreground color.
	 * @return Color The foreground color.
	 */
	public Color getSelectionForeground ()
	{
		return selectionForeground;
	}

	/**
	 * Initialiaze the panel.
	 */
	public void initPanel ()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setOpaque(false);
		if (this.manager.getPlugins() != null)
		{
			for (Plugin plugin : this.manager.getPlugins())
			{
				mainPanel.add(new PluginManagerCell(plugin));
			}
		}
		this.add( mainPanel, BorderLayout.NORTH );
	}

	/**
	 * Objet représentant un Plugin dans une liste de sélection.
	 * C'est un JPanel affichant le nom, la catégorie, la description et la
	 * version du plugin.
	 */
	private class PluginManagerCell extends JPanel implements MouseListener
	{

		private static final long serialVersionUID = 0;

		private boolean isSelected;

		private Plugin plugin;

		private JLabel name;

		private JLabel category;

		private JLabel version;

		private JTextArea description;

		private JButton boutonExecuter;

		private JPanel container;

		public PluginManagerCell (Plugin p)
		{
			super(new BorderLayout());

			Font styleSimple = (new JLabel("font")).getFont();
			Font styleGras = new Font(styleSimple.getName(), Font.BOLD, styleSimple.getSize());

			this.plugin = p;
			this.isSelected = false;
			this.name = new JLabel(plugin.getName());
			this.name.setFont(styleGras);
			this.name.setOpaque(false);
			if (p.getCategory() != null && p.getCategory().length() > 0)
			{
				this.category = new JLabel(" Catégorie: " + plugin.getCategory());
			}
			else
			{
				this.category = new JLabel(" Catégorie: Aucune");
			}
			this.category.setFont(styleSimple);
			this.category.setOpaque(false);
			this.version = new JLabel(" Version: " + plugin.getVersion());
			this.version.setFont(styleSimple);
			this.version.setOpaque(false);
			this.description = new JTextArea(plugin.getDescription());
			this.description.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			this.description.setEditable(false);
			this.description.setFocusable(false);
			this.description.setFont((new JLabel()).getFont());
			this.description.setOpaque(false);
			this.description.setLineWrap(true);
			this.description.setWrapStyleWord(true);
                        this.boutonExecuter = new JButton("Executer");
                        this.boutonExecuter.addActionListener(new PluginListener(plugin));

			JPanel p11 = new JPanel(new GridLayout(2, 1));
			p11.add(this.category);
			p11.add(this.version);
			p11.setOpaque(false);
			JPanel p1 = new JPanel(new BorderLayout());
			p1.add(this.name, BorderLayout.NORTH);
			p1.add(p11, BorderLayout.CENTER);
			p1.add(this.description, BorderLayout.SOUTH);
			p1.setOpaque(false);
			JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			p2.add(this.boutonExecuter);
			p2.setOpaque(false);

			this.container = new JPanel(new BorderLayout());
			container.add(p1, BorderLayout.CENTER);
			container.add(p2, BorderLayout.SOUTH);
			container.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

			this.add(container, BorderLayout.CENTER);
			this.addMouseListener(this);
			this.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray),
					BorderFactory.createEmptyBorder(3, 3, 3, 3)
				)
			);
			this.setOpaque(false);

			this.update();
		}

		public boolean isSelected ()
		{
			return isSelected;
		}

		public void mousePressed (MouseEvent e)
		{
			if (isSelected)
			{
				return;
			}
			this.setSelected(true);
			this.update();

			PluginManagerCell c = PluginManagerPanel.this.selectedValue;
			if (c != null)
			{
				c.setSelected(false);
				c.update();
			}
			PluginManagerPanel.this.selectedValue = this;
		}

		public void mouseClicked (MouseEvent e)
		{
		}

		public void mouseEntered (MouseEvent e)
		{
		}

		public void mouseExited (MouseEvent e)
		{
		}

		public void mouseReleased (MouseEvent e)
		{
		}

		public void setSelected (boolean selected)
		{
			this.isSelected = selected;
		}

		public void update ()
		{
			if (isSelected)
			{
				this.name.setForeground(PluginManagerPanel.this.getSelectionForeground());
				this.category.setForeground(PluginManagerPanel.this.getSelectionForeground());
				this.version.setForeground(PluginManagerPanel.this.getSelectionForeground());
				this.description.setForeground(PluginManagerPanel.this.getSelectionForeground());
				this.description.setVisible(true);
				this.container.setBackground(PluginManagerPanel.this.getSelectionBackground());
				this.boutonExecuter.setVisible(true);
			}
			else
			{
				this.name.setForeground((new JLabel()).getForeground());
				this.category.setForeground((new JLabel()).getForeground());
				this.version.setForeground((new JLabel()).getForeground());
				this.description.setForeground((new JLabel()).getForeground());
				this.description.setVisible(false);
				this.container.setBackground(PluginManagerPanel.this.getBackground());
				this.boutonExecuter.setVisible(false);
			}
			if (!plugin.canRun())
			{
				this.boutonExecuter.setEnabled(false);
			}
			else
			{
				this.boutonExecuter.setEnabled(true);
			}
		}

	}

}
