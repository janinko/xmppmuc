package eu.janinko.xmppmuc.commands;

import java.io.Serializable;

public class Task implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String who;
	private String subject;
	private boolean active;
	
	public Task(String w, String s){
		who = w;
		subject = s;
		active = true;
	}

	public boolean isActive() {
		return active;
	}
	
	public String getWho() {
		return who;
	}
	
	public String getSubject() {
		return subject;
	}

	public void deactivate() {
		active = false;
	}
}
