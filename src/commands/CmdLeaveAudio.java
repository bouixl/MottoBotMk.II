package commands;

import main.MottoBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdLeaveAudio extends Command {

	public CmdLeaveAudio(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		event.getChannel().sendMessage("Bye bye!").queue();
		bot.getGuildMusicManager(event.getGuild()).scheduler.clearPlaylist();
		event.getGuild().getAudioManager().closeAudioConnection();
	}

}
