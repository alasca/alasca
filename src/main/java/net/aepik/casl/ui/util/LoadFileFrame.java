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

package net.aepik.casl.ui.util;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaFile;
import net.aepik.casl.core.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.util.Pref;
import net.aepik.casl.ui.ManagerFrame;
import net.aepik.casl.ui.util.DescriptiveInternalFrame;
import org.apache.commons.lang3.text.WordUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.SuppressWarnings;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;

public class LoadFileFrame extends JDialog implements ActionListener, WindowListener
{

	private static final long serialVersionUID = 0;

	/**
	 * La fenêtre appelante 
	 */
	private JFrame mainFrame;

	/**
	 * Le manager de schéma
	 */
	private SchemaManager manager;

	/**
	 * Le champs contenant le nom du fichier
	 */
	private JTextField filename;

	/**
	 * La liste contenant l'ensemble des syntaxes disponibles
	 */
	private JComboBox syntaxes;

	/**
	 * Le bouton de choix du fichier
	 */
	private JButton boutonOpenFile;

	/**
	 * Le bouton Ok
	 */
	private JButton boutonOk;

	/**
	 * Le bouton Annuler
	 */
	private JButton boutonAnnuler;

	/**
	 * Contains error message.
	 */
	private String errorMessage;

	/**
	 * Build new LoadFileFrame object.
	 */
	public LoadFileFrame ( JFrame f, SchemaManager m )
	{
		super(f, "Ouvrir un fichier Schema", true);
		this.setSize(400, 380);
		this.setResizable(false);
		this.setLocationRelativeTo(f);
		this.mainFrame = f;
		this.manager = m;
		this.filename = new JTextField();
		this.syntaxes = new JComboBox();
		this.boutonOpenFile = new JButton("...");
		this.boutonOk = new JButton("Charger");
		this.boutonAnnuler = new JButton("Annuler");
		this.errorMessage = "";
		initFrame();
	}

	/**
	 * Perform action on event for this object.
	 */
	public void actionPerformed ( ActionEvent e )
	{
		Object o = e.getSource();
		if (o == boutonOpenFile)
		{
			JFileChooser jfcProgramme = new JFileChooser(".");
			jfcProgramme.setMultiSelectionEnabled(false);
			jfcProgramme.setDialogTitle("Selectionner un fichier");
			jfcProgramme.setApproveButtonText("Selectionner");
			jfcProgramme.setApproveButtonToolTipText("Cliquer apres avoir selectionné un fichier");
			jfcProgramme.setAcceptAllFileFilterUsed(false);
			if (jfcProgramme.showDialog(this, null) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					filename.setText(jfcProgramme.getSelectedFile().getCanonicalPath());
				}
				catch (IOException ioe)
				{
					JOptionPane.showMessageDialog(null, "Erreur de nom de fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (o == boutonOk && filename.getText().length() != 0)
		{
			if (!this.loadFile(filename.getText(), (String) this.syntaxes.getSelectedItem()))
			{
				JOptionPane.showMessageDialog(
					this,
					this.getErrorMessage(),
					"Erreur",
					JOptionPane.ERROR_MESSAGE
				);
			}
			else
			{
				windowClosing(null);
			}
		}
		if (o == boutonAnnuler)
		{
			windowClosing(null);
		}
	}

	/**
	 * Return a error message.
	 * @return String
	 */
	public String getErrorMessage ()
	{
		return WordUtils.wrap(this.errorMessage, 80);
	}

	/**
	 * Initialize frame content.
	 */
	private void initFrame ()
	{
		// - Panel bouton du bas -

		JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		boutonsPanel.add(boutonOk);
		boutonsPanel.add(boutonAnnuler);

		// - Panel nom du fichier -

		JTextArea textAreaFilename = new JTextArea(
			"Indiquez le nom du fichier contenant les définitions du schéma LDAP que vous souhaitez charger."
		);
		textAreaFilename.setEditable(false);
		textAreaFilename.setLineWrap(true);
		textAreaFilename.setWrapStyleWord(true);
		textAreaFilename.setFont((new JLabel()).getFont());
		textAreaFilename.setBorder(BorderFactory.createEmptyBorder(7, 6, 12, 6));
		textAreaFilename.setBackground(new Color(240, 235, 226));

		boutonOpenFile.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 5, 0, 0, boutonsPanel.getBackground()),
				boutonOpenFile.getBorder()
			)
		);

		JPanel filenamePanel = new JPanel(new BorderLayout());
		filenamePanel.add(textAreaFilename, BorderLayout.NORTH);
		filenamePanel.add(filename, BorderLayout.CENTER);
		filenamePanel.add(boutonOpenFile, BorderLayout.EAST);
		filenamePanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 4, 1, 4),
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" Schéma LDAP "),
					BorderFactory.createEmptyBorder(0, 5, 5, 5 )
				)
			)
		);

		// - Panel du selecteur de syntaxes -

		JTextArea textAreaSyntaxes = new JTextArea(
			"Il vous faut appliquer un filtre sur le schéma que vous voulez charger. Voici la liste des filtres disponibles :"
		);
		textAreaSyntaxes.setEditable(false);
		textAreaSyntaxes.setLineWrap(true);
		textAreaSyntaxes.setWrapStyleWord(true);
		textAreaSyntaxes.setFont((new JLabel()).getFont());
		textAreaSyntaxes.setBorder(BorderFactory.createEmptyBorder(7, 6, 12, 6));
		textAreaSyntaxes.setBackground(new Color(240, 235, 226));

		JPanel syntaxesPanel = new JPanel(new BorderLayout());
		syntaxesPanel.add(textAreaSyntaxes, BorderLayout.NORTH);
		syntaxesPanel.add(syntaxes);
		syntaxesPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 4, 1, 4),
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(" Syntaxe LDAP "),
					BorderFactory.createEmptyBorder(0, 5, 5, 5)
				)
			)
		);

		// On injecte le nom des classes de syntaxes possibles
		// dynamiquement.
		String[] syntaxesName = Schema.getSyntaxeNames();
		for (int i = 0; syntaxesName != null && i < syntaxesName.length; i++)
		{
			syntaxes.addItem(syntaxesName[i]);
		}

		// - Organisation générale -

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(filenamePanel, BorderLayout.NORTH);
		mainPanel.add(syntaxesPanel, BorderLayout.CENTER);

		JPanel mainPanelContainer = new JPanel(new BorderLayout());
		mainPanelContainer.add(mainPanel, BorderLayout.NORTH);
		mainPanelContainer.add(boutonsPanel, BorderLayout.SOUTH);
		mainPanelContainer.setBorder(BorderFactory.createEmptyBorder(2, 1, 1, 1));

		getContentPane().add(
			new DescriptiveInternalFrame(
				mainFrame.getIconImage(),
				"Sélectionner un fichier et sa syntaxe.",
				mainPanelContainer
			)
		);

		// - Listeners -

		addWindowListener(this);
		boutonOpenFile.addActionListener(this);
		boutonOk.addActionListener(this);
		boutonAnnuler.addActionListener(this);
	}


	/**
	 * Load a file.
	 */
	public boolean loadFile ( String filename, String syntaxe )
	{
		if (!(new File(filename)).exists())
		{
			this.errorMessage = "Le fichier n'existe pas.";
			return false;
		}
		try
		{
			SchemaSyntax syntax = Schema.getSyntax(syntaxe);
			SchemaFile schemaFile = Schema.createAndLoad(syntax, filename, true);
			Schema schema = schemaFile.getSchema();
			if (schema == null)
			{
				String message = "";
				if (schemaFile.isError())
				{
					message += "\n\n"
						+  "Line " + schemaFile.getErrorLine() + ":\n"
						+  schemaFile.getErrorMessage();
				}
				this.errorMessage = "Le format du fichier est incorrect." + message;
				return false;
			}
			if (manager.isSchemaIdExists((new File(filename)).getName()))
			{
				this.errorMessage = "Le fichier est déjà ouvert.";
				return false;
			}
			Vector<String> files = Pref.getVector(Pref.PREF_LASTOPENFILES);
			Vector<String> syntaxes = Pref.getVector(Pref.PREF_LASTOPENSYNTAXES);
			int index = files.indexOf(filename);
			if (index >= 0)
			{
				files.removeElementAt(index);
				syntaxes.removeElementAt(index);
			}
			files.add(filename);
			syntaxes.add(syntaxe);
			if (files.size() > 10)
			{
				files.removeElementAt(0);
				syntaxes.removeElementAt(0);
			}
			Pref.set(Pref.PREF_LASTOPENFILES, files.toArray(new String[0]));
			Pref.set(Pref.PREF_LASTOPENSYNTAXES, syntaxes.toArray(new String[0]));
			manager.addSchema((new File(filename)).getName(), schema);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.errorMessage = "Une erreur inattendue est survenue.";
			return false;
		}
		return true;
	}

	public void windowActivated (WindowEvent e)
	{
	}

	public void windowClosed ( WindowEvent e )
	{
	}

 	public void windowClosing ( WindowEvent e )
	{
		this.setVisible(false);
	}

	public void windowDeactivated (WindowEvent e)
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
	}
}
