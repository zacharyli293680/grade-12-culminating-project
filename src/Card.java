// blueprint for card object

// imports
import java.util.*;
import java.io.*;

// card class
public class Card {
	
	// fields
	private String[] suits = {"Diamonds", "Clubs", "Hearts", "Spades"};
	private String[] ranks = {"Ace", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King"};
	private String suit;
	private String rank;
	private int cardNum;
	private int value;
	
	// constructor
	public Card(int cardNum, String game) {
		this.cardNum = cardNum + 1;
		this.suit = suits[cardNum / 13];
		this.rank = ranks[cardNum % 13];
		if (game.equals("Blackjack")) { // for blackjack
			int tempValue = (cardNum % 13) + 1;
			if (tempValue > 10) {
				tempValue = 10;
			} else if (tempValue == 0) {
				tempValue = 11;
			}
			this.value = tempValue;
		} else if (game.equals("Baccarat")) { // for baccarat
			int tempValue = (cardNum % 13);
			if (tempValue >= 10) {
				tempValue = 0;
			}
			this.value = tempValue;
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
	
	public int getNum() {
		return cardNum - 1;
	}
	
	// toString
	public String toString() {
		return String.format(this.rank + " of " + this.suit + " value :" + cardNum);
	}
	
}
