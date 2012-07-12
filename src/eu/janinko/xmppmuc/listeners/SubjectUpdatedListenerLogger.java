package eu.janinko.xmppmuc.listeners;


import org.apache.log4j.Logger;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

public class SubjectUpdatedListenerLogger implements SubjectUpdatedListener {
	
	private static Logger logger = Logger.getLogger(SubjectUpdatedListenerLogger.class);

	public SubjectUpdatedListenerLogger(){
	}

	@Override
	public void subjectUpdated(String subject, String from) {
		logger.debug("subjectUpdated: subject='" + subject + "', from='" + from +"'");
	}

}
