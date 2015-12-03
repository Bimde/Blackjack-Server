package gameplay;

public class Card {
	private char suit, rank;

	public Card(char suit, char rank) {
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Gets the suit of a card
	 * 
	 * @return The suit of the card
	 */
	public char getSuit() {
		return suit;
	}

	/**
	 * Gets the rank of a card
	 * 
	 * @return The rank of the card
	 */
	public char getRank() {
		return rank;
	}

	/**
	 * Overrides the toString() method. Combines the suit and rank of a card
	 * into a String message for client broadcast.
	 */
	public String toString() {
		return "#" + " " + suit + " " + rank;
	}
}