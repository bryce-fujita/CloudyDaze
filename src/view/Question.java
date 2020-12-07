package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class Question extends JPanel {
    
    /** Amount in Pixels for the Horizontal margin. */
    private static final int HORIZONATAL_MARGIN = 20; 
    
    /** Amount in Pixels for the Vertical margin. */
    private static final int VERTICALL_MARGIN = 10; 
    
    /** The number of rows. */
    private static final int ROW = 5;
    
    /** The number of columns. */
    private static final int COL = 1;
    
    public Question() {
        super(new GridLayout(ROW, COL, 0, HORIZONATAL_MARGIN));
        setBorder(BorderFactory.createEmptyBorder(VERTICALL_MARGIN, 
                                                  HORIZONATAL_MARGIN, 
                                                  VERTICALL_MARGIN, 
                                                  VERTICALL_MARGIN));
        //layoutComponents();
    }
    
    public static synchronized void createAndShowGUI(final IntConsumer theSet, String q,
            String a,
            String w1,
            String w2,
            String w3) {
        //Create and set up the window.
        final JFrame frame = new JFrame("Answer the question");
        
        //Put answer and questions into collection.
        List<String> answers = new ArrayList<String>();
        answers.add(a);
        answers.add(w1);
        answers.add(w2);
        answers.add(w3);
        
       
        //Create and set up the content pane.
        final Question pane = new Question();
        pane.setOpaque(true); //content panes must be opaque
        
        //Add the ColorSlider object itself as a PCL to the model.  
        frame.setContentPane(pane);
        
        //Add question to pane.
        JLabel question = new JLabel(q);
        pane.add(question);
        
        
        //Add answers to pane
        Random rand = new Random();
        int numAns = answers.size();
        for (int i = 0; i < numAns; i++) {
            System.out.println(answers.size());
            int index = rand.nextInt(answers.size());
            JButton ansLabel = new JButton(answers.get(index));
            ansLabel.addActionListener(ae -> {
                if (a.equals(ansLabel.getText())) {
                    theSet.accept(5);
                } else {
                    theSet.accept(-5);
                }
            });
            pane.add(ansLabel);
            answers.remove(index);
        }
        
        //Display the window.
        // position the frame in the center of the screen
        frame.pack();
        frame.setSize(400,400);
        frame.setVisible(true);
    }
    
}
