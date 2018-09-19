package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdLeaveAudio extends Command {

	public CmdLeaveAudio(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("Bye bye!").queue();
		MottoBot.INSTANCE.getGuildMusicManager(event.getGuild().getIdLong()).scheduler.clearPlaylist();
		event.getGuild().getAudioManager().closeAudioConnection();
	}

}
