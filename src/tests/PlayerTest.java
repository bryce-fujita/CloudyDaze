package tests;

import static org.junit.Assert.*;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_POSITION;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_SCORE;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_WIN;

import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;

import logic.Coin;
import logic.Direction;
import logic.Player;
import model.Vertex;

public class PlayerTest {

    Player myPlayer;
    Vertex start;
    Vertex end;
    boolean mySeen;
    @Before
    public void setUp() throws Exception {
        myPlayer = new Player();
        start = new Vertex(0,0);
        end = new Vertex(1,1, true);
        myPlayer.setMove(start);
        end.setItem(new Coin(end));
    }

    @Test
    public void testIsMoving() {
        assertFalse("Testing stopped", myPlayer.isMoving());
        myPlayer.setMoving(true);
        assertTrue("Testing moving", myPlayer.isMoving());
        assertTrue("Testing Consistency", myPlayer.isMoving());
    }

    @Test
    public void testSetMoving() {
        assertFalse("Testing stopped", myPlayer.isMoving());
        myPlayer.setMoving(true);
        assertTrue("Testing moving", myPlayer.isMoving());
        myPlayer.setMoving(false);
        assertFalse("Testing stopped 2", myPlayer.isMoving());
        assertFalse("Testing consistency", myPlayer.isMoving());
    }
    

    @Test
    public void testSetDirection() {
        myPlayer.setDirection(Direction.UP);
        assertEquals("Testing Current Direction 1", Direction.UP, myPlayer.getDirection());
        myPlayer.setDirection(Direction.DOWN);
        assertEquals("Testing Current Direction 2", Direction.DOWN, myPlayer.getDirection());
        assertEquals("Testing Consistency", Direction.DOWN, myPlayer.getDirection());
    }

    @Test
    public void testGetDirection() {
        assertEquals("Testing Current Get Direction 1", Direction.DOWN, myPlayer.getDirection());
        myPlayer.setDirection(Direction.UP);
        assertEquals("Testing Current Get Direction 2", Direction.UP, myPlayer.getDirection());
        assertEquals("Testing Consistency", Direction.UP, myPlayer.getDirection());
    }

    @Test
    public void testSetMove() {
        PropertyChangeListener pcl =  theEvent -> {
            if (PROPERTY_POSITION.equals(theEvent.getPropertyName())) {
                assertEquals(end, myPlayer.getVertex());
            } 
        };
        myPlayer.addPropertyChangeListener(pcl);
        myPlayer.setMove(end);
        myPlayer.removePropertyChangeListener(pcl);
    }
    
    @Test
    public void testPlayerScores() {
        PropertyChangeListener pcl =  theEvent -> {
            if (PROPERTY_SCORE.equals(theEvent.getPropertyName())) {
                assertEquals((Integer) 5, (Integer) theEvent.getNewValue());
            } 
        };
        myPlayer.addPropertyChangeListener(pcl);
        myPlayer.setMove(end);
        myPlayer.removePropertyChangeListener(pcl);
    }
    
    @Test
    public void testPlayerWins() {
        mySeen = false;
        PropertyChangeListener pcl =  theEvent -> {
            if (PROPERTY_WIN.equals(theEvent.getPropertyName())) {
                mySeen = true;
            } 
        };
        myPlayer.addPropertyChangeListener(pcl);
        myPlayer.setMove(end);
        assertTrue("Testing for Vertex is end position", myPlayer.getVertex().isEnd());
        assertTrue("Testing for winning position", mySeen);
        myPlayer.removePropertyChangeListener(pcl);
    }

    @Test
    public void testGetVertex() {
        assertEquals("Testing current Vertex", start, myPlayer.getVertex());
        myPlayer.setMove(end);
        assertEquals("Testing end Vertex", end, myPlayer.getVertex());
    }

}
