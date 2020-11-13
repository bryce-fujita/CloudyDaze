package model;

import java.util.HashSet;

public class Vertex {
    private HashSet<Edge> edges;
    private int row;
    private int col;
    boolean path = false;
    public Vertex(int theRow, int theCol) {
        row = theRow;
        col = theCol;
        edges = new HashSet<Edge>();
    }
    
    public HashSet<Edge> getEdges() {
        return edges;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
