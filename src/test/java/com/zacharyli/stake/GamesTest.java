package com.zacharyli.stake;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class GamesTest {

	@Test
	void calculateWinningsReturnsStakePlusProfit() {
		assertEquals(200, Game.calculateWinnings(100, 1, 1));
		assertEquals(250, Game.calculateWinnings(100, 3, 2));
		assertEquals(195, Game.calculateWinnings(100, 95, 100));
		assertEquals(Integer.MAX_VALUE, Game.calculateWinnings(Integer.MAX_VALUE, 1, 1));
	}

	@Test
	void diceCanRollEveryFace() {
		Dice dice = new Dice(new Player("D", 1000));
		boolean[] seen = new boolean[7];
		for (int i = 0; i < 2000; i++) {
			dice.generateDice();
			for (int v : dice.diceValues) {
				assertTrue(v >= 1 && v <= 6);
				seen[v] = true;
			}
		}
		for (int face = 1; face <= 6; face++) {
			assertTrue(seen[face], "never rolled a " + face);
		}
	}

	@Test
	void diceMultiplierIsInverseProbability() {
		Dice dice = new Dice(new Player("D", 1000));
		dice.over = true;
		dice.betAmount = 35;
		assertEquals(46656.0, dice.calculateMultiplier());
		dice.betAmount = 6;
		assertEquals(1.0, dice.calculateMultiplier());
		dice.over = false;
		dice.betAmount = 21;
		double under21 = dice.calculateMultiplier();
		assertTrue(under21 > 2.1 && under21 < 2.3);
		dice.betAmount = 0;
		assertEquals(0.0, dice.calculateMultiplier());
	}

	@Test
	void diceWinCheckIsStrict() {
		Dice dice = new Dice(new Player("D", 1000));
		dice.diceValues.clear();
		for (int i = 0; i < 6; i++) {
			dice.diceValues.add(3);
		}
		dice.countTotal();
		dice.over = true;
		dice.betAmount = 18;
		assertFalse(dice.isWin());
		dice.betAmount = 17;
		assertTrue(dice.isWin());
		dice.over = false;
		dice.betAmount = 19;
		assertTrue(dice.isWin());
	}

	@Test
	void dragonTowerOddsGrowPerRow() {
		Dragontower dt = new Dragontower(new Player("E", 1000));
		Map<Integer, Odds> easy = dt.initializeMultiplierArray(3);
		assertEquals(1, easy.get(0).getLoss());
		assertEquals(1, easy.get(0).getWin());
		assertEquals(4, easy.get(1).getLoss());
		assertEquals(3, easy.get(1).getWin());
		Map<Integer, Odds> hard = dt.initializeMultiplierArray(1);
		assertEquals(262144, hard.get(9).getLoss());
		assertEquals(1, hard.get(9).getWin());
	}

	@Test
	void dragonTowerStartsAtBottomRowAndIsIdleUntilBet() {
		Dragontower dt = new Dragontower(new Player("E", 1000));
		assertFalse(dt.isInProgress());
		assertEquals(0.0, dt.getMultiplier());
		assertEquals(8, dt.getCurrentRow());
	}

	@Test
	void minesMultiplierResetsAndCountsEachDiamondOnce() {
		Mines m = new Mines(new Player("M", 1000));
		m.generateGrid(24);
		assertEquals(0.0, m.getMultiplier());
		int diamond = -1;
		for (Cell c : m.grid) {
			if (c.getDiamond()) {
				diamond = c.getCellNum();
				break;
			}
		}
		assertTrue(m.checkCell(diamond));
		assertEquals(1, m.getCheckedCells());
		assertEquals(0.04, m.getMultiplier());
		assertTrue(m.isChecked(diamond));
	}

	@Test
	void minesGridHasRequestedDiamondCount() {
		Mines m = new Mines(new Player("M", 1000));
		m.generateGrid(5);
		long diamonds = m.grid.stream().filter(Cell::getDiamond).count();
		assertEquals(5, diamonds);
		assertEquals(25, m.grid.size());
	}

	@Test
	void minesCellLookupMapsClicksToCells() {
		Mines m = new Mines(new Player("M", 1000));
		assertEquals(1, m.getCellNum(346, 112));
		assertEquals(5, m.getCellNum(850, 112));
		assertEquals(25, m.getCellNum(850, 611));
	}

	@Test
	void cellsCompareByPosition() {
		assertEquals(new Cell(3, true), new Cell(3, false));
		assertEquals(new Cell(3, true).hashCode(), new Cell(3, false).hashCode());
		assertNotEquals(new Cell(1, 2, true), new Cell(2, 1, true));
	}
}
