// blueprint for dice game

// imports
import java.util.*;

// dice class
public class Dice extends Game {

	// fields
	private static final int NUM_DICE = 6;
	private static final int NUM_SIDES = 6;
	private static final double TOTAL_OUTCOMES = Math.pow(NUM_SIDES, NUM_DICE);
	public Player player;
	public int bet;
	public int betAmount;
	public int diceTotal;
	public boolean over;
	public ArrayList<Integer> diceValues = new ArrayList<Integer>();
	public TreeMap<Integer, Integer> multipliers; // number of ways to roll each total

	// constructor
	public Dice(Player player) {
		this.player = player;
		multipliers = initializeMultipliers();
		generateDice();
	}

	// generates the values of the 6 dice
	// parameters: none
	// return: void
	public void generateDice() {
		diceValues.clear();
		for (int i = 0; i < NUM_DICE; i++) {
			diceValues.add((int)(Math.random() * NUM_SIDES) + 1);
		}
		countTotal();
	}

	// counts the total of the dice values
	// parameters: none
	// return: int total
	public int countTotal() {
		diceTotal = 0;
		for (int i = 0; i < NUM_DICE; i++) {
			diceTotal += diceValues.get(i);
		}
		return diceTotal;
	}

	// gets an over bet (wins when the total is strictly over the chosen number)
	// parameters: none
	// return: boolean for whether a bet was placed
	public boolean overBet() {
		int number = promptInt("Enter over number (6 - 35): ", 6, 35);
		if (number < 0) {
			return false;
		}
		int amount = promptBet(player, "Enter bet: ");
		if (amount < 0) {
			return false;
		}
		over = true;
		betAmount = number;
		bet = amount;
		return true;
	}

	// gets an under bet (wins when the total is strictly under the chosen number)
	// parameters: none
	// return: boolean for whether a bet was placed
	public boolean underBet() {
		int number = promptInt("Enter under number (7 - 36): ", 7, 36);
		if (number < 0) {
			return false;
		}
		int amount = promptBet(player, "Enter bet: ");
		if (amount < 0) {
			return false;
		}
		over = false;
		betAmount = number;
		bet = amount;
		return true;
	}

	// counts the number of ways the dice can add to a target sum
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

	// initializes map of how many ways each total can be rolled
	// parameters: none
	// return: treemap of totals to combination counts
	public static TreeMap<Integer, Integer> initializeMultipliers(){
		TreeMap<Integer, Integer> mapa = new TreeMap<Integer, Integer>();
		for (int i = NUM_DICE; i <= NUM_DICE * NUM_SIDES; i++) {
			mapa.put(i, (int)(countCombinations(NUM_DICE, NUM_SIDES, i)));
		}
		return mapa;
	}

	// calculates the fair multiplier for the current bet (1 / probability of winning)
	// parameters: none
	// return: double multiplier applied to the stake
	public double calculateMultiplier() {
		if (betAmount == 0) {
			return 0;
		}
		long favourable = 0;
		if (over) {
			for (int i = betAmount + 1; i <= NUM_DICE * NUM_SIDES; i++) {
				favourable += multipliers.get(i);
			}
		} else {
			for (int i = NUM_DICE; i < betAmount; i++) {
				favourable += multipliers.get(i);
			}
		}
		if (favourable == 0) {
			return 0;
		}
		return Math.round((TOTAL_OUTCOMES / favourable) * 100.0) / 100.0;
	}

	// checks whether the current roll wins the current bet
	// parameters: none
	// return: boolean
	public boolean isWin() {
		return over ? diceTotal > betAmount : diceTotal < betAmount;
	}

	// rolls the dice and settles the bet
	// parameters: none
	// return: String describing the result
	public String resolve() {
		generateDice();
		String result = "Rolled " + diceTotal + " (" + (over ? "over " : "under ") + betAmount + ")";
		if (isWin()) {
			long payout = Math.round(bet * calculateMultiplier());
			player.recordWin(payout);
			return result + "\nYou won " + payout;
		}
		player.recordLoss();
		return result + "\nYou lost";
	}

	//getters and setters
	public int getBets() {
		return bet;
	}

	public int getDiceTotal() {
		return diceTotal;
	}

}
