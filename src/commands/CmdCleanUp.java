package commands;

import main.MottoBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdCleanUp extends Command {

	public CmdCleanUp(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if(event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE))
		{
			int deletedCount = bot.clearChannelTab(event.getTextChannel());
			if(deletedCount>1) {
				event.getChannel().sendMessage(":gear: "+deletedCount+" messages supprimés.").queue();
			}
			else if (deletedCount>0) {
				event.getChannel().sendMessage(":gear: 1 message supprimé.").queue();
			}
		}
		else {
			event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: PERM)").queue();
		}
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		if(event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE))
		{
			int deletedCount = bot.clearChannelTab(event.getTextChannel());
			if(deletedCount>1) {
				event.getChannel().sendMessage(":gear: "+deletedCount+" messages supprimés.").queue();
			}
			else if (deletedCount>0) {
				event.getChannel().sendMessage(":gear: 1 message supprimé.").queue();
			}
		}
		else {
			event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: PERM)").queue();
		}
	}
}
