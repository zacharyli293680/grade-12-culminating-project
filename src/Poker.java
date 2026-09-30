// blueprint for poker game

// imports
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

// poker class
public class Poker {
	
	// fields
	public Player player;
	public Hand hand;
	public Deck deck;
	Map<Integer, Integer> bets = new HashMap<>(); // bets for each hand
	Map<Integer, Integer> payouts = new HashMap<>() {{ // payouts for each hand
		put(1, 649740);
		put(2, 72193);
		put(3, 4165);
		put(4, 624);
		put(5, 509);
		put(6, 255);
		put(7, 47);
		put(8, 21);
		put(9, 7);
		put(10, 0);
	}};
	
	// constructor
	public Poker (Player player) {
		this.player = player;
		this.deck = new Deck(1, "Poker");
		resetBets();
	}
	
	// gets the total amount bet from all hands
	// parameters: none
	// return: void
	public int getTotalBets() {
		int betAmount = 0;
		for (Integer i : bets.values()) {
			betAmount += i;
		}
		return betAmount;
	}
	
	// players poker game
	// parameters: none
	// return: void
	public void playGame() {
		deck.shuffle();
		hand = new Hand("Poker", deck);
		determinePayout();
		resetBets();
	}
	
	// determines the payout given the hand
	// parameters: none
	// return: void
	public void determinePayout(){
		int totalPayout = 0;
		if (hand.getValue() < 10) {
			totalPayout = bets.get(hand.getValue()) * payouts.get(hand.getValue());
		}
		player.setBalance(player.getBalance() + totalPayout);
	}

	// resets the bets 
	// parameters: none
	// return: void
	public void resetBets() {
		bets.clear();
		for (int i = 1; i <= 9; i++) {
			bets.put(i, 0);
		}
	}

	// gets all the card numbers in hand
	// parameters: none
	// return: void
	public int[] getCardNums() {
		int[] cardNums = new int[5];
		for (int i = 0; i < 5; i++) {
			cardNums[i] = hand.getCards().get(i).getNum();
		}
		return cardNums;
	}

	// deals the hand
	// parameters: graphics, panel
	// return: void
	public void dealHand(Graphics g, Panel myPanel) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image cardBack = t.getImage("CardBack.png");
		g.drawImage(cardBack, 100, 100, 100, 150, myPanel);
	}
	
	// gets the bets and the hand type from player
	// parameters: none
	// return: void
	public void getPlayerBets() {
		Integer handKey = 1;
		Integer betAmount = 0;
		
		// for hand type
		boolean validInput1 = false;
		while (!validInput1) {
			try {
				handKey = Integer.parseInt(JOptionPane.showInputDialog("Enter hand strength (1-9): "));
				if (handKey > 0 && handKey < 10) {
					validInput1 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		
		// for bet
		boolean validInput2 = false;
		while (!validInput2) {
			try {
				betAmount = Integer.parseInt(JOptionPane.showInputDialog("Enter bet: "));
				if (betAmount > 0 && betAmount < player.getBalance()) {
					validInput2 = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		player.setBalance(player.getBalance() - betAmount);
		bets.put(handKey, bets.getOrDefault(handKey, 0) + betAmount);
	}

	// getters
	public Map<Integer, Integer> getBets() {
		return bets;
	}


}
