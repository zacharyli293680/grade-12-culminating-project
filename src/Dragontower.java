// blueprint for dragontower game

// imports
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

// dragontower class
public class Dragontower {
	
	// fields
	public Player player;
	public int bet = 0;
	public String difficulty;
	public ArrayList<ArrayList<Cell>> grid;
	public int multiplier;
	private Map<Integer, Odds> easyMultipliers = initializeMultiplierArray(3);
	private Map<Integer, Odds> mediumMultipliers = initializeMultiplierArray(2);
	private Map<Integer, Odds> hardMultipliers = initializeMultiplierArray(1);
	
	// constructor
	public Dragontower(Player player) {
		this.player = player;
		grid = new ArrayList<ArrayList<Cell>>();
	}
	
	// initialize the multiplier maps
	// parameters: int difficulty
	// return: Map with all the odds
	public Map<Integer, Odds> initializeMultiplierArray (int mode){
		Map<Integer, Odds> multipliers = new HashMap<Integer, Odds>();
		for (int i = 0; i < 9; i++) {
			multipliers.put(8 - i, new Odds((int)(Math.pow(mode, i + 1)), (int)(Math.pow(4, i + 1))));
		}
		return multipliers;
	}
	
	// calculates the payout if player chashes out
	// paramters: int rank
	// return: int winnings
	public int calcPayout(int rank) {
		if (difficulty.equalsIgnoreCase("easy")) {
			return Game.calculateWinnings(bet, easyMultipliers.get(rank).getLoss(), easyMultipliers.get(rank).getWin());
		} else if (difficulty.equalsIgnoreCase("medium")) {
			return Game.calculateWinnings(bet, mediumMultipliers.get(rank).getLoss(), mediumMultipliers.get(rank).getWin());
		} else {
			return Game.calculateWinnings(bet, hardMultipliers.get(rank).getLoss(), 1);
		}
	}
	
	// calculates the multiplier for winnings calculation
	// parameters: int rank
	// return: double multiplier
	public double getMultiplier(int rank) {
		double multiplier;
		if (difficulty.equalsIgnoreCase("easy")) {
			multiplier = easyMultipliers.get(rank).getLoss() * 1.0 / easyMultipliers.get(rank).getWin();
		} else if (difficulty.equalsIgnoreCase("medium")) {
			multiplier = mediumMultipliers.get(rank).getLoss() * 1.0 / mediumMultipliers.get(rank).getWin();
		} else {
			multiplier = hardMultipliers.get(rank).getLoss() * 1.0;
		}
		return Math.round(multiplier * 100)/100.0;
	}
	
	// generates the grid for the game
	// paramters: none
	// return: none
	public void generateGrid() {
		if (grid != null) {
			grid.clear();
		}
		if (difficulty.equalsIgnoreCase("easy")) {
			for (int i = 0; i < 9; i++) {
				int eggCell = (int)(Math.random()*((3) + 1));
				ArrayList<Cell> row = new ArrayList<Cell>();
				for (int j = 0; j < 4; j++) {
					if(j == eggCell) {
						row.add(new Cell(j, i, true));
					} else {
						row.add(new Cell(j, i, false));
					}
				}
				grid.add(row);
			}
		} else if (difficulty.equalsIgnoreCase("medium")) {
			for (int i = 0; i < 9; i++) {
				ArrayList<Integer> nums = new ArrayList<Integer>();
				for (int j = 0; j < 4; j++) {
					nums.add(j);
				}
				Collections.shuffle(nums);
				ArrayList<Cell> row = new ArrayList<Cell>();
				for (int k = 0; k < 4; k++) {
					if (k == nums.get(0) || k == nums.get(1)) {
						row.add(new Cell(k, i, true));
					} else {
						row.add(new Cell(k, i, false));
					}
				}
				grid.add(row);
			}
		} else {
			for (int i = 0; i < 9; i++) {
				int notEggCell = (int)(Math.random() * ((3) + 1));
				ArrayList<Cell> row = new ArrayList<Cell>();
				for (int j = 0; j < 4; j++) {
					if(j == notEggCell){
						row.add(new Cell(j, i, false));
					} else {
						row.add(new Cell(j, i, true));
					}
				}
				grid.add(row);
			}
		}
		System.out.println(grid);
	}
	// checks a cell for egg 
	// paramters: int x, int y of mouse input
	// return boolean for egg
	public boolean checkCell(int y, int x) {
		grid.get(y).get(x).setChecked(true);
		Cell c = grid.get(y).get(x);
		if (c.getEgg()) {
			return false;
		}
		return true;
	}
	
	// gets player bets and difficulty
	// parameters: none
	// return: void
	public void getBet() {
		
		// for difficulty
		String[] options = {"Easy", "Medium", "Hard"};
		int difficult = JOptionPane.showOptionDialog(null, "Chose Difficulty", "Chose", 0, 3, null, options, options[0]);
		if (difficult == 0) {
			difficulty = "easy";
		} else if (difficult == 1) {
			difficulty = "medium";
		} else {
			difficulty = "hard";
		}
		
		// for bets
		boolean validInput = false;
		while (!validInput) {
			try {
				bet = Integer.parseInt(JOptionPane.showInputDialog("Enter bet: "));
				if (bet > 0 && bet < player.getBalance()) {
					validInput = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		player.setBalance(player.getBalance() - bet);
		player.setWagered(player.getWagered() + bet);
	}
	
	// getters and setters
	public int getCellCol(int x) {
		x -=450;
		return x / 106;
	}
	
	public int getCellRow(int y) {
		y -= 243;
		return y / 50;
	}
	
	public int getBetAmount() {
		return bet;
	}
	
}
