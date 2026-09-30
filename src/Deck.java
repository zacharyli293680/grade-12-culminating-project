// blueprint for deck object

// imports
import java.util.*;
import java.io.*;

// deck class
public class Deck {
	
	// fields
	LinkedList<Card> deckCards;
	private int cardsDrawn;
	private int numOfCards;
	private String game;
	
	// constructor
	public Deck(int numOfDecks, String game) {
		this.game = game;
		this.numOfCards = numOfDecks * 52;
		deckCards = initializeCards();		
	}
	
	// initializes cards in the deck
	// parameters: none
	// return: LinkedList of card objects
	public LinkedList<Card> initializeCards () {
		LinkedList<Card> cards = new LinkedList<Card>();
		for (int i = 0; i < numOfCards; i++) {
			cards.add(new Card(i % 52, game));
		}
		Collections.shuffle(cards);
		return cards;
	}
	
	// shuffles deck
	// parameters: none
	// return: void
	public void shuffle () {
		Collections.shuffle(deckCards);
	}
	
	// gets next card in deck
	// parameters: none
	// return: void
	public Card nextCard() {
		cardsDrawn++;
		if (cardsDrawn == numOfCards) {
			Card c = deckCards.removeFirst();
			deckCards = initializeCards();
			cardsDrawn = 0;
			return c;
		}
		return deckCards.removeFirst();
		
	}
	
	// prints deck for checking
	public void printDeck() {
		for (int i = 0; i < numOfCards; i++) {
			System.out.println(deckCards.getFirst());
			deckCards.removeFirst();
		}
	}
}
