package com.zacharyli.stake;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CardTest {

	@Test
	void ranksRunAceThroughKing() {
		assertEquals("Ace", new Card(0, "Poker").getRank());
		assertEquals("Ten", new Card(9, "Poker").getRank());
		assertEquals("Jack", new Card(10, "Poker").getRank());
		assertEquals("King", new Card(12, "Poker").getRank());
	}

	@Test
	void suitsFollowThirteenCardBlocks() {
		assertEquals("Diamonds", new Card(0, "Poker").getSuit());
		assertEquals("Clubs", new Card(13, "Poker").getSuit());
		assertEquals("Hearts", new Card(26, "Poker").getSuit());
		assertEquals("Spades", new Card(51, "Poker").getSuit());
	}

	@Test
	void imageNumberIsIndexPlusOne() {
		assertEquals(1, new Card(0, "Poker").getImageNum());
		assertEquals(52, new Card(51, "Poker").getImageNum());
	}

	@Test
	void rankIndexIgnoresSuit() {
		assertEquals(0, new Card(0, "Poker").getRankIndex());
		assertEquals(0, new Card(13, "Poker").getRankIndex());
		assertEquals(12, new Card(51, "Poker").getRankIndex());
	}

	@Test
	void blackjackValues() {
		assertEquals(1, new Card(0, "Blackjack").getValue());
		assertEquals(10, new Card(9, "Blackjack").getValue());
		assertEquals(10, new Card(10, "Blackjack").getValue());
		assertEquals(10, new Card(12, "Blackjack").getValue());
	}

	@Test
	void baccaratValues() {
		assertEquals(1, new Card(0, "Baccarat").getValue());
		assertEquals(9, new Card(8, "Baccarat").getValue());
		assertEquals(0, new Card(9, "Baccarat").getValue());
		assertEquals(0, new Card(12, "Baccarat").getValue());
	}

	@Test
	void deckHoldsFiftyTwoCardsPerDeck() {
		Deck deck = new Deck(2, "Blackjack");
		assertEquals(104, deck.deckCards.size());
	}
}
