package jamlai.core;

import jamlai.util.Dictionary;

public class Main {

	public static void main(String[] args) {
		System.out.println("Loading dictionary...");
		Dictionary.load();
		System.out.println("Done.");

		if (args.length == 1) {
			Bot bot = new Bot();
			bot.build(args[0]);
			bot.start();
		} else {
			System.out.println("Run the program with a bot token as the singular argument");
		}
	}

}
