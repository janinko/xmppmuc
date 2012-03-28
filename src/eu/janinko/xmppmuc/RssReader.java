package eu.janinko.xmppmuc;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import yarfraw.io.FeedReader;
import yarfraw.core.datamodel.*;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class RssReader  implements Runnable {
	private FeedReader feedReader;
	private Date lastUpdate; 
	private MultiUserChat muc;
	private String title;
	private int flags;
	
	private static HashMap<String,Thread> threads = new HashMap<String,Thread>();
	
	public static final int AUTHOR = 0x1;
	public static final int LINK = 0x2;
	public static final int TITLE = 0x4;
	public static final int CONTENT = 0x8;
	
	public RssReader(String url, MultiUserChat muc, String title, int flags) throws URIException, YarfrawException, IOException {
		this.muc = muc;
		this.feedReader = new FeedReader(new HttpURL(url));
		this.lastUpdate = new Date(); // getLastest(); //
		this.title = title;
		this.flags = flags;
    }
	
	@SuppressWarnings("unused")
	private Date getLastest() {
		SimpleDateFormat sdf = new SimpleDateFormat("d.M.yy, k:m", Locale.US);
		Date max = null;
		try {
			ChannelFeed channelFeed = feedReader.readChannel();
			for(ItemEntry i : channelFeed.getItems()){
				String isdate = i.getPubDate();
				if(isdate == null){		// FIXME: When corrected on web, remove this hotfix
					isdate = i.getElementByLocalName("pubdate").getFirstChild().getNodeValue();
				}
				Date idate = sdf.parse(isdate);
				if(max==null){
					max = idate;
				}
				if(idate.getTime() > max.getTime())
				{
					if(idate.getTime() > max.getTime()){
						max = idate;
					}
				}
			}
		} catch (YarfrawException e) {
			System.err.println("RssReader.getLastest() A");
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("RssReader.getLastest() B");
			e.printStackTrace();
		}
		return new Date(max.getTime()-1);
	}

	public void printNew() throws XMPPException{
		SimpleDateFormat sdf = new SimpleDateFormat("d.M.yy, k:m", Locale.US);
		try {
			ChannelFeed channelFeed = feedReader.readChannel();
			Date max = lastUpdate;
			for(ItemEntry i : channelFeed.getItems()){
				String isdate = i.getPubDate();
				if(isdate == null){		// FIXME: When corrected on web, remove this hotfix
					isdate = i.getElementByLocalName("pubdate").getFirstChild().getNodeValue();
				}
				Date idate = sdf.parse(isdate);
				if(idate.getTime() > lastUpdate.getTime())
				{
					if(idate.getTime() > max.getTime()){
						max = idate;
					}
					String content = i.getDescriptionOrSummaryText();
					if(content.length() > 220){
						content = content.substring(0, 180) + "... ";
					}
					String send = title + ": ";
					if((flags & LINK) != 0){
						send += i.getLinks().get(0).getHref();
					}
					send += "\n";
					if((flags & TITLE) != 0){
						send += i.getTitleText() + "\n";
					}
					if((flags & CONTENT) != 0){
						send += content;
					}
					if((flags & AUTHOR) != 0){
						send += " | " + i.getAuthorOrCreator().get(0).getEmailOrText();
					}
					
					muc.sendMessage(send);
				}
			}
			lastUpdate = new Date(max.getTime());
		} catch (YarfrawException e) {
			System.err.println("YarfrawException A: " + e.getMessage());
			//e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("ParseException B: " + e.getMessage());
			//e.printStackTrace();
		}
		
	}
	
	public void run(){
		try {
			while(true){
				try{
					printNew();
				} catch (XMPPException e) {
					e.printStackTrace();
				}
				Thread.sleep(1000*60*5);
			}
		} catch (InterruptedException e) {
			System.err.println("RssReader.run() A");
		}
	}
	

	public static void lunchRssFeed(String url, MultiUserChat muc, String title, int flags) throws URIException, YarfrawException, IOException{
		Thread newThread = new Thread(new RssReader(url, muc, title, flags), title);
		newThread.start();
		threads.put(title, newThread);		
	}
	public static void lunchRssFeed(String url, MultiUserChat muc, String title) throws URIException, YarfrawException, IOException{
		lunchRssFeed(url,muc,title,LINK | TITLE | CONTENT);
	}
	public static void lunchRssFeed(String url, MultiUserChat muc, int flags) throws URIException, YarfrawException, IOException{
		lunchRssFeed(url,muc,"RSS",flags);
	}
	public static void lunchRssFeed(String url, MultiUserChat muc) throws URIException, YarfrawException, IOException{
		lunchRssFeed(url,muc,"RSS",LINK | TITLE | CONTENT);
	}

	public static void killAllFeeds() {
		for(Thread t : threads.values()){
			t.interrupt();
		}
		threads.clear();
	}
}
