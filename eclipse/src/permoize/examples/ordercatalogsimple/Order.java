package permoize.examples.ordercatalogsimple;

public interface Order {
	void addLine(Line line);
	Object locationOfLine(Line line);
	Line getLine(Object location);
}
