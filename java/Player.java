import java.util.LinkedList;
import java.util.Stack;

public class Player {
	// Fields
	private LinkedList<Line> graph;
	private Stack<Undo> undoStack;
	private int linesDrawn, linesErased;

	// Constructor
	public Player() {
		graph = new LinkedList<Line>();
		undoStack = new Stack<Undo>();
		linesDrawn = 0;
		linesErased = 0;
	}

	// Methods
	public LinkedList<Line> getGraph() { return graph; }
	public int getLinesDrawn() { return linesDrawn; }
	public int getLinesErased() { return linesErased; }
	public void setGraph(LinkedList<Line> g) {
		graph.clear();
		for (Line l : g) { graph.addLast(l.clone()); }
		undoStack.clear();
		linesDrawn = 0;
		linesErased = 0;
	}
	public void addLine(Line l) {
		graph.addLast(l);
		++linesDrawn;
		undoStack.push(new Undo(Move.Draw, l));
	}
	public void removeLine(Line l) {
		graph.remove(l);
		if (l.getOwner() == Owner.App) ++linesErased;
		else						   --linesDrawn;
		undoStack.push(new Undo(Move.Erase, l));
	}
	public void rotateGraphR() {
		for (Line l : graph) l.rotateRight();
	}
	public void rotateGraphL() {
		for (Line l : graph) l.rotateLeft(); 
	}
	public void flipGraphH() {
		for (Line l : graph) l.flipH(); 
	}
	public void flipGraphV() {
		for (Line l : graph) l.flipV();
	}
	public void goBack() { //goBack(Canvas c)
		if (undoStack.empty()) {
			// display error box
		} else {
			Undo u = undoStack.peek();
			undoStack.pop();
			switch (u.getMove()) {
				case Draw:
					graph.remove(u.getLine());
					--linesDrawn;
					// remove line from canvas
				case Erase:
					graph.addLast(u.getLine());
					if (u.getLine().getOwner() == Owner.App) --linesErased;
					// add line to canvas (c)
				case RotateR:
					// work in progress
				case RotateL:
					// work in progress
				case FlipH:
					// work in progress
				case FlipV:
					// work in progress
			}
		}
	}
}