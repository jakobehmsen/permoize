package permoize;

public interface Memoizer {
	<T> T recollect(Object tag, Producer<T> source) throws Exception;
}
