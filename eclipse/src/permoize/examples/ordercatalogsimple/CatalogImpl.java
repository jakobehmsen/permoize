package permoize.examples.ordercatalogsimple;

import java.util.ArrayList;

public class CatalogImpl implements Catalog {
	private ArrayList<Order> orders = new ArrayList<Order>();
	private ArrayList<CatalogListener> listeners = new ArrayList<CatalogListener>();
	
	public CatalogImpl() {
		orders = new ArrayList<Order>();
	}
	
	public void START() {
		listeners.forEach(l -> l.startedLoading());
	}
	
	public void END() {
		listeners.forEach(l -> l.finishedLoading());
	}

	@Override
	public void addOrder(Order order) {
		orders.add(order);
		listeners.forEach(l -> l.addedOrder(order));
	}

	@Override
	public Object locationOfOrder(Order order) {
		return orders.indexOf(order);
	}

	@Override
	public Order getOrder(Object location) {
		return orders.get((int)location);
	}
	
	@Override
	public void addListener(CatalogListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(CatalogListener listener) {
		listeners.remove(listener);
	}
}
