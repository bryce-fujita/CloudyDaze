package logic;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import model.Vertex;

public class Player implements PropertyChangeEnabledPlayer {
    
    private Point myPosition;
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
    
    public void setPosition(Vertex theVertex) {
        myPcs.firePropertyChange(PROPERTY_POSITION, null, null);
        myVertex = theVertex;
    }
    
    public Vertex getVertex() {
        return myVertex;
    }
    public Point getPosition() {
        return new Point(myPosition);
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
