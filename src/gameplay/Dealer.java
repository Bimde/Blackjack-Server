package gameplay;

import java.util.ArrayList;

import connection.Client;
import connection.Player;
import connection.Server;
import utilities.ClientList;

/**
 * Handles the actual gameplay, i.e. which player's turn is it, giving the
 * dealer cards, etc.
 */
public class Dealer implements Runnable {
	public static final char[] SUITS = { 'S', 'C', 'H', 'D' };
	public static final char[] RANKS = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };
	public static final int NUMBER_OF_DECKS = 6;

	/**
	 * Minimum cards required in the deck before the start of the round
	 */
	public static final int MINIMUM_CARDS_PER_PLAYER = 40;

	public static final int BETTING_TIME = 60;
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
	private int totalActive;
	private ArrayList<Card> dealerCards;
	private int dealerHand;
	private Boolean bettingIsActive;
	private int currentPlayerTurn;
	private BettingTimer betTimer;
	private Thread betTimerThread;

	// The timer for the betting period before each round
	private class BettingTimer implements Runnable {
		@Override
		public void run() {
			// Wait 30 seconds and end the betting time
			int bettingElapsedTime = 0;
			while (Dealer.this.bettingIsActive) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bettingElapsedTime += 1000;

				if (bettingElapsedTime >= BETTING_TIME * 1000) {
					synchronized (Dealer.this.bettingIsActive) {
						Dealer.this.bettingIsActive = false;
					}
				}
			}
		}
	}

	/**
	 * Constructs a new Dealer object. Starts up the actual main game of
	 * Blackjack.
	 * 
	 * @param server
	 *            the server that the game is in.
	 * @param players
	 *            a list of all of the player.
	 */
	public Dealer(Server server, ClientList players) {
		this.deck = new Deck(NUMBER_OF_DECKS);
		this.server = server;
		this.players = players;
		this.dealerHand = 0;
		this.totalActive = players.size();
		this.dealerCards = new ArrayList<Card>();

		// Send this dealer object to all the players
		for (Client player : players) {
			player.setDealer(this);
		}

		// Place the betting timer in the thread
		this.betTimer = new BettingTimer();
		this.betTimerThread = new Thread(this.betTimer);
	}

	/**
	 * Begins the game.
	 */
	public void run() {
		while (this.totalActive > 0) {
			// Broadcast that a new round has started
			this.server.queueMessage("% NEWROUND");
			System.out.println("Starting new round...");

			System.out.println("Betting starts now...");
			// Give players 60 seconds to place their bets, and reset all their
			// previous bets
			this.bettingIsActive = true;
			this.betTimerThread = new Thread(new BettingTimer());
			this.betTimerThread.start();
			for (Client player : this.players) {
				if (player.isPlayer()) {
					player.setBet(0);
				}
			}

			// Continually check if everyone has placed a bet, and stop after 30
			// seconds
			while (this.bettingIsActive) {
				boolean allBet = true;
				for (Client currentPlayer : this.players) {
					if (currentPlayer.isPlayer() && currentPlayer.getBet() == 0) {
						allBet = false;
					}
				}
				if (allBet) {
					synchronized (Dealer.this.bettingIsActive) {
						this.bettingIsActive = false;
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				this.betTimerThread.join();
			} catch (InterruptedException e1) {
			}

			// Disconnect all players who haven't bet
			for (Client currentPlayer : this.players) {
				if (currentPlayer.isPlayer() && currentPlayer.getBet() == 0) {
					currentPlayer.disconnect();
					// for now
					// players.remove(currentPlayer);
				}
			}

			// Broadcast the dealer's cards and add them to the dealer's hand
			Card cardDrawn = this.deck.getCard();
			this.dealTheDealer(cardDrawn);
			this.server.queueMessage("# 0 X X");
			cardDrawn = this.deck.getCard();
			this.dealTheDealer(cardDrawn);
			this.server.queueMessage("# 0 " + cardDrawn.toString());

			// Go through each player and deal them two cards each
			for (Client player : this.players) {
				for (int card = 0; card <= 1; card++) {
					cardDrawn = this.deck.getCard();
					player.getPlayer().addCard(cardDrawn);
					this.server.queueMessage("# " + (player.getPlayerNo()) + " " + cardDrawn.toString());
				}
			}

			// Goes through each client
			for (Client currentPlayer : this.players) {
				this.currentPlayerTurn = currentPlayer.getPlayerNo();
				boolean endTurn = false;
				while (!endTurn) {
					currentPlayer.getPlayer().setCurrentMove('N');
					this.server.queueMessage("% " + (currentPlayer.getPlayerNo()) + " turn");

					char currentMove;

					// Wait for a response from the player
					while ((currentMove = currentPlayer.getPlayer().getCurrentMove()) == 'N') {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (currentMove == 'H') {
						// Draw a new card and give it to the player
						cardDrawn = this.deck.getCard();
						this.server.queueMessage("# " + (currentPlayer.getPlayerNo()) + " " + cardDrawn.toString());
						currentPlayer.getPlayer().addCard(cardDrawn);

						if (currentPlayer.getPlayer().getHandValue() > 21) {
							int newCoins = currentPlayer.getCoins() - currentPlayer.getBet();
							currentPlayer.setCoins(newCoins);
							this.server.queueMessage("& " + currentPlayer.getPlayerNo() + " bust " + newCoins);
							if (currentPlayer.getPlayer().getCoins() < Server.MIN_BET) {
								System.out.println("Entered die");
								this.totalActive--;
								this.server.disconnectPlayer(currentPlayer);
							}
							endTurn = true;
						} else if (currentPlayer.getPlayer().getHandValue() == 21) {
							int newCoins = currentPlayer.getCoins() + currentPlayer.getBet();
							currentPlayer.setCoins(newCoins);
							this.server.queueMessage("& " + currentPlayer.getPlayerNo() + " blackjack " + newCoins);
							endTurn = true;
						}

					} else if (currentMove == 'S') {
						endTurn = true;
						this.server.queueMessage(
								"& " + currentPlayer.getPlayerNo() + " stand " + currentPlayer.getCoins());
					} else if (currentMove == 'D' && currentPlayer.getCoins() >= currentPlayer.getBet() * 2) {

						// If the client double downs, double their bet
						currentPlayer.setBet(currentPlayer.getBet() * 2);

						// Draw a new card and give it to the player
						cardDrawn = this.deck.getCard();
						this.server.queueMessage("# " + (currentPlayer.getPlayerNo()) + " " + cardDrawn.toString());
						currentPlayer.getPlayer().addCard(cardDrawn);

						// Change to stand
						endTurn = true;
						currentMove = 'S';

					} else {
						currentPlayer.sendMessage("% FORMATERROR");
					}

				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Show the dealer's hidden card
			this.server.queueMessage("# 0 " + this.dealerCards.get(0));

			// Keep drawing cards for the dealer until the dealer hits 17 or
			// higher
			// Broadcast each card as the dealer draws
			while (this.dealerHand < 17) {

				// Should have a pause of at least 1 second between draws
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cardDrawn = this.deck.getCard();
				this.dealTheDealer(cardDrawn);
				this.server.queueMessage("# 0 " + cardDrawn.toString());
			}

			// Bust the dealer if the dealer gets over 21 (basically guarantee
			// win by making the value -1)
			if (this.dealerHand > 21) {
				this.dealerHand = -1;
			}

			// Check for winners amongst all the players who said to stand
			for (Client player : this.players) {
				if (player.isPlayer() && player.getPlayer().getCurrentMove() == 'S') {
					this.checkResult(player);
				}
			}

			// Loop through each player and get their coins, adding it to the
			// standings string. Broadcasts the string at the end.
			String standings = "+ ";
			for (Client player : this.players) {
				standings += (player.getPlayerNo() + " " + player.getPlayer().getCoins()) + " ";
			}
			this.server.queueMessage(standings);

			// Clear the cards of each player including the dealer
			this.dealerCards.clear();
			for (Client player : this.players) {
				if (player.isPlayer()) {
					player.getPlayer().clearHand();
				}
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
	 * Handles dealing to the dealer and updates the current hand's value.
	 * 
	 * @param card
	 *            the card to deal to the dealer.
	 */
	public void dealTheDealer(Card card) {
		this.dealerCards.add(card);
		int handTotal = 0;

		// If the cards in the hand busts, try to keep deranking aces until it
		// stops busting
		boolean tryDeranking = true;
		while ((handTotal = this.calculateHand(this.dealerCards)) > 21 && tryDeranking) {
			tryDeranking = false;
			for (int cardNo = 0; cardNo < this.dealerCards.size(); cardNo++) {
				if (this.dealerCards.get(cardNo).derankAce()) {
					tryDeranking = true;
					break;
				}
			}
		}

		// Update the dealer's total value
		this.dealerHand = handTotal;
		System.out.println(this.dealerHand);
	}

	/**
	 * Sums up the current value of a hand.
	 * 
	 * @param cards
	 *            a list of cards.
	 */
	private int calculateHand(ArrayList<Card> cards) {
		int total = 0;
		for (int cardNo = 0; cardNo < cards.size(); cardNo++) {
			total += cards.get(cardNo).getValue();
		}
		return total;
	}

	/**
	 * Gets the deck of cards.
	 * 
	 * @return the deck of cards.
	 */
	public Deck getDeck() {
		return this.deck;
	}

	public boolean bettingIsActive() {
		return this.bettingIsActive;
	}

	public int getCurrentPlayerTurn() {
		return this.currentPlayerTurn;
	}

	/**
	 * Checks for the winner.
	 * 
	 * @param playerNo
	 *            the player number of the player to check.
	 */
	public void checkResult(Client client) {
		System.out.println("Check result");
		// If the player gets anything closer to the blackjack than the
		// dealer they win
		Player player = client.getPlayer();
		if (player.getHandValue() > this.dealerHand) {
			player.setCoins(player.getCoins() + player.getCurrentBet());
		} else {
			System.out.println(player.getCoins());
			player.setCoins(player.getCoins() - player.getCurrentBet());
			System.out.println("Entered lose coins");
			System.out.println(player.getCoins());
			if (player.getCoins() < Server.MIN_BET) {
				System.out.println("Entered die");
				this.totalActive--;
				this.server.disconnectPlayer(client);
			}
		}
	}
}