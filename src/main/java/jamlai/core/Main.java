package jamlai.core;

public class Main {

	public static void main(String[] args) {
		if (args.length == 1) {
			Bot bot = new Bot();
			bot.build(args[0]);
		} else {
			System.out.println("Run the program with a bot token as the singular argument");
		}
	}

}
