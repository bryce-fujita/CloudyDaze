package model;

import java.util.HashSet;

import logic.Item;

public class Vertex {
    private HashSet<Edge> edges;
    private int row;
    private int col;
    private Item myItem;
    boolean path = false;
    
    public Vertex(int theRow, int theCol) {
        row = theRow;
        col = theCol;
        edges = new HashSet<Edge>();
        myItem = null;
    }
    
    public HashSet<Edge> getEdges() {
        return edges;
    }
    
    public void setItem(Item theItem) {
        myItem = theItem;
    }
    
    public Item getItem() {
        return myItem;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
