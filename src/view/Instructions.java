package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Instructions extends JPanel {
    
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 400;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1407855415284320483L;

    private Instructions() {
        super();
        layoutComponents();
    }
    
    private void layoutComponents() {
        JLabel img = new JLabel(new ImageIcon("icons//HOW_TO.png"));
        img.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.add(img);
    }
    
    public static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("How To Play");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   
        
        
        //Create and set up the content pane.
        final Instructions pane = new Instructions();
        
        pane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(pane);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setIconImage(new ImageIcon("icons//CloudTile.png").getImage());
        frame.setBackground(new Color(89, 205, 238));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
}
