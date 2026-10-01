// blueprint for baccarat game

// imports
import java.util.*;

// baccarat class
public class Baccarat extends Game {

	// fields and variables
    public Player player;
    private int totalBet;
    public Deck deck;
    public Hand playerHand;
    public Hand bankerHand;
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
    }

    // gets player bets
    // parameters: String for bet type
    // return: boolean for whether a bet was placed
    public boolean getPlayerBets(String betType) {
    	int bet = promptBet(player, "Enter bet for " + betType + ": ");
    	if (bet < 0) {
    		return false;
    	}
        bets.put(betType, bets.getOrDefault(betType, 0) + bet);
        return true;
    }

    // calculate total bets
    // paramters: none
    // return: void
    public void calcTotalBets() {
        totalBet = bets.get("Player") + bets.get("Banker") + bets.get("Tie");
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

    // settles all bets against the dealt hands
    // parameters: none
    // return: String describing the result
    public String calcWinnings() {
    	int playerBet = bets.get("Player");
    	int bankerBet = bets.get("Banker");
    	int tieBet = bets.get("Tie");
    	long payout = 0;
    	String result;
    	boolean tie = false;
    	if (playerHand.getValue() > bankerHand.getValue()) { // player wins, pays 1:1
    		result = "Player wins";
    		if (playerBet > 0) {
    			payout = calculateWinnings(playerBet, 1, 1);
    		}
    	} else if (playerHand.getValue() < bankerHand.getValue()) { // banker wins, pays 19:20
    		result = "Banker wins";
    		if (bankerBet > 0) {
    			payout = calculateWinnings(bankerBet, 95, 100);
    		}
    	} else { // tie pays 8:1, player and banker bets are returned
    		result = "Tie";
    		tie = true;
    		if (tieBet > 0) {
    			payout = calculateWinnings(tieBet, 8, 1);
    		}
    		player.recordPush(playerBet + bankerBet);
    	}
    	if (payout > 0) {
    		player.recordWin(payout);
    		result += " - you won " + payout;
    	} else if (tie && playerBet + bankerBet > 0) {
    		result += " - bets returned";
    	} else if (playerBet + bankerBet + tieBet > 0) {
    		player.recordLoss();
    		result += " - you lost";
    	}
    	resetBets();
    	return result;
    }

    // generates the player and the banker hands and determines if they draw a third card
    // parameters: none
    // return: void
    public void generateHands() {
        playerHand = new Hand("Baccarat", deck);
        bankerHand = new Hand("Baccarat", deck);
        if (playerHand.getValue() >= 8 || bankerHand.getValue() >= 8) {
            return; // natural, both stand
        }
        boolean playerDrew = false;
        if (playerHand.getValue() <= 5) {
            playerHand.getCards().add(deck.nextCard());
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
            int playerThirdValue = playerHand.getCards().get(2).getValue();
            if (bankerRule[bankerTotal][playerThirdValue] == 1) {
                bankerHand.getCards().add(deck.nextCard());
                bankerHand.handValueBaccarat();
            }
        }
    }

}
