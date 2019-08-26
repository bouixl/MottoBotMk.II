package commands;

import java.time.temporal.ChronoUnit;

import main.MottoBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdPing extends Command {

	public CmdPing(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va.").queue(m -> {
            m.editMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va. ("+event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS)+"ms)").queue();
        });
	}
}
