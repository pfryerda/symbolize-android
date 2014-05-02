public class Game {
	// Constant
	//public static final colour BACKGROUND_COLOR = color.WHITE;

	// Fields
	private Player player;
	private Level currLevel;
	// <private Canvas canvas;>
	// <private Paint paint;>

	// Consturctor
	public Game() {
		player = new Player();
		currLevel = null;
		// <canvas = new Canvas();>
		// <paint = new Paint();>
		// <set up paint i.e. stroke and such>
	}

	// Methods
	public void clearCanvas() { /* canvas.drawColor(BACKGROUND_COLOR) */ }
	public void setLevel(Level l) {
		currLevel = l;
		player.setGraph(l.getBoard());
		clearCanvas();
		// < drawGraph(); >
	}
	public void reset() {
		setLevel(currLevel);
	}
	public boolean checkSolution() {
		return currLevel.checkCorrectness(player.getGraph());
		/* Check last level      */
		/* setLevel(next level!) */
	}
	public void drawGraph() {
		for (Line l : player.getGraph()) {
			// paint.setColor(l.getColor())
			// <canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2()0.x(), l.getP2().y(), paint)>
		}
	}
	public void drawLine(Line l) {
		if (player.getLinesDrawn() < currLevel.getDrawRestirction()) {
			player.addLine(l);
			// paint.setColor(l.getColor())
			// <canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2()0.x(), l.getP2().y(), paint)>
		} else {
			// [Display error box]
		}
	}
	public void eraseLine(Line l) {
		if ((player.getLinesErased() < currLevel.getEraseRestirction()) || (l.getOwner() == Owner.User)) {
			player.removeLine(l);
			clearCanvas();
			// <canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2()0.x(), l.getP2().y(), paint)>
		} else {
			// [Display error box]
		}
	}
	public void undo() {
		if (player.getPastState() == null) {
			// [Displayer error box]
		} else {
			player = player.getPastState();
			clearCanvas();
			// drawGraph();
		}	
	}
	public void rotateRight() {
		if (currLevel.canRotate()) {
			player.rotateGraphR();
			// <Canvas rotate 90 degree animation>
		}
	}
	public void rotateLeft() {
		if (currLevel.canRotate()) {
			player.rotateGraphL();
			// <Canvas rotate -90 degree animation>
		}
	}
	public void flipHorizontally() {
		if (currLevel.canFlip()) {
			player.flipGraphH();
			// <Canvas scale negative animation>
		}
	}
	public void flipVertically() {
		if (currLevel.canFlip()) {
			player.flipGraphV();
			// <Canvas scale negative animation>
		}
	}
	public void shift() {
		if (currLevel.canShift()) {
			player.shiftGraph(currLevel.incIt());
			clearCanvas();
			// < drawGraph(); > or keep a backup of the bitmap and revert to backup
		}
	}
}