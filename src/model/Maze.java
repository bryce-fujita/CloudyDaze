package model;

import static logic.PropertyChangeEnabledPlayer.PROPERTY_POSITION;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;

import logic.Direction;
import logic.Player;

public class Maze implements PropertyChangeEnabledMaze, PropertyChangeListener {
    
    /** The number of rows in the maze. */
    private int numRows;
    
    /** The number of columns in the maze. */
    private int numCols;
    
    /** A 2D array to store all of the vertices. */
    private Vertex [][] myMatrix;
    
    /** A 2D array that represents all possible paths. */
    private char[][] myCharMatrix;
    
    /**Used for notifying listeners of any changes. */
    private PropertyChangeSupport myPcs;
    
    /** A class used to keep track of the players current status. */
    private Player myPlayer;

    /** 
     * Constructor for Maze Model.
     * @param rows The number of rows in the maze
     * @param cols The number of columns in the maze
     * @param debug A tool used to show the solution path to the end
     */
    public Maze(int rows, int cols, boolean debug) {
        numRows = rows;
        numCols = cols;
        myPcs = new PropertyChangeSupport(this);
        myMatrix = new Vertex[rows][cols];
        constructMatrix();
        findPath();
        myCharMatrix = makeXMatrix();
        myPlayer = new Player();
        myPlayer.addPropertyChangeListener(this);
        myPlayer.setPosition(myMatrix[0][0]);
    }

    /**
     * A recursive method to find the solution path.
     * @param theVertex The starting vertex
     * @param theVisited The 
     * @return Return true if the path is a correct path.  False if there is no feasible way to the solution.
     */
    private boolean recPath(Vertex theVertex,boolean[][] theVisited) {
        if ((theVertex.getRow() == (numRows-1)) && (theVertex.getCol() == (numCols-1))) {// end case / last vertex
            theVertex.path = true;
            return true;
        }
        theVisited[theVertex.getRow()][theVertex.getCol()] = true;
        HashSet<Edge> edges = theVertex.getEdges();
        for (Edge edge : edges) {
            Vertex end = edge.end;
            if (!theVisited[end.getRow()][end.getCol()]) {
                boolean truePath = recPath(end, theVisited);
                if (truePath) {
                    theVertex.path = true;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * A method that calls the recursive method to finding the path.
     */
    private void findPath() {
        boolean[][] visited = new boolean[numRows][numCols];
        recPath(myMatrix[0][0], visited);
    }

    /**
     * Returns the edge between two vertices.
     * @param look The starting edge
     * @param find The destination edge
     * @return
     */
    private Edge findEdge(Vertex look, Vertex find) {
        HashSet<Edge> set = look.getEdges();
        for (Edge temp : set) {
            if ((temp.start == look) && (temp.end == find)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Used in prims algorithm for finding the smallest weighted edge
     * @param theList An array list of vertex's that have been currently travelled to
     * @param theSeenMatrix An array booleans that shows on the graph which vertexes have been seen
     * @return The Edge with the lowest weight
     */
    public Edge findLowestEdge(ArrayList<Vertex> theList, boolean[][] theSeenMatrix) { // Super important for prim's algorithm.
        if (theList.size() <= 0){
            System.out.println("list empty");
            return null;
        }
        Edge temp = null;
        for (int i = 0; i < theList.size(); i++) {
            Vertex v = theList.get(i);
            HashSet<Edge> edges = v.getEdges();
            for (Edge cur : edges) {
                if (!theSeenMatrix[cur.end.getRow()][cur.end.getCol()]) {
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

    /**
     * Checks to see if every vertex has been seen.  Used to
     * determine if to continue Prim's algorithm.
     * @param theVisited A 2D array which shows all the visited locations
     * @return False if a vertex hasn't been seen and true if all have been seen.
     */
    private boolean checkBool(boolean[][] theVisited) {
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                if (!theVisited[i][j]) return false;
            }
        }
        return true;
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
            myMatrix[end.getRow()][end.getCol()] = new Vertex(end.getRow(),end.getCol());
            myMatrix[end.getRow()][end.getCol()].getEdges().add(new Edge(myMatrix[end.getRow()][end.getCol()], myMatrix[start.getRow()][start.getCol()], weight, Direction.reverseValueOf(dir)));
            myMatrix[start.getRow()][start.getCol()].getEdges().add(new Edge(myMatrix[start.getRow()][start.getCol()], myMatrix[end.getRow()][end.getCol()], weight, dir));
            visited[end.getRow()][end.getCol()] = true;
            seenAll = checkBool(visited);
        }
    }

    /**
     * This constructs a maze that fills it with vertices.  Each
     * vertex will have a weighted edge that will connect it to its
     * adjacent vertices.
     */
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
                    temp[i][j].getEdges().add(new Edge(cur,toRight, Direction.RIGHT));
                }
                if (numRows > (i + 1)) { //Checking for below vertex;
                    Vertex toBelow = temp[i+1][j];
                    temp[i][j].getEdges().add(new Edge(cur,toBelow, Direction.DOWN));
                }
                if (-1 < (j - 1)) { //Checking for left vertex;
                    Vertex toLeft = temp[i][j-1];
                    Edge exist = findEdge(toLeft, cur);
                    temp[i][j].getEdges().add(new Edge(exist, Direction.LEFT));
                }
                if (-1 < (i - 1)) { //Checking for above vertex;
                    Vertex toAbove = temp[i-1][j];
                    Edge exist = findEdge(toAbove, cur);
                    temp[i][j].getEdges().add(new Edge(exist, Direction.UP));
                }
            }
        }
        primTheTree(temp);
    }
    
    /**
     * Constructs a 2D char array that will be used to interpret
     * all the paths of maze.
     * @return A 2D char array
     */
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
                HashSet<Edge> edges = myMatrix[i][j].getEdges();
                for (Edge edge : edges) {
                    int startRow = edge.start.getRow();
                    int startCol = edge.start.getCol();
                    int endRow = edge.end.getRow();
                    int endCol = edge.end.getCol();
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

    /**
     * Allows access to the character array.
     * @return Returns character array
     */
    public char[][] getMatrix() {
        return myCharMatrix;
    }
    
    /**
     * The number of rows the maze has.
     * @return Returns number of rows the maze has
     */
    public int getRows() {
        return numRows;
    }
    
    /**
     * The number of columns the array has.
     * @return Returns number of columns the array has
     */
    public int getCols() {
        return numCols;
    }
    
    /**
     * Allows access to the player model.
     * @return Returns the player model.
     */
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
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROPERTY_POSITION.equals(evt.getPropertyName())) {
            myPcs.firePropertyChange(PROPERTY_PLAYER, null, null);
        }
    }
}
