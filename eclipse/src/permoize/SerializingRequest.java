package permoize;

import java.io.Serializable;

public class SerializingRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	Instances should somehow represent both a play context and a replay context.
	
	These requests will, more or less, represent invocations, where each of the arguments
	may be a "complex" object for which references must be maintained - both transiently
	and persistently.
	*/
}
