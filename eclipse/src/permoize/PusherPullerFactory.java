package permoize;

public interface PusherPullerFactory<T, P> {
	Puller<T> createPuller(Memoizer memoizer);
	P createPusher(Puller<T> puller);
}
