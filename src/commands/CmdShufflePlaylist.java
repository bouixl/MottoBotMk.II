package commands;

import java.util.concurrent.TimeUnit;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.PaginatorAutoStop;

public class CmdShufflePlaylist extends Command {

	public CmdShufflePlaylist(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if (!event.getMember().getVoiceState().inVoiceChannel()) {
			event.getChannel().sendMessage(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.shufflePlaylist();
		gmm.scheduler.setActiveTextChannel(event.getTextChannel());

		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		if (titles.length > 0) {
			PaginatorAutoStop.Builder pgBuilder = new PaginatorAutoStop.Builder();
			pgBuilder.setText(":musical_score: Nouvelle playlist: ");
			pgBuilder.useNumberedItems(true);
			pgBuilder.setItems(titles);
			pgBuilder.setItemsPerPage(10);
			pgBuilder.setColumns(1);
			pgBuilder.setEventWaiter(bot.getWaiter());
			pgBuilder.setTimeout(3, TimeUnit.MINUTES);
			PaginatorAutoStop pg = pgBuilder.build();

			pg.display(event.getChannel());
		}
		else {
			event.getChannel().sendMessage(":musical_score: Playlist vide").queue();
		}
	}
}
