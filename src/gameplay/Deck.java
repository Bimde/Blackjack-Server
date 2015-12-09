package gameplay;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Deck object, contains all the decks of cards to be used.
 */
public class Deck {
	private int numOfDecks;
	private ArrayList<Card> cards;

	/**
	 * Constructs a new Deck object. Sets the number of decks to the class
	 * variable number of decks. Loads up the deck.
	 * 
	 * @param numOfDecks
	 *            the number of decks to be used in the game.
	 */
	public Deck(int numOfDecks) {
		this.numOfDecks = numOfDecks;
		this.cards = new ArrayList<Card>();
		this.reloadDeck();
	}

	/**
	 * Loops through the number of decks and adds the according number of cards
	 * to the deck.
	 */
	public void reloadDeck() {
		// Clear the deck first to make things easier
		this.cards.clear();

		// For the number of decks required, go through the number of suits and
		// add each rank for each suit
		for (int i = 0; i < this.numOfDecks; i++) {
			for (int suit = 0; suit < Dealer.SUITS.length; suit++) {
				for (int rank = 0; rank < Dealer.RANKS.length; rank++) {
					this.cards.add(new Card(Dealer.SUITS[suit], Dealer.RANKS[rank]));
				}
			}
		}

		// Shuffle the deck
		this.shuffle();
	}

	/**
	 * Shuffles the deck.
	 */
	public void shuffle() {
		Collections.shuffle(this.cards);
	}

	public int size() {
		return this.cards.size();
	}

	/**
	 * Removes the top card from the deck.
	 * 
	 * @return the card removed from the deck.
	 */
	public Card getCard() {
		Card card = this.cards.get(0);
		this.cards.remove(0);
		return card;
	}
}