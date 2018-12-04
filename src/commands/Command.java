package commands;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {

	public final String			name;
	protected List<String>		aliases;
	protected boolean			guildOnly;
	protected boolean			privateOnly;
	protected boolean			nsfw;
	protected List<String>		authorizedGuildIds;
	protected List<String>		authorizedUserIds;
	protected List<String>		blacklistedGuildIds;
	protected List<String>		blacklistedUserIds;
	protected List<Permission>	permissionsRequired;

	public Command(String name) {
		super();
		this.name = name;
		this.aliases = new ArrayList<>();
		this.aliases.add(name);
		this.guildOnly = false;
		this.privateOnly = false;
		this.nsfw = false;
		this.authorizedGuildIds = new ArrayList<>();
		this.authorizedUserIds = new ArrayList<>();
		this.blacklistedGuildIds = new ArrayList<>();
		this.blacklistedUserIds = new ArrayList<>();
		this.permissionsRequired = new ArrayList<>();
	}

	public Command addAlias(String alias) {
		this.aliases.add(alias);

		return this;
	}

	public Command addAliases(String... aliases) {
		for (int i = 0; i < aliases.length; ++i) {
			this.aliases.add(aliases[i]);
		}

		return this;
	}

	public Command addAuthorizedGuildId(String guildId) {
		this.setGuildOnly();
		this.authorizedGuildIds.add(guildId);

		return this;
	}

	public Command addAuthorizedUserId(String userId) {
		this.authorizedUserIds.add(userId);

		return this;
	}

	public Command addBlacklistedGuildId(String guildId) {
		this.blacklistedGuildIds.add(guildId);

		return this;
	}

	public Command addBlacklistedUserId(String userId) {
		this.blacklistedUserIds.add(userId);

		return this;
	}

	public Command addRequiredPermission(Permission permission) {
		this.setGuildOnly();
		this.permissionsRequired.add(permission);

		return this;
	}

	public Command setGuildOnly() {
		this.guildOnly = true;
		this.privateOnly = false;

		return this;
	}

	public Command setPrivateOnly() {
		this.guildOnly = false;
		this.privateOnly = true;

		return this;
	}

	public Command setNSFW() {
		this.nsfw = true;

		return this;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public void run(MottoBot bot, MessageReceivedEvent event, String args) {
		if (event.getChannelType() == ChannelType.TEXT) // Guild Message
		{
			if (this.privateOnly) {
				// Only available in PM
				event.getChannel().sendMessage(":x: Cette commande ne peut pas être utilisée ici. (Err: G_NO)").queue();
				return;
			}
			if (this.blacklistedGuildIds != null && this.blacklistedGuildIds.contains(event.getGuild().getId())) {
				// Guild is blacklisted for this command
				return;
			}
			if (this.authorizedGuildIds != null && !this.authorizedGuildIds.isEmpty() && !this.authorizedGuildIds.contains(event.getGuild().getId())) {
				// Guild is not whitelisted for this command
				return;
			}
			if (this.permissionsRequired != null && !this.permissionsRequired.isEmpty()
					&& (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR) || !event.getMember().getPermissions().containsAll(this.permissionsRequired))) {
				// User doesn't have the required permissions to use that command.
				event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: PERM)").queue();
				return;
			}
			if (this.nsfw && !((TextChannel) event.getChannel()).isNSFW()) {
				// Command is NSFW but channel is not
				event.getChannel().sendMessage(":x: Cette commande ne peut pas être utilisée ici. (Err: NSFW)").queue();
				return;
			}
		}
		else if (event.getChannelType() == ChannelType.PRIVATE) // Private Message
		{
			if (this.guildOnly) {
				// Not available in PM
				event.getChannel().sendMessage(":x: Cette commande ne peut pas être utilisée ici. (Err: PM_NO)").queue();
				return;
			}
			if (this.nsfw) {
				// No NSFW in PM
				return;
			}
		}
		else // WTF
		{
			return;
		}
		if (this.blacklistedUserIds != null && this.blacklistedUserIds.contains(event.getAuthor().getId())) {
			// User is blacklisted for this command
			event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: UID_BL)").queue();
			return;
		}
		if (this.authorizedUserIds != null && !this.authorizedUserIds.isEmpty() && !this.authorizedUserIds.contains(event.getAuthor().getId())) {
			// User is not whitelisted for this command
			event.getChannel().sendMessage(":x: Vous n'avez pas la permission d'utiliser cette commande. (Err: UID_WL)").queue();
			return;
		}

		// All checks good, execute the command
		this.execute(bot, event, args);
	}

	public abstract void execute(MottoBot bot, MessageReceivedEvent event, String args);
}
