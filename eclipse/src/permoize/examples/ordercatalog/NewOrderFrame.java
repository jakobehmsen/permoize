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
