package permoize;

public class RunningServer<T> {
	private Thread thread;
	
	private RunningServer(Thread thread) {
		this.thread = thread;
	}
	
	public static <T> RunningServer<T> start(Server<T> server) {
		Thread thread = new Thread(() -> {
			while(true) {
				try {
					System.out.println("Processing next...");
					server.processNext();
				} catch (DontCollectException e) {
					System.out.println("Stream processor stopped.");
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		return new RunningServer<T>(thread);
	}
	
	public void stop() {
		thread.interrupt();
	}
}
