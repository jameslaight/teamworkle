package jamlai.core;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class LetterEmoji {

	private static final Map<Character, String> solvedMap = new HashMap<>(), misplacedMap = new HashMap<>();
	private static String blankEmoji;

	private static boolean loaded = false;

	public static void load() {
		if (loaded) throw new IllegalStateException("Emojis already loaded");

		try (BufferedReader reader = new BufferedReader(new FileReader("emojis.txt"))) {
			blankEmoji = reader.readLine();

			for (char c = 'a'; c <= 'z'; c++) {
				solvedMap.put(c, reader.readLine());
				misplacedMap.put(c, reader.readLine());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		loaded = true;
	}

	public static @NotNull String getEmoji(char c, @NotNull Type type) {
		if (!loaded) load();

		if (c < 'a' || c > 'z') throw new IllegalArgumentException("c must be between 'a' and 'z' inclusive, got '" + c + "'");

		return switch (type) {
			case SOLVED -> solvedMap.get(c);
			case MISPLACED -> misplacedMap.get(c);
		};
	}

	public static String getBlankEmoji() {
		if (!loaded) load();

		return blankEmoji;
	}

	public enum Type {
		SOLVED, MISPLACED
	}


}
