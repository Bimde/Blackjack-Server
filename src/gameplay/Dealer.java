package gameplay;

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
	public static final int NUMBER_OF_DECKS = 4;
	private Deck deck;
	private ClientList clients;
	private Server server;
	private int currentPlayer;
	public int[] playerCoins;

	public Dealer(Server server, ClientList clients) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.currentPlayer = 0;
		this.server = server;
		this.clients = clients;
	}

	public Deck getDeck() {
		return this.deck;
	}

	public void deal() {
		for (int card = 0; card < deck.size(); card++) {
			server.broadcast(deck.getCard().toString());
		}
	}
}