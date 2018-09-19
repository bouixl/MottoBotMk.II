package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdTestArgs extends Command {

	public CmdTestArgs(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("+"+args+"+").queue();
	}

}
