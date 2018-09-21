package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdCleanUp extends Command {

	public CmdCleanUp(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		int deletedCount = bot.clearChannelTab(event.getTextChannel());
		if(deletedCount>1) {
			event.getChannel().sendMessage(":gear: "+deletedCount+" messages supprimés.").queue();
		}
		else if (deletedCount>0) {
			event.getChannel().sendMessage(":gear: 1 message supprimé.").queue();
		}
	}
}
