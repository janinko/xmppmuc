package eu.janinko.xmppmuc.commands;


public abstract class AbstractCommand implements Command {

	@Override
	public String help(String prefix) {
		return null;
	}

	@Override
	public int getPrivLevel() {
		return 0;
	}

	@Override
	public void destroy() {
		disconnected();
	}

	@Override
	public void connected() {}

	@Override
	public void disconnected() {}

}
