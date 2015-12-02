package utilities;

public class Validator {

	public static final int MIN_BET = 1;

	// Name checker here or let the user handle it
	/**
	 * Check if a string is a valid port number.
	 *
	 * @param str
	 *            the string to check.
	 * @return whether or not the string is a valid port number.
	 */
	@SuppressWarnings("unused")
	public static boolean isValidPort(String str) {
		// Store the length of the string in order to check its validity
		int length = str.length();

		// Make sure the length is between 1 and 5
		if (length > 5 || length == 0) {
			return false;
		}

		// Make sure each character is a digit
		for (int ch = 0; ch < length; ch++) {
			if (!Character.isDigit(str.charAt(ch))) {
				return false;
			}
		}

		// If the string passed all of the tests, it is valid
		return true;
	}

	/**
	 * 
	 * @param bet
	 *            The string containing the bet from the client
	 * @param maxBet
	 *            The maximum bet value (usually the player's current balance)
	 * @return The player's bet in integer form or -1 if not valid
	 */
	public static int isValidBet(String bet, int maxBet) {
		if (bet.matches("([0-9])+")) {
			int amount = Integer.parseInt(bet);
			if (amount <= maxBet && amount >= MIN_BET)
				return amount;
		}
		return -1;
	}
}