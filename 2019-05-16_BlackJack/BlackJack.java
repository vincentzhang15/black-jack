/**
 * Vincent Zhang
 * May 16, 2019
 * Black Jack game. More detailed description will follow.
 * ICS3U7-02 Mr. Anthony
 */
// PLEASE MARK THIS ASSIGNMENT IN CONSOLE AND PLEASE TURN ON SOUND

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// Cards class for organizing deck of cards
class Cards
{
	private ArrayList<String> deck;
	
	/* Constructor Method: Cards()
	 * Purpose: initializations.
	 * Pre: none.
	 * Post: initializations complete. */
	public Cards()
	{
		deck = new ArrayList<String>();
		generateDeck();
	}
	
	/* Method: generateDeck().
	 * Purpose: make a deck of cards with suits and values.
	 * Pre: none.
	 * Post: deck of cards generated. */
	private void generateDeck()
	{
		for(int i = 2; i <= 14; i++) // 2 to 14(Ace)
			for(int j = 1; j <= 4; j++) // 1(Clubs) to 4(Spades)
				deck.add((j==1)?("D"+((i < 10)?(i):((i==10)?("T"):((i==11)?("J"):((i==12)?("Q"):((i==13)?("K"):((i==14)?("A"):("ERROR")))))))):
						((j==2)?("C"+((i < 10)?(i):((i==10)?("T"):((i==11)?("J"):((i==12)?("Q"):((i==13)?("K"):((i==14)?("A"):("ERROR")))))))):
						((j==3)?("H"+((i < 10)?(i):((i==10)?("T"):((i==11)?("J"):((i==12)?("Q"):((i==13)?("K"):((i==14)?("A"):("ERROR")))))))):
						((j==4)?("S"+((i < 10)?(i):((i==10)?("T"):((i==11)?("J"):((i==12)?("Q"):((i==13)?("K"):((i==14)?("A"):("ERROR")))))))):
						("ERROR")))));
	}
	
	/* Method: drawCard().
	 * Purpose: draw a card from the deck, referesh deck if cards less than 10.
	 * Pre: none
	 * Post: card drawn. */
	public String drawCard()
	{
		if(deck.size() < 10)
			generateDeck();
		
		String card = deck.get(0);
		deck.remove(0);
		
		return card;
	}
	
	/* Method: returnDeck()
	 * Purpose: give deck to where requested.
	 * Pre: there is a deck
	 * Post: deck of cards returned to where requested. */
	public ArrayList<String> returnDeck()
	{
		return deck;
	}
	
	/* Method: shuffleDeck().
	 * Purpose: shuffle the deck of cards.
	 * Pre: there is a deck.
	 * Post: current deck shuffled. */
	public void shuffleDeck()
	{
		while(true)
		{
			boolean exit = false;
			Collections.shuffle(deck);
			// Prevent aces to apear within 8 cards of each other because 4 starting 4 more draws
			for(int i = 8; i < deck.size(); i++)
			{
				String s = "";
				for(int j = i-8; j <= i; j++)
					s += deck.get(j);
				
				int count = 0;
				for(int j = 0; j < s.length(); j++)
					if(s.charAt(j) == 'A')
						count++;
					
				if(count > 1)
				{
					exit = true;
					break;
				}
			}
			if(!exit)
				return;
		}
	}

	/* Method: printDeck().
	 * Purpose: print elements in the deck.
	 * Pre: there is a deck.
	 * Post: current deck printed. */
	public void printDeck()
	{
		for(String s : deck)
			System.out.print(s + " ");
		System.out.println();
	}
}

// Player class for game interaction
class Player
{
	// Instantiations/Delcarations
	public ArrayList<String> hand;
	private boolean hasAce;
	
	/* Constructor Method: Player().
	 * Purpose: initializations.
	 * Pre: none.
	 * Post: initializations complete. */
	public Player()
	{
		hand = new ArrayList<String>();
		hasAce = false;
	}
	
	/* Method: returnHand().
	 * Purpose: return list containing cards.
	 * Pre: there's something in the hand.
	 * Post: hand returned. */
	public ArrayList<String> returnHand()
	{
		return hand;
	}
	
	/* Method: takeCard().
	 * Purpose: add card to hand.
	 * Pre: none.
	 * Post: card added to hand. */
	public void takeCard(String s)
	{
		hand.add(s);
	}
	
	/* Method: getScore().
	 * Purpose: calculate score of hand.
	 * Pre: ace is 0 or 1.
	 * Post: score calculated for the hand. */
	public int getScore(int ace)
	{
		// When ace is 0, ace is worth 11 points. When ace is 1, ace is worth 1 point
		int score = 0;
		for(String s : hand)
		{
			char c = s.charAt(1);
			if(c == 'A')
			{
				if(ace == 1 || hasAce)
				{
					hasAce = true; // Ace is worth 1 point for every card draw after the if statement has been entered
					score += 1;
				}
				else
				{
					score += 11;
				}
			}
			else if(c == 'K' || c == 'Q' || c == 'J' || c == 'T') // Face cards and Ten worth 10 points
				score += 10;
			else
				score += Integer.parseInt(s.charAt(1)+""); // Number cards worth their number value
		}
		
		return score;
	}

	/* Method: toString().
	 * Purpose: override default toString to output object.
	 * Pre: none
	 * Post: player victory outputted. */
	public String toString()
	{
		return "You Win!";
	}
}

// Dealer calss inherits player class
class Dealer extends Player
{
	// Declaration
	private String name;
	
	/* Constructor Method: Dealer().
	 * Purpose: initialization.
	 * Pre: none.
	 * Post: initialization complete. */
	public Dealer()
	{
		name = "Dealer";
	}

	/* Method: toString().
	 * Purpose: override default toString to output object.
	 * Pre: none
	 * Post: dealer victory outputted. */
	public String toString()
	{
		// Congratulate dealer on winning
		return name + " Win!";
	}

}

// Main Class
public class BlackJack
{
	// Instantiations/Delcarations/Initializations
	private static ArrayList<String> layout = new ArrayList<String>();
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private Cards cards = new Cards();
	private boolean calledUpdateTable = false;
	private boolean chooseStay;
	private boolean dealerAce;
	private boolean playerAce;
	private int dealerScore = 0;
	private int playerScore = 0;
	private static int dealerLine = 0;
	private static int playerLine = 0;
	
	/* Method: printLayout().
	 * Purpose: print homepage layout.
	 * Pre: none.
	 * Post: homepage layout printed. */
	private void printLayout() throws IOException
	{
		// Declaration / Initialization.
		final int SCREEN_WIDTH = 80; // Width between two double bar lines.
		try{
			new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor(); // Clear console screen.
		} catch(Exception e){};
		
		// Print ArrayList layout.
		for(int i = 0; i < layout.size(); i++)
		{
			String s = layout.get(i);
			System.out.print("||"); // Left double bar.
			for(int j = 0; j < ((SCREEN_WIDTH - s.length())/2)+1; j++) // Left spaces.
				System.out.print(" ");
			System.out.print(s.substring(0, s.length()-1)); // ArrayList content.
			for(int j = 0; j < SCREEN_WIDTH - s.length() - ((SCREEN_WIDTH - s.length())/2); j++) // Right spaces.
				System.out.print(" ");
			System.out.print("||\n"); // Right double bar.
			if(s.charAt(s.length()-1) == '-') // Horizontal rule.
			{
				System.out.print("||");
				for(int j = 0; j < SCREEN_WIDTH; j++)
					System.out.print("-");
				System.out.print("||\n");
			}
		}
	}
	
	/* Method: playSound().
	 * Purpose: play a sound.
	 * Pre: sound file exists.
	 * Post: sound file played. */
	private void playSound(File sound)
	{
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start(); // Play .wav file
			
			Thread.sleep(clip.getMicrosecondLength()/1000); // Freeze activity to allow file to play
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
	}

	/* Method: addDeck().
	 * Purpose: add deck of cards to layout.
	 * Pre: none.
	 * Post: deck of cards added to layout on 4 separate lines of 13 cards each. */
	private void addDeck()
	{
		ArrayList<String> currentDeck = cards.returnDeck();
		String deckString = "";
		for(int i = 0; i < currentDeck.size(); i++)
		{
			deckString += currentDeck.get(i) + " ";
			if((i+1) % 13 == 0) // If there are thirteen cards, add a new group of cards
			{
				layout.add((i==currentDeck.size()-1)?deckString+"-":deckString); // Add a dash to indicate horizontal rule if it is last iteration
				deckString = "";
			}
		}
	}
	
	/* Method: giveCard().
	 * Purpose: give a card to dealer if who is 0 and player if who is 1 while keeping track if dealer or player received an ace.
	 * Pre: who is 0 or 1.
	 * Post: player or dealer given card, who got an ace is recorded. */
	private String giveCard(int who)
	{
		String card = cards.drawCard(); // Draw card
		if(card.charAt(1) == 'A')
		{
			if(who == 0)
				dealerAce = true; // Dealer has an ace
			else
				playerAce = true; // Player has an ace
		}
		return card; // Give card
	}
	
	/* Method: updateTable().
	 * Purpose: change cards on table according to most recent move.
	 * Pre: none.
	 * Post: layout changed to current card status. */
	private void updateTable(Dealer dealer, Player player) throws IOException
	{
		// Instantiations
		ArrayList<String> dealerHand = dealer.returnHand();
		ArrayList<String> playerHand = player.returnHand();
		
		// If first time called this method, layout has not been added to yet
		if(!calledUpdateTable)
		{
			// Space or dash requred after every layout add for layout printing
			layout.add("Dealer Score: " + dealerScore + " ");
			dealerLine = layout.size(); // Get where the first line of dealer information starts so it can be changed upon second call
			layout.add(dealerHand.get(0) + " ? ");
			layout.add(" ");
			layout.add(playerHand.get(0) + " " + playerHand.get(1) + " ");
			playerLine = layout.size(); // Get where the first line of player information is
			layout.add("Player Score: " + playerScore + "-");
		}
		else // If not the first time called, elements already exit in layout, change elements instead of add
		{
			// Declarations
			String dHand = "";
			String pHand = "";
			
			// Add cards into dealer and player hand
			for(String s : dealerHand)
				dHand += s + " ";
			for(String s : playerHand)
				pHand += s + " ";
			
			// Update scores
			layout.set(dealerLine-1, "Dealer Score: " + dealerScore + " ");
			layout.set(playerLine, "Player Score: " + playerScore + "-");
			layout.set(dealerLine, dHand + ((!chooseStay)?"? ":"")); // If player chooses stay, second dealer card is revealed
			layout.set(playerLine-1, pHand);
		}
		calledUpdateTable = true;
	}
	
	/* Method: playGame().
	 * Purpose: start Black Jack game.
	 * Pre: none.
	 * Post: game started. */
	private void playGame() throws IOException
	{
		// Instantiation of Objects
		Player player = new Player();
		Dealer dealer = new Dealer();
		
		// Delcarations/Initializations
		boolean visitedPlayer = false;
		boolean visitedDealer = false;
		dealerAce = false;
		playerAce = false;
		chooseStay = false;
		playerScore = 0;
		dealerScore = 0;

		// Start Table
		player.takeCard(giveCard(1));
		playerScore = player.getScore(0);
		player.takeCard(giveCard(1));
		playerScore = player.getScore(0);
		dealer.takeCard(giveCard(0));
		dealerScore = dealer.getScore(0);
		
		// Display Startup
		updateTable(dealer, player);
		printLayout();
		
		// Proccess Game
		while(true)
		{
			if(playerScore > 21)
			{
				// If player has Ace, if changing Ace value makes a difference, and if Ace is still worth 11 points
				if(playerAce && playerScore-10 <= 21 && !visitedPlayer)
				{
					playerScore = player.getScore(1); // Calculate player score with Ace as 1 point
					visitedPlayer = true;
				}
				else // Player is busted
				{
					updateTable(dealer, player);
					printLayout();
					System.out.println(dealer);
					break;
				}
			}
			else if(dealerScore > 21)
			{
				// If dealer has Ace, if changing Ace value makes a difference, and if Ace is still worth 11 points
				if(dealerAce && dealerScore-10 <= 21 && !visitedDealer)
				{
					dealerScore = dealer.getScore(1); // Calculate dealer score with Ace as 1 point
					visitedDealer = true;
				}
				else
				{
					updateTable(dealer, player);
					printLayout();
					
					// Congratulate Player
					System.out.println(player);
					playSound(new File("YouWin.wav"));
					break;
				}
			}
			else if(playerScore == 21)
			{
				updateTable(dealer, player);
				printLayout();

				// Congratulate Player
				System.out.println(player);
				playSound(new File("YouWin.wav"));
				break;
			}
			else if(dealerScore == 21)
			{
				updateTable(dealer, player);
				printLayout();
				System.out.println(dealer);
				break;
			}
			
			// Update data
			updateTable(dealer, player);
			printLayout();

			// Get player choice of hit or stay
			String sInput = "S"; // Default value to stay
			if(!chooseStay) // If stay is chosen, choice of hit or stay is not given
			{
				while(true)
				{
					System.out.print("Hit or Stay? (H/S): ");
					sInput = br.readLine();
					
					if(sInput.equals("H") || sInput.equals("S"))
						break;
					System.out.println("Enter 'H' or 'S'.");
				}
			}
			
			if(sInput.equals("S"))
			{
				chooseStay = true;
				String draw = giveCard(0); // 0 represent give dealer a card
				System.out.println("Dealer got: " + draw);
				dealer.takeCard(draw);
				dealerScore = dealer.getScore(0); // 0 represent dealer score with ace as value 11
			}
			else if(sInput.equals("H"))
			{
				String draw = giveCard(1); // 1 represent give player a card
				player.takeCard(draw);
				if(visitedPlayer)
					playerScore = player.getScore(1); // Ace is worth 1 point since player entered bust zone already
				else
					playerScore = player.getScore(0); // Ace is worth 11 points since player did not ever exceed 21 points
			}
		}
	}
	
	/* Method: homePage().
	 * Purpose: set up game.
	 * Pre: none.
	 * Post: game set up. */
	private void homePage() throws IOException
	{
		// Print Decks
		layout.add("Starting Deck-");
		addDeck();
		layout.add("Shuffled Deck-");
		cards.shuffleDeck();
		addDeck();
		
		// Shuffle deck after displaying a shuffled deck
		cards.shuffleDeck();

		// Salutations
		while(true)
		{
			System.out.print("Welcome Player, please enter your name: ");
			String name = br.readLine();
			if(name.length() > 1)
			{
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				layout.add("Welcome " + name + "-");
				break;
			}
		}

		// Output Initializations
		printLayout();
		
		// Play sound to welcome player
		playSound(new File("Welcome.wav"));
		
		// Play Game
		while(true)
		{
			// Process Game
			playGame();
			
			// Display final deck then shuffle
			System.out.print("Current deck: ");
			cards.printDeck();
			cards.shuffleDeck();
			System.out.println();
			
			// Game Ends
			System.out.print("Type \"QUIT\" to stop playing, anything else to play again: ");
			String quit = br.readLine();
			if(quit.equals("QUIT"))
			{
				// Program Ends
				System.out.println("Thank you for playing!");
				
				// Bid Farewell
				playSound(new File("HaveANiceDay.wav"));

				// Exit Program
				System.exit(0);
			}
		}
	}
	
	/* Method: main().
	 * Purpose: initialize output screen.
	 * Pre: none.
	 * Post: add content to layout and create an object. */
	public static void main(String [] args) throws IOException
	{
		// Add homepage layout to ArrayList.
		layout.add("    ____  __           __          __           __   ");
		layout.add("   / __ )/ /___ ______/ /__       / /___ ______/ /__ ");
		layout.add("  / __  / / __ `/ ___/ //_/  __  / / __ `/ ___/ //_/ ");
		layout.add(" / /_/ / / /_/ / /__/ ,<    / /_/ / /_/ / /__/ ,<    ");
		layout.add("/_____/_/\\__,_/\\___/_/|_|   \\____/\\__,_/\\___/_/|_|  -");
		layout.add("Vincent Zhang | May 16, 2019 | ICS3U7-02-");
		layout.add("This  version  of  \"Black Jack\" is  a game where a single player plays ");
		layout.add("against  the  dealer.  The objective of the game is to get as close to ");
		layout.add("21  points as possible. All face cards are worth 10 points. All number ");
		layout.add("cards  are  worth  their  number  value.  Ace is worth 1 or 11 points, ");
		layout.add("whichever  benefits  the player. When the game starts, both player and ");
		layout.add("dealer is given two cards. The player's  cards are visible to only the ");
		layout.add("player  and  only  the  dealer's  first card is visible to the player. ");
		layout.add("Player can  hit  or  stand.  If  the player choose hit, they are given ");
		layout.add("another card. If  their  total  card value goes above 21 points player ");
		layout.add("looses.  The  player  can  choose  to stand. After which, the dealer's ");
		layout.add("second card  is  revealed and the dealer continues to draw cards until ");
		layout.add("the dealer goes bust or scores  above player score. The game ends when ");
		layout.add("the  user  does  not  wish  to  play. Notation: Clubs(C), Diamonds(D), ");
		layout.add("Hearts(H),  Spades(S),  Ten(T),  Jack(J),  Queen(Q),  King(K), Ace(A).-");

		BlackJack obj = new BlackJack(); // Create obj of class BlackJack.
		obj.homePage(); // Call method of object / instance of class BlackJack so references are made from non-static context.
	}
}