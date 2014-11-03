package permoize;

import java.util.Hashtable;

public class CommonMemoizeContainer implements MemoizeContainer {
	private MemoizeEntryList recollections;
	private Hashtable<Object, Integer> tracks = new Hashtable<Object, Integer>();
	
	public CommonMemoizeContainer(MemoizeEntryList recollections) {
		this.recollections = recollections;
	}

	@Override
	public MemoizeStream getStream(Object tag) {
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
