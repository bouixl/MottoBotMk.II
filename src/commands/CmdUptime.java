package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdUptime extends Command {

	public CmdUptime(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("En ligne depuis "+bot.getUptime()).queue();
	}

}
