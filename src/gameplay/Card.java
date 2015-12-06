package gameplay;

public class Card {
	private char suit, rank;
	private int value;

	/**
	 * Constructs a new Card object.
	 * 
	 * @param suit
	 *            the suit of the card (D, C, H, S).
	 * @param rank
	 *            the rank of the card (A, 2-9, T, J, Q, K).
	 */
	public Card(char suit, char rank) {
		this.suit = suit;
		this.rank = rank;

		// Assign the card value to the card, everything else is just for show
		if (rank == 'A') {
			this.value = 11;
		} else if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K') {
			this.value = 10;
		} else {
			this.value = Integer.parseInt(rank + "");
		}
	}

	/**
	 * De-ranks the card from 11 to 1 if it is an ace.
	 * 
	 * @return whether or not the card could be deranked.
	 */
	public boolean derankAce() {
		boolean canDerank = (this.rank == 'A' && this.value == 11);
		if (canDerank) {
			this.value = 1;
		}
		return canDerank;
	}

	/**
	 * Gets the value of the card.
	 * 
	 * @return the value of the card.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets the suit of the card.
	 * 
	 * @return the suit of the card.
	 */
	public char getSuit() {
		return suit;
	}

	/**
	 * Gets the rank of a card.
	 * 
	 * @return the rank of the card.
	 */
	public char getRank() {
		return rank;
	}

	/**
	 * Combines the suit and rank of a card into a String message for a client
	 * broadcast.
	 */
	public String toString() {
		return suit + " " + rank;
	}
}