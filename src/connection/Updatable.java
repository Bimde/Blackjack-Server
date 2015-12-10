package connection;

public abstract class Updatable implements Runnable {
	protected int value;

	public Updatable(int value) {
		this.value = value;
	}
}