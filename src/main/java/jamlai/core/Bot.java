package jamlai.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

public class Bot {

	private JDA jda;

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

}
