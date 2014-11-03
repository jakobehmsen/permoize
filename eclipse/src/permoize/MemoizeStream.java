package permoize;

public interface MemoizeStream {
	boolean hasNext();
	Object next();
	void put(Object value);
}
