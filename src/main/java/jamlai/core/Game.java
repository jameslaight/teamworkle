package jamlai.core;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Game {

	private final @NotNull String solution;
	private final int maxGuesses;
	private int guessesLeft;

	private final String[] board;

	public Game(@NotNull String solution, int maxGuesses) {
		this.solution = solution;
		this.maxGuesses = maxGuesses;

		board = new String[maxGuesses];
		String line = LetterEmoji.getBlankEmoji().repeat(solution.length());
		Arrays.fill(board, line); //fill board with blank lines to begin

		guessesLeft = maxGuesses;
	}

}
