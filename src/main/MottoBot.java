package main;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import audio.GuildMusicManager;
import commands.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.CommonIDs;

public class MottoBot extends ListenerAdapter {

	public static final String MOTTO_VERSION = "180918-1";

	public static MottoBot INSTANCE;

	public JDA									jda;
	private String								token;
	private final Instant						startTime;
	private final CommandClient					commandClient;
	private final AudioPlayerManager			playerManager;
	private final Map<Long, GuildMusicManager>	musicManagers;
	private final EventWaiter					waiter;

	public static void main(String[] args) {
		if (args.length < 1)
			throw new IllegalArgumentException("Il faut un token d'authentification !");

		if (args.length > 1) {
			int sec;
			try {
				sec = Integer.parseInt(args[1]);
			}
			catch (Exception e) {
				sec = 10;
				e.printStackTrace();
			}

			System.out.println("Lancement dans " + sec + " secondes.");

			try {
				Thread.sleep(1000 * sec);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Lancement !");
		}

		INSTANCE = new MottoBot(args[0]);
	}

	public MottoBot(String token) {
		this.startTime = Instant.now();
		this.token = token;

		this.commandClient = new CommandClient(this);
		this.waiter = new EventWaiter();

		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		this.musicManagers = new HashMap<>();

		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(token);
		builder.setAudioEnabled(true);
		builder.setStatus(OnlineStatus.INVISIBLE);
		builder.setGame(Game.playing("Initialisation..."));
		builder.addEventListener(this);
		builder.addEventListener(this.commandClient);
		builder.addEventListener(this.waiter);

		try {
			this.jda = builder.build().awaitReady();
		}
		catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(timestamp() + "Connecté en tant que \"" + this.jda.getSelfUser().getName() + "\"");
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		for (Guild g : this.jda.getGuilds()) {
			System.out.println("\t" + g.getName() + " - " + g.getId());
		}

		this.setDefaultPresence();
		this.registerCommands();
		this.registerTriggers();
	}

	public synchronized GuildMusicManager getGuildMusicManager(long gID) {
		GuildMusicManager gmm = null;
		if ((gmm = this.musicManagers.get(gID)) == null) {
			gmm = new GuildMusicManager(this.playerManager);
			this.musicManagers.put(gID, gmm);

			this.jda.getGuildById(gID).getAudioManager().setSendingHandler(gmm.getSendHandler());
		}

		return gmm;
	}

	public void shutdown() {
		this.jda.getGuilds().stream().forEach(g -> {
			g.getAudioManager().closeAudioConnection();
			GuildMusicManager m = this.musicManagers.get(g.getIdLong());
			if (m != null) {
				m.scheduler.clearPlaylist();
				m.player.destroy();
			}
		});
		this.playerManager.shutdown();
		this.jda.shutdown();
	}

	private void registerTriggers() {
		// TODO
	}

	private void registerCommands() {
		this.commandClient.addCommand(new CmdRestart("restart").addAliases("reboot", "mreboot", "mrestart")
				.addAuthorizedUserId(CommonIDs.U_WYLENTAR).addAuthorizedUserId(CommonIDs.U_MOMOJEAN));
		this.commandClient.addCommand(new CmdShutdown("shutdown").addAuthorizedUserId(CommonIDs.U_WYLENTAR)
				.addAuthorizedUserId(CommonIDs.U_MOMOJEAN));

		this.commandClient.addCommand(new CmdPlaySong("play").addAliases("mottoplay", "mplay", "mp").setGuildOnly());
		this.commandClient.addCommand(new CmdSkipSong("skip").addAliases("mottoskip", "mskip", "ms").setGuildOnly());
		this.commandClient
				.addCommand(new CmdLeaveAudio("leave").addAliases("mottoleave", "mleave", "ml").setGuildOnly());
		this.commandClient
				.addCommand(new CmdPlaylist("playlist").addAliases("mottoplaylist", "mplaylist", "mpl").setGuildOnly());
		this.commandClient
				.addCommand(new CmdShufflePlaylist("shuffle").addAliases("mottoshuffle", "mshuffle").setGuildOnly());

		this.commandClient.addCommand(new CmdVersion("version").addAlias("mversion"));

		this.commandClient.addCommand(new CmdTestArgs("test"));
	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println(timestamp() + "Prêt !");
	}

	public void setDefaultPresence() {
		this.jda.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing(CommandClient.COMMAND_PREFIX + "help"),
				true);
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		System.out.println(timestamp() + "J'ai rejoint la guilde \"" + event.getGuild().getName() + "\" ["
				+ event.getGuild().getId() + "].");
	}

	@Override
	public void onDisconnect(DisconnectEvent event) {
		System.err.println(
				event.getDisconnectTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.FRANCE))
						+ "\tDéconnecté ! Tentative de reconnection...");
	}

	@Override
	public void onResume(ResumedEvent event) {
		System.err.println(timestamp() + "Connection rétablie ! Aucun event perdu.");
	}

	@Override
	public void onReconnect(ReconnectedEvent event) {
		System.err.println(timestamp() + "Reconnecté ! Peut-être que certains events n'ont pas été traités...");
	}

	@Override
	public void onShutdown(ShutdownEvent event) {
		System.out.println(timestamp() + "Déconnecté ! Extinction...");
	}

	public static String timestamp() {
		return OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.FRANCE))
				+ "\t";
	}

	@SuppressWarnings("unused")
	private static String authorString(MessageReceivedEvent e) {
		String res = "";
		res += e.getMember().getEffectiveName();
		res += "[" + e.getAuthor().getId() + "]";
		if (e.isFromType(ChannelType.TEXT)) {
			res += "@[" + e.getGuild().getId() + "]";
		}
		else if (e.isFromType(ChannelType.PRIVATE)) {
			res += "@PRIVATE";
		}
		return res;
	}

	public String getUptime() {
		Duration d = Duration.between(this.startTime, Instant.now());
		String uptime = formatDuration(d);
		return uptime;
	}

	public static String formatDuration(Duration d) {
		String res;

		long jours = d.toDays();
		long heures = d.minusDays(jours).toHours();
		long minutes = d.minusDays(jours).minusHours(heures).toMinutes();
		long secondes = d.minusDays(jours).minusHours(heures).minusMinutes(minutes).getSeconds();

		res = jours + " jours, " + heures + " heures, " + minutes + " minutes et " + secondes + " secondes ";

		return res;
	}

	public String getToken() {
		return this.token;
	}

	public AudioPlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public EventWaiter getWaiter() {
		return this.waiter;
	}
}
