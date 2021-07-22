package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class DarkJTable extends JTable {
	

	private static Color FOREGROUND_COLOR = new Color(0x008b8b);
	private static Color BACKGROUND_COLOR = new Color(0x262626);
	private static Color VIEWPORT_COLOR = new Color(0x2f2f2f);

	private static DefaultTableCellRenderer CELL_RENDERER = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isSelected) {
				c.setBackground(FOREGROUND_COLOR);
				c.setForeground(BACKGROUND_COLOR);
			}
			else {
				c.setBackground(BACKGROUND_COLOR);
				c.setForeground(FOREGROUND_COLOR);
			}

			return c;
		};
	};
	
	private static DefaultTableCellRenderer HEADER_CELL_RENDERER = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			c.setBackground(Color.BLACK);
			c.setForeground(FOREGROUND_COLOR);
			
			return c;
		};
	};
	public DarkJTable(TableModel model) {
		super(model);
		
		this.setAutoCreateColumnsFromModel(true);
		this.setFillsViewportHeight(true);
		this.setBackground(VIEWPORT_COLOR);
		this.setGridColor(Color.BLACK);
		
		JTableHeader header = this.getTableHeader();
		header.setDefaultRenderer(HEADER_CELL_RENDERER);
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);
		
		this.setDefaultRenderer(Object.class, CELL_RENDERER);

	}

}
