/*
 * SchemaValueEditorFrame.java		0.1		05/07/2006
 * 
 * Copyright (C) 2006 Thomas Chemineau
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

import net.aepik.casl.core.ldap.SchemaValue;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Fenetre d'edition de valeur d'un objet du schéma.
**/

public class SchemaValueEditorFrame
		extends
			JDialog
		implements
			ActionListener,
			ListSelectionListener
{

////////////////////////////////
// Attributs
////////////////////////////////

	private JButton boutonOk = new JButton( "Valider" );
	private JButton boutonAnnuler = new JButton( "Annuler" );
	private JButton boutonAjouter = new JButton( "Ajouter" );
	private JButton boutonSupprimer = new JButton( "Supprimer" );

	private SchemaValue value ;
	private Vector<String> values ;
	private JList valuesList ;
	private JTextField champsAddNewValue ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SchemaValueEditorFrame( SchemaValue v ) {

		super();
		setTitle( "Editer une valeur" );
		setModal( true );
		setResizable( false );
		setSize( 300, 260 );
		setLocationRelativeTo( null );

		value = v ;
		values = new Vector<String>();
		valuesList = new JList();
		champsAddNewValue = new JTextField();

		if( v!=null ) {
			String[] tmp = v.getValues();

			for( int i=0; tmp!=null && i<tmp.length; i++ )
				values.add( tmp[i] );
		}

		initFrame();
		updateList();
		updateButtons();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void addValidationListener( SchemaValueEditorLauncher l ) {
		boutonOk.addActionListener( l );
		addWindowListener( l );
	}

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		if( o==boutonAjouter ) {
			if( champsAddNewValue.getText().trim().length()==0 ) {
				JOptionPane.showMessageDialog(
					this,
					"Impossible d'ajouter une valeur nulle.",
					"Erreur",
					JOptionPane.ERROR_MESSAGE );
			} else if( values.contains( champsAddNewValue.getText() ) ) {
				JOptionPane.showMessageDialog(
					this,
					"Impossible d'ajouter une valeur déjà existante.",
					"Erreur",
					JOptionPane.ERROR_MESSAGE );
			} else {
				values.add( champsAddNewValue.getText() );
				updateList();
			}

		} else if( o==boutonSupprimer ) {
			values.remove( valuesList.getSelectedValue().toString() );
			updateList();

		} else if( o==boutonAnnuler ) {
			dispose();

		}
	}

	public String[] getFinalValues() {

		String[] result = new String[values.size()];
		Enumeration<String> elem = values.elements();

		for( int i=0; i<result.length; i++ )
			result[i] = elem.nextElement();

		return result ;
	}

	public void removeValidationListener( SchemaValueEditorLauncher l ) {
		boutonOk.removeActionListener( l );
		removeWindowListener( l );
	}

	public void valueChanged( ListSelectionEvent e ) {

		Object o = ( e!=null ) ? e.getSource() : null;
		if( o==valuesList ) {
			updateButtons();
		}
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	private void initFrame() {

		// - Panel d'informations -

		JTextArea textAreaValues = new JTextArea(
				"Pour ajouter une valeur, remplissez le champs ci-dessous"
				+ " puis cliquez sur \"Ajouter\". La nouvelle valeur sera"
				+ " visible dans la liste plus bas." );
		textAreaValues.setEditable( false );
		textAreaValues.setLineWrap( true );
		textAreaValues.setWrapStyleWord( true );
		textAreaValues.setFont( (new JLabel()).getFont() );
		textAreaValues.setBorder( BorderFactory.createEmptyBorder( 7, 8, 7, 8 ) );
		textAreaValues.setBackground( (new JLabel()).getBackground() );

		// - Panel d'ajout -

		JPanel addPanel = new JPanel( new BorderLayout() );
		addPanel.add( champsAddNewValue, BorderLayout.CENTER );
		addPanel.add( boutonAjouter, BorderLayout.EAST );
		addPanel.setBorder( BorderFactory.createEmptyBorder( 0, 6, 5, 6 ) );

		boutonAjouter.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder( 0, 5, 0, 0, textAreaValues.getBackground() ),
				boutonAjouter.getBorder() ) );

		// - Panel de boutons -

		JPanel boutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		boutonsPanel.add( boutonOk );
		boutonsPanel.add( boutonAnnuler );

		// - Panel liste des valeurs -

		valuesList.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
		valuesList.setVisibleRowCount( 4 );
		valuesList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		JPanel valeursBoutonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		valeursBoutonsPanel.add( boutonSupprimer );

		JScrollPane valeursScroller = new JScrollPane( valuesList );
		valeursScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		JPanel valeursPanel = new JPanel( new BorderLayout() );
		valeursPanel.add( valeursScroller, BorderLayout.CENTER );
		valeursPanel.add( valeursBoutonsPanel, BorderLayout.SOUTH );
		valeursPanel.setBorder( BorderFactory.createEmptyBorder( 0, 6, 11, 6 ) );

		// - Organisation générale -

		JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( addPanel, BorderLayout.NORTH );
		mainPanel.add( valeursPanel, BorderLayout.CENTER );

		JPanel mainPanelContainer = new JPanel( new BorderLayout() );
		mainPanelContainer.add( textAreaValues, BorderLayout.NORTH );
		mainPanelContainer.add( mainPanel, BorderLayout.CENTER );
		mainPanelContainer.add( boutonsPanel, BorderLayout.SOUTH );
		mainPanelContainer.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );

		getContentPane().add( mainPanelContainer );

		// - Listeners -

		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );
		boutonAjouter.addActionListener( this );
		boutonSupprimer.addActionListener( this );
		valuesList.addListSelectionListener( this );
	}

	private void updateButtons() {

		if( valuesList.getSelectedIndex()!=-1 ) {
			boutonSupprimer.setEnabled( true );

		} else {
			boutonSupprimer.setEnabled( false );
		}
	}

	private void updateList() {

		DefaultListModel model = new DefaultListModel();
		for( Enumeration<String> v = values.elements();
				v.hasMoreElements() ; ) {
			String val = v.nextElement();
			model.addElement( val );
		}

		valuesList.setModel( model );
		valuesList.repaint();
	}

}
