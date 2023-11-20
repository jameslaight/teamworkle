package jamlai.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

	public MessageEmbed getEmbed() {
		EmbedBuilder builder = new EmbedBuilder();

		StringBuilder strBuilder = new StringBuilder();
		boolean first = true;
		for (String line : board) {
			if (first) first = false;
			else strBuilder.append("\n");

			strBuilder.append(line);
		}
		builder.addField("Board", strBuilder.toString(), false);

		return builder.build();
	}

}
