package connection;

/**
 * Runnable with ability to store a value on instantiation
 * 
 * @author Bimesh De Silva, Patrick Liu, William Xu, Barbara Guo
 * @version December 10, 2015
 */
public abstract class Updatable implements Runnable {
	protected int value;

	public Updatable(int value) {
		this.value = value;
	}
}