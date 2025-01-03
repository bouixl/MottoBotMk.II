package commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import audio.GuildMusicManager;
import main.MottoBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class CmdPlaySong extends Command {

	public CmdPlaySong(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		if(args == null || args.trim().isEmpty())
			return;

		VoiceChannel voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
		if (voiceChannel == null) {
			event.getChannel().sendMessage(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		// Connection audio si necessaire
		AudioManager audioManager = event.getGuild().getAudioManager();
		if (!audioManager.isConnected())
			audioManager.openAudioConnection(voiceChannel);

		// Chargement et lancement de la musique
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.setActiveTextChannel((TextChannel) event.getChannel());
		bot.getPlayerManager().loadItemOrdered(gmm.player, args, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				gmm.scheduler.queue(track, (TextChannel) event.getChannel());
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				if (args.startsWith("ytsearch:")) {
					// On récupère un seul résultat de recherche
					gmm.scheduler.queue(playlist.getTracks().get(0), (TextChannel) event.getChannel());
				}
				else {
					gmm.scheduler.queuePlayList(playlist, (TextChannel) event.getChannel());
				}
			}

			@Override
			public void noMatches() {
				// event.getChannel().sendMessage(":x: Musique introuvable.").queue();
				// On tente une recherche
				bot.getPlayerManager().loadItemOrdered(gmm.player, "ytsearch:" + args, new AudioLoadResultHandler() {

					@Override
					public void trackLoaded(AudioTrack track) {
						gmm.scheduler.queue(track, (TextChannel) event.getChannel());
					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist) {
						gmm.scheduler.queue(playlist.getTracks().get(0), (TextChannel) event.getChannel());
					}

					@Override
					public void noMatches() {
						event.getChannel().sendMessage(":x: Musique introuvable.").queue();
					}

					@Override
					public void loadFailed(FriendlyException throwable) {
						if (throwable.severity == Severity.COMMON) {
							event.getChannel().sendMessage(":x: Musique non disponible.").queue();
						}
						else {
							event.getChannel().sendMessage(":x: Erreur lors du chargement de la musique.").queue();
						}
					}
				});
			}

			@Override
			public void loadFailed(FriendlyException throwable) {
				if (throwable.severity == Severity.COMMON) {
					event.getChannel().sendMessage(":x: Musique non disponible.").queue();
				}
				else {
					event.getChannel().sendMessage(":x: Erreur lors du chargement de la musique.").queue();
				}
			}
		});
	}

	@Override
	public void execute(MottoBot bot, SlashCommandInteractionEvent event, String args) {
		if(args == null || args.trim().isEmpty())
			return;

		VoiceChannel voiceChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
		if (voiceChannel == null) {
			event.reply(":x: Vous devez être dans un canal vocal pour ça.").queue();
			return;
		}

		// Connection audio si necessaire
		AudioManager audioManager = event.getGuild().getAudioManager();
		if (!audioManager.isConnected())
			audioManager.openAudioConnection(voiceChannel);

		// Chargement et lancement de la musique
		GuildMusicManager gmm = bot.getGuildMusicManager(event.getGuild());
		gmm.scheduler.setActiveTextChannel((TextChannel) event.getChannel());
		bot.getPlayerManager().loadItemOrdered(gmm.player, args, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				gmm.scheduler.queue(track, event);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				if (args.startsWith("ytsearch:")) {
					// On récupère un seul résultat de recherche
					gmm.scheduler.queue(playlist.getTracks().get(0), event);
				}
				else {
					gmm.scheduler.queuePlayList(playlist, event);
				}
			}

			@Override
			public void noMatches() {
				// event.getChannel().sendMessage(":x: Musique introuvable.").queue();
				// On tente une recherche
				bot.getPlayerManager().loadItemOrdered(gmm.player, "ytsearch:" + args, new AudioLoadResultHandler() {

					@Override
					public void trackLoaded(AudioTrack track) {
						gmm.scheduler.queue(track, event);
					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist) {
						gmm.scheduler.queue(playlist.getTracks().get(0), event);
					}

					@Override
					public void noMatches() {
						event.reply(":x: Musique introuvable.").queue();
					}

					@Override
					public void loadFailed(FriendlyException throwable) {
						if (throwable.severity == Severity.COMMON) {
							event.reply(":x: Musique non disponible.").queue();
						}
						else {
							event.reply(":x: Erreur lors du chargement de la musique.").queue();
						}
					}
				});
			}

			@Override
			public void loadFailed(FriendlyException throwable) {
				if (throwable.severity == Severity.COMMON) {
					event.reply(":x: Musique non disponible.").queue();
				}
				else {
					event.reply(":x: Erreur lors du chargement de la musique.").queue();
				}
			}
		});
	}
}
