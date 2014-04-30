public class Line {
	public static final int DRAWINGWIGGLEROOM = 14;

	// Fields
	private Posn p1, p2;
	private Owner owner;

	// Constructors
	public Line() {
		p1 = new Posn();
		p2 = new Posn();
		owner = Owner.App;
	}
	public Line(Posn pt1, Posn pt2) {
		if (pt1.lt(pt2)) {
			p1 = pt1;
			p2 = pt2;
		} else {
			p1 = pt2;
			p2 = pt1;
		}
		owner = Owner.App;
	}
	public Line(Posn pt1, Posn pt2, Owner creator) {
		if (pt1.lt(pt2)) {
			p1 = pt1;
			p2 = pt2;
		} else {
			p1 = pt2;
			p2 = pt1;
		}
		owner = creator;
	}

	// Methods
	public Posn getP1()      { return p1; }
	public Posn getP2()      { return p2; }
	public int score(Line l) { return p1.distSqr(l.getP1()) + p2.distSqr(l.getP2()); }
	public boolean eq(Line soln) {		// Approximatly Equals
		return (
			(((p1.x() - DRAWINGWIGGLEROOM) <= soln.getP1().x()) && (soln.getP1().x() <= (p1.x() + DRAWINGWIGGLEROOM))  &&
			 ((p1.y() - DRAWINGWIGGLEROOM) <= soln.getP1().y()) && (soln.getP1().y() <= (p1.y() + DRAWINGWIGGLEROOM))  &&
			 ((p2.x() - DRAWINGWIGGLEROOM) <= soln.getP2().x()) && (soln.getP2().x() <= (p2.x() + DRAWINGWIGGLEROOM))  &&
			 ((p2.y() - DRAWINGWIGGLEROOM) <= soln.getP2().y()) && (soln.getP2().y() <= (p2.y() + DRAWINGWIGGLEROOM))) ||
			(((p2.x() - DRAWINGWIGGLEROOM) <= soln.getP1().x()) && (soln.getP1().x() <= (p2.x() + DRAWINGWIGGLEROOM))  &&
			 ((p2.y() - DRAWINGWIGGLEROOM) <= soln.getP1().y()) && (soln.getP1().y() <= (p2.y() + DRAWINGWIGGLEROOM))  &&
			 ((p1.x() - DRAWINGWIGGLEROOM) <= soln.getP2().x()) && (soln.getP2().x() <= (p1.x() + DRAWINGWIGGLEROOM))  &&
			 ((p1.y() - DRAWINGWIGGLEROOM) <= soln.getP2().y()) && (soln.getP2().y() <= (p1.y() + DRAWINGWIGGLEROOM)))
			);
	} 
}