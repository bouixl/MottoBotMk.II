package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdTestArgs extends Command {

	public CmdTestArgs(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("+"+args+"+").queue();
	}

}
