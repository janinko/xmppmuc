package eu.janinko.xmppmuc;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;


public class MucSeer {
	private MultiUserChat muc;


	public MucSeer(){
		muc = null;
	}
	
	public void setMUC(MultiUserChat muc) throws XMPPException{
		this.muc = muc;
	}
	
	public void checkMessage(Message p) {
		if(p.getBody().toLowerCase().contains("prodam") || p.getBody().toLowerCase().contains("prodám") || 
				   p.getBody().toLowerCase().contains("koupim") || p.getBody().toLowerCase().contains("koupím") || 
				   p.getBody().toLowerCase().contains("kupim") || p.getBody().toLowerCase().contains("kůpím")){
			try {
				muc.sendMessage(MucCommands.hGetNick(p) + ": Tohle neni inzerce!");
			} catch (XMPPException e) {
				System.err.println("MucSeer.checkMessage() A");
				e.printStackTrace();
			}
		}
	}

}
