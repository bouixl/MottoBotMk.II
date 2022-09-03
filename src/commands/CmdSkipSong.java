package commands;

import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

			bot.getGuildMusicManager(event.getGuild()).scheduler.skipTrack(nbOfSkips);
		}
		else {
			event.getChannel().sendMessage(":x: <@" + event.getAuthor().getId() + ">, tu dois être dans un channel vocal pour effectuer cette commande.").queue();
		}
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		if (event.getMember().getVoiceState().inVoiceChannel()) {
			int nbOfSkips = 1;
			try {
				if (args != null)
					nbOfSkips = args.equals("") ? 1 : Integer.parseInt(args);
			}
			catch (NumberFormatException e1) {
				nbOfSkips = 1;
			}
			bot.getGuildMusicManager(event.getGuild()).scheduler.skipTrack(nbOfSkips);
			if(nbOfSkips > 1)
				event.reply(nbOfSkips + " songs skipped").setEphemeral(true).queue();
			else
				event.reply("song skipped").setEphemeral(true).queue();
		}
		else {
			event.reply(":x: <@" + event.getUser().getId() + ">, tu dois être dans un channel vocal pour effectuer cette commande.").queue();
		}
	}
}
