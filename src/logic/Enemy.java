package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.Vertex;

public class Enemy extends Item {
    
    /** Question used. */
    private final String question;
    
    /** Correct answer. */
    private final String answer;
    
    /** Wrong Answer. */
    private final String wrong1;
    
    /** Wrong Answer. */
    private final String wrong2;
    
    /** Wrong Answer. */
    private final String wrong3;
    
    /** Number of points to reward player. */
    private final int points;
    
    /** THe number of points given. */
    private int returned;
    
    /**
     * Constructor (not the best).
     * @param location Location
     * @param value Points given
     * @param icon What icon to use
     * @param ques The Question
     * @param ans The Answer
     * @param w1 Wrong answer
     * @param w2 Wrong answer
     * @param w3 Wrong Answer
     */
    public Enemy(Vertex location, int value, String icon, String ques, String ans, String w1, String w2, String w3) {
        super(value, icon, "Enemy",location);
        points = value;
        question = ques;
        answer= ans;
        wrong1 = w1;
        wrong2 = w2;
        wrong3 = w3;
    }
    
    /**
     * Used to either reward a player with positive or negative points.
     */
    @Override
    public int itemAction() {
        //OpenQUESTION GUI...
        
        /*Question.createAndShowGUI((x) -> setReturn(x),
                question,
                answer,
                wrong1,
                wrong2,
                wrong3); */
        List<String> answers = new ArrayList<String>();
        answers.add(answer);
        answers.add(wrong1);
        answers.add(wrong2);
        answers.add(wrong3);
        
        String[] posans = new String [4];
        
        Random rand = new Random();
        int numAns = answers.size();
        for (int i = 0; i < numAns; i++) {
            int index = rand.nextInt(answers.size());
            posans[i] = answers.get(index);
            answers.remove(index);
        }
        
        String s = (String) JOptionPane.showInputDialog(null, question, "Answer the question", JOptionPane.PLAIN_MESSAGE, null, posans,posans[0]);
        
        returned = s.equals(answer) ? points : -points; 
        
        if (returned > 0) {
            remove();
        }
        return returned;
    }
    
    /** Sets what the returned value should be. */
    protected void setReturn(int i) {
        returned = i;
    }
}
