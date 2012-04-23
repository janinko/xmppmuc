package eu.janinko.xmppmuc.listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

import eu.janinko.xmppmuc.MucCommands;
import eu.janinko.xmppmuc.MucSeer;

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
		System.out.println("Packet + " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S")).format(new Date()));
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
				mucc.handleMessage(p);
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
		return (c.get(Calendar.HOUR_OF_DAY) < 10? "0" : "" ) + c.get(Calendar.HOUR_OF_DAY)   + ":" + 
		       (c.get(Calendar.MINUTE)      < 10? "0" : "" ) + c.get(Calendar.MINUTE) + ":" + 
		       (c.get(Calendar.SECOND)      < 10? "0" : "" ) + c.get(Calendar.SECOND);
	}
}
