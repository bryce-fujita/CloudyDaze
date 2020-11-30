package logic;

import model.Vertex;
import view.Question;

public class Enemy extends Item {
    
    String question;
    String answer;
    String wrong1;
    String wrong2;
    String wrong3;
    int returned;
    
    public Enemy(Vertex location, int value, String icon, String ques, String ans, String w1, String w2, String w3) {
        super(value, icon, "Enemy",location);
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
        
        Question.createAndShowGUI((x) -> setReturn(x));
        
        if (returned > 0) {
            remove();
        }
        return returned;
    }
    
    protected void setReturn(int i) {
        returned = i;
    }
}
