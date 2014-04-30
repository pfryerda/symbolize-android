public class Posn {
	// Fields
	private int first,second;

	// Constructors
	public Posn() {
		first = 0;
		second = 0;
	}
	public Posn(int x0, int y0) {
		first = x0;
		second = y0;
	}

	// Methods
	public int x() { return first; }
	public int y() { return second;}
	public boolean lt(Posn p) {						// Less than
		if (first != p.x()) return first < p.x();
		else		 		return second < p.y();
	}
	public int distSqr(Posn p) {
		return (first-p.x())*(first-p.x()) + (second-p.y())*(second-p.y());
	}
}