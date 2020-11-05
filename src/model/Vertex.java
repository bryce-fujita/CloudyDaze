package model;

import java.util.HashSet;

public class Vertex {
    public HashSet<Edge> edges;
    public int row;
    public int col;
    boolean path = false;
    public Vertex(int theRow, int theCol) {
        row = theRow;
        col = theCol;
        edges = new HashSet<Edge>();
    }
    
    public HashSet<Edge> getEdges() {
        return edges;
    }
}
