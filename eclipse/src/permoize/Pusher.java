package permoize;

import java.util.concurrent.BlockingQueue;

public class Pusher<T> {
	private BlockingQueue<T> requestStream;
	
	public Pusher(BlockingQueue<T> requestStream) {
		this.requestStream = requestStream;
	}

	public void put(T o) throws InterruptedException {
		requestStream.put(o);
	}
}
