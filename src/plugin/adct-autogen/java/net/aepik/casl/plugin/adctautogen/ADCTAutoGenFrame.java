/*
 * ADCTAutoGenTableModel.java		0.1		26/05/2006
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


package net.aepik.casl.plugin.adctautogen;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;
import net.aepik.casl.ui.util.DescriptiveInternalFrame;
import net.aepik.casl.ui.util.TableCellJComboBoxRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

public class ADCTAutoGenFrame extends JFrame
		implements ActionListener, WindowListener {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Constantes
////////////////////////////////

	/** Le bouton Ok **/
	public JButton boutonOk = new JButton( "Valider" );
	/** Le bouton Annuler **/
	public JButton boutonAnnuler = new JButton( "Annuler" );

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le schéma concerné **/
	private Schema schema ;
	/** La fenêtre parente **/
	private JFrame mainFrame ;

	/** Les modèles de données **/
	private ADCTAutoGenTableModel[] modeles ;
	/** Les valeurs possibles **/
	String[] values = new String[]{ "add", "modify" };

	/** Le système d'onglets **/
	private JTabbedPane onglets ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public ADCTAutoGenFrame( JFrame f, Schema s ) {

		super();
		setTitle( "Paramêtrer ChangeTypes" );
		//setModal( true );
		setSize( 400, 450 );
		setResizable( false );
		setLocationRelativeTo( f );

		if( f!=null )
			setIconImage( f.getIconImage() );

		mainFrame = f;
		schema = s;
		onglets = new JTabbedPane();

		SchemaObject[] objets = s.getObjects( schema.getSyntax().getObjectClassType() );
		SchemaObject[] attributs = s.getObjects( schema.getSyntax().getAttributeType() );

		modeles = new ADCTAutoGenTableModel[]{
			new ADCTAutoGenTableModel( objets, values[0] ),
			new ADCTAutoGenTableModel( attributs, values[0] )
		};

		initFrame();
	}

////////////////////////////////
// Méthodes publiques
////////////////////////////////

	/** Gère les actions sur les boutons Ok et Annuler.
	 * @param e Un événement de boutons.
	**/
    public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		if( o==boutonOk ) {

			boutonOk.setEnabled( false );
			SchemaSyntax syntax = schema.getSyntax();
			SchemaObject objets[] = schema.getObjects(syntax.getObjectClassType());
			SchemaObject attributs[] = schema.getObjects(syntax.getAttributeType());

			for( int i=0; objets!=null && i<objets.length; i++ )
				if( modeles[0].isCellEditable( i, 2 ) )
				{
					SchemaValue v = syntax.createSchemaValue( syntax.getObjectClassType(), "changetype", (String) modeles[0].getValueAt( i, 2 ) );
					objets[i].addValue( "changetype", v );
				}

			for( int i=0; attributs!=null && i<attributs.length; i++ )
				if( modeles[1].isCellEditable( i, 2 ) )
				{
					SchemaValue v = syntax.createSchemaValue( syntax.getAttributeType(), "changetype", (String) modeles[1].getValueAt( i, 2 ) );
					attributs[i].addValue( "changetype", v );
				}

			schema.notifyUpdates();
			windowClosing(null);

		} else if( o==boutonAnnuler )
			windowClosing(null);
	}

	public void windowActivated( WindowEvent windowevent ) {}
    public void windowClosed( WindowEvent windowevent ){}
    public void windowClosing( WindowEvent e ) { setVisible(false); }
    public void windowDeactivated( WindowEvent windowevent ) {}
    public void windowDeiconified( WindowEvent windowevent ) {}
    public void windowIconified( WindowEvent windowevent ) {}
    public void windowOpened( WindowEvent windowevent ) {}

////////////////////////////////
// Méthodes privées
////////////////////////////////

	private void initFrame() {

		// - Boutons -

		JPanel panelBoutons = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		panelBoutons.add( boutonOk );
		panelBoutons.add( boutonAnnuler );

		// - ComboBox pour les table -

		JComboBox changetypeComboBox = new JComboBox( values );

		// - Les tables -

		String[] titres = new String[]{ "Objets", "Attributs" };

		for( int i=0; i<modeles.length; i++ ) {

			JTable table = new JTable( modeles[i] );
			table.setTableHeader( null );
			table.removeEditor();
			table.setGridColor( table.getBackground() );
			table.setRowSelectionAllowed( false );

			table.getColumnModel().getColumn(0).setPreferredWidth( 100 );
			table.getColumnModel().getColumn(1).setPreferredWidth( 100 );
			table.getColumnModel().getColumn(2).setPreferredWidth( 100 );
			table.getColumnModel().getColumn(2).setCellEditor( new DefaultCellEditor( changetypeComboBox) );

			JPanel tablePanel = new JPanel();
			tablePanel.setBackground( table.getBackground() );
			tablePanel.add( table );

			JScrollPane tableScroller = new JScrollPane( tablePanel );
			tableScroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
			tableScroller.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
			tableScroller.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder( 11, 11, 11, 11 ),
						BorderFactory.createBevelBorder( BevelBorder.LOWERED, Color.lightGray, Color.darkGray ) ) );

			onglets.add( titres[i], tableScroller );

		}

		// - Panel de description -

		JTextArea textAreaDescription = new JTextArea(
				"Cet outils vous permet de gérér automatiquement les types de"
				+ " changement à considérer pour chacun des objets de ce schéma." );
		textAreaDescription.setEditable( false );
		textAreaDescription.setLineWrap( true );
		textAreaDescription.setWrapStyleWord( true );
		textAreaDescription.setFont( (new JLabel()).getFont() );
		textAreaDescription.setBorder( BorderFactory.createEmptyBorder( 7, 8, 12, 8 ) );
		textAreaDescription.setBackground( (new JLabel()).getBackground() );

		// - Design -

		onglets.setBorder( BorderFactory.createEmptyBorder( 0, 5, 1, 5 ) );
		onglets.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

		JPanel panelMain = new JPanel( new BorderLayout() );
		panelMain.setBorder( BorderFactory.createEmptyBorder( 2, 1, 1, 1 ) );
		panelMain.add( textAreaDescription, BorderLayout.NORTH );
		panelMain.add( onglets );
		panelMain.add( panelBoutons, BorderLayout.SOUTH );

		getContentPane().add( new DescriptiveInternalFrame(
				( mainFrame!=null ) ? mainFrame.getIconImage() : null,
				"Sélectionner les objets pour lesquels il faut gérer un ChangeType particulier.",
				panelMain ) );

		// - Listener -

		addWindowListener( this );
		boutonOk.addActionListener( this );
		boutonAnnuler.addActionListener( this );
	}
}
