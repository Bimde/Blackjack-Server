package connection;

import java.util.ArrayList;

import gameplay.Card;

public class Player {

	private ArrayList<Card> currentCards = new ArrayList<Card>();
	private int handValue;
	private int coins;
	private int playerNo;
	private int currentBet = 0;
	private boolean isStanding;

	// 'N' for none, 'H' for hit, 'S' for stand, and 'D' for doubledown
	private char currentMove;

	/**
	 * Initialize a new player
	 * 
	 * @param client
	 * @param server
	 * @param playerNo
	 */
	public Player(Server server, int playerNo) {
		this.coins = Server.START_COINS;
		this.playerNo = playerNo;
		this.isStanding = false;
		this.currentMove = 'N';
	}

	/**
	 * Adds a card to the player's hand
	 * 
	 * @param newCard
	 *            the new card to add to the player's hand
	 */
	public void addCard(Card card) {
		this.currentCards.add(card);
		int handTotal = 0;
		System.out.println("Card rank: " + card.getRank());

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
		System.out.println("Hand: " + this.handValue);
	}

	/**
	 * Sums up the current value of a hand
	 */
	private int calculateHand(ArrayList<Card> cards) {
		int total = 0;
		for (int cardNo = 0; cardNo < cards.size(); cardNo++) {
			total += cards.get(cardNo).getValue();
		}
		return total;
	}

	/**
	 * Clears the current player's hand
	 */
	public void clearHand() {
		this.currentCards.clear();
	}

	/**
	 * Get the final value of all the cards in the player's hands
	 * 
	 * @return The value of the player's hand
	 */
	public int getHandValue() {
		return this.handValue;
	}

	/**
	 * Returns the current move of the player (hit, stand, or double down)
	 * 
	 * @return The current decision of the player
	 */
	public char getCurrentMove() {
		return this.currentMove;
	}

	/**
	 * Sets the current move of the player to either hit, stand, or double down
	 * 
	 * @param currentMove
	 *            The current move specified by the player (hit, stand, or
	 *            double down)
	 */
	public void setCurrentMove(char currentMove) {
		this.currentMove = currentMove;
	}

	/**
	 * Determines if the player is standing or not
	 * 
	 * @return Whether or not the player's current move is stand
	 */
	public boolean getIsStanding() {
		return this.isStanding;
	}

	/**
	 * Sets the player's current move to stand and it's current state for
	 * isStanding to true
	 * 
	 * @param isStanding
	 *            Dictates the state of the player (stand or not stand)
	 */
	public void setStanding(boolean isStanding) {
		this.setCurrentMove('S');
		this.isStanding = isStanding;
	}

	/**
	 * Gets the player's hand
	 * 
	 * @return All of the cards in the player's hand in an array list
	 */
	public ArrayList<Card> getCurrentCards() {
		return this.currentCards;
	}

	/**
	 * Sets the hand of the player
	 * 
	 * @param currentCards
	 *            An array list containing cards to be put into the player's
	 *            hand
	 */
	public void setCurrentCards(ArrayList<Card> currentCards) {
		this.currentCards = currentCards;
	}

	/**
	 * Get the number of coins that a player has
	 * 
	 * @return The number of coins that a player has
	 */
	public int getCoins() {
		return this.coins;
	}

	/**
	 * Sets the number of coins for the player
	 * 
	 * @param coins
	 *            The new number of coins for the player
	 */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/**
	 * Gets the assigned player number
	 * @return The player number of the player
	 */
	public int getPlayerNo() {
		return this.playerNo;
	}

	/**
	 * Assigns the player number
	 * @param playerNo The player number for the player
	 */
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	/**
	 * Gets the current bet of the player
	 * @return The current bet of the player
	 */
	public int getCurrentBet() {
		return this.currentBet;
	}

	/**
	 * Sets the current bet for the player
	 * @param currentBet The current bet for the player
	 */
	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}

}
