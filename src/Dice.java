// blueprint for dice game

// imports
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

// dice class
public class Dice {
	
	// fields
	public Player player;
	public int bet;
	public int betAmount;
	public int diceTotal;
	public boolean over;
	public ArrayList<Integer> diceValues = new ArrayList<Integer>(); 
	public TreeMap<Integer, Integer> multipliers;
	
	// constructor
	public Dice(Player player) {
		this.player = player;
		generateDice();
		multipliers = initializeMultipliers();
	}
	
	// generates the values of the 6 dice
	// parameters: none
	// return: void
	public void generateDice() {
		if (diceValues != null) {
			diceValues.clear();
		}
		for (int i = 0; i < 6; i++) {
			diceValues.add((int)(Math.random()*(6 - 1) + 1));
		}
		countTotal();
	}
	
	// counts the total of the dice values
	// parameters: none
	// return: void
	public int countTotal() {
		diceTotal = 0;
		for (int i = 0; i < 6; i++) {
			diceTotal += diceValues.get(i);
		}
		return diceTotal;
	}
	
	// gets over bets
	// parameters: none
	// return: void
	public void overBet() {
		
		// getting bet
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
		over = true;
		
		// getting dice amount
		boolean validInput2 = false;
		while (!validInput2) {
			try {
				betAmount = Integer.parseInt(JOptionPane.showInputDialog("Enter over number(6 - 35): "));
				if (betAmount > 5 && betAmount < 36) {
					validInput2 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		player.setBalance(player.getBalance() - bet);
		player.setWagered(player.getWagered() + bet);
	}
	
	// gets the under bet
	// parameters: none
	// return: void
	public void underBet() {
		
		// getting bet
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
		over = false;
		
		// getting dice amount
		boolean validInput2 = false;
		while (!validInput2) {
			try {
				betAmount = Integer.parseInt(JOptionPane.showInputDialog("Enter under number(7 - 36): "));
				if (betAmount > 7 && betAmount < 37) {
					validInput2 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		player.setBalance(player.getBalance() - bet);
		player.setWagered(player.getWagered() + bet);
	}
	
	// counts the total combinations for the multiplier
	// parameters: int number of dice, int number of sides, int target sum
	// return: long for number of combinations
	public static long countCombinations(int numDice, int numSides, int targetSum) {
        if (targetSum < numDice || targetSum > numDice * numSides) {
            return 0;
        }
        long[] dpPrev = new long[targetSum + 1];
        long[] dpCurr = new long[targetSum + 1];
        dpPrev[0] = 1;
        for (int die = 1; die <= numDice; die++) {
            for (int sum = die; sum <= die * numSides && sum <= targetSum; sum++) {
                for (int face = 1; face <= numSides; face++) {
                    if (sum - face >= 0) {
                        dpCurr[sum] += dpPrev[sum - face];
                    }
                }
            }
            System.arraycopy(dpCurr, 0, dpPrev, 0, dpPrev.length);
            Arrays.fill(dpCurr, 0);
        }
        return dpPrev[targetSum];
    }
	
	// initializes map of multipliers
	// parameters: none
	// return: treemap of multipliers
	public static TreeMap<Integer, Integer> initializeMultipliers(){
		TreeMap<Integer, Integer> mapa = new TreeMap<Integer, Integer>();
		for (int i = 6; i < 37; i++) {
			mapa.put(i, (int)(countCombinations(6, 6, i)));
		}
		return mapa;
	}
	
	// calculates multiplier
	// parameters: none
	// return: double multiplier
	public double calculateMultiplier() {
		int odds = 0;
		if (over) {
			for (int i = 36; i > betAmount; i--) {
				odds += multipliers.get(i);
			}
		} else {
			for (int i = 6; i < betAmount; i++) {
				odds += multipliers.get(i);
			}
		}
		return Math.round((odds/Math.pow(6, 6))*100.0)/100.0;
	}
	
	// calculates winnings
	// parameters: none
	// return: void
	public void calculateWinnings() {
		double multiplier = calculateMultiplier();
		player.setBalance(player.getBalance() + bet + (int)(bet * multiplier));
		player.setWins(player.getWins() + 1);
		player.setProfit(player.getBalance() - player.getInitialBalance());
	}
	
	//getters and setters
	public int getBets() {
		return bet;
	}
	
	public int getDiceTotal() {
		return diceTotal;
	}
	
}
