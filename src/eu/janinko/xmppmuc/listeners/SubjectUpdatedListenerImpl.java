package eu.janinko.xmppmuc.listeners;


import org.apache.log4j.Logger;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import eu.janinko.xmppmuc.Xmppmuc;

public class SubjectUpdatedListenerImpl implements SubjectUpdatedListener {
	
	private Xmppmuc xmppmuc;
	private static Logger logger = Logger.getLogger(SubjectUpdatedListenerImpl.class);

	public SubjectUpdatedListenerImpl(Xmppmuc xmppmuc){
		this.xmppmuc = xmppmuc;
	}

	@Override
	public void subjectUpdated(String subject, String from) {
		logger.debug("subjectUpdated: subject='" + subject + "', from='" + from +"'");
	}

}
