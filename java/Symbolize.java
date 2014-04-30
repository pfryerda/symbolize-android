import java.util.LinkedList;

public class Symbolize {
	public static void main(String[] args) {
		LinkedList<Line> puzzle = new LinkedList<Line>();
		puzzle.addLast(new Line(new Posn(10, 10), new Posn(10, 10)));
		puzzle.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));

		LinkedList<LinkedList<Line>> solns = new LinkedList<LinkedList<Line>>();
		LinkedList<Line> soln = new LinkedList<Line>();
		soln.addLast(new Line(new Posn(10, 10), new Posn(10, 10)));
		soln.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));
		soln.addLast(new Line(new Posn(50, 50), new Posn(90, 90)));
		solns.addLast(soln);

		Level level = new Level(puzzle, solns, 1, 0);

		LinkedList<Line> guess = new LinkedList<Line>();
		guess.addLast(new Line(new Posn(10, 10), new Posn(10, 10)));
		guess.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));
		guess.addLast(new Line(new Posn(50, 50), new Posn(90, 90)));

		System.out.println(level.checkSolution(guess));
	}
}