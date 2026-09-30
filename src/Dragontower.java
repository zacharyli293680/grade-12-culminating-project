// blueprint for dragontower game

// imports
import java.util.*;
import javax.swing.*;

// dragontower class
public class Dragontower extends Game {

	// fields
	private static final int ROWS = 9;
	private static final int COLS = 4;
	public Player player;
	public int bet = 0;
	public String difficulty;
	public ArrayList<ArrayList<Cell>> grid;
	private int mode; // safe cells per row: easy 3, medium 2, hard 1
	private int rowsCleared;
	private boolean inProgress;
	private Map<Integer, Odds> multipliers; // rows cleared -> odds (win : loss) for the current difficulty

	// constructor
	public Dragontower(Player player) {
		this.player = player;
		grid = new ArrayList<ArrayList<Cell>>();
	}

	// initialize the multiplier map for a difficulty: after k rows the fair multiplier is (4 / safe)^k
	// parameters: int safe cells per row
	// return: Map from rows cleared to odds
	public Map<Integer, Odds> initializeMultiplierArray (int mode){
		Map<Integer, Odds> multipliers = new HashMap<Integer, Odds>();
		for (int k = 0; k <= ROWS; k++) {
			multipliers.put(k, new Odds((int)(Math.pow(mode, k)), (int)(Math.pow(COLS, k))));
		}
		return multipliers;
	}

	// calculates the total payout (stake included) for the rows cleared so far
	// parameters: none
	// return: int winnings
	public int calcPayout() {
		Odds odds = multipliers.get(rowsCleared);
		long payout = ((long) bet * odds.getLoss()) / odds.getWin();
		return (int) Math.min(payout, Integer.MAX_VALUE);
	}

	// calculates the multiplier for display
	// parameters: none
	// return: double multiplier
	public double getMultiplier() {
		if (multipliers == null) {
			return 0;
		}
		Odds odds = multipliers.get(rowsCleared);
		return Math.round(odds.getLoss() * 100.0 / odds.getWin()) / 100.0;
	}

	// generates the grid for the game, with (4 - mode) eggs per row
	// paramters: none
	// return: none
	public void generateGrid() {
		grid.clear();
		int eggsPerRow = COLS - mode;
		for (int i = 0; i < ROWS; i++) {
			ArrayList<Integer> nums = new ArrayList<Integer>();
			for (int j = 0; j < COLS; j++) {
				nums.add(j);
			}
			Collections.shuffle(nums);
			Set<Integer> eggCols = new HashSet<Integer>(nums.subList(0, eggsPerRow));
			ArrayList<Cell> row = new ArrayList<Cell>();
			for (int j = 0; j < COLS; j++) {
				row.add(new Cell(j, i, eggCols.contains(j)));
			}
			grid.add(row);
		}
		rowsCleared = 0;
		inProgress = true;
	}

	// checks a cell in the current row; a safe cell moves the player up one row, an egg ends the game
	// paramters: int col of the clicked cell
	// return boolean for safe
	public boolean checkCell(int col) {
		Cell c = grid.get(getCurrentRow()).get(col);
		c.setChecked(true);
		if (c.getEgg()) {
			inProgress = false;
			return false;
		}
		rowsCleared++;
		if (rowsCleared == ROWS) {
			inProgress = false;
		}
		return true;
	}

	// pays out the current multiplier and ends the round
	// parameters: none
	// return: int payout
	public int cashOut() {
		int payout = calcPayout();
		player.recordWin(payout);
		inProgress = false;
		return payout;
	}

	// gets player bets and difficulty
	// parameters: none
	// return: boolean for whether a bet was placed
	public boolean getBet() {
		String[] options = {"Easy", "Medium", "Hard"};
		int difficult = JOptionPane.showOptionDialog(null, "Choose Difficulty", "Choose", 0, 3, null, options, options[0]);
		if (difficult < 0) {
			return false;
		}
		int amount = promptBet(player, "Enter bet: ");
		if (amount < 0) {
			return false;
		}
		if (difficult == 0) {
			difficulty = "easy";
			mode = 3;
		} else if (difficult == 1) {
			difficulty = "medium";
			mode = 2;
		} else {
			difficulty = "hard";
			mode = 1;
		}
		multipliers = initializeMultiplierArray(mode);
		bet = amount;
		return true;
	}

	// getters and setters
	public int getCellCol(int x) {
		x -= 450;
		return x / 106;
	}

	public int getCellRow(int y) {
		y -= 243;
		return y / 50;
	}

	public int getBetAmount() {
		return bet;
	}

	// the row the player must click next (8 = bottom, -1 once the tower is finished)
	public int getCurrentRow() {
		return ROWS - 1 - rowsCleared;
	}

	public int getRowsCleared() {
		return rowsCleared;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public boolean isFinished() {
		return rowsCleared == ROWS;
	}

}
