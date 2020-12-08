package model;

import java.io.Serializable;
import java.util.Random;

import logic.Direction;

public class Edge implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6305063974782922428L;
    Vertex start;
    public Vertex end;
    int weight;
    public Direction myDir;
    
    /**Normal constructor. */
    public Edge(Vertex theStart, Vertex theEnd, Direction theDir){
        start = theStart;
        end = theEnd;
        weight = new Random().nextInt(100);
        myDir = theDir;
    }
    
    public Edge(Vertex theStart, Vertex theEnd, int theWeight, Direction theDir){
        start = theStart;
        end = theEnd;
        weight = theWeight;
        myDir = theDir;
    }
    
     /** For opposite direction. */
    public Edge(Edge theEdge, Direction theDir) {
        start = theEdge.end;
        end = theEdge.start;
        weight = theEdge.weight;
        myDir = theDir;
    }
}
