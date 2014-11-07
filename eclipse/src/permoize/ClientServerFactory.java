package permoize;

public interface ClientServerFactory<T, P> {
	Server<T> createServer(Memoizer memoizer);
	P createClient(Server<T> server);
}
