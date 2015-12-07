package utilities;

public class Validator {

	public static final int MIN_BET = 1;

	// Name checker here or let the user handle it
	/**
	 * Checks if a string is a valid port number.
	 *
	 * @param str
	 *            the string to check.
	 * @return whether or not the string is a valid port number.
	 */
	public static boolean isValidPort(String str) {
		if (str.matches("(\\d{1,5})")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if a bet is valid.
	 * 
	 * @param bet
	 *            the string containing the bet from the client.
	 * @param maxBet
	 *            the maximum bet value (usually the player's current balance).
	 * @return the player's bet in integer form. If the bet is not valid,
	 *         returns -1.
	 */
	public static boolean isValidBet(String bet, int maxBet) {
		if (bet.matches("([0-9])+")) {
			int amount = Integer.parseInt(bet);
			if (amount <= maxBet && amount >= MIN_BET)
				return true;
		}
		return false;
	}

	/**
	 * Checks if a name is valid.
	 * 
	 * @param name
	 *            the name to check.
	 * @return whether or not the name is valid.
	 */
	public static boolean isValidName(String name) {
		if (name.matches("([a-zA-Z0-9 ]){1,16}")) {
			return true;
		} else {
			return false;
		}
	}
}