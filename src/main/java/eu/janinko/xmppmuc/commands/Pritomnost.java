package eu.janinko.xmppmuc.commands;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Pritomnost implements Serializable {
	private static final long serialVersionUID = 1L;
	private static SimpleDateFormat sdf = new SimpleDateFormat("E d.M.yyyy HH:mm:ss");

	private Date date;
	private Presence.Type presence;
	private Message.Type message;

	public Pritomnost(Presence.Type type) {
		date = new Date();
		presence = type;
		message = null;
	}

	public Pritomnost(Message.Type type) {
		date = new Date();
		presence = null;
		message = type;
	}

	@Override
	public String toString() {
		if (presence != null) {
			switch (presence) {
				case available:
					return "Teď tu byl! Vlastně by tu furt měl být, od " + sdf.format(date);
				case error:
					return "HUH! WTF? " + sdf.format(date);
				case subscribe:
					return "Teď tu byl! A cosi chtěl v " + sdf.format(date);
				case subscribed:
					return "Teď tu byl! A cosi bylo schváleno v " + sdf.format(date);
				case unavailable:
					return "Teď tu byl! Ale zdrhnul v " + sdf.format(date);
				case unsubscribe:
					return "Teď tu byl! A cosi chtěl zrušit v " + sdf.format(date);
				case unsubscribed:
					return "Teď tu byl! A cosi bylo zrušeno v " + sdf.format(date);
			}
		}
		if (message != null) {
			switch (message) {
				case chat:
					return "Teď tu byl! A cosi žvatlal kolem " + sdf.format(date);
				case error:
					return "WTF? HUH! " + sdf.format(date);
				case groupchat:
					return "Teď tu byl! A cosi vykládal kolem " + sdf.format(date);
				case headline:
					return "Teď tu byl! A cosi zdůrazňoval kolem " + sdf.format(date);
				case normal:
					return "Teď tu byl! A cosi psal kolem " + sdf.format(date);
			}
		}
		return "Bug!";
	}

}
