package eu.janinko.xmppmuc.listeners;


import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import eu.janinko.xmppmuc.Xmppmuc;

public class PacketListenerImpl implements PacketListener {
	
	private Xmppmuc xmppmuc;
	private static Logger logger = Logger.getLogger(PacketListenerImpl.class);

	public PacketListenerImpl(Xmppmuc xmppmuc){
		this.xmppmuc = xmppmuc;
	}
	@Override
	public void processPacket(Packet packet) {
		logger.debug("processPacket: packet='" + packet + "'" );
	}

}
