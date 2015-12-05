package gameplay;

import java.util.ArrayList;

import connection.Server;
import utilities.ClientList;

/**
 * Handles the actual gameplay, i.e. which player's turn is it, giving the
 * dealer cards, etc.
 */
public class Dealer {
	public static final char[] SUITS = { 'S', 'C', 'H', 'D' };
	public static final char[] RANKS = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };
	public static final int NUMBER_OF_DECKS = 6;
	private Server server;
	private Deck deck;
	private ClientList clients;
	private boolean[] isStand;
	private int totalActive;
	private ArrayList<Card> dealerCards;
	private int dealerHand;

	/**
	 * Initializes a new Dealer object. Starts up the actual main game of
	 * Blackjack.
	 * 
	 * @param server
	 *            the server that the game is in.
	 * @param clients
	 *            a list of all of the clients.
	 */
	public Dealer(Server server, ClientList clients) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.server = server;
		// TODO Shouldn't the clients be only the players?
		// The clients getting passed in from the server is the list of actual
		// clients rather than player
		this.clients = clients;
		this.dealerHand = 0;
		this.isStand = new boolean[clients.size()];
		this.totalActive = clients.size();

		// Gives each player 1000 coins to start
		for (int player = 0; player < clients.size(); player++) {
			this.clients.get(player).setCoins(1000);
		}

		while (this.totalActive > 0) {
			// Broadcast that a new round has started
			this.server.queueMessage("% NEWROUND");

			// Broadcast the dealer's cards and add them to the dealer's hand
			Card cardDrawn = this.deck.getCard();
			this.dealerDeal(cardDrawn);
			this.server.queueMessage("# 0 X X");
			cardDrawn = this.deck.getCard();
			this.dealerDeal(cardDrawn);
			this.server.queueMessage("# 0 " + cardDrawn.toString());

			// Go through each player and deal them two cards each
			for (int player = 0; player <= clients.size(); player++) {
				for (int card = 0; card < 1; card++) {
					cardDrawn = this.deck.getCard();
					clients.get(player).getPlayer().addCard(cardDrawn);
					this.server.queueMessage("# " + (player + 1) + " " + cardDrawn.toString());
				}
			}

			boolean allBet = false;

			while (!allBet) {
			}

			// Goes through each client
			for (int client = 0; client < this.isStand.length; client++) {
				if (this.clients.get(client).getIsStanding() != true) {
					this.server.queueMessage("% " + (client + 1) + " turn");
					Card card = this.deck.getCard();
					if (this.clients.get(client).getIn().equals("hit")) {
						this.clients.get(client).message("# " + (client + 1) + " " + card.toString());
						this.clients.get(client).getPlayer().addCard(card);
					} else if (this.clients.get(client).getIn().equals("stand")) {
						this.clients.get(client).setIsStanding(true);
						this.totalActive--;
					} else if (this.clients.get(client).getIn().equals("doubledown")) {

						// If the client double downs, double their bet
						this.clients.get(client).setBet(this.clients.get(client).getBet() * 2);
						this.clients.get(client).message("# " + (client + 1) + " " + this.deck.getCard().toString());
						this.clients.get(client).getPlayer().addCard(card);
						this.clients.get(client).setIsStanding(true);
						this.totalActive--;
					}
				}

				// Reloads the deck when there aren't enough cards
				if (this.deck.size() <= 6) {
					this.deck.reloadDeck();
				}
			}

			// Keep drawing cards for the dealer until the dealer hits 17 or
			// higher
			// Broadcast each card as the dealer draws
			while (this.dealerHand <= 17) {
				cardDrawn = this.deck.getCard();
				this.dealerDeal(cardDrawn);
				this.server.queueMessage("# 0 " + cardDrawn.toString());
			}

			this.findWinner();

			// Clear the cards of each player including the dealer
			this.dealerCards.clear();
			for (int i = 0; i < this.clients.size(); i++) {
				this.clients.get(i).getPlayer().clearHand();
			}

			// Shuffle deck and broadcast the message
			this.deck.reloadDeck(); // Requirements for when to shuffle?
			this.server.queueMessage("% SHUFFLE");
		}
	}

	/**
	 * Handles dealing to the dealer
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

			// All other character values (T, J, Q, K) are worth ten
			else {
				this.dealerHand += 10;
			}
		} else {
			// If the rank is numeric, add it's value accordingly
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

	// TODO
	public void findWinner() {

	}
}