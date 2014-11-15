package permoize.examples.ordercatalogsimple;

import java.util.ArrayList;

public class OrderImpl implements Order {
	private ArrayList<Line> lines = new ArrayList<Line>();

	@Override
	public void addLine(Line line) {
		lines.add(line);
	}

	@Override
	public Object locationOfLine(Line line) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Line getLine(Object location) {
		// TODO Auto-generated method stub
		return null;
	}
}
