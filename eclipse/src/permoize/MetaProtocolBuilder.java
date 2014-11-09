package permoize;

public interface MetaProtocolBuilder<T> {
	MetaPuller<T> createPuller(Memoizer memoizer);
}
