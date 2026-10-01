package com.zacharyli.stake;

// blueprint for odds object used to calculate winnings

// imports
import java.util.*;
import java.io.*;

// odds class
public class Odds {
	
	// fields
	private int win; // win odds
	private int loss; // lose odds
	
	// constructor
	public Odds(int win, int loss) {
		this.win = win;
		this.loss = loss;
	}
	
	// getters and setters
	public int getWin() {
		return win;
	}
	
	public int getLoss() {
		return loss;
	}
	
	// toString
	public String toString() {
		return win + ":" + loss;
	}
}
