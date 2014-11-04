package permoize;

import java.io.Serializable;
import java.util.Hashtable;

public class CommonMemoizer implements Memoizer {
	private static class ThrownException implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Exception exception;

		public ThrownException(Exception exception) {
			this.exception = exception;
		}
	}
	
	private MemoizeContainer container;
	private Hashtable<Object, MemoizeStream> streams = new Hashtable<Object, MemoizeStream>();
	
	public CommonMemoizer(MemoizeContainer container) {
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T recollect(Object tag, Producer<T> source) throws Exception {
		MemoizeStream stream = streams.computeIfAbsent(tag, t -> container.getStream(t));
		
		if(stream.hasNext()) {
			Object next = stream.next();
			if(next instanceof ThrownException)
				throw ((ThrownException)next).exception;
			return (T)next;
		} else {
			try {
				T value = source.get();
				stream.put(value);
				
				return value;
			} catch(DontCollectException e) {
				throw e;
			} catch(Exception e) {
				stream.put(new ThrownException(e));
				
				throw e;
			}
		}
	}
}
