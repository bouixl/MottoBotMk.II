package commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.menu.Paginator;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdShufflePlaylist extends Command {

	public CmdShufflePlaylist(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if (!event.getMember().getVoiceState().inAudioChannel()) {
			event.getChannel().sendMessage(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.shufflePlaylist();
		gmm.scheduler.setActiveTextChannel((TextChannel) event.getChannel());

		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		if (titles.length > 0) {
			Paginator.Builder pgBuilder = new Paginator.Builder();
			pgBuilder.setText(":musical_score: Nouvelle playlist: ");
			pgBuilder.useNumberedItems(true);
			pgBuilder.setItems(titles);
			pgBuilder.setItemsPerPage(10);
			pgBuilder.setColumns(1);
			pgBuilder.setEventWaiter(bot.getWaiter());
			pgBuilder.setTimeout(3, TimeUnit.MINUTES);
			pgBuilder.waitOnSinglePage(true);
			Paginator pg = pgBuilder.build();

			pg.display(event.getChannel());
		}
		else {
			event.getChannel().sendMessage(":musical_score: Playlist vide").queue();
		}
	}

	@Override
	public void execute(MottoBot bot, SlashCommandInteractionEvent event, String args) {
		if (!event.getMember().getVoiceState().inAudioChannel()) {
			event.reply(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.shufflePlaylist();
		gmm.scheduler.setActiveTextChannel((TextChannel) event.getChannel());

		String[] titles = gmm.scheduler.getPlaylist().toArray(new String[0]);

		if (titles.length > 0) {
			Paginator.Builder pgBuilder = new Paginator.Builder();
			pgBuilder.setText(":musical_score: Nouvelle playlist: ");
			pgBuilder.useNumberedItems(true);
			pgBuilder.setItems(titles);
			pgBuilder.setItemsPerPage(10);
			pgBuilder.setColumns(1);
			pgBuilder.setEventWaiter(bot.getWaiter());
			pgBuilder.setTimeout(3, TimeUnit.MINUTES);
			pgBuilder.waitOnSinglePage(true);
			Paginator pg = pgBuilder.build();

			pg.display(event.getChannel());
			event.reply("Ok!").setEphemeral(true).queue();
		}
		else {
			event.reply(":musical_score: Playlist vide").queue();
		}
	}
}
