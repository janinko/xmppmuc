package eu.janinko.xmppmuc;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DelayInformation;

public class PacketListenerConsole implements PacketListener{
	private MucCommands mucc;
	private MucSeer mucs;
	private Set<String> onlineUsers;
	private String commandPrefix;

	public PacketListenerConsole(MucCommands mucc, MucSeer mucs, MultiUserChat muc) {
		this.mucc = mucc;
		this.commandPrefix = mucc.getPrefix();
		onlineUsers = new HashSet<String>();
		Iterator<String> is = muc.getOccupants();
		while(is.hasNext()){
			String s = is.next();
			onlineUsers.add(s.split("/")[1]);
		}
		this.mucs = mucs;
	}

	@Override
	public void processPacket(Packet packet) {
		String nick = packet.getFrom().split("/")[1];
		PacketExtension px = packet.getExtension("jabber:x:delay");
		Calendar c = new GregorianCalendar();
		
		if((px != null) && (px instanceof DelayInformation)){
			DelayInformation di = (DelayInformation) px;
			c.setTime(di.getStamp());
		}
		
		if(packet instanceof Message){
			Message p = (Message) packet;

			System.out.print("[" + getPrintableTime(c) + "] ");
			System.out.print("<" + nick + "> ");
			System.out.println(p.getBody());
			
			if(p.getBody().startsWith(commandPrefix) && px == null){
				mucc.handleCommand(p);
			}else{
				mucs.checkMessage(p);
			}
		}else if(packet instanceof IQ){
			IQ p = (IQ) packet;
			System.out.println("Received packet: " + p);
			System.out.println(p.getType());
			System.out.println(p.toXML());
		}else if(packet instanceof Presence){
			Presence p = (Presence) packet;
			
			if(p.getType() == Presence.Type.available){
				if(!onlineUsers.contains(nick)){
					onlineUsers.add(nick);
					System.out.print("[" + getPrintableTime(c) + "] ");
					System.out.println("***" + nick + " prisel");
					mucc.handlePresence(p);
				}
			}else if(p.getType() == Presence.Type.unavailable){
				onlineUsers.remove(nick);
				System.out.print("[" + getPrintableTime(c) + "] ");
				System.out.println("***" + nick + " odesel");
				mucc.handlePresence(p);
			}else{
				System.out.print("[" + getPrintableTime(c) + "] ");
				System.out.println("***" + nick + " " + p.getType());
			}

		}else{
			System.out.println("Chyba");
		}
	}
	

	private static String getPrintableTime(Calendar c){
		return (c.get(Calendar.HOUR)   < 10? "0" : "" ) + c.get(Calendar.HOUR)   + ":" + 
		       (c.get(Calendar.MINUTE) < 10? "0" : "" ) + c.get(Calendar.MINUTE) + ":" + 
		       (c.get(Calendar.SECOND) < 10? "0" : "" ) + c.get(Calendar.SECOND);
	}
}
