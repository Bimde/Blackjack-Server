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

	public void reloadDeck() {
		this.clear();
		for (int i = 0; i < this.numOfDecks; i++) {
			for (int suit = 0; suit < Dealer.SUITS.length; suit++) {
				for (int rank = 0; rank < Dealer.RANKS.length; rank++) {
					this.add(new Card(Dealer.SUITS[suit], Dealer.RANKS[rank]));
				}
			}
		}
		this.shuffle();
	}

	public void shuffle() {
		Collections.shuffle(this);
	}
	
	public String getCard()
	{
		Card card = this.get(0);
		String suit = card.getSuit();
		String rank = card.getRank();
		String send = suit + " " + rank;
		this.remove(0);
		return send;
	}

}
