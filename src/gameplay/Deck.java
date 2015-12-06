package gameplay;

import java.util.ArrayList;
import java.util.Collections;

public class Deck extends ArrayList<Card> {
	private int numOfDecks;

	public Deck(int numOfDecks) {
		super();
		this.numOfDecks = numOfDecks;
		this.reloadDeck();
	}

	/**
	 * Loops through the number of decks and adds the according number of cards
	 * to the deck
	 */
	public void reloadDeck() {

		// Clear the deck first to make things easier
		this.clear();

		// For the number of decks required, go through the number of suits and
		// add each rank for each suit
		for (int i = 0; i < this.numOfDecks; i++) {
			for (int suit = 0; suit < Game.SUITS.length; suit++) {
				for (int rank = 0; rank < Game.RANKS.length; rank++) {
					this.add(new Card(Game.SUITS[suit], Game.RANKS[rank]));
				}
			}
		}

		// Shuffle the deck
		this.shuffle();
	}

	/**
	 * Shuffles the deck
	 */
	public void shuffle() {
		Collections.shuffle(this);
	}

	/**
	 * Removes a card from the deck and returns that card
	 * 
	 * @return The card removed from the deck
	 */
	public Card getCard() {
		Card card = this.get(0);
		this.remove(0);
		return card;
	}
}