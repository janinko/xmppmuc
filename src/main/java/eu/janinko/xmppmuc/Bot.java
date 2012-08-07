package eu.janinko.xmppmuc;

import java.util.logging.Level;
import org.apache.log4j.Logger;

public class Bot {
	private XmppConnection connection;
	private Commands commands;

	private static Logger logger = Logger.getLogger(Bot.class);
	
	public Bot(){
		commands = new Commands(this);
		connection = new XmppConnection(commands);
	}
	
	public XmppConnection getConnection(){
		return connection;
	}
	
	public void start(){
		int retry = 10;
		while(!connection.connect() && retry-- > 0){
			logger.warn("Connection failed, retry in " + (100-retry*10) + " seconds.");
			try {
				Thread.sleep(1000*(100-retry*10));
			} catch (InterruptedException e) {
                                logger.error("Recconection interrupted.", e);
			}
		}
	}
        
        public void stop(){
            connection.stop();
            connection = new XmppConnection(connection);
            synchronized(this){
                this.notify();
            }
        }
        
        public void sleep(){
            synchronized(this){
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    logger.warn("Waiting for connection to finish interupted.");
                }
            }
        }
        
	
	public void setPrefix(String prefix){
		commands.setPrefix(prefix);
	}
	
	public String getPrefix(){
		return commands.getPrefix();
	}

}
