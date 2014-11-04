package permoize;

public class DontCollectException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DontCollectException() { }
	
	public DontCollectException(String message) {
		super(message);
	}
	
	public DontCollectException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public DontCollectException(Throwable cause) {
        super(cause);
    }
}
