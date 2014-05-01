public class Move {
	// Fields
	private final Action action;
	private final Line line; 	// If Action != (Draw || Erase), then Line == Dummy (null)

	// Constructors
	public Move(Action a) {
		action = a;
		line =  null;
	}
	public Move(Action a, Line l) {
		action = a;
		line = l;
	}

	// Methods
	public Action getAction() { return action; }
	public Line getLine() { return line; }
}