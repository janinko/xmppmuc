package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.api.plugin.AbstractCommand;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import eu.janinko.xmppmuc.CommandWrapper;
import eu.janinko.xmppmuc.Message;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RssReader extends AbstractCommand{
	private HashMap<String,Feed> feeds = new HashMap<>();
	private static Logger logger = Logger.getLogger(RssReader.class);

    @Override
    public void setWrapper(CommandWrapper commandWrapper) {
		super.setWrapper(commandWrapper);
		cw.startRepeatingTask(new RssTimerTask(), 1000*60*5);
		lunchRssFeed("http://www.andaria.cz/rss_novinky.php", "novinky", Feed.LINK | Feed.TITLE | Feed.CONTENT);
		lunchRssFeed("http://www.andaria.cz/rss_prohresky.php", "prohresky", Feed.AUTHOR | Feed.TITLE | Feed.CONTENT);
		lunchRssFeed("http://www.andaria.cz/rss_trubaci.php", "trubaci", Feed.LINK | Feed.TITLE | Feed.CONTENT);
		lunchRssFeed("http://www.andaria.cz/rss_mesta.php", "mesta", Feed.LINK | Feed.TITLE | Feed.CONTENT | Feed.AUTHOR);
	}

	@Override
	public String getCommand() {
		return "rss";
	}

	@Override
	public void handle(Message m, String[] args) {
	}

	void read(){
		for(Feed f : feeds.values()){
			f.read();
		}
	}

	void printNew(){
		if(!connected) return;
		StringBuilder sb = new StringBuilder();
		for(Feed f : feeds.values()){
			String news = f.getNews();
			if(news == null) continue;
			sb.append(news);
			sb.append("\n");
		}
		if(sb.length() == 0) return;
		sb.deleteCharAt(sb.length() - 1);
		cw.sendMessage(sb.toString());
	}

	@Override
	public void connected() {
		connected = true;
	}

	@Override
	public void disconnected() {
		connected = false;
	}

	public void lunchRssFeed(String url, String title, int flags){
		try {
			Feed feed = new Feed(new URL(url), title, flags);
			feeds.put(title, feed);
		} catch (ParserConfigurationException | MalformedURLException ex) {
			logger.warn("Couldn't add feed", ex);
		}
	}
	public void lunchRssFeed(String url, String title){
		lunchRssFeed(url,title,Feed.LINK | Feed.TITLE | Feed.CONTENT);
	}
	public void lunchRssFeed(String url, int flags){
		lunchRssFeed(url,"RSS",flags);
	}
	public void lunchRssFeed(String url){
		lunchRssFeed(url,"RSS",Feed.LINK | Feed.TITLE | Feed.CONTENT);
	}

	private class RssTimerTask extends TimerTask {
		@Override
		public void run() {
			read();
			printNew();
		}
	}

	private static class Feed{
		private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		private final DocumentBuilder docbuilder;

		public static final int AUTHOR = 0x1;
		public static final int LINK = 0x2;
		public static final int TITLE = 0x4;
		public static final int CONTENT = 0x8;

		private URL url;
		private SyndFeedInput sfInput;
		private TreeSet<SyndEntry> newEntries;
		private Date lastUpdate;
		private String title;
		private int flags;

		public Feed(URL url, String title, int flags) throws ParserConfigurationException {
			docbuilder = dbf.newDocumentBuilder();
			this.url = url;
			this.sfInput = new SyndFeedInput();
			newEntries = new TreeSet<>(new Comparator<SyndEntry>() {
				@Override
				public int compare(SyndEntry o1, SyndEntry o2) {
					return o1.getPublishedDate().compareTo(o2.getPublishedDate());
				}});

			this.lastUpdate = new Date();
			this.title = title;
			this.flags = flags;
		}

		void read() {
			try {
				SyndFeed feed = sfInput.build(docbuilder.parse(url.openConnection().getInputStream()));
				for (Object a : feed.getEntries()) {
					SyndEntry e = (SyndEntry) a;
					if (e.getPublishedDate().compareTo(lastUpdate) > 0) {
						newEntries.add(e);
					}
				}
				lastUpdate = new Date();
			} catch (IllegalArgumentException | FeedException | SAXException | IOException e1) {
				logger.error("Failed to read feed", e1);
			}
		}

		private StringBuilder formatEntry(SyndEntry entry) {
			StringBuilder sb = new StringBuilder();
			if ((flags & LINK) != 0) {
				sb.append(entry.getLink());
			}
			sb.append('\n');
			if ((flags & TITLE) != 0) {
				sb.append(entry.getTitle());
				sb.append('\n');
			}
			if ((flags & CONTENT) != 0) {
				String content = entry.getDescription().getValue();
				if (content.length() > 220) {
					content = content.substring(0, 180) + "... ";
				}
				sb.append(content);
			}
			if ((flags & AUTHOR) != 0) {
				sb.append(" | ");
				sb.append(entry.getAuthor());
			}
			return sb;
		}

		public String getNews() {
			if (newEntries.isEmpty()) return null;
			
			StringBuilder sb = new StringBuilder(title);
			sb.append(": ");
			for (SyndEntry e : newEntries) {
				sb.append(formatEntry(e));
				sb.append('\n');
			}
			sb.deleteCharAt(sb.length() - 1);
			newEntries.clear();

			return sb.toString();
		}
	}

}
