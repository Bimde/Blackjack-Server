package gameplay;

import java.util.ArrayList;
import java.io.*;

import connection.Client;
import connection.Server;
import utilities.ClientList;

/**
 * Handles the actual gameplay, i.e. which player's turn is it, giving the
 * dealer cards, etc.
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

	/**
	 *
	 * @param server
	 * @param clients
	 */
	public Dealer(Server server, ClientList clients) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.server = server;
		this.clients = clients;
		this.dealerHand = 0;

		// Deal to the dealer first
		// Handle the situation where the first card drawn is also an ace
		Card temp = this.deck.getCard();
		if (Character.isLetter(temp.getRank())) {

			// If the rank is an ace, determine whether the value should be
			// 11 or 1
			if (temp.getRank() == 'A') {
				if (this.dealerHand + 11 > 17) {
					this.dealerHand++;
				} else {
					this.dealerHand += 11;
				}
			}

			// All other character values (J,K,Q,T) are worth ten
			else {
				this.dealerHand += 10;
			}
		}

		// If the rank is numeric, add it's value accordingly
		else {
			this.dealerHand += (int) temp.getRank();
		}
		this.server.broadcast("# 0 X X");
		this.server.broadcast("# 0" + " " + temp.toString());

		// Go through each player and deal them two cards each
		for (int card = 0; card <= clients.size(); card++) {
			this.server.broadcast("# " + this.deck.getCard().toString());
			this.server.broadcast("# " + this.deck.getCard().toString());

		}

	}

	/**
	 * Special case for the dealer
	 */
	public void dealerDeal(Card card) {
		if (Character.isLetter(card.getRank())) {

			// If the rank is an ace, determine whether the value should be
			// 11 or 1
			if (card.getRank() == 'A') {
				if (this.dealerHand + 11 > 17) {
					this.dealerHand++;
				} else {
					this.dealerHand += 11;
				}
			}

			// All other character values (J,K,Q,T) are worth ten
			else {
				this.dealerHand += 10;
			}
		}

		// If the rank is numeric, add it's value accordingly
		else {
			this.dealerHand += (int) card.getRank();
		}
	}

	public void circle() {
		// Each round go through each client that has not stood and ask hit or
		// not
		// Ask for bets once

		// Go around and ask for hits
		// If hit is requested deal

		if (dealerHand <= 17) {
			dealerDeal(this.deck.getCard());
		}
		for (int client = 0; client < clients.size(); client++) {
			this.server.broadcast("% " + (client + 1) + " turn");
			// There are some timer specifics that need to put here I guess

		}

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
	 * Deals cards to the player or the dealer if hit is requested
	 */
	public void hit() {
		this.server.broadcast("#" + " " + this.deck.getCard().toString());
	}

	private void startGame() {
		for (int player = 0; player < clients.size(); player++) {
			clients.get(player).setCoins(1000);
		}
		while (clients.size() > 0) { // Keep playing until when?
			this.server.broadcast("NEWROUND");
			int playersBet = 0;
			while (playersBet != clients.size()) {
				for (int player = 0; player < clients.size(); player++) {
					Client currentPlayer = clients.get(player);
					BufferedReader currentIn = currentPlayer.getIn();
					PrintWriter currentOut = currentPlayer.getOut();

					try {
						if (currentIn.ready()) {
							currentPlayer.setBet(Integer.parseInt(currentIn
									.readLine()));
							if (currentPlayer.getBet() < 10
									|| currentPlayer.getBet() > currentPlayer
											.getCoins()) {
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