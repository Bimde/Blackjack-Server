package utilities;

import connection.Server;

/**
 * Validates strings to see if they are valid for the specified input.
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 1, 2015
 */
public class Validator {
	/**
	 * Checks if a string is a valid port number.
	 *
	 * @param str
	 *            the String to check.
	 * @return whether or not the String is a valid port number.
	 */
	public static boolean isValidPort(String str) {
		return str.matches("([0-9]{1,5})");
	}

	/**
	 * Checks if a string contains an integer within the 
	 * {@link Server#MIN_BET minimum bet} and specified maximum.
	 * 
	 * @param bet
	 *            the String containing the bet from the client.
	 * @param maxBet
	 *            maximum bet value (usually the player's current coin balance).
	 * @return whether or not the bet is valid.
	 */
	public static boolean isValidBet(String bet, int maxBet) {
		if (bet.matches("([0-9]){1, 8}")) {
			int amount = Integer.parseInt(bet);
			if (amount <= maxBet && amount >= Server.MIN_BET)
				return true;
		}
		return false;
	}

	/**
	 * Checks if a name is alphanumeric and within 1 to 16 characters.
	 * 
	 * @param name
	 *            the name to check.
	 * @return whether or not the name is valid.
	 */
	public static boolean isValidName(String name) {
		return name.matches("([a-zA-Z0-9 ]){1,16}");
	}
}