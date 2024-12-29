package commands;

import java.io.IOException;

import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdRestart extends Command {

	public CmdRestart(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		try {
			bot.shutdown();
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", "MottoBot.jar", bot.getToken(), "10");
			pb.inheritIO();
			pb.start();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void execute(MottoBot bot, SlashCommandInteractionEvent event, String args) {
		try {
			bot.shutdown();
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", "MottoBot.jar", bot.getToken(), "10");
			pb.inheritIO();
			pb.start();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
