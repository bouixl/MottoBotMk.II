package commands;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdSkipSong extends Command {

	public CmdSkipSong(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if (event.getMember().getVoiceState().inVoiceChannel()) {
			int nbOfSkips = 1;
			try {
				if (args != null)
					nbOfSkips = args.equals("") ? 1 : Integer.parseInt(args);
			}
			catch (NumberFormatException e1) {
				nbOfSkips = 1;
			}

			for (int i = 0; i < nbOfSkips; i++) {
				bot.getGuildMusicManager(event.getGuild().getIdLong()).scheduler.nextTrack();
			}
		}
		else {
			event.getChannel().sendMessage(":x: <@" + event.getAuthor().getId() + ">, tu dois être dans un channel vocal pour effectuer cette commande.").queue();
		}
	}
}
