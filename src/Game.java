// useless abstract game class

// imports
import java.util.*;
import java.io.*;

// game class
public abstract class Game {
	int minBet = 1;
	
	// method calculates winnings given the odds of winnings from multiplier
	// parameters: int bet, int lose odds, int win odds
	// return: int for winnings
	public static int calculateWinnings(int bet, int a, int b) {
		int winnings = 0;
		winnings = bet + (bet * a)/b;
		return winnings;
	}
	
}
