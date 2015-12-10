package connection;

import java.util.ArrayList;

import gameplay.Card;

/**
 * Object for actual players of blackjack servers. Will be put in a client
 * object if said client is a player.
 * 
 * @author Bimesh De Silva, Barbara Guo, William Xu, Patrick Liu
 * @version December 3, 2015
 */
public class Player {
	private ArrayList<Card> currentCards = new ArrayList<Card>();
	private Server server;
	private int handValue;
	private int coins;
	private int playerNo;
	private int currentBet = 0;
	private boolean isStanding;

	/**
	 * 'N' for none, 'H' for hit, 'S' for stand, and 'D' for doubledown
	 */
	private char currentMove;

	/**
	 * Constructor for a new Player object.
	 * 
	 * @param server
	 *            the server to put the new player in.
	 * @param playerNo
	 *            the new player number of the new player.
	 */
	public Player(Server server, int playerNo) {
		this.server = server;
		this.playerNo = playerNo;
		this.coins = Server.START_COINS;
		this.isStanding = false;
		this.currentMove = 'N';
	}

	/**
	 * Adds a card to the player's hand.
	 * 
	 * @param newCard
	 *            the new card to add to the player's hand.
	 */
	public void addCard(Card card) {
		// Add the card to the array of cards
		this.currentCards.add(card);

		// Update the value of the player's hand
		int handTotal = 0;
		this.server.println("Card rank: " + card.getRank());

		// If the cards in the hand busts, try to keep deranking aces until it
		// stops busting
		boolean tryDeranking = true;
		while ((handTotal = calculateHand(this.currentCards)) > 21
				&& tryDeranking) {
			tryDeranking = false;
			for (int cardNo = 0; cardNo < this.currentCards.size(); cardNo++) {
				if (this.currentCards.get(cardNo).derankAce()) {
					tryDeranking = true;
					break;
				}
			}
		}

		// Update the player's total value
		this.handValue = handTotal;
		this.server.println("Hand: " + this.handValue);
	}

	/**
	 * Sums up the current value of a hand.
	 * 
	 * @param cards
	 *            the hand of cards to calculate the value of.
	 */
	private int calculateHand(ArrayList<Card> cards) {
		int total = 0;
		for (int cardNo = 0; cardNo < cards.size(); cardNo++) {
			total += cards.get(cardNo).getValue();
		}
		return total;
	}

	/**
	 * Clears the current player's hand.
	 */
	public void clearHand() {
		this.currentCards.clear();
	}

	/**
	 * Gets the final value of all the cards in the player's hands.
	 * 
	 * @return the value of the player's hand.
	 */
	public int getHandValue() {
		return this.handValue;
	}

	/**
	 * Gets the current move of the player (hit, stand, or double down).
	 * 
	 * @return the current move of the player ('H', 'S', or 'D').
	 */
	public char getCurrentMove() {
		return this.currentMove;
	}

	/**
	 * Sets the current move of the player to either hit, stand, or double down.
	 * 
	 * @param currentMove
	 *            the new move of the player ('H', 'S', or 'D').
	 */
	public void setCurrentMove(char currentMove) {
		this.currentMove = currentMove;
	}

	/**
	 * Gets the player's hand
	 * 
	 * @return a list of the cards in the player's hand in the form of an
	 *         ArrayList of Card objects.
	 */
	public ArrayList<Card> getCurrentCards() {
		return this.currentCards;
	}

	/**
	 * Sets the hand of the player.
	 * 
	 * @param currentCards
	 *            a list of the cards in the player's hand in the form of an
	 *            ArrayList of Card objects..
	 */
	public void setCurrentCards(ArrayList<Card> currentCards) {
		this.currentCards = currentCards;
	}

	public boolean getIsStanding() {
		return this.isStanding;
	}

	public void setStanding(boolean isStanding) {
		this.setCurrentMove('S');
		this.isStanding = isStanding;
	}

	public int getCoins() {
		return this.coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public int getPlayerNo() {
		return this.playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public int getCurrentBet() {
		return this.currentBet;
	}

	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}
}