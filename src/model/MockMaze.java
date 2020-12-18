package model;

import static logic.PropertyChangeEnabledPlayer.PROPERTY_POSITION;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_SCORE;
import static logic.PropertyChangeEnabledPlayer.PROPERTY_WIN;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import logic.Coin;
import logic.Direction;
import logic.Enemy;
import logic.Item;
import logic.Player;

import org.sqlite.SQLiteDataSource;

public class MockMaze implements PropertyChangeEnabledMaze, PropertyChangeListener, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3799202001320223164L;

    /** The percentage used to determine the number of coins. **/
    private static int coinPerc = 10; // EX 10 = 10%
    
    /** The number of rows in the maze. */
    private int numRows;
    
    /** The number of columns in the maze. */
    private int numCols;
    
    /** The number of points the player has. */
    private int myScore;
    
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
    public MockMaze(int rows, int cols) {
        numRows = 2;
        numCols = 2;
        myScore = 0;
        myPcs = new PropertyChangeSupport(this);
        myMatrix = new Vertex[rows][cols];
        constructMatrix();
        findPath();
        fillItemsMatrix();
        constructEnd();
        myCharMatrix = makeXMatrix();
        myPlayer = new Player();
        myPlayer.addPropertyChangeListener(this);
        myPlayer.setMove(myMatrix[0][0]);
        
    }
    
    private void constructEnd() {
        Vertex last = myMatrix[numRows-1][numCols-1];
        Vertex next = new Vertex(numRows, numCols-1, true);
        last.getEdges().add(new Edge(last, next, Direction.DOWN));
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

    /**
     * This constructs a maze that fills it with vertices.  Each
     * vertex will have a weighted edge that will connect it to its
     * adjacent vertices.
     */
    private void constructMatrix() {
        myMatrix = new Vertex[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                myMatrix[i][j] = new Vertex(i,j);
            }
        }
        myMatrix[0][0].getEdges().add(new Edge(myMatrix[0][0], myMatrix[1][0], Direction.DOWN));
        myMatrix[1][0].getEdges().add(new Edge(myMatrix[1][0], myMatrix[0][0], Direction.UP));
        myMatrix[0][0].getEdges().add(new Edge(myMatrix[0][0], myMatrix[0][1], Direction.RIGHT));
        myMatrix[0][1].getEdges().add(new Edge(myMatrix[0][1], myMatrix[0][0], Direction.LEFT));
        myMatrix[1][0].getEdges().add(new Edge(myMatrix[1][0], myMatrix[1][1], Direction.RIGHT));
        myMatrix[1][1].getEdges().add(new Edge(myMatrix[1][1], myMatrix[1][0], Direction.LEFT));
    }
    
    /**
     * Constructs a 2D char array that will be used to interpret
     * all the paths of maze.
     * @return A 2D char array
     */
    private char[][] makeXMatrix() {
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
                    xMatrix[xRow][xCol] = '+'; // Insert '+-' into the matrix if it's the solution path.
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
    
    public void fillItemsMatrix() {
        final int numSpots = numRows * numCols;
        final List<Integer> indexAvailable = new ArrayList<>();
        final Random rand = new Random();
        for (int i = 0; i < numSpots; i++) {  //by filtering out available locations, we can guarantee we won't have repeated locations
            indexAvailable.add(i);
        }
        //Needed to remove first and list index since this is the starting and ending location.
        indexAvailable.remove(indexAvailable.size()-1);// last location.
        indexAvailable.remove(0);// first location
        int numCoins = numSpots/coinPerc;
        for (int i = 0; i < numCoins; i++) {
            final int randomIndex = rand.nextInt(indexAvailable.size());
            final int arrayIndex = indexAvailable.get(randomIndex);
            indexAvailable.remove(randomIndex);
            final double rowLoc;
            if (arrayIndex % numRows == 0) {
                rowLoc = arrayIndex / numCols;
            } else {
                rowLoc = Math.ceil(arrayIndex / numCols);
            }
            final double colLoc = arrayIndex % numCols;
            Vertex location = myMatrix[(int) rowLoc][(int) colLoc];
            location.setItem(new Coin(location));
        }
        //FILLING ENEMIES USING SQLITE
        
        SQLiteDataSource ds = null;
        
        try {
            ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:trivia.db");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        System.out.println("Opened database successfully!");
        String query = "SELECT * FROM trivia";
        
        try ( Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement(); ) {
              
              ResultSet rs = stmt.executeQuery(query);
              
              //walk through each 'row' of results, grab data by column/field name
              // and print it
              while ( rs.next() && !indexAvailable.isEmpty()) {
                  String value = rs.getString( "VALUE" );
                  String img = rs.getString("ICON");
                  String question = rs.getString( "QUESTION" );
                  String answer = rs.getString( "ANSWER" );
                  String w1 = rs.getString("WRONG1");
                  String w2 = rs.getString("WRONG2");
                  String w3 = rs.getString("WRONG3");
                  final int randomIndex = rand.nextInt(indexAvailable.size());
                  final int arrayIndex = indexAvailable.get(randomIndex);
                  indexAvailable.remove(randomIndex);
                  final double rowLoc;
                  if (arrayIndex % numRows == 0) {
                      rowLoc = arrayIndex / numCols;
                  } else {
                      rowLoc = Math.ceil(arrayIndex / numCols);
                  }
                  final double colLoc = arrayIndex % numCols;
                  Vertex location = myMatrix[(int) rowLoc][(int) colLoc];
                  location.setItem(new Enemy(location,Integer.parseInt(value), img, question, answer, w1, w2, w3));
              }
          } catch ( SQLException e ) {
              e.printStackTrace();
              System.exit( 0 );
          }
    }

    /**
     * Allows access to the character array.
     * @return Returns character array
     */
    public char[][] getCharMatrix() {
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
    
    public List<Vertex> getItemLocations() {
        List<Vertex> returnMe = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Vertex location = myMatrix[i][j];
                if (location.getItem() != null) {
                    returnMe.add(location);
                }
            }
        }
        return returnMe;
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
        } else if(PROPERTY_SCORE.equals(evt.getPropertyName())) {
            myScore += (Integer) evt.getNewValue();
            myPcs.firePropertyChange(PROPERTY_SCORED, null, myScore);
        } else if(PROPERTY_WIN.equals(evt.getPropertyName())) {
            myPcs.firePropertyChange(PROPERTY_WON, null, myScore);
        }
    }
}
