package logic;

import java.io.Serializable;

import model.Vertex;

public abstract class Item implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -335541443809781570L;

    protected int scoreValue;
    
    private String type;
    
    private String imgIcon;
    
    private Vertex location;
    
    public Item(int theValue, String theIcon, String theType, Vertex theVertex) {
        scoreValue = theValue;
        imgIcon = theIcon;
        type = theType;
        location = theVertex;
    }
    
    public int itemAction() {
        return 0;
    }
    
    public String getType() {
        return type;
    }
    
    public String getIcon() {
        return imgIcon;
    }
    
    protected void remove() {
        location.setItem(null);
    }

}
