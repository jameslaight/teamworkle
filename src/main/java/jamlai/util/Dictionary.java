package jamlai.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Dictionary {

	private static Set<String> validWords;
	private static List<String> solutionWords;

	private static boolean loaded = false;

	public boolean isValidWord(String word) {
		return validWords.contains(word);
	}

	public static List<String> getSolutionWords() {
		return Collections.unmodifiableList(solutionWords);
	}

	public static void load() {
		if (loaded) throw new IllegalStateException("Words already loaded");

		validWords = loadSet(new File("txt/valid.txt"));
		solutionWords = loadList(new File("txt/solutions.txt"));

		validWords.addAll(solutionWords); //ensure all solution words are also valid words

		loaded = true;
	}

	private static Set<String> loadSet(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.lines().collect(Collectors.toSet());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<String> loadList(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.lines().toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
