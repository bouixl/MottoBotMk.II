package commands;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.menu.Paginator;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShufflePlaylist extends Command {

	public CmdShufflePlaylist(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		if(!event.getMember().getVoiceState().inVoiceChannel()) {
			event.getChannel().sendMessage(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		MottoBot.INSTANCE.getGuildMusicManager(event.getGuild().getIdLong()).scheduler.shufflePlaylist();
		MottoBot.INSTANCE.getGuildMusicManager(event.getGuild().getIdLong()).scheduler.setActiveTextChannel(event.getTextChannel());

		String[] titles = MottoBot.INSTANCE.getGuildMusicManager(event.getGuild().getIdLong()).scheduler.getPlaylist().toArray(new String[0]);

		Paginator.Builder pgBuilder = new Paginator.Builder();
		pgBuilder.setText("Nouvelle playlist: ");
		pgBuilder.useNumberedItems(true);
		pgBuilder.setItems(titles);
		pgBuilder.setItemsPerPage(10);
		pgBuilder.setColumns(1);
		pgBuilder.setEventWaiter(MottoBot.INSTANCE.getWaiter());
		pgBuilder.setTimeout(90, TimeUnit.SECONDS);
		pgBuilder.setFinalAction(m -> m.delete().complete());
		Paginator pg = pgBuilder.build();

		pg.display(event.getChannel());
	}
}
