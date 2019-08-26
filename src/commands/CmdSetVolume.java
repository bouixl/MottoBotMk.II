package commands;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdSetVolume extends Command {

	public CmdSetVolume(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		if(args == null || args.trim().isEmpty()) {
			event.getChannel().sendMessage(":gear: Volume actuel: " + gmm.player.getVolume()).queue();
		}
		else {
			try {
				int newVol = Integer.valueOf(args);
				if(newVol>80) {
					newVol = 80;
				}
				else if(newVol<5) {
					newVol = 5;
				}
				gmm.player.setVolume(newVol);
				event.getChannel().sendMessage(":gear: Mon volume de base est maintenant réglé sur "+newVol+" !").queue();
			}
			catch (Exception e) {
				event.getChannel().sendMessage(":x: Merci de renseigner un nombre entier compris entre 5 et 80.").queue();
				e.printStackTrace();
			}
		}
	}
}
