package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class Question extends JPanel {
    
    /** Amount in Pixels for the Horizontal margin. */
    private static final int HORIZONATAL_MARGIN = 20; 
    
    /** Amount in Pixels for the Vertical margin. */
    private static final int VERTICALL_MARGIN = 10; 
    
    /** The number of rows. */
    private static final int ROW = 4;
    
    /** The number of columns. */
    private static final int COL = 1;
    
    private final IntConsumer mySet;
    
    private final JFrame myFrame;
    
    public Question(final IntConsumer theSet, final JFrame frame) {
        super(new GridLayout(ROW, COL, 0, HORIZONATAL_MARGIN));
        setBorder(BorderFactory.createEmptyBorder(VERTICALL_MARGIN, 
                                                  HORIZONATAL_MARGIN, 
                                                  VERTICALL_MARGIN, 
                                                  VERTICALL_MARGIN));
        mySet = theSet;
        myFrame = frame;
        mySet.accept(0);
        //layoutComponents();
    }
    
    public static void createAndShowGUI(final IntConsumer theSet) {
        //Create and set up the window.
        final JFrame frame = new JFrame("Answer the question");
        
       
        //Create and set up the content pane.
        final Question pane = new Question(theSet, frame);
        pane.setOpaque(true); //content panes must be opaque
        
        //Add the ColorSlider object itself as a PCL to the model.  
        frame.setContentPane(pane);
        
        
        //Display the window.
        // position the frame in the center of the screen
        frame.pack();
        frame.setLocation(400,200);
        frame.setVisible(true);
    }
}
