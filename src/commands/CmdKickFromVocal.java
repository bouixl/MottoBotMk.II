package commands;

import main.MottoBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdKickFromVocal extends Command {

	public CmdKickFromVocal(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if(args == null || args.trim().isEmpty())
			return;

		if(event.getMessage().getMentionedMembers().size()==1) {
			Member author = event.getMember();
			Member target = event.getMessage().getMentionedMembers().get(0);
			if(author.canInteract(target)) {
				if(target.getVoiceState().inVoiceChannel()) {
					if(event.getGuild().getVoiceChannelsByName("Kick en cours", true).isEmpty()) {
						event.getGuild().createVoiceChannel("Kick en cours").queue(c -> event.getGuild().moveVoiceMember(target, (VoiceChannel) c).queue(success -> c.delete().queue()));
					}
				}
			}
		}
	}
}
