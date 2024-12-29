package main;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import audio.GuildMusicManager;
import audio.TrackScheduler;
import commands.*;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidMusic;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.Tv;
import dev.lavalink.youtube.clients.TvHtml5Embedded;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.WebEmbedded;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import utils.CommonIDs;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class MottoBot extends ListenerAdapter {

	public static final String MOTTO_VERSION = "5";

	public static MottoBot INSTANCE;

	public JDA									jda;
	private String								token;
	private final Instant						startTime;
	private final CommandClient					commandClient;
	private final AudioPlayerManager			playerManager;
	private final Map<Long, GuildMusicManager>	musicManagers;
	private final EventWaiter					waiter;
	private HashMap<Long, HashMap<Long, CircularFifoQueue<String>>> clearTab;

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
		this.clearTab = new HashMap<>();

		this.commandClient = new CommandClient(this);
		this.waiter = new EventWaiter();
		
		this.playerManager = new DefaultAudioPlayerManager();
		YoutubeAudioSourceManager ytSourceManager = new YoutubeAudioSourceManager(
	            /*allowSearch:*/ true,
	            new Music(),
	            new Web(),
	            new TvHtml5Embedded(),
	            new AndroidMusic(),
	            new WebEmbedded(),
	            new Tv());
		ytSourceManager.useOauth2("1//03ECv_fHXN7oaCgYIARAAGAMSNwF-L9Ir0kxEBnVIr1IO9kJpBAw49_pdSV3kaEOYJffeY0qAFIkYdkpGeejQ1GtyGbLlQ_YVGEc", true);
		//ytSourceManager.useOauth2(null, false);
		this.playerManager.registerSourceManager(ytSourceManager);
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		this.musicManagers = new HashMap<>();

		try {
			this.jda = JDABuilder.createDefault(token)
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.addEventListeners(this)
					.addEventListeners(this.commandClient)
					.addEventListeners(this.waiter)
					.setActivity(Activity.playing("Initialisation..."))
					.build();
			jda.awaitReady();
		}
		catch (InterruptedException e) {
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
		
		// These commands take up to an hour to be activated after creation/update/delete
        CommandListUpdateAction commands = jda.updateCommands();
        
        commands.addCommands(
                new CommandDataImpl("motto","motto ?")
                .addOptions(new OptionData(STRING, "args", "tags"))
            );
        
        commands.addCommands(
            new CommandDataImpl("help", "help command")
        );
        
        commands.addCommands(
                new CommandDataImpl("cleanup", "Clean bots commands from this channel")
        );
        
        commands.addCommands(
                new CommandDataImpl("ninja", "better cleanup :)")
        );
        
        commands.addCommands(
                new CommandDataImpl("voicekick", "A simple voice kick")
                .addOptions(new OptionData(STRING, "args", "user").setRequired(true))
        );
        
        commands.addCommands(
                new CommandDataImpl("uptime", "Uptime")
        );
        
        commands.addCommands(
                new CommandDataImpl("version", "Version")
        );
        
        commands.addCommands(
                new CommandDataImpl("ping", "Pong")
        );
        
        commands.addCommands(
                new CommandDataImpl("mp", "Play music from youtube or URL")
                .addOptions(new OptionData(STRING, "args", "nom de la recherche ou URL").setRequired(true))
        );
        
        commands.addCommands(
                new CommandDataImpl("ms", "Skip the current music")
                .addOptions(new OptionData(STRING, "args", "number of music to skip"))
        );
        
        commands.addCommands(
                new CommandDataImpl("ml", "Leave the current voice channel")
        );
        
        commands.addCommands(
                new CommandDataImpl("pl", "Show the current playlist for this bot")
        );
        
        commands.addCommands(
                new CommandDataImpl("shuffle", "Shuffle the current playlist for this bot")
        );
        
        commands.addCommands(
                new CommandDataImpl("volume", "Change the volume for the bot")
                .addOptions(new OptionData(STRING, "args", "volume between 5 and 80"))
        );

        // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
        commands.queue();
	}

	private void registerCommands() {
		this.commandClient.addCommand(new CmdMotto("motto").addAliases("motto"));

		this.commandClient.addCommand(new CmdHelp("help").addAliases("mhelp", "mottohelp"));

		this.commandClient.addCommand(new CmdRestart("restart").addAliases("reboot", "mreboot", "mrestart").setPrivateOnly().addAuthorizedUserId(CommonIDs.U_WYLENTAR).addAuthorizedUserId(CommonIDs.U_MOMOJEAN));
		this.commandClient.addCommand(new CmdShutdown("shutdown").setPrivateOnly().addAuthorizedUserId(CommonIDs.U_WYLENTAR).addAuthorizedUserId(CommonIDs.U_MOMOJEAN));

		this.commandClient.addCommand(new CmdCleanUp("cleanup").addAliases("clear","mottoclear","mclear","clean","mclean","mottoclean").setGuildOnly().addRequiredPermission(Permission.MESSAGE_MANAGE));
		this.commandClient.addCommand(new CmdNinja("ninja").addAliases("mottoninja","mninja").setGuildOnly().addRequiredPermission(Permission.ADMINISTRATOR));
		// plus besoin de cette commande (flemme de mettre a jour, trop la galere)
		//this.commandClient.addCommand(new CmdKickFromVocal("voicekick").addAlias("vkick").setGuildOnly().addRequiredPermission(Permission.VOICE_MOVE_OTHERS));

		this.commandClient.addCommand(new CmdUptime("uptime").addAliases("muptime", "mottouptime"));
		this.commandClient.addCommand(new CmdVersion("version").addAliases("mversion", "mottoversion"));
		this.commandClient.addCommand(new CmdPing("ping").addAliases("mping", "mottoping"));

		this.commandClient.addCommand(new CmdPlaySong("play").addAliases("mottoplay", "mplay", "mp").setGuildOnly());
		this.commandClient.addCommand(new CmdSkipSong("skip").addAliases("mottoskip", "mskip", "ms").setGuildOnly());
		this.commandClient.addCommand(new CmdLeaveAudio("leave").addAliases("mottoleave", "mleave", "ml").setGuildOnly());
		this.commandClient.addCommand(new CmdPlaylist("playlist").addAliases("mottoplaylist", "mplaylist", "mpl", "pl").setGuildOnly());
		this.commandClient.addCommand(new CmdShufflePlaylist("shuffle").addAliases("mottoshuffle", "mshuffle").setGuildOnly());
		this.commandClient.addCommand(new CmdSetVolume("volume").addAliases("mvolume","mottovolume").setGuildOnly().addRequiredPermission(Permission.VOICE_CONNECT).addRequiredPermission(Permission.VOICE_DEAF_OTHERS));
	}

	private void registerTriggers() {
		// TODO
	}

	public String getToken() {
		return this.token;
	}

	public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
		GuildMusicManager gmm = null;
		long gID = guild.getIdLong();

		if ((gmm = this.musicManagers.get(gID)) == null) {
			gmm = new GuildMusicManager(guild, this.playerManager);
			this.musicManagers.put(gID, gmm);

			this.jda.getGuildById(gID).getAudioManager().setSendingHandler(gmm.getSendHandler());
		}

		return gmm;
	}

	public AudioPlayerManager getPlayerManager() {
		return this.playerManager;
	}

	public EventWaiter getWaiter() {
		return this.waiter;
	}

	public String getUptime() {
		Duration d = Duration.between(this.startTime, Instant.now());
		String uptime = formatDuration(d);
		return uptime;
	}

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println(timestamp() + "Prêt !");
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		System.out.println(timestamp() + "J'ai rejoint la guilde \"" + event.getGuild().getName() + "\" [" + event.getGuild().getId() + "].");
	}

	@Override
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		System.err.println(event.getTimeDisconnected().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.FRANCE)) + "\tDéconnecté ! Tentative de reconnection...");
	}

	@Override
	public void onSessionResume(SessionResumeEvent event) {
		System.err.println(timestamp() + "Connection rétablie ! Aucun event perdu.");
	}

	/*
	@Override
	public void onReconnect(ReconnectedEvent event) {
		System.err.println(timestamp() + "Reconnecté ! Peut-être que certains events n'ont pas été traités...");
	}*/

	@Override
	public void onShutdown(ShutdownEvent event) {
		System.out.println(timestamp() + "Déconnecté ! Extinction...");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().equals(this.jda.getSelfUser())) {
			this.addToClearTab(event.getMessage());
		}
	}

    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		VoiceChannel channel = (VoiceChannel) event.getEntity().getGuild().getAudioManager().getConnectedChannel();
		if(channel!=null) {
			if(event.getChannelLeft().equals(channel)) {

				if(!TrackScheduler.hasAtLeastOneListener(channel)) {
			    	Long gID = event.getEntity().getGuild().getIdLong();
			    	GuildMusicManager gmm = this.musicManagers.get(gID);
			    	if(gmm!=null) {
			    		gmm.player.stopTrack();
			    		gmm.scheduler.clearPlaylist();
			    	}
					event.getEntity().getGuild().getAudioManager().closeAudioConnection();
				}
			}
		}
    }

    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
    	VoiceChannel channel = (VoiceChannel) event.getGuild().getAudioManager().getConnectedChannel();
		if(channel!=null) {
			if(event.getVoiceState().getChannel().equals(channel)) {

				if(!TrackScheduler.hasAtLeastOneListener(channel)) {
			    	Long gID = event.getGuild().getIdLong();
			    	GuildMusicManager gmm = this.musicManagers.get(gID);
			    	if(gmm!=null) {
			    		gmm.player.stopTrack();
			    		gmm.scheduler.clearPlaylist();
			    	}
					event.getGuild().getAudioManager().closeAudioConnection();
				}
			}
		}
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

	public static String timestamp() {
		return OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.FRANCE)) + "\t";
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

	public void setDefaultPresence() {
		this.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(CommandClient.COMMAND_PREFIX + "help"), true);
	}

	public int clearChannelTab(TextChannel channel) {
		long gID = channel.getGuild().getIdLong();
		long cID = channel.getIdLong();

		this.clearTab.putIfAbsent(gID, new HashMap<Long, CircularFifoQueue<String>>());
		HashMap<Long, CircularFifoQueue<String>> guildTab = this.clearTab.get(gID);
		CircularFifoQueue<String> messages = guildTab.get(cID);
		if (messages==null || messages.size()<2)
			return 0;

		channel.deleteMessagesByIds(messages).queue();
		guildTab.put(cID, new CircularFifoQueue<String>(100));

		return messages.size();
	}

	public void addToClearTab(Message m) {
		if(m.getChannelType().isGuild()) {
			Long gID = m.getGuild().getIdLong();
			Long cID = m.getChannel().getIdLong();

			this.clearTab.putIfAbsent(gID, new HashMap<Long, CircularFifoQueue<String>>());
			HashMap<Long, CircularFifoQueue<String>> guildTab = this.clearTab.get(gID);
			guildTab.putIfAbsent(cID, new CircularFifoQueue<String>(100));

			guildTab.get(cID).add(m.getId());
		}
	}
}
