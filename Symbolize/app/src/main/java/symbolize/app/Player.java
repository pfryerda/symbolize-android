package symbolize.app;

import java.util.LinkedList;

public class Player {
    // Fields
    private LinkedList<Line> graph;
    private int linesDrawn, linesErased;
    private Player pastState;

    // Constructors
    public Player() {
        graph = new LinkedList<Line>();
        linesDrawn = 0;
        linesErased = 0;
        pastState = null;
    }
    public Player(LinkedList<Line> g, int ld, int le, Player ps) {
        graph = g;
        linesDrawn = ld;
        linesErased = le;
        pastState = ps;
    }

    // Methods
    public LinkedList<Line> getGraph() { return graph; }
    public int getLinesDrawn() 		   { return linesDrawn; }
    public int getLinesErased() 	   { return linesErased; }
    public Player getPastState() 	   { return pastState; }
    public void pushState() 		   { pastState = clone(); }
    public Player clone() {
        LinkedList<Line> clonedGraph = new LinkedList<Line>();
        for (Line l : graph) { clonedGraph.addLast(l.clone()); }
        return new Player(clonedGraph, linesDrawn, linesErased, pastState);
    }
    public void setGraph(LinkedList<Line> g) {
        graph.clear();
        for (Line l : g) graph.addLast(l.clone());
        linesDrawn = 0;
        linesErased = 0;
        pastState = null;
    }
    public void addLine(Line l) {
        pushState();
        graph.addLast(l);
        ++linesDrawn;
    }
    public void removeLine(Line l) {
        pushState();
        graph.remove(l);
        if (l.getOwner() == Owner.App) ++linesErased;
        else						   --linesDrawn;
    }
    public void rotateGraphR() {
        pushState();
        for (Line l : graph) l.rotateRight();
    }
    public void rotateGraphL() {
        pushState();
        for (Line l : graph) l.rotateLeft();
    }
    public void flipGraphH() {
        pushState();
        for (Line l : graph) l.flipH();
    }
    public void flipGraphV() {
        pushState();
        for (Line l : graph) l.flipV();
    }
    public void shiftGraph(LinkedList<Line> g) {
        pushState();
        for (Line l : g) graph.addLast(l.clone());
        linesDrawn = 0;
        linesErased = 0;
    }
}