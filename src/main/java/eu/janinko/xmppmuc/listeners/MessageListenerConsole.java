package eu.janinko.xmppmuc.listeners;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class MessageListenerConsole implements MessageListener{
	@Override
	public void processMessage(Chat chat, Message message) {
		System.out.println("Received message: " + message);
		System.out.println(message.toXML());
	}
}
