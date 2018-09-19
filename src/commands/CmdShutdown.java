package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShutdown extends Command {

	public CmdShutdown(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		MottoBot.INSTANCE.shutdown();
	}

}
