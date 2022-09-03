package commands;

import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdShutdown extends Command {

	public CmdShutdown(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		bot.shutdown();
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		bot.shutdown();
	}

}
