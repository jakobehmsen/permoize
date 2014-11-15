package permoize.examples.ordercatalogsimple;

import java.io.Serializable;

public class Line implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String item;
	private int amount;
	
	public Line(String item, int amount) {
		this.item = item;
		this.amount = amount;
	}
}
