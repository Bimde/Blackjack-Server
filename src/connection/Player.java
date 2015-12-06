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

		this.currentCards.add(newCard);
	}

	/**
	 * Clears the current player's hand
	 */
	public void clearHand() {
		this.currentCards.clear();
	}
	
	public int getHandValue() {
		return this.handValue;
	}

	public boolean getIsStanding()
	{
		return this.isStanding;
	}
	
	public void setIsStanding(boolean isStanding)
	{
		this.isStanding = isStanding;
	}
	
	public ArrayList<Card> getCurrentCards() {
		return this.currentCards;
	}

	public void setCurrentCards(ArrayList<Card> currentCards) {
		this.currentCards = currentCards;
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

	public boolean checkBust()
	{
		if(this.getHandValue() > 21)
		{
			this.setIsStanding(true);
			return true;
		}
		else
		{
			return false;
		}
		
	}
}
