package permoize.examples.ordercatalogsimple;

import permoize.Memoize;

public interface Line {
	String getItem();
	@Memoize
	void setItem(String item);
	int getAmount();
	@Memoize
	void setAmount(int amount);
}
