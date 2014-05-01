import java.util.LinkedList;

public class Symbolize {
	public static void main(String[] args) {
		// Build game
		Game game = new Game();

		// Build level 1
		LinkedList<Line> puzzle = new LinkedList<Line>();
		puzzle.addLast(new Line(new Posn(10, 10), new Posn(10, 10)));
		puzzle.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));

		LinkedList<LinkedList<Line>> solns = new LinkedList<LinkedList<Line>>();
		LinkedList<Line> soln = new LinkedList<Line>();
		soln.addLast(new Line(new Posn(10, 10), new Posn(10, 10)));
		soln.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));
		soln.addLast(new Line(new Posn(50, 50), new Posn(90, 90)));
		solns.addLast(soln);

		Level level = new Level(1, 1, puzzle, solns, 3, 0, true, true, true, null);

		// Load level 1-1
		game.setLevel(level);
		game.drawLine(new Line(new Posn(50, 50), new Posn(90, 90), Owner.User));	// Draw line
		game.drawLine(new Line(new Posn(30, 50), new Posn(90, 90), Owner.User));	// Draw line
		game.drawLine(new Line(new Posn(70, 50), new Posn(90, 90), Owner.User));	// Draw line
		game.undo();
		game.undo();
		System.out.println(game.checkSolution());									// Check solution
	}
}