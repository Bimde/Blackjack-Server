package gameplay;

public class Card {
	private char suit, rank;

	public Card(char suit, char rank) {
		this.suit = suit;
		this.rank = rank;
	}

	public char getSuit() {
		return suit;
	}

	public char getRank() {
		return rank;
	}

	public String toString() {
		return "#" + " " + suit + " " + rank;
	}
}