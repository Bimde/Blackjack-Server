package gameplay;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Leader board for the top ten players. Keeps track of said players and their
 * respective scores,
 * 
 * @author
 *
 */
public class Leaderboard {

	private static PrintWriter fileOut;
	private static Scanner inFile;
	private static int[] highScores;
	private static String[] topPlayers;
	private static String name;
	private static int score;

	/**
	 * Sets up the printwriter, filewriter, and scanner to be used to reading in
	 * and writing out to a text file (topScores.txt)
	 * 
	 * @param name
	 *            Name of the winner
	 * @param score
	 *            Score of the winner
	 * @throws IOException
	 */
	Leaderboard(String name, int score) throws IOException {
		fileOut = new PrintWriter(new FileWriter("topScores.txt"));
		inFile = new Scanner(new File("topScores.txt"));
		highScores = new int[10];
		topPlayers = new String[10];
		this.name = name;
		this.score = score;

		readIn();
		writeOut();
	}

	/**
	 * Read in the scores from the text file
	 */
	public static void readIn() {
		int line = 0;

		// Continuously read line by line while more lines exist in the file
		while (inFile.hasNext()) {
			highScores[line] = inFile.nextInt();
			topPlayers[line] = inFile.nextLine();
		}
		inFile.close();

		for (int playerScore = highScores.length - 1; playerScore >= 0; playerScore--) {
			if (highScores[playerScore] > playerScore
					&& playerScore != (highScores.length - 1)) {
				for (int next = highScores.length - 1; next > playerScore + 1; next--) {
					highScores[next] = highScores[next - 1];
					topPlayers[next] = topPlayers[next - 1];
				}
				highScores[playerScore] = score;
				topPlayers[playerScore] = name;

			}
			// Handles last index situation
			else if (highScores[playerScore] <= score && playerScore == 0) {
				for (int next = highScores.length - 1; next > 0; next--) {
					highScores[next - 1] = highScores[next];
					topPlayers[next - 1] = topPlayers[next];
				}
				highScores[playerScore] = score;
				topPlayers[playerScore] = name;
			}
		}
	}

	/**
	 * Write out the sorted data back into the text file
	 */
	public static void writeOut() {
		for (int player = 0; player < topPlayers.length; player++) {
			fileOut.println(topPlayers[player] + " " + highScores[player]);
		}
		fileOut.close();
	}

}