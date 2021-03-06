package permoize;

public class RunningPuller {
	private Thread thread;
	
	private RunningPuller(Thread thread) {
		this.thread = thread;
	}
	
	public static <T> RunningPuller start(Puller<T> puller) {
		Thread thread = new Thread(() -> {
			while(true) {
				try {
//					System.out.println("Processing next...");
					puller.processNext();
				} catch (DontCollectException e) {
//					System.out.println("Stream processor stopped.");
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		return new RunningPuller(thread);
	}
	
	public void stop() {
		thread.interrupt();
	}
}
