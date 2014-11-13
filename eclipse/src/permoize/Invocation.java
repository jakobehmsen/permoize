package permoize;

import java.io.Serializable;
import java.lang.reflect.Method;

public class Invocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final Method method;
	public final Object[] args;
	
	public Invocation(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}
}
