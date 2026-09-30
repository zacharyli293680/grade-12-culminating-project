// blueprint for player object

// imports
import java.util.*;
import java.io.*;

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
		this.name = name;
		if (name.equalsIgnoreCase("wong")) {
			this.name = "Old Grumpy Woman";
		}
		this.balance = 10000;
		this.initialBalance = 10000;
		this.turn = false;
		this.wagered = 0;
		this.profit = 0;
		this.wins = 0;
		this.losses = 0;
	}
	
	// constructor for existing player
	public Player (String name, int balance) {
		this.name = name;
		if (name.equalsIgnoreCase("DesiUncle")) {
			this.name = "Pranav";
		}
		this.balance = balance;
		this.initialBalance = balance;
		this.turn = false;
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
	}
	
	public int getInitialBalance() {
		return initialBalance;
	}
	
	public int getWagered() {
		return wagered;
	}
	
	public void setWagered(int wagered) {
		this.wagered = wagered;
	}
	
	public int getProfit() {
		return profit;
	}
	
	public void setProfit(int profit) {
		this.profit = profit;
	}
	
	public int getWins() {
		return wins;
	}
	
	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public void setLosses(int losses) {
		this.losses = losses;
	}
	
	public boolean getTurn() {
		return turn;
	}
	
	public void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	public boolean equals(Player p) {
		if (this.name.equals(p.name)) {
			return true;
		}
		return false;
	}
	
	// toString
	public String toString() {
		return name + " " + balance;
	}
	
	// compareTo
	public int compareTo(Player p) {
		return this.name.compareTo(p.getName());
	}
}
