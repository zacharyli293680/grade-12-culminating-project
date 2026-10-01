package com.zacharyli.stake;

// blueprint for poker game

// imports
import java.util.*;

// poker class
public class Poker extends Game {

	// fields
	public Player player;
	public Hand hand;
	public Deck deck;
	Map<Integer, Integer> bets = new HashMap<>(); // bets for each hand
	Map<Integer, Integer> payouts = new HashMap<>() {{ // payouts for each hand
		put(1, 649740);
		put(2, 72193);
		put(3, 4165);
		put(4, 624);
		put(5, 509);
		put(6, 255);
		put(7, 47);
		put(8, 21);
		put(9, 7);
		put(10, 0);
	}};

	// constructor
	public Poker (Player player) {
		this.player = player;
		this.deck = new Deck(1, "Poker");
		resetBets();
	}

	// gets the total amount bet from all hands
	// parameters: none
	// return: int total
	public int getTotalBets() {
		int betAmount = 0;
		for (Integer i : bets.values()) {
			betAmount += i;
		}
		return betAmount;
	}

	// deals a hand and settles the bets
	// parameters: none
	// return: String describing the result
	public String playGame() {
		deck.shuffle();
		hand = new Hand("Poker", deck);
		long payout = determinePayout();
		String result = "You got: " + hand.getHandStrength();
		if (payout > 0) {
			player.recordWin(payout);
			result += "\nYou won " + payout;
		} else if (getTotalBets() > 0) {
			player.recordLoss();
			result += "\nNo bet on that hand - you lost";
		}
		resetBets();
		return result;
	}

	// determines the payout given the hand (stake + profit on the matching bet)
	// parameters: none
	// return: long payout
	public long determinePayout(){
		int rank = hand.getValue();
		if (rank >= 10) {
			return 0;
		}
		int bet = bets.get(rank);
		if (bet == 0) {
			return 0;
		}
		return (long) bet + (long) bet * payouts.get(rank);
	}

	// resets the bets
	// parameters: none
	// return: void
	public void resetBets() {
		bets.clear();
		for (int i = 1; i <= 9; i++) {
			bets.put(i, 0);
		}
	}

	// gets the image numbers of all cards in hand
	// parameters: none
	// return: int array of image numbers (1-52)
	public int[] getCardNums() {
		int[] cardNums = new int[5];
		for (int i = 0; i < 5; i++) {
			cardNums[i] = hand.getCards().get(i).getImageNum();
		}
		return cardNums;
	}

	// gets the bets and the hand type from player
	// parameters: none
	// return: boolean for whether a bet was placed
	public boolean getPlayerBets() {
		StringBuilder prompt = new StringBuilder("Enter the hand to bet on:\n");
		for (int i = 1; i <= 9; i++) {
			prompt.append(i).append(" = ").append(Hand.POKER_HANDS[i - 1]).append(" (pays ").append(payouts.get(i)).append(":1)\n");
		}
		int handKey = promptInt(prompt.toString(), 1, 9);
		if (handKey < 0) {
			return false;
		}
		int betAmount = promptBet(player, "Enter bet on " + Hand.POKER_HANDS[handKey - 1] + ": ");
		if (betAmount < 0) {
			return false;
		}
		bets.put(handKey, bets.getOrDefault(handKey, 0) + betAmount);
		return true;
	}

	// getters
	public Map<Integer, Integer> getBets() {
		return bets;
	}
}
