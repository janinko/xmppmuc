package eu.janinko.xmppmuc;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;

public class XMPPConnection2 extends XMPPConnection {
	Logger logger = Logger.getLogger(XMPPConnection2.class);

	public XMPPConnection2(ConnectionConfiguration config) {
		super(config);
	}
	
	public Collection<ConnectionListener> getConnectionListeners(){
		return super.getConnectionListeners();
	}
	
	public Map<PacketListener, ListenerWrapper> getPacketListeners(){
		return super.getPacketListeners();
	}
	
	@Override
	public void addPacketListener(PacketListener packetListener,
			PacketFilter packetFilter) {
		logger.trace("PacketListener: '" + packetListener + "'; PacketFilter: '" + packetFilter + "'");
		super.addPacketListener(packetListener, packetFilter);
	}
	
	@Override
	public void removePacketListener(PacketListener packetListener) {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		for(StackTraceElement ste : st){
			sb.append(ste.toString());
			sb.append("\n");
		}
		logger.trace("PacketListener: '" + packetListener + "'");
		logger.trace("Trace: \n" + sb.toString());
		super.removePacketListener(packetListener);
	}

}
