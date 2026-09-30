// blueprint for baccarat game

// imports
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

// baccarat class
public class Baccarat {

	// fields and variables
    public Player player;
    private int totalBet;
    public Deck deck;
    public Hand playerHand;
    public Hand bankerHand;
    private boolean natural;
    public Map<String, Integer> bets = new HashMap<String, Integer>() {{ // bets 
        put("Player", 0);
        put("Banker", 0);
        put("Tie", 0);
    }};
    public int[][] bankerRule = 
        {{1,1,1,1,1,1,1,1,1,1}, 
         {1,1,1,1,1,1,1,1,1,1},
         {1,1,1,1,1,1,1,1,1,1},
         {1,1,1,1,1,1,1,1,0,1},
         {0,0,1,1,1,1,1,1,0,0},
         {0,0,0,0,1,1,1,1,0,0},
         {0,0,0,0,0,0,1,1,0,0},
         {0,0,0,0,0,0,0,0,0,0}}; // for when the dealer has to draw or stand
    
    // constructor
    public Baccarat(Player player) {
        this.player = player;
        this.deck = new Deck(6, "Baccarat");
        generateHands();
    }

    // gets player bets
    // parameters: String for bet type
    // return: void
    public void getPlayerBets(String betType) {
    	boolean validInput = false;
    	int bet = 0;
		while (!validInput) {
			try {
				bet = Integer.parseInt(JOptionPane.showInputDialog("Enter bet for " + betType + ": "));
				if (bet > 0 && bet < player.getBalance()) {
					validInput = true;
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid Input");
			}
		}
        int oldBet = bets.getOrDefault(betType, 0);
        int newBet = oldBet + bet;
        bets.put(betType, newBet);
        player.setBalance(player.getBalance() - bet);
    }

    // calculate total bets 
    // paramters: none
    // return: void
    public void calcTotalBets() {
        int playerBet = bets.getOrDefault("Player", 0);
        int bankerBet = bets.getOrDefault("Banker", 0);
        int tieBet    = bets.getOrDefault("Tie", 0);

        totalBet = playerBet + bankerBet + tieBet;
    }
    
    // getter
    public int getTotalBets() {
        calcTotalBets();
        return totalBet;
    }
    
    // resets bets
    // paramters: none
    // return: void
    public void resetBets() {
    	bets.put("Player", 0);
        bets.put("Banker", 0);
        bets.put("Tie", 0);
    }
    
    // calculates winnings if won
    // parameters: none
    // return: void
    public void calcWinnings() {
    	if (playerHand.getValue() > bankerHand.getValue()) { // if player wins
    		if (bets.get("Player") > 0) {
    			player.setBalance(player.getBalance() + Game.calculateWinnings(bets.get("Player"), 1, 1));
    			player.setWins(player.getWins() + 1);
    			player.setProfit(player.getBalance() - player.getInitialBalance());
    		} else if (bets.get("Tie") > 0 || bets.get("Banker") > 0) {
    			player.setLosses(player.getLosses() + 1);
    		}
    	} else if (playerHand.getValue() < bankerHand.getValue()) { // if banker wins
    		if (bets.get("Banker") > 0) {
    			player.setBalance(player.getBalance() + Game.calculateWinnings(bets.get("Banker"), 95, 100));
    			player.setWins(player.getWins() + 1);
    			player.setProfit(player.getBalance() - player.getInitialBalance());
    		} else if (bets.get("Tie") > 0 || bets.get("Player") > 0) {
    			player.setLosses(player.getLosses() + 1);
    		}
    	} else { // if there is a tie
    		if (bets.get("Tie") > 0) {
    			player.setBalance(player.getBalance() + Game.calculateWinnings(bets.get("Banker"), 8, 1));
    			player.setWins(player.getWins() + 1);
    			player.setProfit(player.getBalance() - player.getInitialBalance());
    		} else if (bets.get("Player") > 0 || bets.get("Banker") > 0) {
    			player.setLosses(player.getLosses() + 1);
    		}
    	}
    	resetBets();
    }
    
    // generates the player and the banker hands and determines if they draw a third card
    // parameters: none
    // return: void
    public void generateHands() {
        playerHand = new Hand("Baccarat", deck);
        bankerHand = new Hand("Baccarat", deck);
        if (playerHand.getValue() >= 8 || bankerHand.getValue() >= 8) {
            natural = true;
            return;
        }
        boolean playerDrew = false;
        if (playerHand.getValue() <= 5) {
            Card playerThird = deck.nextCard();
            playerHand.getCards().add(playerThird);
            playerHand.handValueBaccarat();
            playerDrew = true;
        }
        if (!playerDrew) {
            if (bankerHand.getValue() <= 5) {
                bankerHand.getCards().add(deck.nextCard());
                bankerHand.handValueBaccarat();
            }
        } else {
            int bankerTotal = bankerHand.getValue();
            Card playerThirdCard = playerHand.getCards().get(2);
            int playerThirdValue = playerThirdCard.getValue(); 
            if (bankerRule[bankerTotal][playerThirdValue] == 1) {
                bankerHand.getCards().add(deck.nextCard());
                bankerHand.handValueBaccarat();
            }
        }
    }

}
