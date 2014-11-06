package permoize;

public interface ServiceProvider<T> {
	void serve(T request);
}
