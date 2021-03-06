package permoize.examples.ordercatalog;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NewLineFrame extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Hashtable<String, JTextField> fields = new Hashtable<String, JTextField>();
	
	public NewLineFrame(Consumer<Line> okAction) {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		appendLabelValue("Item");
		appendLabelValue("Amount");
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		JPanel buttonPanel = new JPanel();
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			okAction.accept(new Line(getItem(), getAmount()));
			setVisible(false);
		});
		buttonPanel.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		buttonPanel.add(cancelButton);
		
		add(buttonPanel);
		
		pack();
		
		setTitle("New Line");
		setModal(true);
		setLocationRelativeTo(null);
	}
	
	private String getItem() {
		return fields.get("Item").getText();
	}
	
	private int getAmount() {
		return Integer.parseInt(fields.get("Amount").getText());
	}
	
	private void appendLabelValue(String name) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(name));
		JTextField text = new JTextField();
		fields.put(name, text);
		text.setPreferredSize(new Dimension(150, text.getPreferredSize().height));
		panel.add(text);
		add(panel);
	}
}
