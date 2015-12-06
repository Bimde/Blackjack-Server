package gameplay;

public class Card {
	private char suit, rank;
	private int value;

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
	 * De-ranks the card from 11 to 1 if it is an ace
	 * @return Whether or not the card could be deranked
	 */
	public boolean derankAce()
	{
		boolean canDerank = (rank=='A' && value==11);
		if (canDerank)
		{
			this.value = 1;
		}
		return canDerank;
	}
	
	/**
	 * Gets the integer value of value
	 * @return Value variable value
	 */
	public int getValue()
	{
		return value;
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
	 * Gets the rank of a card for broadcast
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
		return suit + " " + rank;
	}
}