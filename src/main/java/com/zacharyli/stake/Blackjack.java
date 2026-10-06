package com.zacharyli.stake;

// blackjack game blueprint

//imports
import java.util.*;

// blackjack class
public class Blackjack extends Game {

	//variables
	public Player player;
	public Deck deck;
	public ArrayList<Hand> playerHands = new ArrayList<Hand>();
	public Hand currentPlayerHand;
	public Hand dealerHand;
	public int bet;

	// constructor
	public Blackjack (Player player) {
		this.player = player;
		this.deck = new Deck(6, "Blackjack");
	}

	// gets the player bet and deducts it
	// parameters: none
	// return: boolean for whether a bet was placed
	public boolean getPlayerBet() {
		int amount = promptBet(player, "Enter bet: ");
		if (amount < 0) {
			return false;
		}
		bet = amount;
		return true;
	}

	// initializes hands at the beginning of every round
	// parameters: none
	// return: void
	public void initializeHands() {
		playerHands.clear();
		generatePlayerHand();
		generateDealerHand();
	}

	// generates the players hand
	// parameters: none
	// return: void
	public void generatePlayerHand() {
		currentPlayerHand = new Hand("Blackjack", deck);
		currentPlayerHand.setBet(bet);
		playerHands.add(currentPlayerHand);
	}

	// generates the dealers hand
	// parameters: none
	// return: void
	public void generateDealerHand() {
		dealerHand = new Hand("Blackjack", deck);
	}

	// for when the player action is hit
	// parameters: none
	// return: void
	public void hit() {
		currentPlayerHand.hit();
	}

	// for when the player action is stand
	// parameters: none
	// return: void
	public void stand() {
		dealerDraw();
	}

	// draws cards for the dealer until the hand value is 17 or more
	// parameters: none
	// return: void
	public void dealerDraw() {
		while (dealerHand.getValue() < 17) {
			dealerHand.hit();
		}
	}

	// checks whether the player is allowed to double down
	// parameters: none
	// return: boolean
	public boolean canDoubleDown() {
		return currentPlayerHand.getCards().size() == 2 && player.getBalance() >= bet;
	}

	// for when the player action is double down: doubles the bet, takes one card, then the dealer plays
	// parameters: none
	// return: void
	public void doubleDown() {
		player.placeBet(bet);
		bet *= 2;
		currentPlayerHand.setBet(bet);
		hit();
		if (!currentPlayerHand.getBust()) {
			dealerDraw();
		}
	}

	// getters and setters
	public int getTotalBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}
}
