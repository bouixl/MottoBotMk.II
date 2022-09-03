package commands;

import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdVersion extends Command {

	public CmdVersion(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("Version: " + MottoBot.MOTTO_VERSION).queue();
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		event.reply("Version: " + MottoBot.MOTTO_VERSION).queue();
	}

}
