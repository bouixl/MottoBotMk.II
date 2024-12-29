package commands;



import main.MottoBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CmdHelp extends Command {

	public CmdHelp(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		MessageCreateBuilder mb = new MessageCreateBuilder();
		mb.addContent("Liste des commandes: \n");
		mb.addContent("```");
		mb.addContent("=motto [tag]:       Affiche un lien vers l'image voulue\n");
		mb.addContent("=mclear :      	   Supprime les 50 derniers messages de Motto Bot dans le canal(et les commandes qui les ont déclenchés)\n");
		mb.addContent("=mninja :     	   Supprime autant de messages que possible dans le canal\n");
		mb.addContent("=mhelp :      	   Affiche les commandes disponible\n\n");
		mb.addContent("=muptime :   	   Temps écoulé depuis le dernier reboot de MottoBot\n");
		mb.addContent("=mping :     	   Ping MottoBot\n");
		mb.addContent("=mversion :   	   Affiche la version courante de MottoBot\n");
		mb.addContent("```");
		mb.addContent("\nPlayer :\n");
		mb.addContent("```");
		mb.addContent("=mp (arg) : 		   Rejoint le channel vocal lance la musique si possible, arg peut etre une URL ou un terme de recherche\n");
		mb.addContent("=mpl :    		   Affiche la playlist\n");
		mb.addContent("=mshuffle :    	   Mélange la playlist\n");
		mb.addContent("=ms :       		   Passe à la prochaine musique dans la playlist\n");
		mb.addContent("=ml :      		   Quitte le channel vocal et vide la playlist\n");
		mb.addContent("\nListe des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs");
		mb.addContent("```");
		MessageCreateData m = mb.build();
		event.getChannel().sendMessage(m).queue();
	}

	@Override
	public void execute(MottoBot bot, SlashCommandInteractionEvent event, String args) {
		MessageCreateBuilder mb = new MessageCreateBuilder();
		mb.addContent("Liste des commandes: \n");
		mb.addContent("```");
		mb.addContent("=motto [tag]:       Affiche un lien vers l'image voulue\n");
		mb.addContent("=mclear :      	   Supprime les 50 derniers messages de Motto Bot dans le canal(et les commandes qui les ont déclenchés)\n");
		mb.addContent("=mninja :     	   Supprime autant de messages que possible dans le canal\n");
		mb.addContent("=mhelp :      	   Affiche les commandes disponible\n\n");
		mb.addContent("=muptime :   	   Temps écoulé depuis le dernier reboot de MottoBot\n");
		mb.addContent("=mping :     	   Ping MottoBot\n");
		mb.addContent("=mversion :   	   Affiche la version courante de MottoBot\n");
		mb.addContent("```");
		mb.addContent("\nPlayer :\n");
		mb.addContent("```");
		mb.addContent("=mp (arg) : 		   Rejoint le channel vocal lance la musique si possible, arg peut etre une URL ou un terme de recherche\n");
		mb.addContent("=mpl :    		   Affiche la playlist\n");
		mb.addContent("=mshuffle :    	   Mélange la playlist\n");
		mb.addContent("=ms :       		   Passe à la prochaine musique dans la playlist\n");
		mb.addContent("=ml :      		   Quitte le channel vocal et vide la playlist\n");
		mb.addContent("\nListe des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs");
		mb.addContent("```");
		MessageCreateData m = mb.build();
		event.reply(m).queue();
	}

}
