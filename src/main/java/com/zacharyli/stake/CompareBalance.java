package com.zacharyli.stake;

// imports
import java.util.*;
import java.io.*;

// comparator  interface
public class CompareBalance implements Comparator<Player>{
	
	// compare method
	public int compare(Player p1, Player p2) {
		return p1.getBalance() - p2.getBalance();
	}
}
