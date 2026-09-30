// blueprint for hand object

// imports
import java.util.*;
import java.io.*;

// hand class
public class Hand {

	// fields and global variables
	private static final Set<String> HIGH_RANKS = new HashSet<>(Arrays.asList("Jack", "Queen", "King", "Ace"));
	private String[] pokerHands = {"Royal Flush", "Straight Flush", "Four of a Kind", "Full House", "Flush",
			"Straight", "Three of a Kind", "Two Pair", "Pair", "High Card"};
	private Set<String> royalRanks = new HashSet<>(Arrays.asList("10", "Jack", "Queen", "King", "Ace"));
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
	private boolean natural;

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
	
	// checks for blackjack
	// parameters: none
	// return: boolean for if there is a blackjack
	public boolean checkPairBlackjack() {
		if (handCards.get(0).getRank().equals(handCards.get(1).getRank())) {
			return true;
		}
		return false;
	}
	
	// checks for blackjack
	// parameters: none
	// return: void
	public void checkBlackjack() {
		if (handCards.get(0).getValue() + handCards.get(1).getValue() == 21) {
			hasBlackjack = true;
		} else {
			hasBlackjack = false;
		}
	}
	
	// determines hand value for a baccarat hand
	// parameters: none
	// return: void
	public void handValueBaccarat() {
		value = 0;
		for (int i = 0; i < handCards.size(); i++) {
			int cardValue = handCards.get(i).getValue() % 10;
			value = (value + cardValue) % 10;
		}
	}
	
	// determines value of a blackjack hand
	// parameters: none
	// return: void
	public void handValueBlackjack() {
		value = 0;
		int aceCount = 0;
		for (int i = 0; i < handCards.size(); i++) {
			int tempValue = handCards.get(i).getValue();
			if (tempValue == 1) {
				value += 10;
				aceCount++;	
			}
			value += handCards.get(i).getValue();
			while (value > 21 && aceCount > 0) {
				value -= 10;
				aceCount--;	
			}
		}
		checkBlackjack();
		if (value > 21) {
			bust = true;
		} else {
			bust = false;
		}
	}
	
	// determines the value of a poker hand
	// parameters: none
	// return: int for hand strength
	public int determineHandStrength() {
		if (checkRoyalFlush()) {
			return 1;
		} else if (checkStraightFlush()) {
			return 2;
		} else if (checkQuads()) {
			return 3;
		} else if (checkFullHouse()) {
			return 4;
		} else if (checkFlush()) {
			return 5;
		} else if (checkStraight()) {
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
			String rank = entry.getKey();
			int count = entry.getValue();
			if (count == 2 && HIGH_RANKS.contains(rank)) {
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
		boolean flush = true;
		String suit = handCards.get(0).getSuit();
		System.out.println(suit);
		for (int i = 1; i < 5; i++) {
			System.out.println(handCards.get(i).getSuit());
			if(!(suit.equalsIgnoreCase(handCards.get(i).getSuit()))) {
				flush = false;
			}
		}
		return flush;
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

	private boolean checkStraight() {
		boolean isStraight = true;
		int[] values = new int[5];
		for (int i = 0; i < 5; i++) {
			values[i] = (handCards.get(i).getNum() - 1)%13;
			System.out.println(values[i]);
		}
		Arrays.sort(values);
		for (int i = 1; i < values.length; i++) {
			if(values[i] != values[i - 1] + 1) {
				isStraight = false;
				break;
			}
		}
		if (isStraight) {
			aceHighStraight = false;
			return true;
		}

		if (values[0] == 0 && values[1] == 9 && values[2] == 10 && values[3] == 11 && values[4] == 12) {
			aceHighStraight = true;
			return true;
		}
		return false;
	}

	private boolean checkStraightFlush() {
		if (checkStraight() && checkFlush()) {
			return true;
		}
		return false;
	}

	private boolean checkRoyalFlush() {
		if(checkStraightFlush() && aceHighStraight) {
			return true;
		}
		return false;
	}

	// hits for blackjack
	// parameters: none
	// return: void
	public void hit() {
		Card c = deck.nextCard();
		handCards.add(c);
		value += c.getValue();
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
