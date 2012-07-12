package eu.janinko.xmppmuc.listeners;


import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

public class PacketListenerLogger implements PacketListener {
	
	private static Logger logger = Logger.getLogger(PacketListenerLogger.class);

	public PacketListenerLogger(){
	}
	@Override
	public void processPacket(Packet packet) {
		logger.debug("processPacket: packet='" + packet + "'" );
	}

}
