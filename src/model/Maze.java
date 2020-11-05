package model;

import static model.PropertyChangeEnabledMaze.PROPERTY_PLAYER;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_POSITION;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.Timer;

import logic.Direction;
import logic.Player;

public class Maze implements PropertyChangeEnabledMaze, ActionListener, PropertyChangeListener {
    
    /** Set the delay for the timer. */
    private static int TIMER_DELAY = 80;
    
    private int numRows;
    private int numCols;
    private boolean dBug;
    private Vertex [][] myMatrix;
    private char[][] myCharMatrix;
    private PropertyChangeSupport myPcs;
    private Timer myTimer;
    private int myTime;
    private Player myPlayer;

    public Maze(int rows, int cols, boolean debug) {
        numRows = rows;
        numCols = cols;
        dBug = debug;
        myPcs = new PropertyChangeSupport(this);
        myMatrix = new Vertex[rows][cols];
        constructMatrix();
        findPath();
        myCharMatrix = makeXMatrix();
        myTimer = new Timer(TIMER_DELAY, this);
        myTimer.start();
        myTime = 0;
        myPlayer = new Player();
        myPlayer.addPropertyChangeListener(this);
        myPlayer.setPosition(myMatrix[0][0]);
    }

    private boolean recPath(Vertex theVertex,boolean[][] theVisited) {
        if ((theVertex.row == (numRows-1)) && (theVertex.col == (numCols-1))) {// end case
            theVertex.path = true;
            return true;
        }
        theVisited[theVertex.row][theVertex.col] = true;
        HashSet<Edge> edges = theVertex.edges;
        for (Edge edge : edges) {
            Vertex end = edge.end;
            if (!theVisited[end.row][end.col]) {
                boolean truePath = recPath(end, theVisited);
                if (truePath) {
                    theVertex.path = true;
                    return true;
                }
            }
        }
        return false;
    }

    private void findPath() {
        boolean[][] visited = new boolean[numRows][numCols];
        recPath(myMatrix[0][0], visited);
    }

    private Edge findEdge(Vertex look, Vertex find) {
        HashSet<Edge> set = look.edges;
        for (Edge temp : set) {
            if ((temp.start == look) && (temp.end == find)) {
                return temp;
            }
        }
        return null;
    }

    public Edge findLowestEdge(ArrayList<Vertex> theList, boolean[][] theSeenMatrix) { // Super important for prim's algorithm.
        if (theList.size() <= 0){
            System.out.println("list empty");
            return null;
        }
        Edge temp = null;
        for (int i = 0; i < theList.size(); i++) {
            Vertex v = theList.get(i);
            HashSet<Edge> edges = v.edges;
            for (Edge cur : edges) {
                if (!theSeenMatrix[cur.end.row][cur.end.col]) {
                    if (temp == null) temp = cur;
                    else if ((cur.weight < temp.weight))  {
                        temp = cur;
                    }
                }
            }
        }
        if (temp == null) {
            System.out.println("Temp was never assigned");
        }
        return temp;
    }

    private boolean checkBool(boolean[][] theVisited) {
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                if (!theVisited[i][j]) return false;
            }
        }
        return true;
    }

    private void debugOn(boolean[][] theVisited) {
        int xRows = (numRows*2)+1;
        int xCols = (numCols*2)+1;
        char[][] xMatrix = new char[xRows][xCols];
        for (int i = 0; i < xRows; i++) { // Constructs an array full of Xs to be changed
            for (int j = 0; j < xCols; j++) {
                if ((i%2 == 1) && (j%2 == 1) && (i != xRows)) {
                    xMatrix[i][j] = ' ';
                } else {
                    xMatrix[i][j] = 'X';
                }
            }
        }
        xMatrix[0][1] = ' ';
        xMatrix[xRows-1][xCols-2] = ' ';
        /// Changing known vertexes for ' ' and the appropriate path
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (theVisited[i][j] == true) { //Show a V on xMatrix
                    int xRow = (i * 2) + 1;
                    int xCol = (j * 2) + 1;
                    xMatrix[xRow][xCol] = 'V'; // Insert V into the matrix
                    HashSet<Edge> edges = myMatrix[i][j].edges;
                    for (Edge edge : edges) {
                        int startRow = edge.start.row;
                        int startCol = edge.start.col;
                        int endRow = edge.end.row;
                        int endCol = edge.end.col;
                        int difRow = (startRow-endRow); //Ex Edge to Below: (2,1)-(2,2) = (0,-1)
                        int difCol = (startCol-endCol);
                        xMatrix[xRow-difRow][xCol-difCol] = ' ';
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xRows; i++) {
            for (int j = 0; j < xCols; j++) {
                sb.append(xMatrix[i][j]);
                sb.append(' ');
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    private void primTheTree(Vertex[][] theMatrix) {
        boolean[][] visited = new boolean[numRows][numCols];
        visited[0][0] = true;
        boolean seenAll = false;
        myMatrix[0][0] = new Vertex(0,0);
        while (!seenAll) {
            ArrayList<Vertex> temp = new ArrayList<Vertex>();
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (visited[i][j] == true) {
                        temp.add(theMatrix[i][j]);
                    }
                }
            }
            Edge lowestEdge = findLowestEdge(temp,visited);
            if(lowestEdge == null) {
                System.out.println("SOmehow we get lols nulls");
            }
            Vertex start = lowestEdge.start;
            Vertex end = lowestEdge.end;
            int weight = lowestEdge.weight;
            Direction dir = lowestEdge.myDir;
            myMatrix[end.row][end.col] = new Vertex(end.row,end.col);
            myMatrix[end.row][end.col].edges.add(new Edge(myMatrix[end.row][end.col], myMatrix[start.row][start.col], weight, Direction.reverseValueOf(dir)));
            myMatrix[start.row][start.col].edges.add(new Edge(myMatrix[start.row][start.col], myMatrix[end.row][end.col], weight, dir));
            visited[end.row][end.col] = true;
            seenAll = checkBool(visited);
            if (dBug) {
                debugOn(visited);
            }
        }
    }

    private void constructMatrix() {
        Vertex [][] temp = new Vertex[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                temp[i][j] = new Vertex(i,j);
            }
        }
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                Vertex cur = temp[i][j];
                if (numCols > (j + 1)) { //Checking for right vertex;
                    Vertex toRight = temp[i][j+1];
                    temp[i][j].edges.add(new Edge(cur,toRight, Direction.RIGHT));
                }
                if (numRows > (i + 1)) { //Checking for below vertex;
                    Vertex toBelow = temp[i+1][j];
                    temp[i][j].edges.add(new Edge(cur,toBelow, Direction.DOWN));
                }
                if (-1 < (j - 1)) { //Checking for left vertex;
                    Vertex toLeft = temp[i][j-1];
                    Edge exist = findEdge(toLeft, cur);
                    temp[i][j].edges.add(new Edge(exist, Direction.LEFT));
                }
                if (-1 < (i - 1)) { //Checking for above vertex;
                    Vertex toAbove = temp[i-1][j];
                    Edge exist = findEdge(toAbove, cur);
                    temp[i][j].edges.add(new Edge(exist, Direction.UP));
                }
            }
        }
        primTheTree(temp);
    }
    

    public char[][] makeXMatrix() {
        int xRows = (numRows*2)+1;
        int xCols = (numCols*2)+1;
        char[][] xMatrix = new char[xRows][xCols];
        for (int i = 0; i < xRows; i++) { // Constructs an array full of Xs to be changed
            for (int j = 0; j < xCols; j++) {
                if ((i%2 == 1) && (j%2 == 1) && (i != xRows)) {
                    xMatrix[i][j] = ' ';
                } else {
                    xMatrix[i][j] = 'X';
                }
            }
        }
        xMatrix[0][1] = ' ';
        xMatrix[xRows-1][xCols-2] = ' ';
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int xRow = (i * 2) + 1;
                int xCol = (j * 2) + 1;
                if (myMatrix[i][j].path) {
                    xMatrix[xRow][xCol] = '+'; // Insert '+' into the matrix if it's the solution path.
                } else {
                    xMatrix[xRow][xCol] = ' '; // Insert ' ' into the matrix
                }
                HashSet<Edge> edges = myMatrix[i][j].edges;
                for (Edge edge : edges) {
                    int startRow = edge.start.row;
                    int startCol = edge.start.col;
                    int endRow = edge.end.row;
                    int endCol = edge.end.col;
                    int difRow = (startRow-endRow); //Ex Edge to Below: (2,1)-(2,2) = (0,-1) need inverse (0,1)
                    int difCol = (startCol-endCol);
                    xMatrix[xRow-difRow][xCol-difCol] = ' ';
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xRows; i++) {
            for (int j = 0; j < xCols; j++) {
                sb.append(xMatrix[i][j]);
                sb.append(' ');
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
        return xMatrix;
    }

    public char[][] getMatrix() {
        return myCharMatrix;
    }
    
    public int getRows() {
        return numRows;
    }
    
    public int getCols() {
        return numCols;
    }
    
    public Player getPlayer() {
        return myPlayer;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        myTime = myTime + 1;
        myPcs.firePropertyChange(PROPERTY_TIME, null, myTime);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROPERTY_POSITION.equals(evt.getPropertyName())) {
            myPcs.firePropertyChange(PROPERTY_PLAYER, null, null);
        }
    }
}
