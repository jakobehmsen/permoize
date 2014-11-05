package permoize;

public class StartEndMemoizeContainer implements MemoizeContainer {
	private Object start;
	private Object end;
	private MemoizeContainer container;
	
	public StartEndMemoizeContainer(Object start, Object end, MemoizeContainer container) {
		this.start = start;
		this.end = end;
		this.container = container;
	}
	
	public static final int STATE_PENDING = 0;
	public static final int STATE_RECOLLECTING = 1;
	public static final int STATE_FINISHED = 2;
	
	@Override
	public MemoizeStream getStream(Object tag) {
		MemoizeStream stream = container.getStream(tag);
		
		return new MemoizeStream() {
			private int state = STATE_PENDING;
			
			@Override
			public void put(Object value) {
				stream.put(value);
			}
			
			@Override
			public Object next() {
				switch(state) {
				case STATE_PENDING:
					state = STATE_RECOLLECTING;
					return start;
				case STATE_RECOLLECTING:
					if(stream.hasNext())
						return stream.next();
					state = STATE_FINISHED;
					return end;
				case STATE_FINISHED:
					return null;
				}
				
				return null;
			}
			
			@Override
			public boolean hasNext() {
				switch(state) {
				case STATE_PENDING:
					return true;
				case STATE_RECOLLECTING:
					return true;
				case STATE_FINISHED:
					return false;
				}
				
				return false;
			}
		};
	}
}
