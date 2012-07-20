package eu.janinko.xmppmuc.commands;

import java.io.IOException;

public class PluginBuildException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PluginBuildException(IOException e) {
		super(e);
	}

	public PluginBuildException() {
		super();
	}

	public PluginBuildException(String string) {
		super(string);
	}

}
