// blueprint for card object

// card class
public class Card {
	
	// fields
	private static final String[] SUITS = {"Diamonds", "Clubs", "Hearts", "Spades"};
	private static final String[] RANKS = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King"};
	private String suit;
	private String rank;
	private int cardNum; // 1 to 52, matches the card image file names
	private int value;
	
	// constructor
	// parameters: int card index 0-51, String game
	public Card(int cardNum, String game) {
		this.cardNum = cardNum + 1;
		this.suit = SUITS[cardNum / 13];
		this.rank = RANKS[cardNum % 13];
		int rankValue = (cardNum % 13) + 1; // Ace = 1 ... King = 13
		if (game.equals("Blackjack")) { // ace counts as 1 here, Hand adds the soft 10
			this.value = Math.min(rankValue, 10);
		} else if (game.equals("Baccarat")) { // ten and face cards count as 0
			this.value = rankValue >= 10 ? 0 : rankValue;
		} else {
			this.value = rankValue;
		}
	}
	
	// getters and setters
	public int getValue() {
		return value;
	}
	
	public String getRank() {
		return rank;
	}
	
	public String getSuit() {
		return suit;
	}
	
	// index of the card 0-51
	public int getNum() {
		return cardNum - 1;
	}
	
	// number of the image file for this card (1-52)
	public int getImageNum() {
		return cardNum;
	}
	
	// rank index 0-12 (Ace = 0 ... King = 12) regardless of suit
	public int getRankIndex() {
		return (cardNum - 1) % 13;
	}
	
	// toString
	public String toString() {
		return this.rank + " of " + this.suit + " value :" + value;
	}
	
}
