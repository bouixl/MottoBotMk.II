package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandClient extends ListenerAdapter {

	public static final String	COMMAND_PREFIX	= "==";
	private Pattern				commandPattern	= Pattern.compile("^" + COMMAND_PREFIX + "([^\\s]+) ?(.*)",
			Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	private final MottoBot	bot;
	private List<Command>	registeredCommands;
	private List<Trigger>	registeredTriggers;

	public CommandClient(MottoBot mottoBot) {
		this.bot = mottoBot;
		this.registeredCommands = new ArrayList<>();
		this.registeredTriggers = new ArrayList<>();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;
		if (event.getMessage().isWebhookMessage())
			return;

		if (event.getMessage().getContentRaw().startsWith(COMMAND_PREFIX)
				&& event.getMessage().getContentRaw().length() > 2)
			this.lookForCommand(event);
		else
			this.lookForTrigger(event);
	}

	public void addCommand(Command c) {
		this.registeredCommands.add(c);
	}

	public void addTrigger(Trigger t) {
		this.registeredTriggers.add(t);
	}

	private void lookForCommand(MessageReceivedEvent event) {
		Matcher matcher = this.commandPattern.matcher(event.getMessage().getContentRaw());
		if (matcher.matches()) { // Should always match at this point
			String word = matcher.group(1).toLowerCase();
			String args = matcher.group(2).isEmpty() ? "" : matcher.group(2);

			final Command command;
			command = this.registeredCommands.stream().filter(cmd -> cmd.getAliases().contains(word)).findAny()
					.orElse(null);
			if (command != null) {
				command.run(this.bot, event, args);
			}
		}
		else {
			event.getChannel().sendMessage("Erreur: Impossible de traiter la commande.").queue();
		}
	}

	private void lookForTrigger(MessageReceivedEvent event) {
		final Trigger trigger;
		trigger = this.registeredTriggers.stream().filter(trgr -> trgr.tryOn(this.bot, event)).findAny().orElse(null);
		if (trigger != null) {
			trigger.run(this.bot, event);
		}
	}
}
