package gameplay;

import java.util.ArrayList;

import connection.Client;
import connection.Server;

/**
 * Handles the actual gameplay, i.e. which player's turn is it, giving the
 * dealer cards, etc.
 *
 */
public class Dealer {
	public static final String[] SUITS = { "S", "C", "H", "D" };
	public static final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
	public static final int NUMBER_OF_DECKS = 4;
	private Deck deck;
	private ArrayList<Client> clients;
	private Server server;
	private int currentPlayer;
	public int[] playerCoins;

	public Dealer(Server server, ArrayList<Client> clients) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.currentPlayer = 0;
		this.server = server;
		this.clients = clients;
	}

	public Deck getDeck() {
		return this.deck;
	}
}
