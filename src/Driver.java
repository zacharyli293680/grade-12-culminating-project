// Zachary Li - ICS4U ISU, 01-20-2025

// This is a replication of the gambling website stake, where there are six mini games: 
// Blackjack, Baccarat, Video Poker, Dice, Dragon Tower, Mines

//imports
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.*;
import java.io.*;

// Driver class
public class Driver extends JPanel implements MouseListener {
	
	// global variable
	JPanel myPanel;
	JFrame frame;
	Toolkit t = Toolkit.getDefaultToolkit();
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
	private String betType;

	// poker variables
	public Poker poker;
	private Map<Integer, Integer> bets;
	private boolean bettingOpen = true;

	// dice variables
	public Dice dice;
	private boolean drawDice = false;

	// dragontower variables
	public Dragontower dragontower;
	private boolean dragontowerBetting;
	private boolean drawRow;
	private int currentRow;
	private Set<Cell>dragontowerCheckedCells = new HashSet<>();

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
	    frame = new JFrame("Poker Home Screen");
	    myPanel = new JPanel();
	    myPanel.setLayout(null);
	    myPanel.setBackground(Color.WHITE);
	    myPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    addMouseListener(this);
	    try {
	        AudioInputStream sound = AudioSystem.getAudioInputStream(new File("gdmusic.wav"));
	        backgroundMusic = AudioSystem.getClip();
	        backgroundMusic.open(sound);
	        backgroundMusic.start();
	    } catch (Exception e) {
	        System.out.println("No music found");
	    }
	}

	
	// method gets players
	// parameters: none
	// return: void
	public void getPlayer() {
	    String name = JOptionPane.showInputDialog("Enter player name: ");
	    int index = Collections.binarySearch(players, new Player(name));
	    if (index >= 0) {
	        player = players.get(index);
	    } else {
	        int insertionPoint = -(index + 1);  
	        Player newPlayer = new Player(name);
	        players.add(insertionPoint, newPlayer);
	        player = newPlayer;
	    }
	    selectedPlayer = true;
	}

	// method intialized players from text file
	// parameters: none
	// return: void
	public static void initializePlayers() {
		try {
			BufferedReader inFile = new BufferedReader(new FileReader("players.txt"));
			String line;
			while((line = inFile.readLine()) != null) {
				String name = line.substring(0, line.indexOf(" "));
				int balance = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
				players.add(new Player(name, balance));
			}
			inFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException e) {
			System.out.println("reading error");
		}
	}
	
	// draws menu screen
	public void menuScreen(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image menuBG = t.getImage("Menu1.png");
		Image aboutIcon = t.getImage("AboutIcon1.png");
		g.drawImage(menuBG,  0,  0,  this);
		g.drawImage(aboutIcon,  10,  710, 30, 30,  this);
	}
	
	// draws blackjack screen
	public void blackjackScreen(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image blackjackBG = t.getImage("Blackjack4.png");
		g.drawImage(blackjackBG, 0, 0, this);
		drawPlayerStats(g);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image baccaratBG = t.getImage("Baccarat2.png");
		g.drawImage(baccaratBG, 0, 0, this);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image pokerBG = t.getImage("Poker3.png");
		g.drawImage(pokerBG, 0, 0, this);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image diceBG = t.getImage("Dice1.png");
		g.drawImage(diceBG, 0, 0, this);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image dragontowerBG = t.getImage("DragonTower1.png");
		g.drawImage(dragontowerBG, 0, 0, this);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		g.setColor(Color.WHITE);
		if (dragontower != null && dragontower.difficulty != null) {
			g.drawString("" + dragontower.getBetAmount(), 120, 180);
			g.drawString("" + dragontower.getMultiplier(currentRow), 120, 430);
		} else {
			g.drawString("0", 120, 180);
			g.drawString("0x", 120, 430);
		}
		drawPlayerStats(g);
	}
	
	// draws dragontower screen
	public void minesScreen(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image minesBG = t.getImage("Mines1.png");
		g.drawImage(minesBG, 0, 0, this);
		Image instructionsIcon = t.getImage("InstructionsIcon.png");
		g.drawImage(instructionsIcon, 960, 710, 30, 30, this);
		drawPlayerStats(g);
		g.setFont(ARIAL_BIG);
		if (mines != null) {
			g.drawString("" + mines.getBet(), 120, 180);
			if (mines.checkedCells > 0) {
				mines.calculateMultiplier(mines.multipliers.get(mines.checkedCells).get(25 - mines.getDiamonds()));
				g.drawString("" + mines.getMultiplier(), 120, 430);
			} else {
				g.drawString("0x", 120, 430);
			}
		} else {
			g.drawString("0", 120, 180);
			g.drawString("0x", 120,  430);;
		}
	}
	
	// draws about page
	public void drawAboutPage(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image aboutBG = t.getImage("About.png");
		g.drawImage(aboutBG, 0, 0, this);
	}
	
	// draws blackjack instructions page
	public void drawBlackjackIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("BlackjackIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws baccarat instructions page
	public void drawBaccaratIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("BaccaratIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws poker instructions page
	public void drawPokerIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("PokerIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws dice instructions page
	public void drawDiceIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("DiceIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws dragontower instructions page
	public void drawDragontowerIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("DragontowerIn.png");
		g.drawImage(in, 0, 0, this);
	}
	
	// draws mines instructions page
	public void drawMinesIn(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image in = t.getImage("MinesIn.png");
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image cardBack = t.getImage("CardBack.png");
		g.drawImage(cardBack, x, y, 100, 150, this);
	}
	
	// draws draw card front
	public void drawCard(Graphics g, int x, int y, int cardNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image card = t.getImage((cardNum) + ".png");
		g.drawImage(card, x, y, 100, 150, this);
	}
	
	// draws player cards for blackjack
	public void drawPlayerCardsBlackjack(Graphics g) {
		for (int i = 0; i < blackjack.currentPlayerHand.getCards().size(); i++) {
			drawCard(g, 325 + (i * 110), 500, blackjack.currentPlayerHand.getCards().get(i).getNum() + 1);
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
		drawCard(g, 325, 185, blackjack.dealerHand.getCards().get(0).getNum());
		if (!actionOver) {
			drawCardBack(g, 435, 185);
			g.drawString("" + blackjack.dealerHand.getCards().get(0).getValue(), 650, 150);
		} else {
			for (int i = 0; i < blackjack.dealerHand.getCards().size(); i++) {
				drawCard(g, 325 + (i * 110), 185, blackjack.dealerHand.getCards().get(i).getNum() + 1);
			}
			g.drawString("" + blackjack.dealerHand.getValue(), 650, 150);
		}
	}
	
	// draws player cards for baccarat
	public void drawPlayerCardsBaccarat(Graphics g, boolean show) {
		if (show) {
			for (int i = 0; i < baccarat.playerHand.getCards().size(); i++) {
				drawCard(g, 350 + (i * 115), 500, baccarat.playerHand.getCards().get(i).getNum());
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
				drawCard(g, 350 + (i *115), 200, baccarat.bankerHand.getCards().get(i).getNum());
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
		Toolkit t = Toolkit.getDefaultToolkit();
		Image showDiamond = t.getImage("DiamondShowIcon.png");
		g.drawImage(showDiamond, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawShowMine(Graphics g, int cellNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image showMine = t.getImage("MineShowIcon.png");
		g.drawImage(showMine, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawHideDiamond(Graphics g, int cellNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image hideDiamond = t.getImage("DiamondHideIcon.png");
		g.drawImage(hideDiamond, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}

	public void drawHideMine(Graphics g, int cellNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image hideMine = t.getImage("MineHideIcon.png");
		g.drawImage(hideMine, 346 + (cellNum - 1) % 5 * 126, 111 + (cellNum - 1) / 5 * 125, 118, 118, this);
	}
	
	// draws the cells for dragontower
	public void drawEgg(Graphics g, int colNum, int rowNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image egg = t.getImage("EggTile.png");
		g.drawImage(egg, 450 + (colNum * 106), 243 + (rowNum * 50), 101, 49, this);
	}

	public void drawCheckedCell(Graphics g, int colNum, int rowNum) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image green = t.getImage("GreenTile.png");
		g.drawImage(green, 450 + (colNum * 106), 243 + (rowNum * 50), 101, 38, this);
	}

	// draws dice for the dice game
	public void drawDie(Graphics g, int dieNum, int dieValue) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image die = t.getImage("diceFace" + (dieValue) + ".png");
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
	
	// draws all the cells in a row
	public void drawRowCells(Graphics g) {
		currentRow++;
		for (int row = 8; row >= currentRow; row--) {
			for (int col = 0; col < 4; col++) {
				if (dragontower.grid.get(row).get(col).getEgg()) {
					drawEgg(g, col, row);
				} else if (dragontower.grid.get(row).get(col).getChecked()) {
					drawCheckedCell(g, col, row);
				}
			}
		}
		currentRow--;
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
		if (blackjack.currentPlayerHand.getValue() > blackjack.dealerHand.getValue() || 
				blackjack.dealerHand.getValue() > 21) {
			player.setBalance(player.getBalance() + Game.calculateWinnings(blackjack.currentPlayerHand.getBet(), 1, 1));
			System.out.println(Game.calculateWinnings(blackjack.currentPlayerHand.getBet(), 1, 1));
			player.setProfit(player.getBalance() - player.getInitialBalance());
			player.setWins(player.getWins() + 1);
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Winner");
		} else if (blackjack.currentPlayerHand.getValue() < blackjack.dealerHand.getValue()){
			player.setProfit(player.getProfit() - blackjack.currentPlayerHand.getBet());
			player.setLosses(player.getLosses() + 1);
			repaint();
			JOptionPane.showMessageDialog(myPanel, "Dealer wins");
		} else {
			player.setBalance(player.getBalance() + blackjack.currentPlayerHand.getBet());
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
			if (dragontower != null && dragontower.grid != null && !dragontowerBetting && dragontower.grid.size() > 0) {
				drawAllEggs(g);
			}
			if (dragontower != null && dragontower.grid != null && drawRow && dragontower.grid.size() > 0) {
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
				screen = 0;
				repaint();
				blackjack = null;
				playerAction = false;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 7;
				repaint();
			}
			if (!playerAction && x > 20 && x < 285 && y > 380 && y < 435) {
				blackjack.getPlayerBet();
				blackjack.initializeHands();
				playerAction = true;
				repaint();
				if (blackjack.currentPlayerHand.getBlackjack() && blackjack.dealerHand.getBlackjack()) {
					JOptionPane.showMessageDialog(myPanel, "Push");
					playerAction = false;
					repaint();
				} else if (blackjack.currentPlayerHand.getBlackjack()) {
					JOptionPane.showMessageDialog(myPanel, "Blackjack");
					player.setBalance(player.getBalance() + Game.calculateWinnings(blackjack.currentPlayerHand.getBet(), 3, 2));
					player.setProfit(player.getBalance() - player.getInitialBalance());
					player.setWins(player.getWins() + 1);
					playerAction = false;
					repaint();
				} else if (blackjack.dealerHand.getBlackjack()) {
					JOptionPane.showMessageDialog(myPanel, "Dealer Blackjack");
					player.setProfit(player.getProfit() - blackjack.currentPlayerHand.getBet());
					player.setLosses(player.getLosses() + 1);
					playerAction = false;
					repaint();
				}
			} else if (playerAction && !blackjack.currentPlayerHand.getBust() && x > 20 && x < 147 && y > 243 && y < 298) {
				blackjack.hit();
				repaint();
				if (blackjack.currentPlayerHand.getBust()) {
					JOptionPane.showMessageDialog(myPanel, "Bust");
					playerAction = false;
				}
			} else if (playerAction && x > 157 && x < 287 && y > 243 && y < 298) {
				blackjack.stand();
				playerAction = false;
				determineWinnerBlackjack();
				repaint();
			} else if (playerAction && x > 20 && x < 147 && y > 312 && y < 365) {
				blackjack.doubleDown();
				if (blackjack.currentPlayerHand.getBust()) {
					playerAction = false;
					JOptionPane.showMessageDialog(myPanel, "Bust");
					repaint();
				} else {
					playerAction = false;
					determineWinnerBlackjack();
					repaint();
				}
			} else if (playerAction && blackjack.currentPlayerHand.getPair() && x > 157 && x < 287 && y > 243 && y < 298) {

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
			} else if (x > 20 && x < 285 && y > 240 && y < 295) {
				betting = false;
				baccarat.generateHands();
				baccarat.calcWinnings();
				repaint();
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
			} else if (x > 20 && x < 285 && y > 215 && y < 270) {
				bettingOpen = true;
				bets = poker.getBets();
				repaint();
				poker.getPlayerBets();
				bets = poker.getBets();
				repaint();
			} else if (bettingOpen && x > 20 && x < 285 && y > 300 && y < 350) {
				bettingOpen = false;
				poker.playGame();
				repaint();
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
			} else if (x > 20 && x < 285 && y > 215 && y < 270) {
				dice.overBet();
				dice.generateDice();
				drawDice = true;
				if (dice.betAmount < dice.diceTotal) {
					dice.calculateWinnings();
				}else {
					player.setLosses(player.getLosses() + 1);
					player.setProfit(player.getBalance() - player.getInitialBalance());
				}
				repaint();
			} else if (x > 20 && x < 285 && y > 300 && y < 350) {
				dice.underBet();
				dice.generateDice();
				drawDice = true;
				if (dice.betAmount > dice.diceTotal) {
					dice.calculateWinnings();
				} else {
					player.setLosses(player.getLosses() + 1);
					player.setProfit(player.getBalance() - player.getInitialBalance());
				}
				repaint();
			}
		} else if (screen == 5) {
			if (dragontower == null) {
				dragontower = new Dragontower(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
				dragontower = null;
				currentRow = 0;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 11;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) {
				dragontower.getBet();
				dragontower.generateGrid();
				repaint();
				currentRow = 8;
				dragontowerBetting = true;
				drawRow = false;
			} else if (dragontowerBetting && x > 450 && x < 874 && y > 243 && y < 693) {
				int cellRow = dragontower.getCellRow(y);
				if (cellRow == currentRow) {
					int cellCol = dragontower.getCellCol(x);
					if (dragontower.checkCell(cellRow, cellCol)) {
						for (int i = 0; i < 4; i++) {
							dragontowerCheckedCells.add(dragontower.grid.get(currentRow).get(i));
						}
						drawRow = true;
						currentRow--;
						repaint();
						if (currentRow == -1) {
							currentRow = 0;
							player.setBalance(player.getBalance() + dragontower.calcPayout(currentRow));
							player.setWins(player.getWins() + 1);
							player.setProfit(player.getBalance() - player.getInitialBalance());
							repaint();
							dragontowerBetting = false;
							repaint();
							JOptionPane.showMessageDialog(null, "Winner");
						}
					} else {
						dragontowerBetting = false;
						player.setLosses(player.getLosses() + 1);
						player.setProfit(player.getBalance() - player.getInitialBalance());
						repaint();
						dragontowerCheckedCells.clear();
						repaint();
					}
				}
			} else if (x > 20 && x < 285 && y > 300 && y < 350) {
				dragontowerBetting = false;
				repaint();
				dragontowerCheckedCells.clear();
				player.setBalance(player.getBalance() + dragontower.calcPayout(currentRow));
				player.setWins(player.getWins() + 1);
				player.setProfit(player.getBalance() - player.getInitialBalance());
				repaint();				
			}

		} else if (screen == 6) {
			if (mines == null) {
				mines = new Mines(player);
			}
			if (x > 870 && x < 980 && y > 15 && y < 75) {
				screen = 0;
				repaint();
				mines = null;
			} else if (x > 960 && x < 990 && y > 710 && y < 745) {
				screen = 12;
				repaint();
			} else if (x > 20 && x < 285 && y > 215 && y < 270) {
				mines.getPlayerBet();
				mines.generateGrid(mines.getDiamonds());
				repaint();
				minesBetting = true;
			} else if (minesBetting && x > 345 && x < 965 && y > 112 && y < 732) {
				int cell = mines.getCellNum(x, y);
				if(mines.checkCell(cell)) {
					minesCheckedCells.add(mines.grid.get(cell - 1));
					mines.calculateMultiplier(mines.multipliers.get(mines.checkedCells).get(25 - mines.getDiamonds()));
					if (mines.getCheckedCells() == mines.getDiamonds()) {
						minesBetting = false;
						player.setBalance(player.getBalance() + mines.getBet() + (int)(mines.getBet() * mines.getMultiplier()));
						player.setProfit(player.getBalance() - player.getInitialBalance());
						player.setWins(player.getWins() + 1);
						minesCheckedCells.clear();
						repaint();
					}
					repaint();
				} else {
					player.setLosses(player.getLosses() + 1);
					minesBetting = false;
					repaint();
					minesCheckedCells.clear();
					repaint();
				}
			} else if (minesBetting && x > 20 && x < 285 && y > 300 && y < 350) {
				minesBetting = false;
				player.setBalance(player.getBalance() + (int)(mines.getBet() * mines.getMultiplier()));
				player.setProfit(player.getBalance() - player.getInitialBalance());
				player.setWins(player.getWins() + 1);
				repaint();
				minesCheckedCells.clear();
				repaint();
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
	}
	
	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
	// main
	public static void main(String[] args) {
		Driver myPanel = new Driver();
		JFrame frame = new JFrame("Poker Home Screen");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the program exits when the window is closed
		frame.add(myPanel);
		frame.pack();
		frame.setVisible(true);
		initializePlayers();
	}
}
