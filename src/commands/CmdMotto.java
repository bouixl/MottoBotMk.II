package commands;

import main.MottoBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdMotto extends Command{

	public CmdMotto(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		(new Thread(new PictureThread(event, args))).start();
		bot.addToClearTab(event.getMessage());

	}

}
