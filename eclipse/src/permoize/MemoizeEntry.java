package permoize;

import java.io.Serializable;

public class MemoizeEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final Object tag;
	public final Object value;
	
	public MemoizeEntry(Object tag, Object value) {
		this.tag = tag;
		this.value = value;
	}
}
