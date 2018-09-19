package commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.menu.Paginator;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlaylist extends Command {

	public CmdPlaylist(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild().getIdLong());
		gmm.scheduler.setActiveTextChannel(event.getTextChannel());
		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		Paginator.Builder pgBuilder = new Paginator.Builder();
		pgBuilder.setText("Playlist: ");
		pgBuilder.useNumberedItems(true);
		pgBuilder.setItems(titles);
		pgBuilder.setItemsPerPage(10);
		pgBuilder.setColumns(1);
		pgBuilder.setEventWaiter(bot.getWaiter());
		pgBuilder.setTimeout(2, TimeUnit.MINUTES);
		pgBuilder.setFinalAction(m -> m.delete().complete());
		Paginator pg = pgBuilder.build();

		pg.display(event.getChannel());
	}
}
