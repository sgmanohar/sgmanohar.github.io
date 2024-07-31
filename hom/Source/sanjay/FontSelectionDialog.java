/*
* @(#)FontSelection.java   1.2 98/07/31
*/

package sanjay;

import java.lang.Integer;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;

/*
 * This displays a String with the user's selected
 * fontname, style and size attributes.
*/

public class FontSelectionDialog extends JDialog implements ItemListener,
ActionListener{
		JLabel fontLabel, sizeLabel, styleLabel;
		FontPanel fontC;
		JComboBox fonts, sizes, styles;
		int index = 0;
		String fontchoice = "fontchoice";
		int stChoice = 0;
		String siChoice = "10";

		public void init() {

				getContentPane().setLayout( new BorderLayout() );

				JPanel topPanel = new JPanel();
				JPanel fontPanel = new JPanel();
				JPanel sizePanel = new JPanel();
				JPanel stylePanel = new JPanel();
				JPanel sizeAndStylePanel = new JPanel();

				topPanel.setLayout( new BorderLayout() );
				fontPanel.setLayout( new GridLayout( 2, 1 ) );
				sizePanel.setLayout( new GridLayout( 2, 1 ) );
				stylePanel.setLayout( new GridLayout( 2, 1 ) );
				sizeAndStylePanel.setLayout( new BorderLayout() );

				topPanel.add( BorderLayout.WEST, fontPanel );
				sizeAndStylePanel.add( BorderLayout.WEST, sizePanel );
				sizeAndStylePanel.add( BorderLayout.CENTER, stylePanel );
				topPanel.add( BorderLayout.CENTER, sizeAndStylePanel );

				getContentPane().add( BorderLayout.NORTH, topPanel );

				fontLabel = new JLabel();
				fontLabel.setText("Fonts");
				Font newFont = getFont();
				fontLabel.setFont(newFont);
				fontLabel.setHorizontalAlignment(JLabel.CENTER);
				fontPanel.add(fontLabel);

				sizeLabel = new JLabel();
				sizeLabel.setText("Sizes");
				sizeLabel.setFont(newFont);
				sizeLabel.setHorizontalAlignment(JLabel.CENTER);
				sizePanel.add(sizeLabel);

				styleLabel = new JLabel();
				styleLabel.setText("Styles");
				styleLabel.setFont(newFont);
				styleLabel.setHorizontalAlignment(JLabel.CENTER);
				stylePanel.add(styleLabel);

				GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
				String envfonts[] = gEnv.getAvailableFontFamilyNames();
				Vector vector = new Vector();
				for ( int i = 1; i < envfonts.length; i++ ) {
						vector.addElement(envfonts[i]);
				}
				fonts = new JComboBox( vector );
				fonts.setMaximumRowCount( 9 );
				fonts.addItemListener(this);
				fontchoice = getFont().getFontName();
				fonts.setSelectedItem(getFont().getFontName());
				fontPanel.add(fonts);

				sizes = new JComboBox( new Object[]{ "10", "12", "14", "18", "24", "48"} );
				sizes.setMaximumRowCount( 9 );
				sizes.addItemListener(this);
				sizes.setEditable(true);
				sizes.setSelectedItem(new Integer(getFont().getSize()));
				sizePanel.add(sizes);

				styles = new JComboBox( new Object[]{
																"PLAIN",
																"BOLD",
																"ITALIC",
																"BOLD & ITALIC"} );
				styles.setMaximumRowCount( 9 );
				styles.addItemListener(this);
				sizes.setMaximumRowCount( 9 );
				stylePanel.add(styles);

				fontC = new FontPanel();
				fontC.setBackground(Color.white);
				getContentPane().add( BorderLayout.CENTER, fontC);
		}

/*
 * Detects a state change in any of the Lists.  Resets the variable corresponding
 * to the selected item in a particular List.  Invokes changeFont with the currently
 * selected fontname, style and size attributes.
*/
		public void itemStateChanged(ItemEvent e) {
				if ( e.getStateChange() != ItemEvent.SELECTED ) {
						return;
				}

				Object list = e.getSource();

				if ( list == fonts ) {
						fontchoice = (String)fonts.getSelectedItem();
				} else if ( list == styles ) {
						index = styles.getSelectedIndex();
						stChoice = index;
				} else {
						siChoice = (String)sizes.getSelectedItem();
				}
				fontC.changeFont(fontchoice, stChoice, siChoice);
		}

		public FontSelectionDialog(){
			setSize(550,250);
			init();
			init2();
		}
		void init2(){
			JPanel bottom=new JPanel();
			getContentPane().add(bottom, BorderLayout.SOUTH);
			JButton ok=new JButton("OK");
			JButton cancel=new JButton("Cancel");
			bottom.add(ok);
			bottom.add(cancel);
			ok.addActionListener(this);
			cancel.addActionListener(this);
			setModal(true);
		}
		boolean OK_PRESSED=false;
		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand().equals("OK")){
				setFont(fontC.thisFont);
				OK_PRESSED=true;
				hide();
			}else if(e.getActionCommand().equals("Cancel")){
				hide();
			}
		}

}


class FontPanel extends JPanel {

		Font thisFont;

		public FontPanel(){
				thisFont = new Font("Arial", Font.PLAIN, 10);
		}

		// Resets thisFont to the currently selected fontname, size and style attributes.
		public void changeFont(String f, int st, String si){
				Integer newSize = new Integer(si);
				int size = newSize.intValue();
				thisFont = new Font(f, st, size);
				repaint();
		}

		public void paintComponent (Graphics g) {
				super.paintComponent( g );
				Graphics2D g2 = (Graphics2D) g;
				int w = getWidth();
				int h = getHeight();

				g2.setColor(Color.darkGray);
				g2.setFont(thisFont);
				String change = "Pick a font, size, and style to change me";
				FontMetrics metrics = g2.getFontMetrics();
				int width = metrics.stringWidth( change );
				int height = metrics.getHeight();
				g2.drawString( change, w/2-width/2, h/2-height/2 );
		}
}
