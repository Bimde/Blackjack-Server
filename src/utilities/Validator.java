package utilities;

public class Validator {
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
}