package commands;

import java.time.temporal.ChronoUnit;

import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdPing extends Command {

	public CmdPing(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("ping.").queue(m -> {
            m.editMessage("pong. ("+event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS)+"ms)").queue();
        });
	}

	@Override
	public void execute(MottoBot bot, SlashCommandInteractionEvent event, String args) {
		event.reply("pong").queue();
	}
}
