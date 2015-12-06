package gameplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

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
	private ClientList clients;
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
		this.clients = clients;
		this.dealerHand = 0;
		this.totalActive = clients.size() - 1;

		// Gives each player 1000 coins to start
		for (int player = 0; player < clients.size(); player++) {
			this.clients.get(player).setCoins(1000);
		}

		while (this.totalActive > 0) {
			// Broadcast that a new round has started
			this.server.queueMessage("% NEWROUND");

			// Broadcast the dealer's cards and add them to the dealer's hand
			Card cardDrawn = this.deck.getCard();
			this.dealTheDealer(cardDrawn);
			this.server.queueMessage("# 0 X X");
			cardDrawn = this.deck.getCard();
			this.dealTheDealer(cardDrawn);
			this.server.queueMessage("# 0 " + cardDrawn.toString());

			// Go through each player and deal them two cards each
			for (int player = 0; player <= clients.size(); player++) {
				for (int card = 0; card < 1; card++) {
					cardDrawn = this.deck.getCard();
					clients.get(player).getPlayer().addCard(cardDrawn);
					this.server.queueMessage("# " + (player + 1) + " "
							+ cardDrawn.toString());
				}
			}

			// Handle the betting
			boolean allBet = false;

			while (!allBet) {
			}

			// Goes through each client
			for (Client client : this.clients) {
				boolean stand = false;

				while (!stand) {
					this.server.queueMessage("% " + (client.getPlayerNo() + 1)
							+ " turn");

					BufferedReader currentIn = client.getIn();

					try {

						Card card = this.deck.getCard();
						if (client.getIn().equals("hit")) {
							client.message("# " + (client) + " "
									+ card.toString());
							client.getPlayer().addCard(card);
							client.getPlayer().addCard(card);

							// Check if the client has busted
							if (client.getPlayer().checkBust()) {
								totalActive--;

							} else if (currentIn.readLine().equals("stand")) {

								client.getPlayer().setIsStanding(true);
								this.totalActive--;

								// Broadcasts stand
								this.server.queueMessage("& "
										+ client.getPlayer().getPlayerNo()
										+ "stand " + client.getCoins());

							} else if (currentIn.readLine()
									.equals("doubledown")) {
								// If the client double downs, double their bet
								// and
								// then set their isStand to true
								client.setBet(client.getBet() * 2);
								client.message("# " + (client) + " "
										+ this.deck.getCard().toString());
								client.getPlayer().addCard(card);
								client.getPlayer().checkBust();

								// Set the player to standing regardless of bust
								// or
								// not
								client.getPlayer().setIsStanding(true);
								this.totalActive--;
							} else {
								client.message("% FORMATERROR");
							}
						}
					} catch (IOException e) {
						System.err.println("Error getting player's decision");
						e.printStackTrace();
					}
				}
			}

			// Keep drawing cards for the dealer until the dealer hits 17 or
			// higher
			// Broadcast each card as the dealer draws
			while (this.dealerHand < 17) {
				cardDrawn = this.deck.getCard();
				this.dealTheDealer(cardDrawn);
				this.server.queueMessage("# 0 " + cardDrawn.toString());
			}

			// Broadcast the cards in the dealer's hand
			for (int i = 0; i < this.dealerCards.size(); i++) {
				this.server.queueMessage("# 0 "
						+ this.deck.getCard().toString());
			}

			// Check for winners amongst all the clients
			for (int i = 1; i < clients.size(); i++) {
				this.checkWinner(clients.get(i).getPlayer().getPlayerNo());
			}

			// Loop through each player who has not buster (including the
			// dealer)
			// and get their coins, adding it to the standings string. Broadcast
			// the
			// string at the end.
			String standings = "+ ";
			for (int i = 0; i < this.clients.size(); i++) {
				if (!clients.get(i).getPlayer().checkBust()) {
					standings += (i + " " + clients.get(i).getPlayer()
							.getCoins())
							+ " ";
				}
			}
			this.server.queueMessage(standings);

			// Clear the cards of each player including the dealer
			this.dealerCards.clear();
			for (int i = 0; i < this.clients.size(); i++) {
				this.clients.get(i).getPlayer().clearHand();
			}

			// Shuffle deck and broadcast the message
			if (this.deck.size() < MINIMUM_CARDS_PER_PLAYER
					* this.clients.size()
					|| Math.random() * 100 < SHUFFLE_CHANCE) {
				this.deck.reloadDeck();
				this.server.queueMessage("% SHUFFLE");
			}
		}
	}

	/**
	 * Handles dealing to the dealer and updates the current hand's value
	 */
	public void dealTheDealer(Card card) {
		this.dealerCards.add(card);
		int handTotal = 0;

		// If the cards in the hand busts, try to keep deranking aces until it
		// stops busting
		boolean tryDeranking = true;
		while ((handTotal = calculateHand(dealerCards)) > 21 && tryDeranking) {
			tryDeranking = false;
			for (int cardNo = 0; cardNo < dealerCards.size(); cardNo++) {
				if (dealerCards.get(cardNo).derankAce()) {
					tryDeranking = true;
					break;
				}
			}
		}

		// Update the dealer's total value
		dealerHand = handTotal;
	}

	/**
	 * Sums up the current value of a hand
	 */
	public int calculateHand(ArrayList<Card> cards) {
		int total = 0;
		for (int cardNo = 0; cardNo < cards.size(); cardNo++) {
			total = cards.get(cardNo).getValue();
		}
		return total;
	}

	/**
	 * Gets the deck of cards
	 * 
	 * @return The Deck of cards
	 */
	public Deck getDeck() {
		return this.deck;
	}

	public void checkWinner(int playerNo) {

		// If the dealer has a blackjack every player loses
		// All ties go to the dealer
		if (this.dealerHand == 21) {

			// Cycles through each client and sets their coins to their initial
			// amount minus the amount they bet
			for (int i = 0; i < clients.size(); i++) {
				Player temp = clients.get(i).getPlayer();
				clients.get(i).setCoins(temp.getCoins() - temp.getCurrentBet());
			}
		} else {

			// If the player gets anything closer to the blackjack than the
			// dealer they win
			Player player = clients.get(playerNo).getPlayer();
			if (player.getHandValue() > dealerHand && (!player.checkBust())
					|| player.getHandValue() == 21) {

				player.setCoins(player.getCurrentBet() * 2);
				this.server.queueMessage("& " + playerNo + "blackjack "
						+ player.getCoins());
			}

		}
	}
}