// blackjack game blueprint

//imports
import java.util.*;
import javax.swing.JOptionPane;
import java.io.*;

// blackjack class
public class Blackjack extends Game {
	
	//variables
	public Player player;
	public Deck deck;
	public ArrayList<Hand> playerHands = new ArrayList<Hand>();
	public Hand currentPlayerHand;
	public Hand dealerHand;
	public Hand playerHand;
	public int bet;
	Scanner in = new Scanner(System.in);

	// constructor
	public Blackjack (Player player) {
		this.player = player;
		this.deck = new Deck(6, "Blackjack");
	}
	
	// gets player bets
	// parameters: none
	// return: void
	public void getPlayerBet() {
		boolean validInput = false;
		while (!validInput) {
			try {
				bet = Integer.parseInt(JOptionPane.showInputDialog("Enter bet: "));
				if (bet > 0 && bet < player.getBalance()) {
					validInput = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
		player.setBalance(player.getBalance() - bet);
		player.setWagered(player.getWagered() + bet);
	}
	
	// initializes hands at the beggining of every round
	// parameters: none
	// return: void
	public void initializeHands() {
		if (playerHands != null) {
			playerHands.clear();
		}
		generatePlayerHand();
		generateDealerHand();
	}
	
	// generates the players hand
	// parameters: none
	// return: void
	public void generatePlayerHand() {
		currentPlayerHand = new Hand("Blackjack", deck);
		currentPlayerHand.setBet(bet);
		playerHands.add(currentPlayerHand);
	}

	// generates the dealers hand
	// parameters: none
	// return: void
	public void generateDealerHand() {
		dealerHand = new Hand("Blackjack", deck);
	}
	
	// for when the player's action is hit
	// parameters: none
	// return: void
	public void hit() {
		currentPlayerHand.getCards().add(deck.nextCard());
		currentPlayerHand.handValueBlackjack();
	}
	
	// for when the player's action is stand
	// parameters: none
	// return: void
	public void stand() {
		dealerDraw();
	}
	
	// draws cards for the dealer until the hand value is over 17
	// parameters: none
	// return: void
	public void dealerDraw() {
		while (dealerHand.getValue() < 17) {
			dealerHand.getCards().add(deck.nextCard());
			dealerHand.handValueBlackjack();
		}
	}
	
	
	// for when the player's action is double dcown
	// parameters: none
	// return: void
	public void doubleDown() {
		hit();
		dealerDraw();
		player.setBalance(player.getBalance() - bet);
		this.bet *= 2;
	}

	
	// getters and setters
	public int getTotalBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}
	
	// draws cards for the dealer until the hand value is over 17
	// parameters: none
	// return: void
	public void dealerTurn() {
		while (dealerHand.getValue() < 17) {
			dealerHand.hit();
		}
	}
}
