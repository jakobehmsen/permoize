package permoize.examples.ordercatalog;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class CatalogImpl implements Catalog {
	private CatalogFrame frame;
	private JList<Order> orders;
	
	public CatalogImpl(CatalogFrame frame, JList<Order> orders) {
		this.frame = frame;
		this.orders = orders;
	}
	
	public void START() {
		frame.showAsLoading();
	}
	
	public void END() {
		frame.showAsLoaded();
	}

	@Override
	public void addOrder(Order order) {
		((DefaultListModel<Order>)orders.getModel()).addElement(order);
	}

	@Override
	public Object locationOfOrder(Order order) {
		return ((DefaultListModel<Order>)orders.getModel()).indexOf(order);
	}

	@Override
	public Order getOrder(Object location) {
		return ((DefaultListModel<Order>)orders.getModel()).get((int)location);
	}
}
