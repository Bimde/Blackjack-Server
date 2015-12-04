package gameplay;

import java.util.ArrayList;
import java.io.*;

import connection.Client;
import connection.Player;
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
	private ArrayList<Card> dealerCards;
	private Deck deck;
	private ArrayList<Client> clients;
	private boolean[] isStand;
	private Server server;
	private int dealerHand;
	private int totalActive;

	/**
	 *
	 * @param server
	 * @param clients
	 */
	public Dealer(Server server, ArrayList<Client> clients) {

		this.deck = new Deck(NUMBER_OF_DECKS);
		this.server = server;
		this.clients = clients;
		this.dealerHand = 0;
		this.isStand = new boolean[clients.size()];
		this.totalActive = clients.size();

		// Deal to the dealer first
		// Handle the situation where the first card drawn is also an ace
		Card temp = this.deck.getCard();
		if (Character.isLetter(temp.getRank())) {
			if (temp.getRank() == 'A') {
				if (this.dealerHand + 11 > 17) {
					this.dealerHand++;
				} else {
					this.dealerHand += 11;
				}
			} else {
				this.dealerHand += 10;
			}
		}

		// If the rank is numeric, add it's value accordingly
		else {
			this.dealerHand += (int) temp.getRank();
		}

		// If the dealer has a blackjack the round ends immediately
		// Otherwise continue normally
		if (this.dealerHand == 21) {
			this.server.broadcast("% NEWROUND");
			this.server.broadcast("% SHUFFLE");
		} else {
			// Broadcast this to the clients
			this.server.broadcast("# 0 X X");
			this.server.broadcast("# 0" + " " + temp.toString());

			// Go through each player and deal them two cards each
			for (int card = 0; card <= clients.size(); card++) {
				this.server.broadcast("# " + this.deck.getCard().toString());
				this.server.broadcast("# " + this.deck.getCard().toString());

			}
		}

	}

	/**
	 * Handles dealing for the dealer
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

		this.dealerCards.add(card);
	}

	/**
	 * Gets the deck of cards
	 * 
	 * @return The Deck of cards
	 */
	public Deck getDeck() {
		return this.deck;
	}

	//TODO
	public void findWinner() {

	}

	/**
	 * Actual game play. Starts and runs the game.
	 */
	private void startGame() {

		// Assigns each player a number of coins
		for (int player = 0; player < clients.size(); player++) {
			this.clients.get(player).setCoins(1000);
		}

		// While the number of clients available is over 0 keep playing
		while (this.clients.size() > 0) {
			this.server.broadcast("NEWROUND");
			int playersBet = 0;
			int clientBet = 0;
			while (playersBet != clients.size()) {
				for (int player = 0; player < clients.size(); player++) {
					Client currentPlayer = clients.get(player);
					BufferedReader currentIn = currentPlayer.getIn();
					PrintWriter currentOut = currentPlayer.getOut();

					try {
						if (currentIn.ready()) {
							currentPlayer.setBet(Integer.parseInt(currentIn
									.readLine()));
							clientBet = Integer.parseInt(currentIn.readLine());
							if (currentPlayer.getBet() < 10
									|| currentPlayer.getBet() > currentPlayer
											.getCoins()) {
								currentOut.println("% FORMATERROR");
								currentOut.flush();
							} else {
								playersBet++;
								server.broadcast("$ " + (currentPlayer)
										+ "bets " + currentPlayer.getBet());
							}
						}
					} catch (IOException e) {
						System.out.println("Error getting player's bet");
						e.printStackTrace();
					}
				}
			}

			// While at least more than one player is willing to hit
			while (this.totalActive >= 0) {
				// Dealer must take cards while less than 17
				if (this.dealerHand <= 17) {
					this.dealerDeal(this.deck.getCard());
				}

				// Goes through each client
				for (int client = 0; client < this.isStand.length; client++) {

					if (this.isStand[client] != true) {
						this.server.broadcast("% " + (client + 1) + " turn");
						// timer needed here

						Card card = this.deck.getCard();
						if (this.clients.get(client).getIn().equals("hit")) {
							this.clients.get(client)
									.message(
											"# " + (client + 1) + " "
													+ card.toString());
							this.clients.get(client).getPlayer().addCard(card);
						} else if (this.clients.get(client).getIn()
								.equals("stand")) {
							this.isStand[client] = true;
							this.totalActive--;
						} else if (this.clients.get(client).getIn()
								.equals("doubledown")) {
							this.clients.get(client).setBet(clientBet * 2);
							this.clients.get(client).message(
									"# " + (client + 1) + " "
											+ this.deck.getCard().toString());
							this.clients.get(client).getPlayer().addCard(card);
						}
					}

					// Reloads the deck when there aren't enough cards
					if (this.deck.size() <= 6) {
						this.deck.reloadDeck();
					}
				}

			}

			// Broadcast the cards in the dealer's hand
			for (int i = 0; i < this.dealerCards.size(); i++) {
				this.server.broadcast("# 0 " + this.deck.getCard().toString());
			}

			this.findWinner();

			// Clear the cards of each player including the dealer
			this.dealerCards.clear();
			for (int i = 0; i < this.clients.size(); i++) {
				this.clients.get(i).getPlayer().clearHand();
			}

			// Shuffle deck and broadcast the message
			this.deck.reloadDeck(); // Requirements for when to shuffle?
			this.server.broadcast("% SHUFFLE");
		}
	}
}