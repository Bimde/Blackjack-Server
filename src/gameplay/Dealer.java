package gameplay;

import java.util.ArrayList;

import connection.Client;
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

	/**
	 * Minimum cards required in the deck before the start of the round
	 */
	public static final int MINIMUM_CARDS_PER_PLAYER = 40;

	/**
	 * Chance that the deck will be shuffled at the end of a round (in
	 * percentage)
	 * 
	 * Setting this to 100 means the deck will be shuffled after every turn
	 */
	public static final int SHUFFLE_CHANCE = 20;
	private Server server;
	private Deck deck;
	private ClientList players;
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
		this.players = clients;
		this.dealerHand = 0;
		this.isStand = new boolean[clients.size()];
		this.totalActive = clients.size();

		// Gives each player 1000 coins to start
		for (int player = 0; player < clients.size(); player++) {
			this.players.get(player).setCoins(1000);
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
			for (Client client : this.players) {
				if (client.getIsStanding() != true) {
					this.server.queueMessage("% " + (client.getPlayerNo() + 1) + " turn");
					Card card = this.deck.getCard();
					if (client.getIn().equals("hit")) {
						client.message("# " + (client.getPlayerNo() + 1) + " " + card.toString());
						client.getPlayer().addCard(card);
					} else if (client.getIn().equals("stand")) {
						client.setIsStanding(true);
						this.totalActive--;
					} else if (client.getIn().equals("doubledown")) {

						// If the client double downs, double their bet
						client.setBet(client.getBet() * 2);
						client.message("# " + (client.getPlayerNo() + 1) + " " + this.deck.getCard().toString());
						client.getPlayer().addCard(card);
						client.setIsStanding(true);
						this.totalActive--;
					}
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
			for (int i = 0; i < this.players.size(); i++) {
				this.players.get(i).getPlayer().clearHand();
			}

			// Shuffle deck and broadcast the message
			if (this.deck.size() < MINIMUM_CARDS_PER_PLAYER * this.players.size()
					|| Math.random() * 100 < SHUFFLE_CHANCE) {
				this.deck.reloadDeck();
				this.server.queueMessage("% SHUFFLE");
			}
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