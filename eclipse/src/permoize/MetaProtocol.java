package permoize;

public interface MetaProtocol<T, P> {
	Puller<T> createPuller(Memoizer memoizer);
	P createPusher(Puller<T> puller);
}
