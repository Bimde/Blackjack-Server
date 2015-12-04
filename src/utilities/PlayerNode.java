package utilities;

import connection.Player;

public class PlayerNode {

	private int playerNo;
	private Player client;
	private PlayerNode next;

	public PlayerNode(int playerNo, Player client) {
		this.client = client;
		this.playerNo = playerNo;
	}

	public int getPlayerNo() {
		return this.playerNo;
	}

	public void setNext(PlayerNode next) {
		this.next = next;
	}

	public Player getPlayer() {
		return this.client;
	}

	public PlayerNode getNext() {
		return this.next;
	}

}
