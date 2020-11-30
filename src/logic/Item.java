package logic;

import model.Vertex;

public abstract class Item {
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
    
    protected void remove() {
        location.setItem(null);
    }

}
