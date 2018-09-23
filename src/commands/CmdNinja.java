package commands;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdNinja extends Command {

	public CmdNinja(String name) {
		super(name);
	}

	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if(event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE))
		{
			OffsetDateTime oldest = event.getMessage().getCreationTime();
			OffsetDateTime twoDaysAgo = OffsetDateTime.now();
			twoDaysAgo = twoDaysAgo.minusDays(2);
			event.getMessage().delete().queue();
			MessageHistory mh = event.getChannel().getHistory();
			List<String> mbot = new ArrayList<String>();
			List<Message> past;

			past = mh.retrievePast(80).complete();
			while((past!=null && past.isEmpty()==false) && oldest.compareTo(twoDaysAgo)>0) {
				mbot.clear();

				List<Message> l = mh.getRetrievedHistory();
				for(Message m:l) {
					if(m.getCreationTime().isAfter(twoDaysAgo) && m.getCreationTime().isBefore(oldest)) {
						if(m.isPinned()==false) {
							mbot.add(m.getId());
							oldest = m.getCreationTime();
						}
					}
				}
				if(mbot.size()>0) {
					event.getChannel().purgeMessagesById(mbot);
					System.out.println("J'ai supprimé " + mbot.size() + " messages.");
				}
				else if(mbot.size()==1) {
					event.getTextChannel().deleteMessageById(mbot.get(0)).queue();
					System.out.println("J'ai supprimé 1 message.");
				}
				else {
					System.out.println("Je n'ai rien supprimé.");
				}
				if(l.size()>1) {
					oldest = l.get(l.size()-1).getCreationTime();
				}
				past = mh.retrievePast(80).complete();
			}
		}
		else {
			event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: PERM)").queue();
		}
	}
}
