package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

@SuppressWarnings("serial")
public class DarkJScrollPane extends JScrollPane {
	public DarkJScrollPane(JComponent component) {
		super(component);
		
		ScrollBarUI scrollBarUI = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.BLACK;
				this.trackColor = new Color(0x262626);
				
			}
		

			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.BLACK);
				button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				return button;
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				button.setBackground(Color.BLACK);
				return button;
			}

		};
		
		this.getVerticalScrollBar().setUI(scrollBarUI);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}


}
