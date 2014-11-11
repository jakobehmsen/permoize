package permoize.examples.ordercatalog;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CatalogFrame extends JFrame implements CatalogListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String title = "Order Catalog";
	
	private JList<Order> orders;
	private JPanel topPanel = new JPanel();
	
	private Catalog catalog;
	
	public CatalogFrame(Catalog catalog) {
		this.catalog = catalog;
		
		orders = new JList<Order>();
		orders.setModel(new DefaultListModel<Order>());
		
		setLayout(new BorderLayout());
		
		appendAction("New Order...", () -> {
			NewOrderFrame newOrderFrame = new NewOrderFrame(catalog);
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

	@Override
	public void addedOrder(Order order) {
		((DefaultListModel<Order>)orders.getModel()).addElement(order);
	}

	@Override
	public void startedLoading() {
		setTitle(title + " - Loading...");
		setEnabled(false);
	}

	@Override
	public void finishedLoading() {
		setTitle(title);
		setEnabled(true);
	}
}
