package eu.janinko.xmppmuc.commands;

/** Abstract class helping with implementing {@link Command} interface. 
 * 
 * @author Honza Brázdil <janinko.g@gmail.com>
 * @see Command
 */
public abstract class AbstractCommand implements Command {
	
	public AbstractCommand(){}

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
