import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class BaldEagle
{
	// CONSTANTS
	public static final int TABLE_HEIGHT = (int)(Card.CARD_HEIGHT * 4.5);
	public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 9) + 100;
	public static final int NUM_FINAL_DECKS = 4;
	public static final int NUM_PLAY_DECKS = 8;
	public static final Point DECK_POS = new Point(5, 5);
	public static final Point SHOW_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 5, DECK_POS.y);
	public static final Point FINAL_POS = new Point(SHOW_POS.x + Card.CARD_WIDTH + 5, DECK_POS.y);
	public static final Point PLAY_POS = new Point(DECK_POS.x, FINAL_POS.y + Card.CARD_HEIGHT + 30);

	// GAMEPLAY STRUCTURES
	private static FinalStack[] foundationStacks;// foundation stacks
	private static CardStack[] tableauStacks; // tableau stacks
	private static CardStack reserve; // reserve pile
	private static final Card newCardPlace = new Card();// waste card spot
	private static CardStack deck; // populated with standard 52 card deck
	private static CardStack waste; // waste pile

	// GUI COMPONENTS (top level)
	private static final JFrame frame = new JFrame("Bald Eagle Solitaire");
	protected static final JPanel table = new JPanel();
	// other components
	private static JEditorPane gameTitle = new JEditorPane("text/html", "");
	private static JButton showRulesButton = new JButton("Show Rules");
	private static JButton newGameButton = new JButton("New Game");
	private static JButton toggleTimerButton = new JButton("Pause Timer");
	private static JButton mainMenuButton = new JButton("Main Menu");
	private static JTextField scoreBox = new JTextField(); // displays the score
	private static JTextField timeBox = new JTextField(); // displays the time
	private static JTextField statusBox = new JTextField(); // status messages
	private static JTextField stockWasteBox = new JTextField();
	private static JTextField redealsBox = new JTextField();
	private static final Card newCardButton = new Card(); // reveal waste card
	private static Card reserveCard = new Card(); // reserve card

	// TIMER UTILITIES
	private static Timer timer = new Timer();
	private static ScoreClock scoreClock = new ScoreClock();

	// MISC TRACKING VARIABLES
	private static boolean timeRunning = false;// timer running?
	private static int score = 0;// keep track of the score
	private static int time = 0;// keep track of seconds elapsed
	private static int newGameCount = 0;
	private static int redeals = 2;
	private static java.util.List<Card.Suit> usedSuits = new java.util.ArrayList<>(); // used suits in Foundations
	private static GameStatus gamestatus;

	// moves a card to abs location within a component
	protected static Card moveCard(Card c, int x, int y)
	{
		c.setBounds(new Rectangle(new Point(x, y), new Dimension(Card.CARD_WIDTH + 10, Card.CARD_HEIGHT + 10)));
		c.setXY(new Point(x, y));
		return c;
	}

	// add/subtract points based on gameplay actions
	protected static void setScore(int deltaScore)
	{
		BaldEagle.score += deltaScore;
		String newScore = "Score: " + BaldEagle.score;
		scoreBox.setText(newScore);
		scoreBox.repaint();
	}

	// GAME TIMER UTILITIES
	protected static void updateTimer()
	{
		stockWasteBox.setText("Stock: " + deck.stackSize() + "  Waste: " + waste.stackSize());
		stockWasteBox.repaint();
		redealsBox.setText("ReDeals: " + redeals);
		redealsBox.repaint();

		time += 1;
		// every 10 seconds elapsed we take away 2 points
		if (time % 10 == 0)
		{
			setScore(-2);
		}

		String timeText = "Seconds: " + time;
		timeBox.setText(timeText);
		timeBox.repaint();
	}

	protected static void startTimer()
	{
		scoreClock = new ScoreClock();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}

	// the pause timer button uses this
	protected static void toggleTimer()
	{
		if (timeRunning && scoreClock != null)
		{
			scoreClock.cancel();
			timeRunning = false;
		}
		else
		{
			startTimer();
		}
	}

	private static class ScoreClock extends TimerTask
	{
		@Override
		public void run()
		{
			updateTimer();
		}
	}

	// BUTTON LISTENERS
	private static class NewGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			playNewGame();
		}

	}

	private static class ToggleTimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			toggleTimer();
			if (!timeRunning)
			{
				toggleTimerButton.setText("Start Timer");
			} else
			{
				toggleTimerButton.setText("Pause Timer");
			}
		}

	}

	private static class ShowRulesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JDialog ruleFrame = new JDialog(frame, true);
			ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ruleFrame.setSize(TABLE_WIDTH, TABLE_HEIGHT);
			JScrollPane scroll;
			JEditorPane rulesTextPane = new JEditorPane("text/html", "");
			rulesTextPane.setEditable(false);
			String rulesText = "<b>Bald Eagle Solitaire Rules</b>"
					+ "<br><br>[From Goodsol]<br><br><b>Objective</b><br>To move all cards to the foundations.<br><br>"
					+ "<b>Layout</b><br><\u2022 4 foundation piles (top) - build up in suit from the rank of first "
					+ "card dealt to the first pile, wrapping from King to Ace as  necessary, until each pile contains "
					+ "13 cards.<br><br>\u2022 8 tableau piles (the wings) - build down in suit, limit of 3 cards per "
					+ "pile, wrapping from Ace to King as necessary. Groups of cards may be moved as as a unit if they "
					+ "are in sequence down in suit. Spaces are filled from the reserve until it is empty, then by any "
					+ "card. At the start of the game, 1 card is dealt face up to each pile.<br><br>\u2022 reserve "
					+ "(middle) - top card is available for play on the foundations or tableau. At the start of the "
					+ "game, 17 cards are dealt to this pile.<br><br>\u2022 stock (face down, top left) - turn over 3 "
					+ "cards at a time to the waste by clicking during the first deal, 2 cards at a time during the "
					+ "2nd deal, and 1 card at a time during the last deal. Two redeals.<br><br>\u2022 waste (next to "
					+ "stock) - top card is available for play on the foundations or tableau.<br><br><b>Scoring</b><br>"
					+ "Moving cards directly from the Waste stack to a Foundation awards 10 points. However, if the "
					+ "card is first moved to a Tableau, and then to a Foundation, then an extra 5 points are received "
					+ "for a total of 15. Thus in order to receive a maximum score, no cards should be moved directly "
					+ "from the Waste to Foundation.<p>Time can also play a factor in Windows Solitaire, if the Timed "
					+ "game option is selected. For every 10 seconds of play, 2 points are taken away."
					+ "<b><br><br>Implementation Notes</b><br>"
					+ "Drag cards to and from any stack (only the Foundation cannot be dragged from). As long as the "
					+ "move is valid, the card or stack of cards will be repositioned in the desired spot. The game "
					+ "follows the standard scoring and time model explained above with only one waste card shown at a "
					+ "time.<p>The timer starts running as soon as the game begins, but it may be paused by pressing "
					+ "the pause button at the bottom of the screen.";
			rulesTextPane.setText(rulesText);
			rulesTextPane.setCaretPosition(0);
			ruleFrame.add(new JScrollPane(rulesTextPane));

			ruleFrame.setVisible(true);
		}
	}

	private static class mainMenuListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.setVisible(false);
			frame.dispose();
			MainMenu.open();
		}
	}

	/*
	 * This class handles all of the logic of moving the Card components as well
	 * as the game logic. This determines where Cards can be moved according to
	 * the rules of Bald Eagle solitaire
	 */
	private static class CardMovementManager extends MouseAdapter
	{
		private Card prevCard = null; // tracking card for waste stack
		private Card movedCard = null; // card moved from waste stack
		private boolean checkForWin = false; // should we check if game is over?
		private boolean gameOver = true; // easier to negate this than affirm it
        private int isWaste = -1;
		private Point start = null; // where mouse was clicked
		private Point stop = null; // where mouse was released
		private Card card = null; // card to be moved
		// used for moving single cards
		private CardStack source = null;
		private CardStack dest = null;
		// used for moving a stack of cards
		private CardStack transferStack = new CardStack(false);
		private int cursX, cursY;

		private boolean validPlayStackMove(Card source, Card dest)
		{
			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();
			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();

			// destination card should be one higher value and be same suit
			if (s_val == 12 && d_val == 0 && s_suit == d_suit) { return true; }
			return (s_val+1) == d_val && s_suit == d_suit;
		}

		private boolean validFinalStackMove(Card source, Card dest)
		{
			int s_val = source.getValue().ordinal();
			int d_val = dest.getValue().ordinal();
			Card.Suit s_suit = source.getSuit();
			Card.Suit d_suit = dest.getSuit();
			// destination must be one lower and same suit
			if (s_val == 0 && d_val == 12 && s_suit == d_suit) { return true; }
			return s_val == (d_val + 1) && s_suit == d_suit;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			start = e.getPoint();
			statusBox.setText("");
			transferStack.makeEmpty();
			isWaste = -1;
			boolean sourceObtained = false;

			/*
			 * Here we use transferStack to temporarily hold all the cards above
			 * the selected card in case player wants to move a stack rather
			 * than a single card
			 */
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				if (sourceObtained)
				{
					break;
				}

				source = tableauStacks[x];
				// pinpointing exact card pressed
				for (Component ca : source.getComponents())
				{
					Card c = (Card) ca;
					if (c.getFaceStatus() && source.contains(start))
					{
						transferStack.putFirst(c);
					}
					if (c.contains(start) && source.contains(start) && c.getFaceStatus())
					{
						card = c;
						System.out.println("Card pressed: " + c.getSuit() + " (" + c.getValue() + ")");
						System.out.println("Transfer Size: " + transferStack.stackSize());
						System.out.println("[1] source: " + source);
						sourceObtained = true;
						break;
					}
				}
			}

			if (transferStack != null)
			{
				System.out.println("Transfer stack size: " + transferStack.stackSize());
			}
			if (transferStack.stackSize() == 1)
			{
				movedCard = transferStack.getLast();
			}

			// SHOW (WASTE) CARD OPERATIONS
			// display new show card
			if (newCardButton.contains(start) && deck.stackSize() > 0)
			{
				int turnOvers = redeals + 1;
				System.out.println("Deck size: " + deck.stackSize());
				System.out.println("Deck: " + deck);
				System.out.println("popping deck " + turnOvers + " times");

				for (int i = 0; i < turnOvers; i++)
                {
                    prevCard = waste.getLast();
					if (prevCard != null)
					{
                        System.out.println("Removing prevCard: " + prevCard.getSuit() + " (" + prevCard.getValue() + ")");
						table.remove(prevCard);
					}

                    Card c = deck.pop();

                    if (c == null)
                    {
                    	prevCard.setFaceup();
						System.out.println("Deck size: " + deck.stackSize());
						table.add(moveCard(prevCard, SHOW_POS.x, SHOW_POS.y));
						prevCard.repaint();
						table.repaint();
						break;
                    }
                    else if (c != null)
					{
						c.setFaceup();
						System.out.println("Deck size: " + deck.stackSize());
						table.add(moveCard(c, SHOW_POS.x, SHOW_POS.y));
						c.repaint();
						table.repaint();
						waste.push(c);
						prevCard = c;
					}
                }

				System.out.println("Deck " + deck);
			}
			else if (newCardButton.contains(start) && deck.stackSize() == 0 && redeals > 0)
            {
				System.out.println("Waste: " + waste);
				System.out.println("Putting back on deck stack: ");
				int wasteSize = waste.stackSize();
				prevCard = waste.getLast();
				table.remove(prevCard);

				for (int i = 0; i < wasteSize; i++)
				{
					Card c = waste.pop();
					deck.push(c);
					c.repaint();
					table.repaint();
				}

				System.out.println("Deck: " + deck);
				if (redeals > 0)
				{
					redeals--;
				}
            }

			// preparing to move show card TODO fix waste stack coordinates
			newCardPlace.setXY(new Point(SHOW_POS.x, SHOW_POS.y));
			System.out.println("newCardPlace coords: " + newCardPlace.getXY().x + ", " + newCardPlace.getXY().y);
			prevCard = waste.getLast();
			if (newCardPlace.contains(start) && prevCard != null)
			{
				movedCard = prevCard;
				System.out.println("prevCard: " + movedCard.getSuit() + " (" + movedCard.getValue() + ")");
				isWaste = 1;
			}

			// RESERVE CARD OPERATIONS
			if (reserveCard.contains(start))
			{
			    isWaste = 0;
			    System.out.println("Reserve: " + reserve);
				movedCard = reserveCard;
			}
		}

		// TODO dragging animation
		public void mouseDragged(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();
			if (transferStack == null && card != null)
            {
                cursX = start.x - card.getWhereAmI().x;
                cursY = start.y - card.getWhereAmI().y;
            }
            if (transferStack != null)
            {
                Point p = e.getPoint();
                transferStack.setXY(p.x - cursX, p.y - cursY);

                table.repaint();
                transferStack.repaint();
            }
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			stop = e.getPoint();
			// used for status bar updates
			boolean validMoveMade = false;
			// TODO waste stack
			if (card != null)
			{
				System.out.println("[1] card: " + card.getSuit() + " (" + card.getValue() + ")");
			}
			if (source != null)
			{
				System.out.println("[2] source: " + source);
			}
			if (movedCard != null)
			{
				System.out.println("[1] movedCard: " + movedCard.getSuit() + " (" + movedCard.getValue() + ")");
			}
			System.out.println(isWaste);

			// SHOW (WASTE) CARD OPERATIONS
			if (movedCard != null && isWaste==1)
			{
				// Moving from SHOW TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++)
				{
					dest = tableauStacks[x];
					// to empty play stack
					if (dest.empty() && movedCard != null && dest.contains(stop))
					{
						System.out.println("moving new card to empty spot");
						movedCard.setXY(dest.getXY());
						validMoveMade = relocateWasteCard(true);
						break;
					}
					// to populated play stack
					if (movedCard != null && dest.contains(stop) && !dest.empty() && dest.getFirst().getFaceStatus()
							&& validPlayStackMove(movedCard, dest.getFirst()) && dest.stackSize() < 3)
					{
						System.out.println("moving new card");
						movedCard.setXY(dest.getFirst().getXY());
						validMoveMade = relocateWasteCard( true);
						break;
					}
				}
				// Moving from SHOW TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = foundationStacks[x];
					// only cards with a different suit
                    // matching the value of the first card in the Foundation can go first
					if (dest.empty() && dest.contains(stop))
					{
						if (movedCard.getValue() == foundationStacks[0].getFirst().getValue()
                                && !usedSuits.contains(movedCard.getSuit()))
						{
							usedSuits.add(movedCard.getSuit());
						    validMoveMade = relocateWasteCard(false);
							break;
						}
					}
					if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCard, dest.getLast()))
					{
						System.out.println("Destination size: " + dest.stackSize());
						validMoveMade = relocateWasteCard(false);
						checkForWin = true;
						break;
					}
				}
			} // END WASTE STACK OPERATIONS
			else if (card != null && source != null) // TABLEAU STACK OPERATIONS
			{ // Moving SINGLE card from PLAY TO PLAY
				for (int x = 0; x < NUM_PLAY_DECKS; x++)
				{
					dest = tableauStacks[x];
					// MOVING TO POPULATED STACK TODO be able to a card from a tableau stack to a different tableau stack
					if (!dest.empty())
					{
						System.out.println("Destination first: " + dest.getFirst().getSuit() + " (" + dest.getFirst().getValue() + ")");
					}
					if (card.getFaceStatus() && dest.contains(stop) && source != dest && !dest.empty()
                            && dest.stackSize() < 3 && validPlayStackMove(card, dest.getFirst())
                            && transferStack.stackSize() == 1)
					{
						Card c = source.popFirst();
						System.out.println("[1] Tableau card being moved: " + c.getSuit() + " (" + c.getValue() + ")");
						c.repaint();

						// if stack of cards, turn next card up
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();
						table.repaint();

						System.out.println("Destination Size: " + dest.stackSize());
						setScore(10);
						if (source.getFirst() == null) { fillEmptyTableau(); }
						validMoveMade = true;

						break;
					}
					else if (dest.empty() && dest.contains(stop) && transferStack.stackSize() == 1)
					{ // MOVING TO EMPTY STACK
						Card c = source.popFirst();
						System.out.println("[2] Tableau card being moved: " + c.getSuit() + " (" + c.getValue() + ")");
						c.repaint();

						// if playstack, turn next card up
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.putFirst(c);

						dest.repaint();
						table.repaint();

						System.out.println("Destination Size: " + dest.stackSize());
						setScore(5);
						if (source.getFirst() == null) { fillEmptyTableau(); }
						validMoveMade = true;

						break;
					}
					// Moving STACK of cards from PLAY TO PLAY
					// to EMPTY STACK
					if (dest.empty() && dest.contains(stop) && transferStack.stackSize() > 1)
					{
						System.out.println("Card to Empty Stack Transfer");
						while (!transferStack.empty())
						{
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							Card c = source.popFirst();
							System.out.println("[3] Tableau card being moved: " + c.getSuit() + " (" + c.getValue() + ")");
						}
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						table.repaint();
						setScore(5);
						if (source.getFirst() == null) { fillEmptyTableau(); }
						validMoveMade = true;

						break;
					}
					// to POPULATED STACK
					else if (!dest.empty() && dest.contains(stop) && dest.stackSize() < 3
                            && transferStack.stackSize() > 1 && (dest.stackSize() + transferStack.stackSize()) < 4
                            && source.contains(start) && validPlayStackMove(transferStack.getFirst(), dest.getFirst()))
					{
						System.out.println("Regular Stack Transfer");
						while (!transferStack.empty())
						{
							System.out.println("popping from transfer: " + transferStack.getFirst().getValue());
							dest.putFirst(transferStack.popFirst());
							Card c = source.popFirst();
							System.out.println("[4] Tableau card being moved: " + c.getSuit() + " (" + c.getValue() + ")");
						}
						if (source.getFirst() != null)
						{
							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.repaint();

						table.repaint();
						setScore(5);
						if (source.getFirst() == null) { fillEmptyTableau(); }
						validMoveMade = true;

						break;
					}
				}
				// from PLAY TO FINAL
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = foundationStacks[x]; // TODO be able to move top of a stack of tableau cards to an existing foundation stack
					if (!dest.empty())
					{
						System.out.println("Destination last: " + dest.getLast().getSuit() + " (" + dest.getLast().getValue() + ")");
					}

					if (card.getFaceStatus() && source != null && dest.contains(stop) && source != dest)
					{ // TO EMPTY STACK
						System.out.println("move to empty final");
						if (dest.empty()) // empty final should only take card dealt to first pile of the foundation
						{
							System.out.println("final empty");
							if (card.getValue() == foundationStacks[0].getFirst().getValue() && !usedSuits.contains(card.getSuit()))
							{
								System.out.println("card matches first foundation card");
								Card c = source.popFirst();
								System.out.println("[5] Card to final: " + card.getSuit() + ", " + card.getValue());
								usedSuits.add(c.getSuit());
								c.repaint();

								if (source.getFirst() != null)
								{
									Card temp = source.getFirst().setFaceup();
									temp.repaint();
									source.repaint();
								}

								dest.setXY(dest.getXY().x, dest.getXY().y);
								dest.push(c);

								dest.repaint();
								table.repaint();

								System.out.println("Destination Size: " + dest.stackSize());
								card = null;
								setScore(10);
								if (source.getFirst() == null) { fillEmptyTableau(); }
								validMoveMade = true;

								break;
							} // TO POPULATED STACK
						}
						else if (validFinalStackMove(card, dest.getLast()))
						{
							Card c = source.popFirst();
							System.out.println("[6] Tableau card being moved: " + c.getSuit() + " (" + c.getValue() + ")");
							c.repaint();
							if (source.getFirst() != null)
							{
								Card temp = source.getFirst().setFaceup();
								temp.repaint();
								source.repaint();
							}

							dest.setXY(dest.getXY().x, dest.getXY().y);
							dest.push(c);

							dest.repaint();
							table.repaint();

							System.out.println("Destination Size: " + dest.stackSize());
							card = null;
							checkForWin = true;
							setScore(10);
							if (source.getFirst() == null) { fillEmptyTableau(); }
							validMoveMade = true;

							break;
						}
					}
				}
			} // end cycle through tableau decks
            if (movedCard != null && isWaste==0) // RESERVE CARD OPERATIONS
            {
                // Moving from RESERVE to TABLEAU
                for (int x = 0; x < NUM_PLAY_DECKS; x++)
                {
                    dest = tableauStacks[x];
                    // to empty play stack
                    if (dest.empty() && movedCard != null)
					{
						System.out.println("moving reserve card to empty spot");
						movedCard.setXY(dest.getXY());
						validMoveMade = relocateReserveCard(true);
						break;
					}
                    // to populated play stack
                    if (movedCard != null && dest.contains(stop) && !dest.empty()
                            && dest.getFirst().getFaceStatus()
                            && validPlayStackMove(movedCard, dest.getFirst()) && dest.stackSize() < 3)
                    {
                        System.out.println("moving reserve card to tableau");
                        movedCard.setXY(dest.getFirst().getXY());
                        validMoveMade = relocateReserveCard( true);
                        break;
                    }
                }
                // Moving from RESERVE TO FOUNDATION
                for (int x = 0; x < NUM_FINAL_DECKS; x++)
                {
                    dest = foundationStacks[x];
                    // only cards with a different suit
                    // matching the value of the first card in the Foundation can go first
                    if (dest.empty() && dest.contains(stop))
                    {
                        if (movedCard.getValue() == foundationStacks[0].getFirst().getValue()
                                && !usedSuits.contains(movedCard.getSuit()))
                        {
                            usedSuits.add(movedCard.getSuit());
                            relocateReserveCard(false);
                            break;
                        }
                    }
                    if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCard, dest.getLast()))
                    {
                        System.out.println("Destination size: " + dest.stackSize());
                        relocateReserveCard(false);
                        checkForWin = true;
                        break;
                    }
                }
            } // END RESERVE STACK OPERATIONS

			// SHOWING STATUS MESSAGE IF MOVE INVALID
			if (!validMoveMade && dest != null && card != null)
			{
				statusBox.setText("Invalid Move");
			}
			else if (validMoveMade)
			{
				statusBox.setText("Valid move");
			}

			// CHECKING FOR WIN
			if (checkForWin)
			{
				// cycle through final decks, if they're all full then game over
				for (int x = 0; x < NUM_FINAL_DECKS; x++)
				{
					dest = foundationStacks[x];
					if (dest.stackSize() != 13)
					{
						// one deck is not full, so game is not over
						gameOver = false;
						break;
					}
					else
                    {
                        gameOver = true;
                    }
				}

			}

			if (checkForWin && gameOver)
			{
				JOptionPane.showMessageDialog(table, "Congratulations! You've Won!");
				statusBox.setText("Game Over!");
			}
			// RESET VARIABLES FOR NEXT EVENT
			start = null;
			stop = null;
			source = null;
			dest = null;
			card = null;
			checkForWin = false;
			gameOver = false;
		}

		// update waste card location
		private boolean relocateWasteCard(boolean toTableau)
		{
			if (toTableau)
			{
				dest.putFirst(movedCard);
				setScore(5);
			}
			else if (!toTableau) // to foundation
			{
				dest.push(movedCard);
				setScore(10);
			}

			waste.pop();
			if (movedCard != null)
			{
                System.out.println("Removing movedCard: " + movedCard.getSuit() + " (" + movedCard.getValue() + ")");
                table.remove(movedCard);
            }

			Card newWasteCard = waste.getLast();
			if (newWasteCard != null)
			{
				newWasteCard.setFaceup();
				table.add(moveCard(newWasteCard, SHOW_POS.x, SHOW_POS.y));
				newWasteCard.repaint();
			}

			table.repaint();
			movedCard = newWasteCard;
			isWaste = -1;

			return true;
		}

		// update reserve card location
        private boolean relocateReserveCard(boolean toTableau)
        {
            if (toTableau)
            {
                dest.putFirst(movedCard);
                setScore(5);
            }
            else if (!toTableau) // to foundation
            {
                dest.push(movedCard);
                setScore(10);
            }

            reserve.pop();
            if (movedCard != null)
            {
                table.remove(movedCard);
            }

            Card newReserveCard = reserve.getLast();
            if (newReserveCard != null)
            {
                newReserveCard.setFaceup();
                table.add(moveCard(newReserveCard, DECK_POS.x + (int)(3.5 * (Card.CARD_WIDTH + 10)), PLAY_POS.y));
                newReserveCard.repaint();
            }

            table.repaint();
            reserveCard = newReserveCard;
            return true;
        }

		// fill empty tableau stacks from reserve
		private void fillEmptyTableau()
		{
			System.out.println("reserve card: " + reserveCard.getSuit() + " (" + reserveCard.getValue() + ")");
			movedCard = reserveCard;
			isWaste = 0;
		}
	}

	private static void playNewGame()
	{
		deck = new CardStack(true); // deal 52 cards
		deck.shuffle();
		table.removeAll();

		redeals = 2;
		// initialize waste
		if (waste != null)
		{
			waste.makeEmpty();
		}
		waste = new CardStack(false);

		// reset stacks if user starts a new game in the middle of one
		if (tableauStacks != null && foundationStacks != null)
		{
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				tableauStacks[x].makeEmpty();
			}
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				foundationStacks[x].makeEmpty();
			}
		}

		// initialize & place final (foundation) decks/stacks
		foundationStacks = new FinalStack[NUM_FINAL_DECKS];
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			foundationStacks[x] = new FinalStack();

			foundationStacks[x].setXY((FINAL_POS.x + (x * (Card.CARD_WIDTH+10))) + 10, FINAL_POS.y);
			table.add(foundationStacks[x]);

		}

		// deal one card face up to foundation's first pile
		Card first = deck.pop().setFaceup();
		usedSuits.clear();
		usedSuits.add(first.getSuit());
		foundationStacks[0].putFirst(first);

		// initialize & place reserve
        if (reserve != null)
        {
            reserve.makeEmpty();
        }

        reserve = new CardStack(false);

        // deal 17 cards to reserve pile
        for (int x = 0; x < 17; x++)
        {
            reserveCard = deck.pop().setFaceup();
            reserve.push(reserveCard);
        }

		// place stock pile
		table.add(moveCard(newCardButton, DECK_POS.x, DECK_POS.y));

        // place movable reserve card
		table.add(moveCard(reserveCard, DECK_POS.x + (int)(3.5 * (Card.CARD_WIDTH + 10)), PLAY_POS.y));

		// initialize & place play (tableau) decks/stacks
		tableauStacks = new CardStack[NUM_PLAY_DECKS];
		for (int x = 0; x < NUM_PLAY_DECKS / 2; x++)
		{
			tableauStacks[x] = new CardStack(false);
			tableauStacks[x].setXY((DECK_POS.x + (x * (Card.CARD_WIDTH + 10))), PLAY_POS.y + (x * 55));

			table.add(tableauStacks[x]);
		}

		int position = NUM_PLAY_DECKS / 2 - 1;
		for (int x = NUM_PLAY_DECKS / 2; x < NUM_PLAY_DECKS; x++)
		{
			tableauStacks[x] = new CardStack(false);
			tableauStacks[x].setXY((DECK_POS.x + (x * (Card.CARD_WIDTH + 10))), PLAY_POS.y + (position * 55));

			table.add(tableauStacks[x]);
			position--;
		}

		// Deal 1 card face up to each tableau pile
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			Card c = deck.pop().setFaceup();
			tableauStacks[x].putFirst(c);
		}

		// reset time and score
		time = 0;
		score = 0;
		timer.cancel();
		timer = new Timer();

		mainMenuButton.setBounds(0, TABLE_HEIGHT - 78, 120, 40);
		mainMenuButton.addActionListener(new mainMenuListener());

		if (newGameCount == 0)
		{
			newGameButton.addActionListener(new NewGameListener());
			newGameButton.setBounds(120, TABLE_HEIGHT - 78, 120, 40);
			showRulesButton.addActionListener(new ShowRulesListener());
			showRulesButton.setBounds(240, TABLE_HEIGHT - 78, 120, 40);
			newGameCount++;
		}

		gameTitle.setText("<b><font face=\"Arial\" size=\"14\">Bald Eagle Solitaire</font></b>");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);
		gameTitle.setBounds(730, 10, 300, 150);

		scoreBox.setBounds(360, TABLE_HEIGHT - 78, 120, 40);
		scoreBox.setText("Score: " + score);
		scoreBox.setHorizontalAlignment(JTextField.CENTER);
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		timeBox.setBounds(480, TABLE_HEIGHT - 78, 120, 40);
		timeBox.setText("Seconds: " + time);
		timeBox.setHorizontalAlignment(JTextField.CENTER);
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		startTimer();

		toggleTimerButton.setBounds(600, TABLE_HEIGHT - 78, 125, 40);
		toggleTimerButton.addActionListener(new ToggleTimerListener());

		statusBox.setBounds(725, TABLE_HEIGHT - 78, 180, 40);
		statusBox.setHorizontalAlignment(JTextField.CENTER);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);

		stockWasteBox.setBounds(0, TABLE_HEIGHT - 103, 120, 25);
		stockWasteBox.setText("Stock: " + deck.stackSize() + "  Waste: " + waste.stackSize());
		stockWasteBox.setHorizontalAlignment(JTextField.CENTER);
		stockWasteBox.setEditable(false);
		stockWasteBox.setOpaque(false);

		redealsBox.setBounds(120, TABLE_HEIGHT - 103, 120, 25);
		redealsBox.setText("ReDeals: " + redeals);
		redealsBox.setHorizontalAlignment(JTextField.CENTER);
		redealsBox.setEditable(false);
		redealsBox.setOpaque(false);

		table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(newGameButton);
		table.add(showRulesButton);
		table.add(scoreBox);
		table.add(mainMenuButton);
		table.add(stockWasteBox);
		table.add(redealsBox);
		table.repaint();
	}

	public static void main(String[] args)
	{
		Container contentPane;
		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(new Color(0, 180, 180));

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); // center frame on screen

		playNewGame();

		table.addMouseListener(new CardMovementManager());
		table.addMouseMotionListener(new CardMovementManager());

		frame.setVisible(true);

	}
}