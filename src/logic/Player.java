package logic;


import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import model.Vertex;

public class Player implements PropertyChangeEnabledPlayer, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2299037678363512327L;
    private int score;
    private Direction myDir;
    private boolean moving;
    private PropertyChangeSupport myPcs;
    private Vertex myVertex;
    
    public Player () {
        score = 0;
        myDir = Direction.DOWN;
        moving = false;
        myPcs = new PropertyChangeSupport(this);
        myVertex = null;
    }
    
    public void setDirection(Direction theDir) {
        myDir = theDir;
    }
    
    public boolean isMoving() {
        return moving;
    }
    
    public void setMoving(boolean theValue) {
        moving = theValue;
    }
    
    public Direction getDirection() {
        return myDir;
    }
    
    public void setMove(Vertex theVertex) {
        if (theVertex.getItem() == null) {
            myVertex = theVertex;
            myPcs.firePropertyChange(PROPERTY_POSITION, null, null);
        } else {
            int value = theVertex.getItem().itemAction();
            myPcs.firePropertyChange(PROPERTY_SCORE, null, value);
            if (value > 0) {
                myVertex = theVertex;
                myPcs.firePropertyChange(PROPERTY_POSITION, null, null);
            }
        }
        if (theVertex.isEnd()) {
            myPcs.firePropertyChange(PROPERTY_WIN, null, null);
        }
    }
    
    public Vertex getVertex() {
        return myVertex;
    }
    

    @Override
    public void addPropertyChangeListener(PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(theListener);
        
    }

    @Override
    public void addPropertyChangeListener(String thePropertyName, PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(thePropertyName, theListener);
        
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(theListener);
        
    }

    @Override
    public void removePropertyChangeListener(String thePropertyName, PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(thePropertyName, theListener);
    }

}
