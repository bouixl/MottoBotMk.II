package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdVersion extends Command {

	public CmdVersion(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("Version: "+MottoBot.MOTTO_VERSION).queue();
	}

}
