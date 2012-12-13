/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.janinko.xmppmuc;

/**
 *
 * @author jbrazdil
 */
public class Presence {
	Commands commands;
	org.jivesoftware.smack.packet.Presence presence;
	
	public Presence(org.jivesoftware.smack.packet.Presence prs, Commands cmd ){
		presence = prs;
		commands = cmd;
		//presence.getType().available.error.subscribe.subscribed.unavailable.unsubscribe.unsubscribed;
	}
	
	public org.jivesoftware.smack.packet.Presence getSmackPresence(){
		return presence;
	}
}
