package jamlai.core;

import jamlai.util.Dictionary;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Bot extends ListenerAdapter {

	private JDA jda;
	private final Random random = new Random();

	private final Map<Guild, Game> games = new HashMap<>(); //games being played in each guild

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
				Commands.slash("board", "View the board state")
						.setGuildOnly(true),
				Commands.slash("unused", "See all unused letters.")
						.setGuildOnly(true),
				Commands.slash("new", "Start a new game.")
						.setGuildOnly(true)
						.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)),
				Commands.slash("lock", "Set whether the same user can guess twice on today's teamworkle.")
						.setGuildOnly(true)
						.addOption(OptionType.BOOLEAN, "locked", "whether game is locked", true, false)
						.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
		).queue();
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Game game = getGameForGuild(event.getGuild());

		if (event.getName().equals("guess")) {
			if (game.isComplete()) { //fail if the game has been completed
				event.reply("Today's game is over. View the board state with ``/board``.").setEphemeral(true).queue();
				return;
			}

			if (game.isLocked() && game.hasGuessed(event.getUser())) { //can't guess twice in locked games
				event.reply("You have already guessed in today's game. View the board state with ``/board``.").setEphemeral(true).queue();
				return;
			}

			String word = event.getOption("word", OptionMapping::getAsString);

			if (word == null) {
				event.reply("No guess given.").setEphemeral(true).queue();
				return;
			}

			int wordLen = word.length(), solutionLen = game.getSolution().length();
			if (wordLen != solutionLen) {
				event.reply("Guess must be the same length as the solution (guess length ``" + wordLen + "``, solution length ``" + solutionLen + "``).").setEphemeral(true).queue();
				return;
			}

			if (!Dictionary.isValidWord(word)) {
				event.reply("Guess must be a valid word.").setEphemeral(true).queue();
				return;
			}

			game.guess(word, event.getUser());
			event.reply(game.getBoardAsString()).queue();

			if (game.isComplete()) { //send completion message
				StringBuilder builder = new StringBuilder();

				if (game.getEndState() == Game.EndState.VICTORY) {
					int remaining = game.getGuessesLeft();
					builder.append(":tada: Victory with ");

					builder.append(switch (remaining) {
						case 0 -> "no guesses remaining!";
						case 1 -> "``1`` guess remaining!";
						default -> "``" + remaining + "`` guesses remaining.";
					});
				} else {
					builder.append(":skull_crossbones: Defeat.");
				}

				builder.append("\nThe word was **").append(game.getSolution().toUpperCase()).append("**.");

				event.getChannel().sendMessage(builder.toString()).queue();
			}
		} else if (event.getName().equals("board")) {
			event.reply(game.getBoardAsString()).setEphemeral(true).queue();
		} else if (event.getName().equals("unused")) {
			event.reply(game.getUnusedAsString()).setEphemeral(true).queue();
		} else if (event.getName().equals("new")) {
			Member member = event.getMember();

			if (member == null) throw new NullPointerException("Member was null"); //should not occur

			if (!member.hasPermission(Permission.MANAGE_SERVER)) { //check perms anyway in case it gets sent
				event.reply("You don't have permission to do that.").setEphemeral(true).queue();
				return;
			}

			createGameForGuild(event.getGuild());

			event.reply("Created new game.").setEphemeral(true).queue();
		} else if (event.getName().equals("lock")) {
			Member member = event.getMember();

			if (member == null) throw new NullPointerException("Member was null"); //should not occur

			if (!member.hasPermission(Permission.MANAGE_SERVER)) { //check perms anyway in case it gets sent
				event.reply("You don't have permission to do that.").setEphemeral(true).queue();
				return;
			}

			Boolean locked = event.getOption("locked", OptionMapping::getAsBoolean);

			if (locked == null) {
				event.reply("No lock state specified.").setEphemeral(true).queue();
				return;
			}

			game.setLocked(locked);

			event.reply("Set game lock state to ``" + locked + "``.").setEphemeral(true).queue();
		}
	}

	private Game getGameForGuild(Guild guild) {
		if (games.containsKey(guild)) {
			return games.get(guild);
		} else {
			return createGameForGuild(guild);
		}
	}

	private Game createGameForGuild(Guild guild) {
		final int wordLength = 5, guesses = 6;
		List<String> words = Dictionary.getSolutionWords().stream().filter(s -> s.length() == wordLength).toList();
		String solution = words.get(random.nextInt(words.size()));

		Game game = new Game(solution, guesses);
		games.put(guild, game);

		return game;
	}

}
