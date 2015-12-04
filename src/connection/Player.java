package connection;

import gameplay.Card;

import java.util.ArrayList;

public class Player {

	private ArrayList<Card> currentCards = new ArrayList<Card>();
	private int coins;
	private int playerNo;
	private int currentBet;
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
	}

	/**
	 * Adds a card to the player's hand
	 * 
	 * @param newCard
	 *            the new card to add to the player's hand
	 */
	public void addCard(Card newCard) {
		this.currentCards.add(newCard);
	}

	/**
	 * Clears the current player's hand
	 */
	public void clearHand() {
		this.currentCards.clear();
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

}
