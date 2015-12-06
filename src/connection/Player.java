package connection;

import gameplay.Card;

import java.util.ArrayList;

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

		// If the cards in the hand busts, try to keep deranking aces until it
		// stops busting
		boolean tryDeranking = true;
		while ((handTotal = calculateHand(this.currentCards)) > 21 && tryDeranking) {
			tryDeranking = false;
			for (int cardNo = 0; cardNo < this.currentCards.size(); cardNo++) {
				if (this.currentCards.get(cardNo).derankAce()) {
					tryDeranking = true;
					break;
				}
			}
		}

		// Update the player's total value
		this.handValue = calculateHand(currentCards);
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
	
	public int getHandValue() {
		return this.handValue;
	}
	
	public char getCurrentMove() {
		return this.currentMove;
	}

	public void setCurrentMove(char currentMove) {
		this.currentMove = currentMove;
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
