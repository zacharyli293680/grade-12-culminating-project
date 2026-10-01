package com.zacharyli.stake;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlayerTest {

	@Test
	void newPlayerStartsWithTenThousand() {
		Player p = new Player("Alex");
		assertEquals(10000, p.getBalance());
		assertEquals(0, p.getProfit());
	}

	@Test
	void betsWinsAndLossesKeepStatsConsistent() {
		Player p = new Player("Alex", 1000);
		p.placeBet(100);
		p.recordWin(Game.calculateWinnings(100, 3, 2));
		assertEquals(1150, p.getBalance());
		assertEquals(150, p.getProfit());
		assertEquals(100, p.getWagered());
		assertEquals(1, p.getWins());

		p.placeBet(50);
		p.recordLoss();
		assertEquals(1100, p.getBalance());
		assertEquals(100, p.getProfit());
		assertEquals(1, p.getLosses());

		p.placeBet(30);
		p.recordPush(30);
		assertEquals(1100, p.getBalance());
		assertEquals(180, p.getWagered());
	}

	@Test
	void payoutsAreCappedAtIntegerMax() {
		Player p = new Player("Alex", Integer.MAX_VALUE - 5);
		p.recordWin(Long.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, p.getBalance());
	}

	@Test
	void equalityIsByName() {
		assertEquals(new Player("Sam", 1), new Player("Sam", 99));
		assertEquals(new Player("Sam", 1).hashCode(), new Player("Sam", 99).hashCode());
		assertNotEquals(new Player("Sam"), new Player("sam"));
	}

	@Test
	void comparableSortsByNameForBinarySearch() {
		List<Player> players = new ArrayList<>();
		for (String n : new String[] {"Ryan", "Zach", "Jenifer", "Jaden"}) {
			players.add(new Player(n, 5));
		}
		Collections.sort(players);
		assertEquals("Jaden", players.get(0).getName());
		assertTrue(Collections.binarySearch(players, new Player("Zach")) >= 0);
		assertTrue(Collections.binarySearch(players, new Player("Nobody")) < 0);
	}

	@Test
	void comparatorSortsByBalance() {
		List<Player> players = new ArrayList<>();
		players.add(new Player("A", 300));
		players.add(new Player("B", 100));
		players.add(new Player("C", 200));
		Collections.sort(players, new CompareBalance().reversed());
		assertEquals("A", players.get(0).getName());
		assertEquals("B", players.get(2).getName());
	}

	@Test
	void toStringMatchesSaveFileFormat() {
		assertEquals("Zach 15000", new Player("Zach", 15000).toString());
	}
}
