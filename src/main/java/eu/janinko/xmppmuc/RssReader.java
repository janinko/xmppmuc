package eu.janinko.xmppmuc;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.xml.sax.SAXException;

public class RssReader  implements Runnable {
	private Date lastUpdate; 
	private MultiUserChat muc;
	private String title;
	private int flags;
	
	private URL url;
	private SyndFeedInput sfInput;
	private TreeSet<SyndEntry> newEntries;
	
	private static HashMap<String,Thread> threads = new HashMap<String,Thread>();
	private static Logger logger = Logger.getLogger(RssReader.class);
	
	public static final int AUTHOR = 0x1;
	public static final int LINK = 0x2;
	public static final int TITLE = 0x4;
	public static final int CONTENT = 0x8;
	
	
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private final DocumentBuilder docbuilder;
	
	public RssReader(URL url, MultiUserChat muc, String title, int flags) throws ParserConfigurationException {
		docbuilder = dbf.newDocumentBuilder();
		this.url = url;
		this.sfInput = new SyndFeedInput();
		newEntries = new TreeSet<SyndEntry>(new Comparator<SyndEntry>() {
			@Override
			public int compare(SyndEntry o1, SyndEntry o2) {
				return o1.getPublishedDate().compareTo(o2.getPublishedDate());
			}});
		
		this.muc = muc;
		this.lastUpdate = new Date(); // getLastest(); //
		this.title = title;
		this.flags = flags;
    }	
	
	public static void main(String[] args){
		try {
			URL feedUrl = new URL("http://www.andaria.cz/rss_novinky.php");
			RssReader r = new RssReader(feedUrl, null, "RSS", AUTHOR | LINK | TITLE | CONTENT);
			r.read();
			r.printNew();
		}  catch (ParserConfigurationException e) {
			logger.error(null, e);
		} catch (MalformedURLException e) {
			logger.error(null, e);
		} catch (Exception e) {
			logger.error(null, e);
		}
			

	}
	
	private void read(){
		SyndFeed feed;
		try {
			feed = sfInput.build(docbuilder.parse(url.openConnection().getInputStream()));
			for(Object a : feed.getEntries() ){
				SyndEntry e = (SyndEntry) a;
				if(e.getPublishedDate().compareTo(lastUpdate) > 0){
					newEntries.add(e);
				}
			}
			lastUpdate = new Date();
		} catch (IllegalArgumentException e1) {
			logger.error("Exception IllegalArgumentException in RssReader.read()", e1);
		} catch (FeedException e1) {
			logger.error("Exception FeedException in RssReader.read()", e1);
		} catch (SAXException e1) {
			logger.error("Exception SAXException in RssReader.read()", e1);
		} catch (IOException e1) {
			if(e1.getClass().equals(UnknownHostException.class)){
				logger.warn("Failed to read feed", e1);
			}else if(e1.getClass().equals(NoRouteToHostException.class)){
				logger.warn("Failed to read feed", e1);
			}else{
				logger.error("Exception IOException in RssReader.read()", e1);
			}
		}
	}
	
	private void printNew(){
		if(newEntries.isEmpty()) return;
		StringBuilder sb = new StringBuilder(title);
		sb.append(": ");
		for(SyndEntry e : newEntries){
			if((flags & LINK) != 0){
				sb.append(e.getLink());
			}
			sb.append('\n');
			if((flags & TITLE) != 0){
				sb.append( e.getTitle());
				sb.append('\n');
			}
			if((flags & CONTENT) != 0){
				String content = e.getDescription().getValue();

				if(content.length() > 220){
					content = content.substring(0, 180) + "... ";
				}
				sb.append(content);
			}
			if((flags & AUTHOR) != 0){
				sb.append(" | ");
				sb.append(e.getAuthor());
			}
			sb.append('\n');
		}
		sb.deleteCharAt(sb.length()-1);
		newEntries.clear();

		try {
			muc.sendMessage(sb.toString());
		} catch (XMPPException e) {
			logger.error(null, e);
		}
	}
	
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        read();
                        printNew();
                    } catch (Exception e) {
                        logger.error(null, e);
                    }
                    Thread.sleep(1000 * 60 * 5);
                }
            } catch (InterruptedException e) {
                logger.debug(null, e);
            }
        }
	

	public static void lunchRssFeed(URL url, MultiUserChat muc, String title, int flags) throws ParserConfigurationException {
		Thread newThread = new Thread(new RssReader(url, muc, title, flags), title);
		newThread.start();
		threads.put(title, newThread);		
	}
	public static void lunchRssFeed(URL url, MultiUserChat muc, String title) throws ParserConfigurationException{
		lunchRssFeed(url,muc,title,LINK | TITLE | CONTENT);
	}
	public static void lunchRssFeed(URL url, MultiUserChat muc, int flags) throws ParserConfigurationException{
		lunchRssFeed(url,muc,"RSS",flags);
	}
	public static void lunchRssFeed(URL url, MultiUserChat muc) throws ParserConfigurationException{
		lunchRssFeed(url,muc,"RSS",LINK | TITLE | CONTENT);
	}

	public static void killAllFeeds() {
		for(Thread t : threads.values()){
			t.interrupt();
		}
		threads.clear();
	}
}
