package commands;

import java.util.concurrent.TimeUnit;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.PaginatorAutoStop;

public class CmdPlaylist extends Command {

	public CmdPlaylist(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.setActiveTextChannel(event.getTextChannel());
		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		if (titles.length > 0) {
			PaginatorAutoStop.Builder pgBuilder = new PaginatorAutoStop.Builder();
			pgBuilder.setText(":musical_score: Playlist: ");
			pgBuilder.useNumberedItems(true);
			pgBuilder.setItems(titles);
			pgBuilder.setItemsPerPage(10);
			pgBuilder.setColumns(1);
			pgBuilder.setEventWaiter(bot.getWaiter());
			pgBuilder.setTimeout(3, TimeUnit.MINUTES);
			pgBuilder.waitOnSinglePage(true);
			PaginatorAutoStop pg = pgBuilder.build();

			pg.display(event.getChannel());
		}
		else {
			event.getChannel().sendMessage(":musical_score: Playlist vide").queue();
		}
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.setActiveTextChannel(event.getTextChannel());
		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		if (titles.length > 0) {
			PaginatorAutoStop.Builder pgBuilder = new PaginatorAutoStop.Builder();
			pgBuilder.setText(":musical_score: Playlist: ");
			pgBuilder.useNumberedItems(true);
			pgBuilder.setItems(titles);
			pgBuilder.setItemsPerPage(10);
			pgBuilder.setColumns(1);
			pgBuilder.setEventWaiter(bot.getWaiter());
			pgBuilder.setTimeout(3, TimeUnit.MINUTES);
			pgBuilder.waitOnSinglePage(true);
			PaginatorAutoStop pg = pgBuilder.build();

			pg.display(event.getChannel());
			event.reply("Ok!").setEphemeral(true).queue();
		}
		else {
			event.reply(":musical_score: Playlist vide").queue();
		}
	}
}
