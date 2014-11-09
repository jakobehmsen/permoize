package permoize;

public interface MetaPuller<T> {
	T createPusher();
	RunningPuller start();
}
