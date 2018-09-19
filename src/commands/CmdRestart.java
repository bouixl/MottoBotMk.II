package commands;

import java.io.IOException;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdRestart extends Command {

	public CmdRestart(String name) {
		super(name);
	}

	@Override
	public void execute(MessageReceivedEvent event, String args) {
		try {
			MottoBot.INSTANCE.shutdown();
			ProcessBuilder pb = new ProcessBuilder("java","-jar","MottoBot.jar",MottoBot.INSTANCE.getToken(),"10");
			pb.inheritIO();
			pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
