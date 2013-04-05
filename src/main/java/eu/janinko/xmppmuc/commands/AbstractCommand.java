package eu.janinko.xmppmuc.commands;

import eu.janinko.xmppmuc.CommandWrapper;

/** Abstract class helping with implementing {@link Command} interface. 
 * 
 * @author Honza Brázdil <janinko.g@gmail.com>
 * @see Command
 */
public abstract class AbstractCommand implements Command {
	protected CommandWrapper cw;
	protected boolean connected = false;
	
	@Override
	public void setWrapper(CommandWrapper commandWrapper){
		cw = commandWrapper;
	}

	@Override
	public String help(String prefix) {
		return null;
	}

	@Override
	public int getPrivLevel() {
		return 0;
	}

	@Override
	public void connected() {
		connected = true;
	}

	@Override
	public void disconnected() {
		connected = false;
	}

}
