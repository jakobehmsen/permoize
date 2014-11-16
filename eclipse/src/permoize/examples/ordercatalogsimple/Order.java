package permoize.examples.ordercatalogsimple;

import permoize.Creator;
import permoize.Memoize;

public interface Order {
	@Memoize
	void addLine(Line line);
	Object locationOfLine(Line line);
	Line getLine(Object location);
}
