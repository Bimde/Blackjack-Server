package gameplay;

/**
 * Object that represents a playing card. Includes a rank and suit.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 1, 2015
 */
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
	 * @return whether or not the card can be de-ranked.
	 */
	public boolean derankAce() {
		boolean canDerank = (this.rank == 'A' && this.value == 11);
		if (canDerank) {
			this.value = 1;
		}
		return canDerank;
	}

	public int getValue() {
		return value;
	}

	public char getSuit() {
		return suit;
	}

	public char getRank() {
		return rank;
	}

	/**
	 * Combines the suit and rank of a card into a String message for a client
	 * broadcast.
	 */
	public String toString() {
		return rank + " " + suit;
	}
}