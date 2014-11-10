package permoize.examples.ordercatalog;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CatalogImpl extends JFrame implements Catalog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String title = "Order Catalog";
	
	private JList<Order> orders;
	private JPanel topPanel = new JPanel();
	
	public CatalogImpl() {
		orders = new JList<Order>();
		orders.setModel(new DefaultListModel<Order>());
		
		setLayout(new BorderLayout());
		
		appendAction("New Order...", () -> { 
			NewOrderFrame newOrderFrame = new NewOrderFrame();
			newOrderFrame.setVisible(true);
		});
		
		add(topPanel, BorderLayout.NORTH);
		add(new JScrollPane(orders), BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
	}
	
	private void appendAction(String name, Runnable action) {
		JButton button = new JButton(name);
		button.addActionListener(e -> action.run());
		topPanel.add(button);
	}
	
	public void START() {
		setTitle(title + " - Loading...");
		setEnabled(false);
	}
	
	public void END() {
		setTitle(title);
		setEnabled(true);
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
