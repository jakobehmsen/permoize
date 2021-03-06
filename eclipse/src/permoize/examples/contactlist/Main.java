package permoize.examples.contactlist;

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
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.DontCollectException;
import permoize.MemoizeContainer;
import permoize.Memoizer;
import permoize.StartEndMemoizeContainer;
import permoize.StreamMemoizeEntryList;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// Create pusher
		// - a "request stream processor", where each request is memoized
		
		String start = "START";
		String end = "END";
		
		BlockingQueue<String> requestStream = new LinkedBlockingDeque<String>();
		MemoizeContainer memoizeContainer = new StartEndMemoizeContainer(
			start, end, new CommonMemoizeContainer(new StreamMemoizeEntryList("memoi.zer")));
		Memoizer memoizer = new CommonMemoizer(memoizeContainer);

		String title = "Contact list";
		JFrame frame = new JFrame();
		JList<String> contacts = new JList<String>();
		contacts.setModel(new DefaultListModel<String>());
		
		Thread streamProcessor = new Thread(() -> {
			boolean recollecting = true;
			
			System.out.println("Stream processor started.");
			while(true) {
				try {
					if(!recollecting)
						System.out.println("Waiting for request...");
					
					String request = memoizer.recollect("request", () -> {
						try {
							return requestStream.take();
						} catch(InterruptedException e) {
							// If the BlockingQueue is interrupted, indicate to the Memoizer that 
			                // the value shouldn't be collected.
							throw new DontCollectException();
						}
					});
					
					if(!recollecting)
						System.out.println("Received request: " + request);
					
					// Process request
					// - a request is a string which content separated by semicolons
					//   - the first item of the content is the selector
					//   - the remaining items of the content constitutes the arguments
					String[] requestSplit = request.split(";");
					String selector = requestSplit[0];
					String[] arguments = new String[requestSplit.length - 1];
					System.arraycopy(requestSplit, 1, arguments, 0, arguments.length);
					
					switch(selector) {
					case "new": {
						String name = arguments[0];
						((DefaultListModel<String>)contacts.getModel()).addElement(name);
						
						if(!recollecting)
							contacts.setSelectedIndex(contacts.getModel().getSize() - 1);
						break;
					} case "update": {
						String name = arguments[0];
						int index = Integer.parseInt(arguments[1]);
						((DefaultListModel<String>)contacts.getModel()).set(index, name);
						break;
					} case "delete": {
						int index = Integer.parseInt(arguments[0]);
						((DefaultListModel<String>)contacts.getModel()).remove(index);
						
						if(!recollecting) {
							if(((DefaultListModel<String>)contacts.getModel()).getSize() > 0) {
								int selectedIndex = Math.min(((DefaultListModel<String>)contacts.getModel()).getSize() - 1, index);
								contacts.setSelectedIndex(selectedIndex);
							}
						}
						break;
					} case "START": {
						recollecting = true;
						System.out.println("Started recollecting...");
						break;
					} case "END": {
						recollecting = false;
						System.out.println("Finished recollecting.");
						frame.setTitle(title);
						frame.setEnabled(true);
						break;
					}
					}
				} catch (DontCollectException e) {
					System.out.println("Stream processor stopped.");
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// Create pusher
		// - a Swing GUI through which the requests are made from events
		JTextField txtName = new JTextField();
		txtName.setPreferredSize(new Dimension(150, txtName.getPreferredSize().height));
		contacts.addListSelectionListener(e -> {
			String name = ((DefaultListModel<String>)contacts.getModel()).get(e.getFirstIndex());
			txtName.setText(name);
		});
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(e -> {
			String name = txtName.getText();
			txtName.setText("");
			txtName.requestFocusInWindow();
			
			if(name.trim().length() > 0) {
				try {
					requestStream.put("new;" + name);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(e -> {
			int selectedIndex = contacts.getSelectedIndex();
			if(selectedIndex != -1) {
				String name = txtName.getText();
				
				if(name.trim().length() > 0) {
					try {
						requestStream.put("update;" + name + ";" + selectedIndex);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			int selectedIndex = contacts.getSelectedIndex();
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
		topPanel.add(btnUpdate);
		topPanel.add(btnDelete);
		
		frame.setLayout(new BorderLayout());
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(new JScrollPane(contacts), BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				streamProcessor.interrupt();
			}
		});
		
		frame.setEnabled(false);
		frame.setVisible(true);
		frame.setTitle(title + " - Loading...");
		
		streamProcessor.start();
	}
}
