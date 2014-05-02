import java.util.LinkedList;
import java.util.Iterator;

public class Level {
	// Fields
	private final int levelNum;
	private final int worldNum;
	private final LinkedList<Line> board;
	private final LinkedList<LinkedList<Line>> solutions;
	private final int drawRestirction;
	private final int eraseRestirction;
	private final boolean rotateEnabled;
	private final boolean flipEnabled;
	private final boolean colourEnabled;
	private final LinkedList<LinkedList<Line>> shiftGraphs; // shiftEnabled == (shiftGraph == null)
	private Iterator<LinkedList<Line>> shiftIterator;

	// Constructors
	public Level(int ln, int wn, LinkedList<Line> b, LinkedList<LinkedList<Line>> s, int dr, int er, boolean r, boolean f, boolean c, LinkedList<LinkedList<Line>> sg) {
		levelNum = ln;
		worldNum = wn;
		board = b;
		solutions = s;
		drawRestirction = dr;
		eraseRestirction = er;
		rotateEnabled = r;
		flipEnabled = f;
		colourEnabled = c;
		shiftGraphs = sg;
		if (shiftGraphs == null) shiftIterator = null;
		else {
			shiftIterator = sg.iterator();
			shiftIterator.next();
		}
	}

	// Methods
	public int getLevelNum()						 		   { return levelNum; }
	public int getWorldNum()		   						   { return worldNum; }
	public LinkedList<Line> getBoard() 						   { return board; }
	public int getDrawRestirction()    						   { return drawRestirction; }
	public int getEraseRestirction()   				 		   { return eraseRestirction; }
	public boolean canRotate()		 			    		   { return rotateEnabled; }
	public boolean canFlip()		  				 		   { return flipEnabled; }
	public boolean canChangeColur()   						   { return colourEnabled; }
	public boolean canShift()		   						   { return (shiftGraphs == null); }
	public LinkedList<LinkedList<Line>> getShiftGraphs()	   { return shiftGraphs; }
	public LinkedList<Line> incIt() { 
		if (shiftIterator.hasNext()) return shiftIterator.next(); 
		else {
			shiftIterator = shiftGraphs.iterator();
			return shiftIterator.next();
		}
	}
	public boolean checkCorrectness(LinkedList<Line> g) {
		for(LinkedList<Line> s : solutions) {
			if (s.size() == g.size()) {
				@SuppressWarnings("unchecked")
				LinkedList<Line> soln = (LinkedList<Line>) s.clone();
				@SuppressWarnings("unchecked")
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