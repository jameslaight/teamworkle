package jamlai.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Bot {

	private JDA jda;

	public void build(String token) {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.setActivity(Activity.playing("Teamwordle"));
		try {
			jda = builder.build().awaitReady();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
