package com.zacharyli.stake;

// Zachary Li - ICS4U ISU, 01-20-2025

// This is a replication of the gambling website stake, where there are six mini games: 
// Blackjack, Baccarat, Video Poker, Dice, Dragon Tower, Mines

//imports
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.*;
import java.io.*;

// Driver class
public class Driver extends JPanel implements MouseListener {
	
	// global variable
	private static final String PLAYER_FILE = "players.txt";
	JPanel myPanel;
	static Clip backgroundMusic;
	public static int screen = 0;
	private Player player;
	private static boolean selectedPlayer = false;
	public static ArrayList<Player> players = new ArrayList<>();

	// blackjack variables
	public Blackjack blackjack;
	private boolean playerAction = false;
	private int handNum;

	// baccarat variables
	public Baccarat baccarat;
	private boolean betting = true;

	// poker variables
	public Poker poker;
	private Map<Integer, Integer> bets;
	private boolean bettingOpen = true;

	// dice variables
	public Dice dice;
	private boolean drawDice = false;

	// dragontower variables
	public Dragontower dragontower;

	// mines variables
	public Mines mines;
	private boolean minesBetting = false;
	private Set<Cell> minesCheckedCells = new HashSet<>();


	private final Font ARIAL_BIG = new Font("Arial", Font.PLAIN, 28);
	private final Font ARIAL_SMALL = new Font("Arial", Font.PLAIN, 13);


	// driver constructor
	public Driver() {
	    setPreferredSize(new Dimension(1000, 750));
	    setBackground(new Color(255, 255, 255));
	    setFont(ARIAL_BIG);
	    myPanel = new JPanel();
	    myPanel.setLayout(null);
	    myPanel.setBackground(Color.WHITE);
	    myPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    addMouseListener(this);
	    backgroundMusic = Assets.audio("gdmusic.wav");
	    if (backgroundMusic != null) {
	        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
	    } else {
	        System.out.println("No music found");
	    }
	}


	// method gets players: looks the name up with a binary search on the name-sorted list, or adds a new player
	// parameters: none
	// return: void
	public void getPlayer() {
	    String name = JOptionPane.showInputDialog("Enter player name: ");
	    if (name == null) {
	        return; // cancelled
	    }
	    name = name.trim();
	    if (name.isEmpty() || name.contains(" ")) {
	        JOptionPane.showMessageDialog(myPanel, "Enter a name with no spaces");
	        return;
	    }
	    int index = Collections.binarySearch(players, new Player(name));
	    if (index >= 0) {
	        player = players.get(index);
	    } else {
	        int insertionPoint = -(index + 1);
	        Player newPlayer = new Player(name);
	        players.add(insertionPoint, newPlayer);
	        player = newPlayer;
	        savePlayers();
	    }
	    selectedPlayer = true;
	}

	// method initializes players from the save file (seeded from the bundled players.txt on first run)
	// and sorts them by name for binary search
	// parameters: none
	// return: void
	public static void initializePlayers() {
		try {
			BufferedReader inFile;
			if (new File(PLAYER_FILE).exists()) {
				inFile = new BufferedReader(new FileReader(PLAYER_FILE));
			} else {
				inFile = new BufferedReader(new InputStreamReader(Assets.stream("/" + PLAYER_FILE)));
			}
			String line;
			while((line = inFile.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || !line.contains(" ")) {
					continue;
				}
				String name = line.substring(0, line.indexOf(" "));
				int balance = Integer.parseInt(line.substring(line.indexOf(" ") + 1).trim());
				players.add(new Player(name, balance));
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException | NumberFormatException e) {
			System.out.println("reading error");
		}
		Collections.sort(players);
	}

	// method writes players back to the text file as a leaderboard, richest first
	// parameters: none
	// return: void
	public static void savePlayers() {
		ArrayList<Player> leaderboard = new ArrayList<>(players);
		Collections.sort(leaderboard, new CompareBalance().reversed());
		try {
			PrintWriter outFile = new PrintWriter(new FileWriter(PLAYER_FILE));
			for (Player p : leaderboard) {
				outFile.println(p);
			}
			outFile.close();
		} catch (IOException e) {
			System.out.println("saving error");
		}
	}
	
	// draws menu screen
	public void menuScreen(Graphics g) {
		Image menuBG = Assets.image("Menu1.png");
		Image aboutIcon = Assets.image("AboutIcon1.png");
		g.drawImage(menuBG,  0,  0,  this);
		g.drawImage(aboutIcon,  10,  710, 30, 30,  this);
	}
	
	// draws blackjack screen
	public void blackjackScreen(Graphics g) {
		Image blackjackBG = Assets.image("Blackjack4.png");
		g.drawImage(blackjackBG, 0, 0, this);
		drawPlayerStats(g);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		g.setFont(ARIAL_BIG);
		if (blackjack != null) {
			g.drawString("" + blackjack.getTotalBet(), 120, 180);
		} else {
			g.drawString("0", 120, 180); 
		}
	}
	
	// draws baccarat screen
	public void baccaratScreen(Graphics g) {
		Image baccaratBG = Assets.image("Baccarat2.png");
		g.drawImage(baccaratBG, 0, 0, this);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		drawPlayerStats(g);
		g.setColor(Color.WHITE);
		g.setFont(ARIAL_BIG);
		if (baccarat != null) {
			g.drawString("" + baccarat.getTotalBets(), 120, 180);
			g.drawString("" + baccarat.bets.get("Player"),  855, 215);
			g.drawString("" + baccarat.bets.get("Tie"),  855, 425);
			g.drawString("" + baccarat.bets.get("Banker"),  855, 640);
		} else {
			g.drawString("0",  120, 180);
			g.drawString("0",  855, 215);
			g.drawString("0",  855, 425);
			g.drawString("0",  855, 640);
		}
	}
	
	// poker screen
	public void pokerScreen(Graphics g) {
		Image pokerBG = Assets.image("Poker3.png");
		g.drawImage(pokerBG, 0, 0, this);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		g.setColor(Color.WHITE);
		if (poker != null) {
			g.drawString("" + poker.getTotalBets(), 120, 180);
		} else {
			g.drawString("0", 120, 180);
		}

		drawPlayerStats(g);
		drawCardBack(g, 350, 500);
		drawCardBack(g, 475, 500);
		drawCardBack(g, 600, 500);
		drawCardBack(g, 725, 500);
		drawCardBack(g, 850, 500);
	}
	
	// draws dice screen
	public void diceScreen(Graphics g) {
		Image diceBG = Assets.image("Dice1.png");
		g.drawImage(diceBG, 0, 0, this);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		drawPlayerStats(g);
		g.setColor(Color.WHITE);
		g.setFont(ARIAL_BIG);
		if (dice != null) {
			g.drawString("" + dice.getBets(), 120, 180);
			if (dice.multipliers != null) {
				g.drawString("" + dice.calculateMultiplier() + "x", 120, 430);
			}
			g.drawString("" + dice.getDiceTotal(), 660, 663);
		} else {
			g.drawString("0", 120, 180);
			g.drawString("0x", 120, 430);
		}
	}
	
	// draws dragontower screen
	public void dragontowerScreen(Graphics g) {
		Image dragontowerBG = Assets.image("DragonTower1.png");
		g.drawImage(dragontowerBG, 0, 0, this);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		g.setColor(Color.WHITE);
		if (dragontower != null && dragontower.difficulty != null) {
			g.drawString("" + dragontower.getBetAmount(), 120, 180);
			g.drawString("" + dragontower.getMultiplier() + "x", 120, 430);
		} else {
			g.drawString("0", 120, 180);
			g.drawString("0x", 120, 430);
		}
		drawPlayerStats(g);
	}
	
	// draws dragontower screen
	public void minesScreen(Graphics g) {
		Image minesBG = Assets.image("Mines1.png");
		g.drawImage(minesBG, 0, 0, this);
		Image instructionsIcon = Assets.image("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		drawPlayerStats(g);
		g.setFont(ARIAL_BIG);
		if (mines != null) {
			g.drawString("" + mines.getBet(), 120, 180);
			g.drawString("" + mines.getMultiplier() + "x", 120, 430);
		} else {
			g.drawString("0", 120, 180);
			g.drawString("0x", 120,  430);
		}
	}
	
	// draws about page
	public void drawAboutPage(Graphics g) {
		Image aboutBG = Assets.image("About.png");
		g.drawImage(aboutBG, 0, 0, this);
	}
	
	// draws blackjack instructions page
	public void drawBlackjackIn(Graphics g) {
		Image in = Assets.image("BlackjackIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws baccarat instructions page
	public void drawBaccaratIn(Graphics g) {
		Image in = Assets.image("BaccaratIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws poker instructions page
	public void drawPokerIn(Graphics g) {
		Image in = Assets.image("PokerIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws dice instructions page
	public void drawDiceIn(Graphics g) {
		Image in = Assets.image("DiceIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws dragontower instructions page
	public void drawDragontowerIn(Graphics g) {
		Image in = Assets.image("DragontowerIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws mines instructions page
	public void drawMinesIn(Graphics g) {
		Image in = Assets.image("MinesIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws player stats
	public void drawPlayerStats(Graphics g) {
		g.setFont(ARIAL_SMALL);
		g.setColor(Color.WHITE);
		g.drawString(player.getName(), 43, 582);
		g.drawString("" + player.getBalance(), 168, 582);
		g.drawString("" + player.getWagered(), 43, 635);
		g.drawString("" + player.getWins(), 168, 635);
		g.drawString("" + player.getProfit(), 43, 700);
		g.drawString("" + player.getLosses(), 168, 700);
	}
	
	// draws card back 
	public void drawCardBack(Graphics g, int x, int y) {
		Image cardBack = Assets.cardBack();
		g.drawImage(cardBack, x, y, 100, 150, this);
	}
	
	// draws a card front; cardNum is the image number 1-52
	public void drawCard(Graphics g, int x, int y, int cardNum) {
		Image card = Assets.card(cardNum);
		g.drawImage(card, x, y, 100, 150, this);
	}

	// draws player cards for blackjack
	public void drawPlayerCardsBlackjack(Graphics g) {
		for (int i = 0; i < blackjack.currentPlayerHand.getCards().size(); i++) {
			drawCard(g, 325 + (i * 110), 500, blackjack.currentPlayerHand.getCards().get(i).getImageNum());
		}
		g.setFont(ARIAL_BIG);
		g.setColor(Color.WHITE);
		g.drawString("" + blackjack.currentPlayerHand.getValue(), 650 , 705);
		g.drawString("" + handNum, 520, 425);
	}

	// draws dealer cards for blackjack
	public void drawDealerCards(Graphics g, boolean actionOver) {
		g.setFont(ARIAL_BIG);
		g.setColor(Color.WHITE);
		Card upCard = blackjack.dealerHand.getCards().get(0);
		drawCard(g, 325, 185, upCard.getImageNum());
		if (!actionOver) {
			drawCardBack(g, 435, 185);
			int upValue = upCard.getValue() == 1 ? 11 : upCard.getValue();
			g.drawString("" + upValue, 650, 150);
		} else {
			for (int i = 0; i < blackjack.dealerHand.getCards().size(); i++) {
				drawCard(g, 325 + (i * 110), 185, blackjack.dealerHand.getCards().get(i).getImageNum());
			}
			g.drawString("" + blackjack.dealerHand.getValue(), 650, 150);
		}
	}

	// draws player cards for baccarat
	public void drawPlayerCardsBaccarat(Graphics g, boolean show) {
		if (show) {
			for (int i = 0; i < baccarat.playerHand.getCards().size(); i++) {
				drawCard(g, 350 + (i * 115), 500, baccarat.playerHand.getCards().get(i).getImageNum());
			}
			g.setFont(ARIAL_BIG);
			g.setColor(Color.WHITE);
			g.drawString("" + baccarat.playerHand.getValue(), 550, 700);
		} else {
			drawCardBack(g, 350, 500);
			drawCardBack(g, 465, 500);
		}
	}
	
	// draws player cards for baccarat
	public void drawBankerCards(Graphics g, boolean show) {
		if(show) {
			for (int i = 0; i < baccarat.bankerHand.getCards().size(); i++) {
				drawCard(g, 350 + (i *115), 200, baccarat.bankerHand.getCards().get(i).getImageNum());
			}
			g.setFont(ARIAL_BIG);
			g.setColor(Color.WHITE);
			g.drawString("" + baccarat.bankerHand.getValue(), 550, 150);
		} else {
			drawCardBack(g, 350, 200);
			drawCardBack(g, 465, 200);
		}
	}

	
	// drawing the icons for mines 
	public void drawShowDiamond(Graphics g, int cellNum) {
		Image showDiamond = Assets.image("DiamondShowIcon.png");
		g.drawImage(showDiamond, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawShowMine(Graphics g, int cellNum) {
		Image showMine = Assets.image("MineShowIcon.png");
		g.drawImage(showMine, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawHideDiamond(Graphics g, int cellNum) {
		Image hideDiamond = Assets.image("DiamondHideIcon.png");
		g.drawImage(hideDiamond, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawHideMine(Graphics g, int cellNum) {
		Image hideMine = Assets.image("MineHideIcon.png");
		g.drawImage(hideMine, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}
	
	// draws the cells for dragontower
	public void drawEgg(Graphics g, int colNum, int rowNum) {
		Image egg = Assets.image("EggTile.png");
		g.drawImage(egg, 450 + (colNum * 106), 243 + (rowNum * 50), 101, 49, this);
	}

	public void drawCheckedCell(Graphics g, int colNum, int rowNum) {
		Image green = Assets.image("GreenTile.png");
		g.drawImage(green, 450 + (colNum * 106), 243 + (rowNum * 50), 101, 38, this);
	}

	// draws dice for the dice game
	public void drawDie(Graphics g, int dieNum, int dieValue) {
		Image die = Assets.image("diceFace" + dieValue + ".png");
		g.drawImage(die, 395 + 200 * (dieNum % 3), 200 + 175 * (dieNum / 3), 128, 128, this);
	}
	
	// displays the drawn cards
	public void displayDrawnCards(Graphics g) {
		if (poker != null) {
			int[] cardNums = poker.getCardNums();
			drawCard(g, 350, 500, cardNums[0]);
			drawCard(g, 475, 500, cardNums[1]);
			drawCard(g, 600, 500, cardNums[2]);
			drawCard(g, 725, 500, cardNums[3]);
			drawCard(g, 850, 500, cardNums[4]);
		}
	}
	
	// draws the bets for the poker game
	public void drawBets(Graphics g) {
		if (poker != null) {
			for (int i = 0; i < 9; i++) {
				g.drawString("" + bets.get(i + 1), 865, 166 + 27 * i);
			}
		} else {
			for (int i = 0; i < 9; i++) {
				g.drawString("0", 865, 166 + 27 * i);
			}
		}
	}
	
	// draws the cells 
	public void drawCell(Graphics g, int cellNum) {
		ArrayList<Cell> grid = mines.grid;
		if(grid.get(cellNum - 1).getChecked()) {
			if(grid.get(cellNum - 1).getDiamond()) {
				drawShowDiamond(g, cellNum);
			} else {
				drawShowMine(g, cellNum);
			}
		} else {
			if (grid.get(cellNum - 1).getDiamond()) {
				drawHideDiamond(g, cellNum);
			} else {
				drawHideMine(g, cellNum);
			}
		}
	}
	
	// draws single cells 
	public void drawCheckedCells(Graphics g) {
		for(Cell c : minesCheckedCells) {
			drawCell(g, c.getCellNum());
		}
	}
	
	// draws all cells
	public void drawAllCells(Graphics g) {
		for (int i = 1; i < 26; i++) {
			drawCell(g, i);
		}
	}
	
	// draws the rows the player has already cleared
	public void drawRowCells(Graphics g) {
		for (int row = 8; row > dragontower.getCurrentRow(); row--) {
			for (int col = 0; col < 4; col++) {
				if (dragontower.grid.get(row).get(col).getEgg()) {
					drawEgg(g, col, row);
				} else if (dragontower.grid.get(row).get(col).getChecked()) {
					drawCheckedCell(g, col, row);
				}
			}
		}
	}

	// draws egg cell
	public void drawAllEggs(Graphics g) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 4; j++) {
				if(dragontower.grid.get(i).get(j).getEgg()) {
					drawEgg(g, j, i);
				}
			}	
		}
	}

	// draw all dice
	public void drawDice(Graphics g) {
		for (int i = 0; i < 6; i++) {
			drawDie(g, i, dice.diceValues.get(i));
		}
	}
	
	// method determines winner and changes accumulators for blackjack game
	// paramters: none
	// return: void
	public void determineWinnerBlackjack() {
		int playerValue = blackjack.currentPlayerHand.getValue();
		int dealerValue = blackjack.dealerHand.getValue();
		int handBet = blackjack.currentPlayerHand.getBet();
		if (playerValue > 21) {
			player.recordLoss();
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Bust");
		} else if (dealerValue > 21 || playerValue > dealerValue) {
			player.recordWin(Game.calculateWinnings(handBet, 1, 1));
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Winner");
		} else if (playerValue < dealerValue){
			player.recordLoss();
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Dealer wins");
		} else {
			player.recordPush(handBet);
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Push");
		}
	}
	
	// paint component
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // Call the parent class method
		if (screen == 0) {
			menuScreen(g);
		} else if (screen == 1) {
			blackjackScreen(g);
			if (blackjack != null && blackjack.currentPlayerHand != null) {
				drawPlayerCardsBlackjack(g);
				drawDealerCards(g, !playerAction);
			}
		} else if (screen == 2) {
			baccaratScreen(g);
			if (baccarat != null) {
				drawPlayerCardsBaccarat(g, !betting);
				drawBankerCards(g, !betting);
			} else {
				drawCardBack(g, 350, 200);
				drawCardBack(g, 465, 200);
				drawCardBack(g, 350, 500);
				drawCardBack(g, 465, 500);
			}
		} else if (screen == 3) {
			pokerScreen(g);
			if (bets != null) {
				drawBets(g);
			}
			if (bettingOpen) {
				drawCardBack(g, 350, 500);
				drawCardBack(g, 475, 500);
				drawCardBack(g, 600, 500);
				drawCardBack(g, 725, 500);
				drawCardBack(g, 850, 500);
			} else {
				displayDrawnCards(g);
			}
		} else if (screen == 4) {
			diceScreen(g);
			if (drawDice) {
				drawDice(g);
			}
		} else if (screen == 5) {
			dragontowerScreen(g);
			if (dragontower != null && dragontower.grid.size() > 0) {
				if (!dragontower.isInProgress()) {
					drawAllEggs(g);
				}
				drawRowCells(g);
			}

		} else if (screen == 6) {
			minesScreen(g);
			if (mines != null && mines.grid != null && !minesBetting && mines.grid.size() > 0) {
				drawAllCells(g);
			}
			drawCheckedCells(g);		
		} else if (screen == 7) {
			drawBlackjackIn(g);
		} else if (screen == 8) {
			drawBaccaratIn(g);
		} else if (screen == 9) {
			drawPokerIn(g);
		} else if (screen == 10) {
			drawDiceIn(g);
		} else if (screen == 11) {
			drawDragontowerIn(g);
		} else if (screen == 12) {
			drawMinesIn(g);
		} else if (screen == 13) {
			drawAboutPage(g);
		}
	}
	
	// // mouseevent method
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (screen == 0) {
			if (x > 120 && x < 320 && y > 105 && y < 375) {
				if (selectedPlayer) {
					screen = 1;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel, "Select player first");
				}
			} else if (x > 400 && x < 600 && y > 105 && y < 375) {
				if (selectedPlayer) {
					screen = 2;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel,  "Select player first");
				}
			} else if (x > 680 && x < 880 && y > 105 && y < 375) {
				if (selectedPlayer) {
					screen = 3;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel,  "Select player first");
				}
			} else if (x > 120 && x < 320 && y > 415 && y < 685) {
				if (selectedPlayer) {
					screen = 4;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel,  "Select player first");
				}
			} else if (x > 400 && x < 600 && y > 415 && y < 685) {
				if (selectedPlayer) {
					screen = 5;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel,  "Select player first");
				}
			} else if (x > 680 && x < 880 && y > 415 && y < 685) {
				if (selectedPlayer) {
					screen = 6;
					repaint();
				} else {
					JOptionPane.showMessageDialog(myPanel,  "Select player first");
				}
			} else if (x > 790 && x < 980 && y > 20 && y < 85) {
				getPlayer();
			} else if (x > 10 && x < 50 && y > 710 && y < 745) {
				screen = 13;
				repaint();
			}
		} else if (screen == 1) {
			if (blackjack == null) {
				blackjack = new Blackjack(player);
				handNum = 1;
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				if (playerAction) {
					JOptionPane.showMessageDialog(myPanel, "Finish the hand first");
					return;
				}
				screen = 0;
				repaint();
				blackjack = null;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 7;
				repaint();
			}
			if (!playerAction && x > 20 && x < 285 && y > 380 && y < 435) {
				if (!blackjack.getPlayerBet()) {
					return;
				}
				blackjack.initializeHands();
				playerAction = true;
				repaint();
				if (blackjack.currentPlayerHand.getBlackjack() || blackjack.dealerHand.getBlackjack()) {
					playerAction = false; // natural blackjack ends the hand before any action
					repaint();
					if (blackjack.currentPlayerHand.getBlackjack() && blackjack.dealerHand.getBlackjack()) {
						player.recordPush(blackjack.currentPlayerHand.getBet());
						JOptionPane.showMessageDialog(myPanel, "Push - both have blackjack");
					} else if (blackjack.currentPlayerHand.getBlackjack()) {
						player.recordWin(Game.calculateWinnings(blackjack.currentPlayerHand.getBet(), 3, 2));
						JOptionPane.showMessageDialog(myPanel, "Blackjack! Pays 3:2");
					} else {
						player.recordLoss();
						JOptionPane.showMessageDialog(myPanel, "Dealer Blackjack");
					}
					repaint();
				}
			} else if (playerAction && x > 20 && x < 147 && y > 243 && y < 298) { // hit
				blackjack.hit();
				repaint();
				if (blackjack.currentPlayerHand.getBust()) {
					playerAction = false;
					determineWinnerBlackjack();
				}
			} else if (playerAction && x > 157 && x < 287 && y > 243 && y < 298) { // stand
				blackjack.stand();
				playerAction = false;
				determineWinnerBlackjack();
				repaint();
			} else if (playerAction && x > 20 && x < 147 && y > 312 && y < 365) { // double down
				if (!blackjack.canDoubleDown()) {
					JOptionPane.showMessageDialog(myPanel, "You can only double down on your first two cards with enough balance");
					return;
				}
				blackjack.doubleDown();
				playerAction = false;
				determineWinnerBlackjack();
				repaint();
			}
		} else if (screen == 2) {
			if (baccarat == null) {
				baccarat = new Baccarat(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
				baccarat = null;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 8;
				repaint();
			} else if (x > 20 && x < 285 && y > 240 && y < 295) { // deal
				if (baccarat.getTotalBets() == 0) {
					JOptionPane.showMessageDialog(myPanel, "Place a bet first");
					return;
				}
				betting = false;
				baccarat.generateHands();
				String result = baccarat.calcWinnings();
				repaint();
				JOptionPane.showMessageDialog(myPanel, result);
			} else if (x > 20 && x < 285 && y > 305 && y < 355) {
				betting = true;
				repaint();
				baccarat.getPlayerBets("Player");
				repaint();
			} else if (x > 20 && x < 285 && y > 365 && y < 415) {
				betting = true;
				repaint();
				baccarat.getPlayerBets("Banker");
				repaint();
			} else if (x > 20 && x < 285 && y > 425 && y < 475) {
				betting = true;
				repaint();
				baccarat.getPlayerBets("Tie");
				repaint();
			}
		} else if (screen == 3) {
			if (poker == null) {
				poker = new Poker(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
				poker = null;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 9;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) { // bet
				bettingOpen = true;
				bets = poker.getBets();
				repaint();
				poker.getPlayerBets();
				repaint();
			} else if (bettingOpen && x > 20 && x < 285 && y > 300 && y < 350) { // deal
				if (poker.getTotalBets() == 0) {
					JOptionPane.showMessageDialog(myPanel, "Place a bet first");
					return;
				}
				bettingOpen = false;
				String result = poker.playGame();
				repaint();
				JOptionPane.showMessageDialog(myPanel, result);
			}
		} else if (screen == 4) {
			if (dice == null) {
				dice = new Dice(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
				dice = null;
				drawDice = false;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 10;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) { // over
				if (dice.overBet()) {
					String result = dice.resolve();
					drawDice = true;
					repaint();
					JOptionPane.showMessageDialog(myPanel, result);
				}
			} else if (x > 20 && x < 285 && y > 300 && y < 350) { // under
				if (dice.underBet()) {
					String result = dice.resolve();
					drawDice = true;
					repaint();
					JOptionPane.showMessageDialog(myPanel, result);
				}
			}
		} else if (screen == 5) {
			if (dragontower == null) {
				dragontower = new Dragontower(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				if (dragontower.isInProgress()) {
					JOptionPane.showMessageDialog(myPanel, "Cash out or finish the round first");
					return;
				}
				screen = 0;
				repaint();
				dragontower = null;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 11;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) { // bet
				if (dragontower.isInProgress()) {
					JOptionPane.showMessageDialog(myPanel, "Cash out or finish the round first");
					return;
				}
				if (dragontower.getBet()) {
					dragontower.generateGrid();
					repaint();
				}
			} else if (dragontower.isInProgress() && x > 450 && x < 874 && y > 243 && y < 693) {
				int cellRow = dragontower.getCellRow(y);
				if (cellRow == dragontower.getCurrentRow()) {
					int cellCol = dragontower.getCellCol(x);
					if (dragontower.checkCell(cellCol)) {
						repaint();
						if (dragontower.isFinished()) {
							int payout = dragontower.cashOut();
							repaint();
							JOptionPane.showMessageDialog(myPanel, "You reached the top! You won " + payout);
						}
					} else {
						player.recordLoss();
						repaint();
						JOptionPane.showMessageDialog(myPanel, "Dragon egg! You lost");
					}
				}
			} else if (x > 20 && x < 285 && y > 300 && y < 350) { // cash out
				if (!dragontower.isInProgress()) {
					JOptionPane.showMessageDialog(myPanel, "Place a bet first");
					return;
				}
				if (dragontower.getRowsCleared() == 0) {
					JOptionPane.showMessageDialog(myPanel, "Clear at least one row before cashing out");
					return;
				}
				int payout = dragontower.cashOut();
				repaint();
				JOptionPane.showMessageDialog(myPanel, "Cashed out " + payout);
			}

		} else if (screen == 6) {
			if (mines == null) {
				mines = new Mines(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				if (minesBetting) {
					JOptionPane.showMessageDialog(myPanel, "Cash out or finish the round first");
					return;
				}
				screen = 0;
				repaint();
				mines = null;
				minesCheckedCells.clear();
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 12;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) { // bet
				if (minesBetting) {
					JOptionPane.showMessageDialog(myPanel, "Cash out or finish the round first");
					return;
				}
				if (mines.getPlayerBet()) {
					mines.generateGrid(mines.getDiamonds());
					minesCheckedCells.clear();
					minesBetting = true;
					repaint();
				}
			} else if (minesBetting && x > 345 && x < 965 && y > 112 && y < 732) {
				int cell = mines.getCellNum(x, y);
				if (cell < 1 || cell > 25 || mines.isChecked(cell)) {
					return;
				}
				if(mines.checkCell(cell)) {
					minesCheckedCells.add(mines.grid.get(cell - 1));
					repaint();
					if (mines.getCheckedCells() == mines.getDiamonds()) {
						minesBetting = false;
						int payout = mines.calcPayout();
						player.recordWin(payout);
						minesCheckedCells.clear();
						repaint();
						JOptionPane.showMessageDialog(myPanel, "You found every diamond! You won " + payout);
					}
				} else {
					minesBetting = false;
					player.recordLoss();
					minesCheckedCells.clear();
					repaint();
					JOptionPane.showMessageDialog(myPanel, "Mine! You lost");
				}
			} else if (minesBetting && x > 20 && x < 285 && y > 300 && y < 350) { // cash out
				if (mines.getCheckedCells() == 0) {
					JOptionPane.showMessageDialog(myPanel, "Find at least one diamond before cashing out");
					return;
				}
				minesBetting = false;
				int payout = mines.calcPayout();
				player.recordWin(payout);
				minesCheckedCells.clear();
				repaint();
				JOptionPane.showMessageDialog(myPanel, "Cashed out " + payout);
			}
		} else if (screen == 7) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 1;
				repaint();
			}
		} else if (screen == 8) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 2;
				repaint();
			}
		} else if (screen == 9) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 3;
				repaint();
			}
		} else if (screen == 10) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 4;
				repaint();
			}
		} else if (screen == 11) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 5;
				repaint();
			}
		} else if (screen == 12) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 6;
				repaint();
			}
		} else if (screen == 13) {
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
			}
		}
		if (selectedPlayer) {
			savePlayers(); // persist balances after every action
		}
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
	// main
	public static void main(String[] args) {
		initializePlayers();
		Driver myPanel = new Driver();
		JFrame frame = new JFrame("Stake");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the program exits when the window is closed
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				savePlayers(); // keep balances between runs
			}
		});
		frame.add(myPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
