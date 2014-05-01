import java.util.LinkedList;
import java.util.Stack;

public class Player {
	// Fields
	private LinkedList<Line> graph;
	private Stack<Move> undoStack;
	private int linesDrawn, linesErased;

	// Constructor
	public Player() {
		graph = new LinkedList<Line>();
		undoStack = new Stack<Move>();
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
		undoStack.push(new Move(Action.Draw, l));
	}
	public void removeLine(Line l) {
		graph.remove(l);
		if (l.getOwner() == Owner.App) ++linesErased;
		else						   --linesDrawn;
		undoStack.push(new Move(Action.Erase, l));
	}
	public void rotateGraphR() {
		for (Line l : graph) l.rotateRight();
		undoStack.push(new Move(Action.RotateR));
	}
	public void rotateGraphL() {
		for (Line l : graph) l.rotateLeft(); 
		undoStack.push(new Move(Action.RotateL));
	}
	public void flipGraphH() {
		for (Line l : graph) l.flipH(); 
		undoStack.push(new Move(Action.FlipH));
	}
	public void flipGraphV() {
		for (Line l : graph) l.flipV();
		undoStack.push(new Move(Action.FlipV));
	}
	public void goBack() { //goBack(Canvas c)
		if (undoStack.empty()) {
			// display error box
		} else {
			Move u = undoStack.peek();
			undoStack.pop();
			switch (u.getAction()) {
				case Draw:
					graph.remove(u.getLine());
					--linesDrawn;
					// remove line from canvas
					break;
				case Erase:
					graph.addLast(u.getLine());
					if (u.getLine().getOwner() == Owner.App) --linesErased;
					// add line to canvas (c)
					break;
				case RotateR:
					for (Line l : graph) l.rotateLeft();
					// Canvas rotate left
					// work in progress
					break;
				case RotateL:
					for (Line l : graph) l.rotateRight();
					// Canvas roate right
					// work in progress
					break;
				case FlipH:
					for (Line l : graph) l.flipH(); 
					// Canvas scale negative
					// work in progress
					break;
				case FlipV:
					for (Line l : graph) l.flipV(); 
					// Canvas scale negative
					// work in progress
					break;
			}
		}
	}
}