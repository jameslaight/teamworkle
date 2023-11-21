package jamlai.core;

import jamlai.util.LetterEmoji;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Game {

	private final @NotNull String solution;
	private final @NotNull Map<Character, Integer> characterCounts = new HashMap<>();

	private final int maxGuesses;
	private final boolean[] usedCharacters;
	private int guesses = 0;
	private boolean complete = false;

	private final String[] board;

	public Game(@NotNull String solution, int maxGuesses) {
		this.solution = solution;
		this.maxGuesses = maxGuesses;

		usedCharacters = new boolean[26]; //initialised as false

		for (char c : solution.toCharArray()) { //count # of characters in solution
			characterCounts.put(c, characterCounts.getOrDefault(c, 0) + 1);
		}

		board = new String[maxGuesses];
		String line = LetterEmoji.getBlankEmoji().repeat(solution.length());
		Arrays.fill(board, line); //fill board with blank lines to begin
	}

	public void guess(@NotNull String guess) {
		StringBuilder builder = new StringBuilder(); //builder for the line on the board

		Map<Character, Integer> counts = new HashMap<>(characterCounts);

		for (int i = 0; i < solution.length(); i++) {
			char c = guess.charAt(i);

			usedCharacters[c - 'a'] = true; //set character to used

			LetterEmoji.Type letterType = LetterEmoji.Type.INCORRECT; //assume letter is incorrect
			int count = counts.getOrDefault(c, 0);
			if (count > 0) { //if the character is in the solution's character counts
				if (solution.charAt(i) == c) { //if character is an exact match, it is solved
					letterType = LetterEmoji.Type.SOLVED;
				} else { //otherwise, it is misplaced
					letterType = LetterEmoji.Type.MISPLACED;
				}

				counts.put(c, --count); //character count is decremented to display correct number of misplaced letters
			}

			builder.append(LetterEmoji.getEmoji(c, letterType)); //get the appropriate emoji and append to line
		}

		if (guess.equals(solution)) { //victory
			complete = true;
			builder.append(":tada:");
		} else if (getGuessesLeft() <= 0) { //defeat
			complete = true;
			builder.append(":skull_crossbones:");
		}

		board[guesses++] = builder.toString();
	}

	public int getGuessesLeft() {
		return maxGuesses - guesses;
	}

	public boolean isComplete() {
		return complete;
	}

	@NotNull
	public String getSolution() {
		return solution;
	}

	public String getBoardAsString() {
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (String line : board) {
			if (first) first = false;
			else builder.append("\n");

			builder.append(line);
		}

		return builder.toString();
	}

	public String getUnusedAsString() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < usedCharacters.length; i++) {
			if (usedCharacters[i]) builder.append(LetterEmoji.getBlankEmoji());
			else builder.append(LetterEmoji.getEmoji((char) ('a' + i), LetterEmoji.Type.INCORRECT));

			if (i == 12) builder.append("\n");
		}

		return builder.toString();
	}

}
