
package net.aepik.casl.ui;

import net.aepik.casl.core.Plugin ;
import net.aepik.casl.core.PluginManager ;

import java.awt.BorderLayout ;
import java.awt.Component ;
import java.awt.Font;
import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.JTable ;
import javax.swing.JTextArea ;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLDocument;

public class PluginManagerTable extends JTable {

	private PluginManager manager ;

	public PluginManagerTable( PluginManager m ) {

		super();

		manager = m;

		Plugin[] plugins = manager.getPlugins();
		Plugin[][] datas = new Plugin[plugins.length][1];
		for( int i=0; i<plugins.length; i++ )
			datas[i][0] = plugins[i];

		DefaultTableModel model = new DefaultTableModel( datas, new String[]{ "Plugin" } );
		setModel( model );
		setTableHeader( null );

		TableColumn colonne = getColumn( getColumnName( 0 ) );
		colonne.setCellRenderer( new PluginManagerTableCellRenderer() );
	}

	private class PluginManagerTableCellRenderer
			implements TableCellRenderer {

		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
		      	int row,
		      	int column ) {

			JPanel result = new JPanel( new BorderLayout() );
			result.setLayout( new BorderLayout() );

			Font styleSimple = (new JLabel( "font" )).getFont();
			Font styleGras = new Font( styleSimple.getName(),
					Font.BOLD, styleSimple.getSize() );

			if( value instanceof Plugin ) {
				Plugin plugin = (Plugin) value ;

				JLabel nameLabel = new JLabel( plugin.getName() );
				nameLabel.setFont( styleGras );
				JLabel categoryLabel = new JLabel( " [ " + plugin.getCategory() + " ]" );
				categoryLabel.setFont( styleSimple );

				JTextArea description = new JTextArea( plugin.getDescription() );
				description.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 0 ) );
				description.setEditable( false );
				description.setFont( styleSimple );
				description.setOpaque( true );
				description.setLineWrap( true );
				description.setWrapStyleWord( true );

				JPanel mainPanelHeader = new JPanel( new BorderLayout() );
				mainPanelHeader.add( nameLabel, BorderLayout.NORTH );
				mainPanelHeader.add( categoryLabel, BorderLayout.CENTER );

				if( isSelected || table.getSelectedRow()==row ) {
					description.setForeground( table.getSelectionForeground() );
					description.setBackground( table.getSelectionBackground() );
				} else {
					description.setForeground( (new JLabel()).getForeground() );
					description.setBackground( table.getBackground() );
				}
	
				result.add( mainPanelHeader, BorderLayout.NORTH );
				result.add( description, BorderLayout.CENTER );
	
			}

			return result ;
		}
	}

}
