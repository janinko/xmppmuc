package eu.janinko.xmppmuc.listeners;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import eu.janinko.xmppmuc.MucCommands;

public class ChatManagerListenerMucCommand implements ChatManagerListener {
	private String room;
	private MucCommands mucCommands;
	String prefix;

	public ChatManagerListenerMucCommand(String room, MucCommands mucCommands) {
		this.room = room;
		this.mucCommands = mucCommands;
		this.prefix = mucCommands.getPrefix();
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		if(! chat.getParticipant().contains(room) ){
			try {
				chat.sendMessage("Přijímám zprávy jen od lidí z konference!");
			} catch (XMPPException e) {
				System.err.println("MucCommands.cHelp() A");
				e.printStackTrace();
			}
			return;
		}
		chat.addMessageListener(new MessageListener() {
			
			public void processMessage(Chat chat, Message message){
				System.out.print("P<" + chat.getParticipant() + "> ");
				System.out.println(message.getBody());
				if(message.getBody().startsWith(prefix))
					mucCommands.handleCommand(message);
			}
		});
	}

}
