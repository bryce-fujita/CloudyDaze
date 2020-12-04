package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.Vertex;
import view.Question;

public class Enemy extends Item {
    
    private String question;
    private String answer;
    private String wrong1;
    private String wrong2;
    private String wrong3;
    private int points;
    private int returned;
    
    public Enemy(Vertex location, int value, String icon, String ques, String ans, String w1, String w2, String w3) {
        super(value, icon, "Enemy",location);
        points = value;
        question = ques;
        answer= ans;
        wrong1 = w1;
        wrong2 = w2;
        wrong3 = w3;
    }
    //LMAO lambda expression idk?
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
    
    protected void setReturn(int i) {
        returned = i;
    }
}
