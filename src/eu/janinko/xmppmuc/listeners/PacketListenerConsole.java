package eu.janinko.xmppmuc.listeners;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.DelayInformation;

import eu.janinko.xmppmuc.MucCommands;

public class PacketListenerConsole implements PacketListener{
	private MucCommands mucc;
	private Set<String> onlineUsers;
	private String commandPrefix;
	
	private Logger logger = Logger.getLogger(PacketListenerConsole.class);

	public PacketListenerConsole(MucCommands mucc) {
		this.mucc = mucc;
		this.commandPrefix = mucc.getPrefix();
	}
	
	public void init(){
		onlineUsers = new HashSet<String>();
		Iterator<String> is = mucc.getMuc().getOccupants();
		while(is.hasNext()){
			String s = is.next();
			onlineUsers.add(s.split("/")[1]);
		}
	}

	@Override
	public void processPacket(Packet packet) {
		if(logger.isTraceEnabled()){logger.trace("Packet + " + packet);}
		
		if(packet instanceof Message){
			processMessage((Message) packet);
		}else if(packet instanceof IQ){
			processIQ((IQ) packet);
		}else if(packet instanceof Presence){
			processPresence((Presence) packet);
		}else{
			logger.warn("Neznamy typ packetu");
		}
	}
	

	private void processPresence(Presence p) {
		if(logger.isTraceEnabled()){logger.trace("  presence packet");}
		String nick = p.getFrom().split("/")[1];
		Calendar c = new GregorianCalendar();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(getPrintableTime(c));
		sb.append(']');
		sb.append("***");
		sb.append(nick);
		switch(p.getType()){
		case available:
			if(!onlineUsers.contains(nick)){
				onlineUsers.add(nick);
				sb.append(" prisel");
				mucc.handlePresence(p);
			}
			break;
		case unavailable:
			onlineUsers.remove(nick);
			sb.append(" odesel");
			mucc.handlePresence(p);
			break;
		default:
			sb.append(' ');
			sb.append(p.getType());
			break;
		}
		logger.info(sb);
	}

	private void processIQ(IQ p) {
		if(logger.isTraceEnabled()){logger.trace("  IQ packet");}
		StringBuilder sb = new StringBuilder();
		sb.append("Received packet: ");
		sb.append(p);
		sb.append(p.getType());
		sb.append(p.toXML());

		logger.debug(sb);
	}

	private void processMessage(Message p) {
		if(logger.isTraceEnabled()){logger.trace("  Message packet");}
		String nick = p.getFrom().split("/")[1];
		PacketExtension px = p.getExtension("jabber:x:delay");
		Calendar c = new GregorianCalendar();
		
		if((px != null) && (px instanceof DelayInformation)){
			DelayInformation di = (DelayInformation) px;
			c.setTime(di.getStamp());
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(getPrintableTime(c));
		sb.append(']');
		sb.append(' ');
		sb.append('<');
		sb.append(nick);
		sb.append('>');
		sb.append(' ');
		sb.append(p.getBody());
		logger.info(sb);
		
		if(p.getBody().startsWith(commandPrefix) && px == null){
			if(logger.isTraceEnabled()){logger.trace("command message");}
			if(logger.isTraceEnabled()){logger.trace("mucc: " + mucc + "; p: "+ p);}
			try{
				mucc.handleCommand(p);
			}catch(Exception e){
				logger.error("Cannot handle message", e);
			}
		}else{
			mucc.handleMessage(p);
		}
	}

	private static String getPrintableTime(Calendar c){
		return (c.get(Calendar.HOUR_OF_DAY) < 10? "0" : "" ) + c.get(Calendar.HOUR_OF_DAY)   + ":" + 
		       (c.get(Calendar.MINUTE)      < 10? "0" : "" ) + c.get(Calendar.MINUTE) + ":" + 
		       (c.get(Calendar.SECOND)      < 10? "0" : "" ) + c.get(Calendar.SECOND);
	}
}
