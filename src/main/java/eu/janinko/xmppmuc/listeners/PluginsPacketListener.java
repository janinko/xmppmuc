package eu.janinko.xmppmuc.listeners;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import eu.janinko.xmppmuc.Commands;

public class PluginsPacketListener implements PacketListener {
	private Commands commands;
	
	private Logger logger = Logger.getLogger(PluginsPacketListener.class);
	
	public PluginsPacketListener(Commands commands){
		this.commands = commands;
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
		commands.handlePresence(p);
	}
	
	private void processIQ(IQ p) {
		if(logger.isTraceEnabled()){logger.trace("  IQ packet");}
	}
	

	private void processMessage(Message p) {
		if(logger.isTraceEnabled()){logger.trace("  Message packet");}
		commands.handleMessage(p);
	}

}
