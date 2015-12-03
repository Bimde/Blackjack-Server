package gameplay;

import java.io.*;
import java.util.ArrayList;

import connection.Server;
import utilities.ClientList;

/**
 * Handles the actual gameplay, i.e. which player's turn is it, giving the
 * dealer cards, etc.
 *
 */
public class Dealer {
	public static final char[] SUITS = { 'S', 'C', 'H', 'D' };
	public static final char[] RANKS = { 'A', '2', '3', '4', '5', '6', '7',
			'8', '9', 'T', 'J', 'Q', 'K' };
	public static final int NUMBER_OF_DECKS = 6;
	private Deck deck;
	private ClientList clients;
	private Server server;
	private int dealerHand;
	private int currentPlayer;
	public int[] playerCoins;
	public int[] currentBets;

	/**
	 *
	 * @param server
	 * @param clients
	 */
	public Dealer(Server server, ClientList clients) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.currentPlayer = 0;
		this.server = server;
		this.clients = clients;
		this.dealerHand = 0;
	}

	/**
	 * Gets the deck of cards
	 * 
	 * @return The Deck of cards
	 */
	public Deck getDeck() {
		return this.deck;
	}

	/**
	 * Deals cards to the player and the dealer
	 */
	public void deal() {
		// Deal to the dealer first
		if (dealerHand <= 17) {
			char rank = deck.getCard().getRank();

			//If the rank is a character
			if (Character.isLetter(rank)) {
				
				//If the rank is an ace, determine whether the value should be 11 or 1
				if (rank == 'A') {
					if (dealerHand + 11 > 17) {
						dealerHand++;
					} else {
						dealerHand += 11;
					}
				} 
				
				//All other character values (J,K,Q,T) are worth ten
				else {
					dealerHand += 10;
				}
			} 
			
			// If the rank is numeric, add it's value accordingly
			else {
				dealerHand += (int) rank;
			}
		}

		// Deal to each player
		for (int card = 0; card < deck.size(); card++) {
			server.broadcast(deck.getCard().toString());
		}
	}
	
	public void startGame() {
		for (int player = 0; player < playerCoins.length; player++) {
			playerCoins[player] = 1000;
		}
		while (true) { // Keep playing until when?
			this.server.broadcast("NEWROUND");
			int playersBet = 0;
			while (playersBet != clients.size()) {
				for (int player = 0; player < clients.size(); player++) {
					BufferedReader currentIn = clients.get(player).getIn();
					PrintWriter currentOut = clients.get(player).getOut();
					
					try {
						if (currentIn.ready()) {
							currentBets[player] = Integer.parseInt(currentIn.readLine());
							if (currentBets[player] < 10 || currentBets[player] > playerCoins[player]) {
								currentOut.println("% FORMATERROR");
								currentOut.flush();
							} else {
								playersBet++;
							}
						}
					} catch (IOException e) {
						System.out.println("Error getting player's bet");
						e.printStackTrace();
					}
				}
			}
		}
	}
}