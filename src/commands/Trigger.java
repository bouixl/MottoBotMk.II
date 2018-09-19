package commands;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Trigger {

	public final String			name;
	protected boolean			nsfw;
	protected List<String>		authorizedGuildIds;
	protected List<String>		authorizedUserIds;
	protected List<String>		blacklistedGuildIds;
	protected List<String>		blacklistedUserIds;
	protected List<Permission>	permissionsRequired;

	public Trigger(String name) {
		super();
		this.name = name;
		this.nsfw = false;
		this.authorizedGuildIds = new ArrayList<>();
		this.authorizedUserIds = new ArrayList<>();
		this.blacklistedGuildIds = new ArrayList<>();
		this.blacklistedUserIds = new ArrayList<>();
		this.permissionsRequired = new ArrayList<>();
	}

	public Trigger(String name, String shortHelp, String longHelp, List<String> authorizedGuildIds,
			List<String> authorizedUserIds, List<String> blacklistedGuildIds, List<String> blacklistedUserIds,
			List<Permission> permissionsRequired) {
		super();
		this.name = name;
		this.authorizedGuildIds = authorizedGuildIds;
		this.authorizedUserIds = authorizedUserIds;
		this.blacklistedGuildIds = blacklistedGuildIds;
		this.blacklistedUserIds = blacklistedUserIds;
		this.permissionsRequired = permissionsRequired;
	}

	public Trigger addAuthorizedGuildId(String guildId) {
		this.authorizedGuildIds.add(guildId);

		return this;
	}

	public Trigger addAuthorizedUserId(String userId) {
		this.authorizedUserIds.add(userId);

		return this;
	}

	public Trigger addBlacklistedGuildId(String guildId) {
		this.blacklistedGuildIds.add(guildId);

		return this;
	}

	public Trigger addBlacklistedUserId(String userId) {
		this.blacklistedUserIds.add(userId);

		return this;
	}

	public Trigger addRequiredPermission(Permission permission) {
		this.permissionsRequired.add(permission);

		return this;
	}

	public Trigger setNSFW() {
		this.nsfw = true;

		return this;
	}

	public void run(MottoBot bot, MessageReceivedEvent event) {
		if (event.getChannelType() == ChannelType.TEXT) // Guild Message
		{
			if (this.blacklistedGuildIds != null && this.blacklistedGuildIds.contains(event.getGuild().getId())) {
				// Guild is blacklisted for this trigger
				return;
			}
			if (this.authorizedGuildIds != null && !this.authorizedGuildIds.isEmpty()
					&& !this.authorizedGuildIds.contains(event.getGuild().getId())) {
				// Guild is not whitelisted for this trigger
				return;
			}
			if (this.permissionsRequired != null && !this.permissionsRequired.isEmpty()
					&& (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)
							|| !event.getMember().getPermissions().containsAll(this.permissionsRequired))) {
				// User doesn't have the required permissions for this trigger
				return;
			}
			if (this.nsfw && !((TextChannel) event.getChannel()).isNSFW()) {
				// Trigger is NSFW but channel is not
				return;
			}
		}
		else // Nope.
		{
			return;
		}
		if (this.blacklistedUserIds != null && this.blacklistedUserIds.contains(event.getGuild().getId())) {
			// User is blacklisted for this trigger
			return;
		}
		if (this.authorizedUserIds != null && !this.authorizedUserIds.isEmpty()
				&& !this.authorizedUserIds.contains(event.getAuthor().getId())) {
			// User is not whitelisted for this trigger
			return;
		}

		// All checks good, execute the trigger
		this.execute(bot, event);
	}

	public abstract void execute(MottoBot bot, MessageReceivedEvent event);

	public abstract boolean tryOn(MottoBot bot, MessageReceivedEvent event);
}
