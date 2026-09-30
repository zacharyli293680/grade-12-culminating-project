// mines game blueprint

// imports
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

// mines class
public class Mines {
    
	// global variables
    public Player player;
    public ArrayList<Cell> grid;
    public ArrayList<Cell> checkCells = new ArrayList<Cell>(); 
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
        if (grid != null) {
            grid.clear();
        }
        ArrayList<Integer> diamondNums = generateDiamondCells(diamonds);
        for (int i = 1; i < 26; i++) {
            if (diamondNums.contains(i)) {
                grid.add(new Cell(i, true));
            } else {
                grid.add(new Cell(i, false));
            }
        }
        checkedCells = 0;
    }
    
    // calculates the multiplier for the winnings
    // parameters: String multiplier
    // return: void
    public void calculateMultiplier(String multi){
    	multi.trim();
    	double win = Double.parseDouble(multi.substring(0, multi.indexOf(":")));
    	double lose = Double.parseDouble(multi.substring(multi.indexOf(":") + 1));
    	multiplier = (int)(Math.round((lose/win) * 100.0))/100.0;
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
        ArrayList<Integer> diamondNums = new ArrayList<>();
        for (int i = 0; i < diamonds; i++) {
            diamondNums.add(nums.get(i));
        }
        return diamondNums;
    }
    
    // checks if the cell is a diamond or a mine
    // paramters: int cell number
    // return: boolean for mine or diamond
    public boolean checkCell(int cell) {
        Cell c = grid.get(cell - 1);
        grid.get(cell - 1).setChecked(true);
        if (c.getDiamond()) {
        	checkedCells++;
        	
            return true;
        }
        return false;
    }
    
    // gets the cell number clicked
    // parameters: int x, int y of mouse input
    // return: int for cell number
    public int getCellNum(int x, int y) {
        x -= 345;
        y -= 112;
        return (y / 124 * 5) + (x / 124 + 1);
    }

    // gets player bets and diamond number
    // parameters: void
    // return: void
    public void getPlayerBet() {
    	
    	// for bets
    	boolean validInput1 = false;
		while (!validInput1) {
			try {
				bet = Integer.parseInt(JOptionPane.showInputDialog("Enter bet: "));
				if (bet > 0 && bet < player.getBalance()) {
					validInput1 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		
		// for number of diamonds
		boolean validInput2 = false;
		while(!validInput2) {
			try {
				diamonds = Integer.parseInt(JOptionPane.showInputDialog("Number of diamonds: "));
				if (diamonds > 0 && diamonds < 25) {
					validInput2 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
        player.setBalance(player.getBalance() - bet);
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
