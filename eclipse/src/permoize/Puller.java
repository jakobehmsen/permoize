package permoize;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Puller<T> {
	private Memoizer memoizer;
	private ServiceProvider<T> serviceProvider;
	private BlockingQueue<T> requestStream = new LinkedBlockingDeque<T>();
	
	public Puller(Memoizer memoizer, ServiceProvider<T> serviceProvider) {
		this.memoizer = memoizer;
		this.serviceProvider = serviceProvider;
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
		
		serviceProvider.serve(request);
	}
}
