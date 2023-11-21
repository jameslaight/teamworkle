package jamlai.core;

import jamlai.util.Dictionary;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class Bot {

	private JDA jda;

	private Game game;

	public void build(@NotNull String token) {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
		builder.addEventListeners(this);
		builder.setActivity(Activity.playing("Teamworkle"));
		try {
			jda = builder.build().awaitReady();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void start() {
		List<String> words = Dictionary.getSolutionWords().stream().filter(s -> s.length() == 5).toList();

		Random random = new Random();
		game = new Game(words.get(random.nextInt(words.size())), 6);
	}

}
