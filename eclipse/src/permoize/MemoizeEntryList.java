package permoize;

public interface MemoizeEntryList {
	MemoizeEntry get(int index);
	void append(MemoizeEntry entry);
	int size();
}
