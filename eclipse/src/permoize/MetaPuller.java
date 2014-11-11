package permoize;

public interface MetaPuller<T> {
	T createPusher();
	T createPusher(Address address);
	RunningPuller start();
}
