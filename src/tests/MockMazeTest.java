package tests;

import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import model.MockMaze;

public class MockMazeTest {
    
    MockMaze myMock;
    
    String myCharMatrix;

    @Before
    public void setUp() throws Exception {
        myMock = new MockMaze(2,2);

    }

    @Test
    public void testGetCharMatrix() {
        String expected =
                "X   X X X \n" + 
                "X +     X \n" + 
                "X   X X X \n" + 
                "X +   + X \n" + 
                "X X X   X \n";
        char[][] xMatrix = myMock.getCharMatrix();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sb.append(xMatrix[i][j]);
                sb.append(' ');
            }
            sb.append('\n');
        }
        assertEquals("Testing character matrix", expected, sb.toString());
    }

    @Test
    public void testGetRows() {
        assertEquals("Testing Rows", 2, myMock.getRows());
    }

    @Test
    public void testGetCols() {
        assertEquals("Testing Cols", 2, myMock.getCols());
    }

    @Test
    public void testGetItemLocations() {
        assertFalse("Testing items", myMock.getItemLocations().isEmpty());
    }

}
