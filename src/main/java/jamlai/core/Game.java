package jamlai.core;

import jamlai.util.LetterEmoji;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game {

	private final @NotNull String solution;
	private final @NotNull Map<Character, Integer> characterCounts = new HashMap<>();
	private EndState endState = null;

	private final int maxGuesses;
	private int guesses = 0;
	private final List<User> guessers = new ArrayList<>();
	private boolean locked = true; //same user can't guess multiple times if true

	private final boolean[] usedCharacters;

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

	public void guess(@NotNull String guess, User guesser) {
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

		board[guesses++] = builder.toString();

		if (guess.equals(solution)) { //victory
			endState = EndState.VICTORY;
		} else if (getGuessesLeft() <= 0) { //defeat
			endState = EndState.DEFEAT;
		}

		guessers.add(guesser);
	}

	public EndState getEndState() {
		return endState;
	}

	public boolean isComplete() {
		return endState != null;
	}

	public int getGuessesLeft() {
		return maxGuesses - guesses;
	}

	public boolean hasGuessed(User user) {
		return guessers.contains(user);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
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

	public enum EndState {
		VICTORY, DEFEAT
	}

}
