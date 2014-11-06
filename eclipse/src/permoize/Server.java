package permoize;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Server<T> {
	private Memoizer memoizer;
	private ServiceProvider<T> serviceProvider;
	private BlockingQueue<T> requestStream = new LinkedBlockingDeque<T>();
	
	public Server(Memoizer memoizer, ServiceProvider<T> serviceProvider) {
		this.memoizer = memoizer;
		this.serviceProvider = serviceProvider;
	}

	public Client<T> newClient() {
		return new Client<T>(requestStream);
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
