package permoize.examples.ordercatalogsimple;

import permoize.Creator;

public interface Order {
	void addLine(Line line);
	Object locationOfLine(Line line);
	Line getLine(Object location);
}
