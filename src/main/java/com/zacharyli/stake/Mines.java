package com.zacharyli.stake;

// mines game blueprint

// imports
import java.util.*;

// mines class
public class Mines extends Game {

	// global variables
    public Player player;
    public ArrayList<Cell> grid;
    public Map<Integer ,Map<Integer, String>> multipliers = new HashMap<>();
    private int bet;
    private double multiplier = 0;
    public int checkedCells = 0;
    private int diamonds;

    // constructor
    public Mines(Player player) {
        this.player = player;
        this.grid = new ArrayList<Cell>();
        initMultiplierTable();
    }

    // generates the grids with the mines and diamonds
    public void generateGrid(int diamonds) {
        this.diamonds = diamonds;
        grid.clear();
        ArrayList<Integer> diamondNums = generateDiamondCells(diamonds);
        for (int i = 1; i < 26; i++) {
            grid.add(new Cell(i, diamondNums.contains(i)));
        }
        checkedCells = 0;
        multiplier = 0;
    }

    // calculates the profit multiplier from an odds string "favourable : unfavourable"
    // parameters: String odds
    // return: void
    public void calculateMultiplier(String multi){
    	multi = multi.trim();
    	double win = Double.parseDouble(multi.substring(0, multi.indexOf(":")).trim());
    	double lose = Double.parseDouble(multi.substring(multi.indexOf(":") + 1).trim());
    	multiplier = Math.round((lose / win) * 100.0) / 100.0;
    }

    // recalculates the multiplier for the number of diamonds found so far
    // parameters: none
    // return: void
    public void updateMultiplier() {
    	if (checkedCells > 0) {
    		calculateMultiplier(multipliers.get(checkedCells).get(25 - diamonds));
    	} else {
    		multiplier = 0;
    	}
    }

    // determines which cells will have diamonds
    // parameters: int number of diamonds
    // return: Arraylist of the cell numbers containing diamonds
    public ArrayList<Integer> generateDiamondCells(int diamonds) {
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i < 26; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);
        return new ArrayList<>(nums.subList(0, diamonds));
    }

    // checks whether a cell has already been revealed
    // parameters: int cell number
    // return: boolean
    public boolean isChecked(int cell) {
        return grid.get(cell - 1).getChecked();
    }

    // reveals a cell and reports whether it was a diamond
    // paramters: int cell number
    // return: boolean true for diamond, false for mine
    public boolean checkCell(int cell) {
        Cell c = grid.get(cell - 1);
        c.setChecked(true);
        if (c.getDiamond()) {
        	checkedCells++;
        	updateMultiplier();
            return true;
        }
        return false;
    }

    // total payout (stake + profit) for the diamonds found so far
    // parameters: none
    // return: int payout
    public int calcPayout() {
    	long payout = bet + Math.round(bet * multiplier);
    	return (int) Math.min(payout, Integer.MAX_VALUE);
    }

    // gets the cell number clicked
    // parameters: int x, int y of mouse input
    // return: int for cell number
    public int getCellNum(int x, int y) {
        x -= 345;
        y -= 112;
        return (y / 124 * 5) + (x / 124 + 1);
    }

    // gets player bet and diamond number
    // parameters: void
    // return: boolean for whether a bet was placed
    public boolean getPlayerBet() {
    	int number = promptInt("Number of diamonds (1 - 24): ", 1, 24);
    	if (number < 0) {
    		return false;
    	}
    	int amount = promptBet(player, "Enter bet: ");
    	if (amount < 0) {
    		return false;
    	}
    	diamonds = number;
    	bet = amount;
    	return true;
    }

    // method initializes the table of multipliers
    // parameters: none
    // return: void
    public void initMultiplierTable() {
        final int TOTAL_TILES = 25;
        Map<Integer, Map<Integer, String>> oddsMap = new HashMap<>();
        for (int d = 1; d <= 24; d++) {
            Map<Integer, String> innerMap = new HashMap<>();
            for (int m = 1; m <= 24; m++) {
                if (d + m <= TOTAL_TILES) {
                    long favorable = combination(TOTAL_TILES - m, d);
                    long total = combination(TOTAL_TILES, d);
                    long unfavorable = total - favorable;
                    if (total == 0) {
                        innerMap.put(m, "0 : 1");
                    } else {
                        long gcdVal = gcd(favorable, unfavorable);
                        long f = (gcdVal == 0) ? 0 : favorable / gcdVal;
                        long u = (gcdVal == 0) ? 1 : unfavorable / gcdVal;
                        innerMap.put(m, f + " : " + u);
                    }
                }
            }

            oddsMap.put(d, innerMap);
        }

        multipliers = oddsMap;
    }

    // combinations method
    // parameters: int n, int k for nCk
    // return: long for number
    private static long combination(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        if (k > n - k) {
            k = n - k;
        }
        long result = 1;
        for (int i = 1; i <= k; i++) {
            result *= (n - (k - i));
            result /= i;
        }
        return result;
    }

    // determines gcd of two numbers
    // paramters: long a, long b
    // return: long gcd
    private static long gcd(long a, long b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }
    // getters and setters
    public int getBet() {
    	return bet;
    }
    public int getDiamonds() {
        return diamonds;
    }

    public double getMultiplier() {
    	return multiplier;
    }

    public int getCheckedCells() {
    	return checkedCells;
    }

}
