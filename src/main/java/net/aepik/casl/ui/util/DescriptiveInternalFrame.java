/*
 * descriptiveInternalFrame.java		0.1		06/06/2006
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


package net.aepik.casl.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DescriptiveInternalFrame extends JPanel {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Attributs
////////////////////////////////

	private String description ;
	private Image image ;
	private JPanel headerPanel ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public DescriptiveInternalFrame(
			Image image,
			String description,
			JComponent content ) {

		super( new BorderLayout() );
		this.image = image ;
		this.description = description ;

        JPanel headerPanel = buildHeaderPanel( image, description );
        add( headerPanel, BorderLayout.NORTH );

        if( content!=null ) {
            setContent( content );
        }
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

    public Component getContent() {
		return hasContent() ? getComponent(1) : null;
	}

	public void setContent( JComponent newContent ) {
		Component oldContent = getContent();
        if( hasContent() ) {
            remove( oldContent );
        }
        add( newContent, BorderLayout.CENTER );
        firePropertyChange( "content", oldContent, newContent );
    }

////////////////////////////////
// Methodes privées
////////////////////////////////

	private JPanel buildHeaderPanel( Image icon, String description ) {

		JLabel descPanelImage = new JLabel();
		if( icon!=null ) {
			Image imageTmp = icon;
			ImageIcon image = new ImageIcon( imageTmp );
			image = new ImageIcon( imageTmp.getScaledInstance(
					50*image.getIconWidth()/image.getIconHeight(), 50, Image.SCALE_SMOOTH ) );
			descPanelImage.setIcon( image );
		}

		JPanel descPanelImageContainer = new JPanel( new BorderLayout() );
		descPanelImageContainer.add( descPanelImage, BorderLayout.NORTH );
		descPanelImageContainer.setOpaque( false );

		JTextArea descPanelTexte = new JTextArea( description );
		descPanelTexte.setBorder( BorderFactory.createEmptyBorder( 10, 3, 3, 23 ) );
		descPanelTexte.setEditable( false );
		descPanelTexte.setFocusable( false );
		descPanelTexte.setFont( (new JLabel()).getFont() );
		descPanelTexte.setLineWrap( true );
		descPanelTexte.setOpaque( true );
		descPanelTexte.setWrapStyleWord( true );

		JPanel descPanel = new JPanel( new BorderLayout() );
		descPanel.add( descPanelTexte );
		descPanel.add( descPanelImageContainer, BorderLayout.EAST );
		descPanel.setBorder( BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.white ),
					BorderFactory.createMatteBorder( 0, 0, 1, 0, Color.gray ) ),
				BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );
		descPanel.setBackground( Color.white );

		return descPanel ;
	}

	private boolean hasContent() {
		return getComponentCount() > 1;
	}

}
