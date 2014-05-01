import java.util.LinkedList;

public class Level {
	// Fields
	private final LinkedList<Line> graph;
	private final LinkedList<LinkedList<Line>> solutions;
	private final int drawRestirction;
	private final int eraseRestirction;

	// Constructors
	public Level() {
		graph = new LinkedList<Line>();
		solutions = new LinkedList<LinkedList<Line>>();
		drawRestirction = 0;
		eraseRestirction = 0;
	}
	public Level(LinkedList<Line> g, LinkedList<LinkedList<Line>> s, int dr, int er) {
		graph = g;
		solutions = s;
		drawRestirction = dr;
		eraseRestirction = er;
	}

	// Methods
	public LinkedList<Line> getGraph() { return graph; }
	public int getDrawRestirction()    { return drawRestirction; }
	public int getEraseRestirction()   { return eraseRestirction; }
	public boolean checkCorrectness(LinkedList<Line> g) {
		for(LinkedList<Line> s : solutions) {
			if (s.size() == g.size()) {
				LinkedList<Line> soln = (LinkedList<Line>) s.clone();
				LinkedList<Line> guess = (LinkedList<Line>) g.clone();
				while(!soln.isEmpty()) {
					Line match = null;
					for (Line l : guess) {
						if (match == null) {
							if(l.eq(soln.getFirst())) match = l;
						} else {
							if(l.eq(soln.getFirst()) && (soln.getFirst().score(l) < soln.getFirst().score(match))) match = l;
						}
					}
					if (match == null) break;
					soln.removeFirst();
					guess.remove(match);
				}
				if (soln.isEmpty()) return true;
			}
		}
		return false;
	}
}