package commands;

import main.MottoBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdHelp extends Command {

	public CmdHelp(String name) {
		super(name);
	}

	@Override
	public void execute(MottoBot bot, MessageReceivedEvent event, String args) {
		MessageBuilder mb = new MessageBuilder();
		mb.append("Liste des commandes: \n");
		mb.append("```");
		mb.append("=motto [tag]:       Affiche un lien vers l'image voulue\n");
		mb.append("=mclear :      	   Supprime les 50 derniers messages de Motto Bot dans le canal(et les commandes qui les ont déclenchés)\n");
		mb.append("=mninja :     	   Supprime autant de messages que possible dans le canal\n");
		mb.append("=vkick @___ :	   Kick le membre mentionné du vocal.\n");
		mb.append("=mhelp :      	   Affiche les commandes disponible\n\n");
		mb.append("=muptime :   	   Temps écoulé depuis le dernier reboot de MottoBot\n");
		mb.append("=mping :     	   Ping MottoBot\n");
		mb.append("=mversion :   	   Affiche la version courante de MottoBot\n");
		mb.append("```");
		mb.append("\nPlayer :\n");
		mb.append("```");
		mb.append("=mp (arg) : 		   Rejoint le channel vocal lance la musique si possible, arg peut etre une URL ou un terme de recherche\n");
		mb.append("=mpl :    		   Affiche la playlist\n");
		mb.append("=mshuffle :    	   Mélange la playlist\n");
		mb.append("=ms :       		   Passe à la prochaine musique dans la playlist\n");
		mb.append("=ml :      		   Quitte le channel vocal et vide la playlist\n");
		mb.append("\nListe des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs");
		mb.append("```");
		Message m = mb.build();
		event.getChannel().sendMessage(m).queue();
	}

	@Override
	public void execute(MottoBot bot, SlashCommandEvent event, String args) {
		MessageBuilder mb = new MessageBuilder();
		mb.append("Liste des commandes: \n");
		mb.append("```");
		mb.append("/motto [tag]:       Affiche un lien vers l'image voulue\n");
		mb.append("/mclear :      	   Supprime les 50 derniers messages de Motto Bot dans le canal(et les commandes qui les ont déclenchés)\n");
		mb.append("/mninja :     	   Supprime autant de messages que possible dans le canal\n");
		mb.append("/vkick @___ :	   Kick le membre mentionné du vocal.\n");
		mb.append("/help :      	   Affiche les commandes disponible\n\n");
		mb.append("/muptime :   	   Temps écoulé depuis le dernier reboot de MottoBot\n");
		mb.append("/mping :     	   Ping MottoBot\n");
		mb.append("/mversion :   	   Affiche la version courante de MottoBot\n");
		mb.append("```");
		mb.append("\nPlayer :\n");
		mb.append("```");
		mb.append("/mp (arg) : 		   Rejoint le channel vocal lance la musique si possible, arg peut etre une URL ou un terme de recherche\n");
		mb.append("/mpl :    		   Affiche la playlist\n");
		mb.append("/mshuffle :    	   Mélange la playlist\n");
		mb.append("/ms :       		   Passe à la prochaine musique dans la playlist\n");
		mb.append("/ml :      		   Quitte le channel vocal et vide la playlist\n");
		mb.append("\nListe des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs");
		mb.append("```");
		Message m = mb.build();
		event.reply(m).queue();
	}

}
