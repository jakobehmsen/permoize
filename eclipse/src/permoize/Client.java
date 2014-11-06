package permoize;

import java.util.concurrent.BlockingQueue;

public class Client<T> {
	private BlockingQueue<T> requestStream;
	
	public Client(BlockingQueue<T> requestStream) {
		this.requestStream = requestStream;
	}

	public void put(T o) throws InterruptedException {
		requestStream.put(o);
	}
}
