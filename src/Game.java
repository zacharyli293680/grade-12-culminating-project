// base class shared by every game: winnings math and bet prompting

// imports
import javax.swing.JOptionPane;

// game class
public abstract class Game {

	// method calculates a total payout (stake + profit) given profit odds a:b
	// parameters: int bet, int a (profit numerator), int b (profit denominator)
	// return: int for total payout
	public static int calculateWinnings(int bet, int a, int b) {
		long profit = ((long) bet * a) / b;
		return (int) Math.min((long) bet + profit, Integer.MAX_VALUE);
	}

	// method prompts for an integer within a range, re-prompting on bad input
	// parameters: String prompt, int min, int max
	// return: int entered, or -1 if the player cancelled the dialog
	public static int promptInt(String prompt, int min, int max) {
		while (true) {
			String input = JOptionPane.showInputDialog(prompt);
			if (input == null) {
				return -1;
			}
			try {
				int value = Integer.parseInt(input.trim());
				if (value >= min && value <= max) {
					return value;
				}
				JOptionPane.showMessageDialog(null, "Enter a number from " + min + " to " + max);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
	}

	// method prompts for a bet and, if valid, deducts it from the player and records it as wagered
	// parameters: Player player, String prompt
	// return: int bet placed, or -1 if the player cancelled or has no money
	public static int promptBet(Player player, String prompt) {
		if (player.getBalance() <= 0) {
			JOptionPane.showMessageDialog(null, "You have no money left to bet");
			return -1;
		}
		int bet = promptInt(prompt, 1, player.getBalance());
		if (bet > 0) {
			player.placeBet(bet);
		}
		return bet;
	}
}
