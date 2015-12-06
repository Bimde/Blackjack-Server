package connection;

import gameplay.Card;

import java.util.ArrayList;

public class Player {

	private ArrayList<Card> currentCards = new ArrayList<Card>();
	private int handValue;
	private int coins;
	private int playerNo;
	private int currentBet;
	private boolean isStanding;
	private Server server;

	/**
	 * Initialize a new player
	 * 
	 * @param client
	 * @param server
	 * @param playerNo
	 */
	public Player(Server server, int playerNo) {
		this.server = server;
		this.coins = Server.START_COINS;
		this.playerNo = playerNo;
		this.isStanding = false;
	}

	/**
	 * Adds a card to the player's hand
	 * 
	 * @param newCard
	 *            the new card to add to the player's hand
	 */
	public void addCard(Card newCard) {
		
		if (Character.isLetter(newCard.getRank())) {
			
			// If the rank is an ace, determine whether the value should be
			// 11 or 1
			if (newCard.getRank() == 'A') {
				if (this.handValue + 11 > 17) {
					this.handValue++;
				} else {
					this.handValue += 11;
				}
			}

			// All other character values (T, J, Q, K) are worth ten
			else {
				this.handValue += 10;
			}
		} else {
			// If the rank is numeric, add it's value accordingly
			this.handValue += (int) newCard.getRank();
		}

		//Add the card to the player's hand
		this.currentCards.add(newCard);
	}

	/**
	 * Clears the current player's hand
	 */
	public void clearHand() {
		this.currentCards.clear();
	}

	/**
	 * Returns the total value of the player's hand
	 * 
	 * @return Numerical value of the player's hand
	 */
	public int getHandValue() {
		return this.handValue;
	}

	/**
	 * States whether or not the player has stood
	 * 
	 * @return Whether or not the player has stood
	 */
	public boolean getIsStanding() {
		return this.isStanding;
	}

	/**
	 * If the player chooses stand during the round, set their status isStanding
	 * to true. Otherwise isStanding will remain false.
	 * 
	 * @param isStanding
	 *            Dictates if the player has chosen to stand or not
	 */
	public void setIsStanding(boolean isStanding) {
		this.isStanding = isStanding;
	}

	/**
	 * Gets the player's hand (all his or her current cards)
	 * 
	 * @return An array list containing the player's hand.
	 */
	public ArrayList<Card> getCurrentCards() {
		return this.currentCards;
	}

	/**
	 * Sets the players hand to a set of cards
	 * 
	 * @param currentCards
	 *            An array list of cards to be set as the player's new hand
	 */
	public void setCurrentCards(ArrayList<Card> currentCards) {
		this.currentCards = currentCards;
	}

	/**
	 * Gets the number of coins the player has
	 * 
	 * @return The number of coins the player has
	 */
	public int getCoins() {
		return this.coins;
	}

	/**
	 * Sets the number of coins the player will have
	 * 
	 * @param coins
	 *            The number of coins the player will have
	 */
	public void setCoins(int coins) {
		this.coins = coins;
	}

	/**
	 * Gets the player's assigned player number
	 * 
	 * @return The player's assigned player number
	 */
	public int getPlayerNo() {
		return this.playerNo;
	}

	/**
	 * Assigns the player with a player number
	 * 
	 * @param playerNo
	 *            The player number to be assigned
	 */
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	/**
	 * Gets the player's most recent bet
	 * 
	 * @return The player's current bet
	 */
	public int getCurrentBet() {
		return this.currentBet;
	}

	/**
	 * Sets the player's bet
	 * 
	 * @param currentBet
	 *            The most recent bet of the player
	 */
	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}

	/**
	 * Determines whether or not the player has bust
	 * 
	 * @return Whether or not the player has bust
	 */
	public boolean checkBust() {

		// Getting over a 21 hand value means an automatic bust for the player
		if (this.getHandValue() > 21) {

			// Players who bust cannot participate in the round any longer and
			// thus are like players who have chosen stand
			this.setIsStanding(true);

			// Set the player's number of coins to what they had prior bet minus
			// their bet
			this.setCoins(this.getCoins() - this.getCurrentBet());
			return true;
			
		} else {
			return false;
		}

	}
}
