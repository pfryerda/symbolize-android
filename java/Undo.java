public class Undo {
	// Fields
	private final Move move;
	private final Line line; 	// If Move != (Draw || Erase), then Line == Dummy (null)

	// Constructors
	public Undo(Move m) {
		move = m;
		line =  null;
	}
	public Undo(Move m, Line l) {
		move = m;
		line = l;
	}

	// Methods
	public Move getMove() { return move; }
	public Line getLine() { return line; }
}