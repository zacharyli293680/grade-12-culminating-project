package com.zacharyli.stake;

// blueprint for hand object

// imports
import java.util.*;

// hand class
public class Hand {

	// fields and global variables
	private static final Set<String> HIGH_RANKS = new HashSet<>(Arrays.asList("Jack", "Queen", "King", "Ace"));
	public static final String[] POKER_HANDS = {"Royal Flush", "Straight Flush", "Four of a Kind", "Full House", "Flush",
			"Straight", "Three of a Kind", "Two Pair", "Pair (Jacks or better)", "High Card"};
	ArrayList<Card> handCards = new ArrayList<Card>();
	Deck deck;
	private int bet = 0;
	private String game;
	private int value;
	private String handStrength;
	private boolean hasAce;
	private boolean aceHighStraight = false;
	private boolean hasPair;
	private boolean hasBlackjack;
	private boolean bust;

	// constructor
	public Hand(String game, Deck deck) {
		this.game = game;
		this.deck = deck;
		if (game.equals("Blackjack")) { // for blackjack
			generateBlackjackHand();
			handValueBlackjack();
			this.hasAce = checkAce();
		} else if (game.equals("Poker")) { // for poker
			generatePokerHand();
			this.hasAce = checkAce();
			this.value = determineHandStrength();
			this.handStrength = POKER_HANDS[value - 1];
		} else if (game.equals("Baccarat")) { // for baccarat
			generateBaccaratHand();
			handValueBaccarat();
		}
	}

	// generates a hand for blackjack
	// parameters: none
	// return: void
	public void generateBlackjackHand() {
		handCards.add(deck.nextCard());
		handCards.add(deck.nextCard());
		hasPair = checkPairBlackjack();
	}

	// generates a hand for poker
	// parameters: none
	// return: void
	public void generatePokerHand() {
		for (int i = 0; i < 5; i++) {
			handCards.add(deck.nextCard());
		}
	}

	// generates a hand for baccarat
	// parameters: none
	// return: void
	public void generateBaccaratHand() {
		handCards.add(deck.nextCard());
		handCards.add(deck.nextCard());
	}

	// checks hand for an ace
	// parameters: none
	// return: boolean for if ace is present
	public boolean checkAce() {
		for (int i = 0; i < handCards.size(); i++) {
			if (handCards.get(i).getRank().equals("Ace")) {
				return true;
			}
		}
		return false;
	}

	// checks for a pair (used for splitting)
	// parameters: none
	// return: boolean for if the first two cards share a rank
	public boolean checkPairBlackjack() {
		if (handCards.get(0).getRank().equals(handCards.get(1).getRank())) {
			return true;
		}
		return false;
	}

	// checks for a natural blackjack (21 with the first two cards)
	// parameters: none
	// return: void
	public void checkBlackjack() {
		hasBlackjack = handCards.size() == 2 && value == 21;
	}

	// determines hand value for a baccarat hand
	// parameters: none
	// return: void
	public void handValueBaccarat() {
		value = 0;
		for (int i = 0; i < handCards.size(); i++) {
			value = (value + handCards.get(i).getValue()) % 10;
		}
	}

	// determines value of a blackjack hand (an ace counts 11 unless that busts)
	// parameters: none
	// return: void
	public void handValueBlackjack() {
		value = 0;
		int aceCount = 0;
		for (int i = 0; i < handCards.size(); i++) {
			int cardValue = handCards.get(i).getValue();
			if (cardValue == 1) {
				aceCount++;
			}
			value += cardValue;
		}
		if (aceCount > 0 && value + 10 <= 21) {
			value += 10;
		}
		checkBlackjack();
		bust = value > 21;
	}

	// determines the value of a poker hand
	// parameters: none
	// return: int for hand strength (1 = royal flush ... 10 = high card)
	public int determineHandStrength() {
		boolean flush = checkFlush();
		boolean straight = checkStraight();
		if (flush && straight && aceHighStraight) {
			return 1;
		} else if (flush && straight) {
			return 2;
		} else if (checkQuads()) {
			return 3;
		} else if (checkFullHouse()) {
			return 4;
		} else if (flush) {
			return 5;
		} else if (straight) {
			return 6;
		} else if (checkTrips()) {
			return 7;
		} else if (checkTwoPair()) {
			return 8;
		} else if (checkPairJacksPlus()) {
			return 9;
		} else {
			return 10;
		}
	}

	// following methods checks for hand strength
	private Map<String, Integer> getRankCount(){
		Map<String, Integer> rankCount = new HashMap<>();
		for (Card card: handCards) {
			String rank = card.getRank();
			rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
		}
		return rankCount;
	}

	private boolean checkPairJacksPlus() {
		Map<String, Integer> rankCount = getRankCount();
		for (Map.Entry<String, Integer> entry : rankCount.entrySet()) {
			if (entry.getValue() == 2 && HIGH_RANKS.contains(entry.getKey())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkTwoPair() {
		Map<String, Integer> rankCount = getRankCount();
		int pairCount = 0;
		for (int count: rankCount.values()) {
			if (count == 2) {
				pairCount++;
			}
		}
		return pairCount >= 2;
	}

	private boolean checkTrips() {
		Map<String, Integer> rankCount = getRankCount();
		for (int count: rankCount.values()) {
			if (count == 3) {
				return true;
			}
		}
		return false;
	}

	private boolean checkQuads() {
		Map<String, Integer> rankCount = getRankCount();
		for (int count: rankCount.values()) {
			if (count == 4) {
				return true;
			}
		}
		return false;
	}

	public boolean checkFlush() {
		String suit = handCards.get(0).getSuit();
		for (int i = 1; i < handCards.size(); i++) {
			if(!suit.equals(handCards.get(i).getSuit())) {
				return false;
			}
		}
		return true;
	}

	private boolean checkFullHouse() {
		Map<String, Integer> rankCount = getRankCount();
		boolean hasTrips = false;
		boolean hasPair = false;
		for (int count: rankCount.values()) {
			if (count == 3) {
				hasTrips = true;
			}
			if (count == 2) {
				hasPair = true;
			}
		}
		return hasTrips && hasPair;
	}

	// checks for a straight; the ace can be low (A-2-3-4-5) or high (10-J-Q-K-A)
	private boolean checkStraight() {
		int[] values = new int[5];
		for (int i = 0; i < 5; i++) {
			values[i] = handCards.get(i).getRankIndex(); // Ace = 0 ... King = 12
		}
		Arrays.sort(values);
		boolean consecutive = true;
		for (int i = 1; i < values.length; i++) {
			if(values[i] != values[i - 1] + 1) {
				consecutive = false;
				break;
			}
		}
		if (consecutive) {
			aceHighStraight = false;
			return true;
		}
		if (values[0] == 0 && values[1] == 9 && values[2] == 10 && values[3] == 11 && values[4] == 12) {
			aceHighStraight = true;
			return true;
		}
		return false;
	}

	// hits for blackjack
	// parameters: none
	// return: void
	public void hit() {
		handCards.add(deck.nextCard());
		handValueBlackjack();
	}

	// getters and setters
	public int getValue() {
		return value;
	}

	public String getHandStrength() {
		return handStrength;
	}

	public boolean getPair() {
		return hasPair;
	}
	public boolean getBlackjack() {
		return hasBlackjack;
	}

	public boolean getBust() {
		return bust;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}
	public ArrayList<Card> getCards(){
		return handCards;
	}

	// for printing while testing
	public void printHand() {
		for (int i = 0; i < handCards.size(); i++) {
			System.out.println(handCards.get(i));
		}
		System.out.println(value);
	}
}
