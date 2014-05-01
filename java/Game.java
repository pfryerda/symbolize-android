public class Game {
	// Fields
	private Player player;
	private Level currLevel;
	// private Canvas canvas;

	// Consturctor
	public Game() {
		player = new Player();
		currLevel = new Level();
	}

	// Methods
	public void setLevel(Level l) {
		currLevel = l;
		player.setGraph(l.getBoard());
	}
	public boolean checkSolution() {
		return currLevel.checkCorrectness(player.getGraph());
		// Check last level
		// setLevel(next level!)
	}
	public void drawLine(Line l) {
		if (player.getLinesDrawn() < currLevel.getDrawRestirction()) {
			player.addLine(l);
			//draw line on canvas
		} else {
			// Display error box
		}
	}
	public void eraseLine(Line l) {
		if ((player.getLinesErased() < currLevel.getEraseRestirction()) || (l.getOwner() == Owner.User)) {
			player.removeLine(l);
			//erase line on canvas
		} else {
			// Display error box
		}
	}
	public void undo() {
		if (player.getPastState() == null) {
			// Displayer error box
		} else {
			player = player.getPastState();
			// redraw canvas
		}	
	}
	public void rotateRight() {
		if (currLevel.canRotate()) {
			// Canvas rotate 90 degree animation
			player.rotateGraphR();
		}
	}
	public void rotateLeft() {
		if (currLevel.canRotate()) {
			// Canvas rotate -90 degree animation
			player.rotateGraphL();
		}
	}
	public void flipHorizontally() {
		if (currLevel.canFlip()) {
			// Canvas scale negative animation
			player.flipGraphH();
		}
	}
	public void flipVertically() {
		if (currLevel.canFlip()) {
			// Canvas scale negative animation
			player.flipGraphV();
		}
	}

}