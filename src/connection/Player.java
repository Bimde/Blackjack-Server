package connection;
import gameplay.Card;
import java.net.Socket;
import java.util.ArrayList;

public class Player {

	private ArrayList<Card> currentCards = new ArrayList<Card>();
	private int coins;
	private int playerNo;
	private int currentBet;
	private Server server;
	
	/** Initialize a new player
	 * 
	 * @param client
	 * @param server
	 * @param playerNo
	 */
	public Player(Server server, int playerNo) {
		this.server = server;
		
		coins = this.server.getStartCoins();
		this.playerNo = playerNo;

	}

	/** Adds a card to the player's hand
	 * 
	 * @param newCard the new card to add to the player's hand
	 */
	public void addCard (Card newCard)
	{
		currentCards.add(newCard);
	}
	
	/**
	 *  Clears the current player's hand
	 */
	public void clearHand ()
	{
		currentCards.clear();
	}

	public ArrayList<Card> getCurrentCards() {
		return currentCards;
	}

	public void setCurrentCards(ArrayList<Card> currentCards) {
		this.currentCards = currentCards;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public int getCurrentBet() {
		return currentBet;
	}

	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}
	
	
}
