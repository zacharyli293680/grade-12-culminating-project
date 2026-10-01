package com.zacharyli.stake;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import org.junit.jupiter.api.Test;

class HandTest {

	// card indices: 0 = Ace, 1 = Two ... 9 = Ten, 10 = Jack, 11 = Queen, 12 = King; add 13 per suit
	private static Hand hand(String game, int... indices) {
		Deck deck = new Deck(1, game);
		LinkedList<Card> cards = new LinkedList<>();
		for (int i : indices) {
			cards.add(new Card(i, game));
		}
		deck.deckCards = cards;
		return new Hand(game, deck);
	}

	@Test
	void royalFlush() {
		assertEquals(1, hand("Poker", 0, 9, 10, 11, 12).getValue());
		assertEquals(1, hand("Poker", 13, 22, 23, 24, 25).getValue());
		assertEquals("Royal Flush", hand("Poker", 0, 9, 10, 11, 12).getHandStrength());
	}

	@Test
	void straightFlush() {
		assertEquals(2, hand("Poker", 1, 2, 3, 4, 5).getValue());
	}

	@Test
	void fourOfAKind() {
		assertEquals(3, hand("Poker", 4, 17, 30, 43, 8).getValue());
	}

	@Test
	void fullHouse() {
		assertEquals(4, hand("Poker", 3, 16, 29, 7, 20).getValue());
	}

	@Test
	void flush() {
		assertEquals(5, hand("Poker", 0, 2, 5, 7, 9).getValue());
	}

	@Test
	void straightsIncludeAceLowAndAceHigh() {
		assertEquals(6, hand("Poker", 0, 14, 2, 3, 4).getValue());
		assertEquals(6, hand("Poker", 0, 22, 10, 11, 12).getValue());
		assertEquals(6, hand("Poker", 5, 19, 7, 8, 9).getValue());
	}

	@Test
	void threeOfAKind() {
		assertEquals(7, hand("Poker", 6, 19, 32, 1, 9).getValue());
	}

	@Test
	void twoPair() {
		assertEquals(8, hand("Poker", 6, 19, 1, 14, 9).getValue());
	}

	@Test
	void pairPaysOnlyJacksOrBetter() {
		assertEquals(9, hand("Poker", 10, 23, 2, 5, 8).getValue());
		assertEquals(9, hand("Poker", 0, 13, 2, 5, 8).getValue());
		assertEquals(10, hand("Poker", 9, 22, 2, 5, 7).getValue());
	}

	@Test
	void highCard() {
		assertEquals(10, hand("Poker", 0, 15, 4, 20, 9).getValue());
	}

	@Test
	void naturalBlackjackNeedsExactlyTwoCards() {
		Hand natural = hand("Blackjack", 0, 12);
		assertTrue(natural.getBlackjack());
		assertEquals(21, natural.getValue());

		Hand threeCard = hand("Blackjack", 4, 5, 9);
		threeCard.hit();
		assertEquals(21, threeCard.getValue());
		assertFalse(threeCard.getBlackjack());
	}

	@Test
	void acesCountElevenUnlessThatBusts() {
		assertEquals(12, hand("Blackjack", 0, 0).getValue());
		Hand soft = hand("Blackjack", 0, 5, 5);
		assertEquals(17, soft.getValue());
		soft.hit();
		assertEquals(13, soft.getValue());
	}

	@Test
	void bustIsFlagged() {
		Hand h = hand("Blackjack", 9, 8, 7);
		assertFalse(h.getBust());
		h.hit();
		assertTrue(h.getBust());
	}

	@Test
	void baccaratValueIsModTen() {
		assertEquals(5, hand("Baccarat", 6, 7).getValue());
		assertEquals(0, hand("Baccarat", 9, 12).getValue());
	}
}
