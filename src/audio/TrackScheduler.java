package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

	private static final int MAX_PLAYLIST_SIZE = 100;

	private final AudioPlayer				player;
	private final Guild						guild;
	private final BlockingQueue<AudioTrack>	queue;
	private TextChannel						activeTextChannel;

	/**
	 * @param player
	 *            The audio player this scheduler uses
	 */
	public TrackScheduler(Guild guild, AudioPlayer player) {
		this.player = player;
		this.guild = guild;
		this.queue = new LinkedBlockingQueue<>(MAX_PLAYLIST_SIZE);
		this.activeTextChannel = null;
	}

	public void setActiveTextChannel(TextChannel textChannel) {
		this.activeTextChannel = textChannel;
	}

	public synchronized void queue(AudioTrack track, TextChannel textChannel) {
		if (!this.player.startTrack(track, true)) {
			if (!this.queue.offer(track)) {
				textChannel.sendMessage(":x: Ma playlist est trop remplie !").queue();
				return;
			}
			textChannel.sendMessage(":musical_score: \"" + track.getInfo().title + "\" ajouté à la file d'attente.").queue();
		}
	}

	public synchronized void queuePlayList(AudioPlaylist playlist, TextChannel textChannel) {
		List<AudioTrack> list = playlist.getTracks();
		if (list.size() == 0)
			return;

		if (!this.player.startTrack(list.get(0), true)) {
			if (!this.queue.offer(list.get(0))) {
				textChannel.sendMessage(":x: Ma playlist est trop remplie !").queue();
				return;
			}
		}
		for (int i = 1; i < list.size(); i++) {
			if (!this.queue.offer(list.get(i))) {
				textChannel.sendMessage(":x: Ma playlist est trop remplie(mais j'ai peut-être réussi à ajouter quelques titres) !").queue();
				return;
			}
		}
		textChannel.sendMessage(":musical_score: Playlist \"" + playlist.getName() + "\" ajoutée à la file d'attente.").queue();
	}

	public synchronized void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
	}

	public synchronized void clearPlaylist() {
		this.queue.clear();
		this.player.startTrack(this.queue.poll(), false);
	}

	public synchronized void shufflePlaylist() {
		ArrayList<AudioTrack> tempPlaylist = new ArrayList<>();
		AudioTrack tmp = null;
		while ((tmp = this.queue.poll()) != null) {
			tempPlaylist.add(tmp);
		}
		Collections.shuffle(tempPlaylist);
		for (AudioTrack a : tempPlaylist) {
			this.queue.offer(a);
		}
	}

	public synchronized List<String> getPlaylist() {
		ArrayList<String> titles = new ArrayList<>();
		this.queue.stream().forEach(a -> titles.add(a.getInfo().title));
		return titles;
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player was paused
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player was resumed
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		String ytURL = (track.getSourceManager().getSourceName().equals("youtube")) ? " (<" + track.getInfo().uri + ">)" : "";

		this.activeTextChannel.sendMessage(":musical_note: Joue maintenant: \"" + track.getInfo().title + "\"" + ytURL).queue();
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			// Inutile de jouer pour personne
			VoiceChannel voiceChannel = this.guild.getAudioManager().getConnectedChannel();
			if(voiceChannel!=null && hasAtLeastOneListener(voiceChannel)) {
				this.nextTrack();
			}
		}

		// endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
		// endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
		// endReason == STOPPED: The player was stopped.
		// endReason == REPLACED: Another track started playing while this had not finished
		// endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
		// clone of this back to your queue
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		// An already playing track threw an exception (track end event will still be received
		// separately)
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start a new track
	}

	private boolean hasAtLeastOneListener(VoiceChannel voiceChannel) {
		for(Member m : voiceChannel.getMembers()) {
			if(!m.getUser().isBot()) {
				if(!m.getVoiceState().isDeafened()) {
					return true;
				}
			}
		}

		return false;
	}
}
