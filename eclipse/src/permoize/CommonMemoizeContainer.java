package permoize;

import java.util.ArrayList;
import java.util.Hashtable;

public class CommonMemoizeContainer implements MemoizeContainer {
	private MemoizeEntryList recollections;
	private Hashtable<Object, Integer> tracks = new Hashtable<Object, Integer>();
	private ArrayList<RecollectListener> listeners = new ArrayList<RecollectListener>();
	
	public CommonMemoizeContainer(MemoizeEntryList recollections) {
		this.recollections = recollections;
	}
	
	@Override
	public void addListener(RecollectListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(RecollectListener listener) {
		listeners.remove(listener);
	}

	@Override
	public MemoizeStream getStream(Object tag) {
		listeners.forEach(l -> l.startedRecollecting(tag));
		
		return new MemoizeStream() {
			private MemoizeEntry next = deriveNext();
			
			private MemoizeEntry deriveNext() {
				Integer trackIndex = tracks.get(tag);
				if(trackIndex == null) {
					trackIndex = -1;
					tracks.put(tag, trackIndex);
				}
				
				for(int i = trackIndex + 1; i < recollections.size(); i++) {
					MemoizeEntry entry = recollections.get(i);
					if(entry.tag.equals(tag)) {
						tracks.put(tag, i);
						return entry;
					}
				}

				return null;
			}
			
			@Override
			public boolean hasNext() {
				return next != null;
			}
			
			@Override
			public Object next() {
				MemoizeEntry prev = next;
				
				next = deriveNext();
				
				if(next == null)
					listeners.forEach(l -> l.finishedRecollecting(tag));
				
				return prev.value;
			}
			
			@Override
			public void put(Object value) {
				recollections.append(new MemoizeEntry(tag, value));
				tracks.put(tag, recollections.size() - 1);
			}
		};
	}
}
