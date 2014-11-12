package permoize;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Puller<T> {
	private Memoizer memoizer;
	private BlockingQueue<T> requestStream = new LinkedBlockingDeque<T>();
	
	public Puller(Memoizer memoizer) {
		this.memoizer = memoizer;
	}

	public Pusher<T> newClient() {
		return new Pusher<T>(requestStream);
	}
	
	public void processNext() throws Exception {
		T request = memoizer.recollect("request", () -> {
			try {
				return requestStream.take();
			} catch(InterruptedException e) {
				// If the BlockingQueue is interrupted, indicate to the Memoizer that 
                // the value shouldn't be collected.
				throw new DontCollectException();
			}
		});
		
		serve(request);
	}
	
	protected abstract void serve(T request);
}
