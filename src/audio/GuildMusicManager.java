package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
	public final AudioPlayer player;
	public final TrackScheduler scheduler;

	/**
	* Creates a player and a track scheduler.
	* @param manager Audio player manager to use for creating the player.
	*/
	public GuildMusicManager(AudioPlayerManager manager) {
		this.player = manager.createPlayer();
		this.player.setVolume(10);
		this.scheduler = new TrackScheduler(this.player);
		this.player.addListener(this.scheduler);
	}

	/**
	* @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
	*/
	public AudioPlayerSendHandler getSendHandler() {
		return new AudioPlayerSendHandler(this.player);
	}
}