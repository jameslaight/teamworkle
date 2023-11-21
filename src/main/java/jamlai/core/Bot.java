package jamlai.core;

import jamlai.util.Dictionary;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class Bot extends ListenerAdapter {

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

		jda.updateCommands().addCommands(
				Commands.slash("guess", "Guess a word in the daily teamworkle.")
						.setGuildOnly(true)
						.addOption(OptionType.STRING, "word", "word to guess", true, false),
				Commands.slash("unused", "See all unused letters.")
						.setGuildOnly(true)
		).queue();
	}

	public void start() {
		List<String> words = Dictionary.getSolutionWords().stream().filter(s -> s.length() == 5).toList();

		Random random = new Random();
		game = new Game(words.get(random.nextInt(words.size())), 6);
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getName().equals("guess")) {
			game.guess(event.getOption("word", OptionMapping::getAsString));
			event.reply(game.getBoardAsString()).queue();
		} else if (event.getName().equals("unused")) {
			event.reply(game.getUnusedAsString()).queue();
		}
	}

}
