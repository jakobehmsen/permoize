package permoize;

public interface MemoizeContainer {
	void addListener(RecollectListener listener);
	void removeListener(RecollectListener listener);
	MemoizeStream getStream(Object tag);
}
