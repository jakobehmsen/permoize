package permoize;

public interface Producer<T> {
	T get() throws Exception;
}
