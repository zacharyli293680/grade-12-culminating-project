// blueprint for player object

// imports
import java.util.*;

// player class
public class Player implements Comparable<Player>{
	
	// fields
	private String name;
	private int balance;
	private int initialBalance;
	private boolean turn;
	private int wagered;
	private int profit;
	private int wins;
	private int losses;
	
	// constructor for new player
	public Player (String name) {
		this(name, 10000);
	}
	
	// constructor for existing player
	public Player (String name, int balance) {
		this.name = name;
		this.balance = balance;
		this.initialBalance = balance;
		this.turn = false;
		this.wagered = 0;
		this.profit = 0;
		this.wins = 0;
		this.losses = 0;
	}
	
	// deducts a bet from the balance and records it as wagered
	// parameters: int bet
	// return: void
	public void placeBet(int bet) {
		balance -= bet;
		wagered += bet;
		updateProfit();
	}
	
	// adds a payout to the balance (capped at the int limit) and records a win
	// parameters: long payout (stake + profit)
	// return: void
	public void recordWin(long payout) {
		payout = Math.min(payout, Integer.MAX_VALUE);
		balance = (int) Math.min((long) balance + payout, Integer.MAX_VALUE);
		wins++;
		updateProfit();
	}
	
	// records a loss (the bet was already deducted when placed)
	// parameters: none
	// return: void
	public void recordLoss() {
		losses++;
		updateProfit();
	}
	
	// returns a stake to the balance without counting a win or loss
	// parameters: int stake
	// return: void
	public void recordPush(int stake) {
		balance = (int) Math.min((long) balance + stake, Integer.MAX_VALUE);
		updateProfit();
	}
	
	// recalculates profit from the balance
	// parameters: none
	// return: void
	public void updateProfit() {
		profit = balance - initialBalance;
	}
	
	// getters and setters
	public String getName() {
		return name;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
		updateProfit();
	}
	
	public int getInitialBalance() {
		return initialBalance;
	}
	
	public int getWagered() {
		return wagered;
	}
	
	public int getProfit() {
		return profit;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public boolean getTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	// equals
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}
		return this.name.equals(((Player) o).name);
	}
	
	// hashcode
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	// toString
	public String toString() {
		return name + " " + balance;
	}
	
	// compareTo (by name, used for binary search)
	public int compareTo(Player p) {
		return this.name.compareTo(p.getName());
	}
}
