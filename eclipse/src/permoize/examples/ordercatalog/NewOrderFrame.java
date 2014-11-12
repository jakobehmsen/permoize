package permoize.examples.ordercatalog;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class NewOrderFrame extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JList<Line> lines;
	private JPanel topPanel = new JPanel();

	public NewOrderFrame(Catalog catalog) {
		lines = new JList<Line>();
		lines.setModel(new DefaultListModel<Line>());
		
		setLayout(new BorderLayout());
		
		/*
		Perhaps, it should be possible to have generic support for
		building new objects based on postponing requests till later
		for the memoizer but let the requests be immediate for target
		being built? 
		*/
		
		appendAction("New Line...", () -> { 
			NewLineFrame newLineFrame = new NewLineFrame(line -> {
				((DefaultListModel<Line>)lines.getModel()).addElement(line);
			});
			newLineFrame.setVisible(true);
		});
		
		add(topPanel, BorderLayout.NORTH);
		add(new JScrollPane(lines), BorderLayout.CENTER);
		
		setTitle("New Order");
		setModal(true);
		
		JPanel buttonPanel = new JPanel();
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			// How to create and Order such that persistence is implicitly supported?
			// Abstract instantiation? How to associate a pusher to an address?
			// The issue is a classic chicken and the egg issue:
			// - To create a pusher order, an address has to be created
			// - The order doesn't have an address before it has been added to the catalog
			// Perhaps, the order should exists in a template catalog, where all the orders
			// being created live until they've been committed or ignored?
			// Then, should the address of the order change after it has been added to the
			// catalog? If an address can change, why not have a direct address, when the order
			// is being created, where requests sent to the order are not persisted.
//			catalog.addOrder(order);
			setVisible(false);
		});
		buttonPanel.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		buttonPanel.add(cancelButton);
		
		add(buttonPanel);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(640, 480);
		setLocationRelativeTo(null);
	}
	
	private void appendAction(String name, Runnable action) {
		JButton button = new JButton(name);
		button.addActionListener(e -> action.run());
		topPanel.add(button);
	}
}
