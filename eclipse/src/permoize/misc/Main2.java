package permoize.misc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.DontCollectException;
import permoize.Memoizer;
import permoize.StreamMemoizeEntryList;

public class Main2 {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// Create request stream processor, where each request is memoized
		
		BlockingQueue<String> requestStream = new LinkedBlockingDeque<String>();
		Memoizer memoizer = new CommonMemoizer(new CommonMemoizeContainer(new StreamMemoizeEntryList("me2.mor")));
		JList<String> names = new JList<String>();
		names.setModel(new DefaultListModel<String>());
		
		Thread streamProcessor = new Thread(() -> {
			while(true) {
				try {
					System.out.println("Waiting for request...");
					String request = memoizer.recollect("request", () -> {
						try {
							return requestStream.take();
						} catch(InterruptedException e) {
							throw new DontCollectException();
						}
					});
					System.out.println("Received request: " + request);
					
					String[] requestSplit = request.split(";");
					String selector = requestSplit[0];
					String[] arguments = new String[requestSplit.length - 1];
					System.arraycopy(requestSplit, 1, arguments, 0, arguments.length);
					
					switch(selector) {
					case "new":
						String name = arguments[0];
						((DefaultListModel<String>)names.getModel()).addElement(name);
						break;
					case "delete":
						int index = Integer.parseInt(arguments[0]);
						((DefaultListModel<String>)names.getModel()).remove(index);
						break;
					}
				} catch (DontCollectException e) {
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		streamProcessor.start();
		
		// Create GUI through which the requests are made
		JFrame frame = new JFrame("Contact list");
		
		JTextField txtName = new JTextField();
		txtName.setPreferredSize(new Dimension(150, txtName.getPreferredSize().height));
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(e -> {
			String name = txtName.getText();
			txtName.setText("");
			
			if(name.trim().length() > 0) {
				try {
					requestStream.put("new;" + name);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			int selectedIndex = names.getSelectedIndex();
			if(selectedIndex != -1) {
				try {
					requestStream.put("delete;" + selectedIndex);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Name"));
		topPanel.add(txtName);
		topPanel.add(btnAdd);
		topPanel.add(btnDelete);
		
		frame.setLayout(new BorderLayout());
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(names, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				streamProcessor.interrupt();
			}
		});
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
