package permoize.examples.contactlistx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import permoize.Client;
import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.MemoizeContainer;
import permoize.Memoizer;
import permoize.RunningServer;
import permoize.Server;
import permoize.ServiceProvider;
import permoize.StartEndMemoizeContainer;
import permoize.StreamMemoizeEntryList;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// Create pusher
		// - a "request stream processor", where each request is memoized
		
		String start = "START";
		String end = "END";
		
		MemoizeContainer memoizeContainer = new StartEndMemoizeContainer(
			start, end, new CommonMemoizeContainer(new StreamMemoizeEntryList("memoi.zer")));
		Memoizer memoizer = new CommonMemoizer(memoizeContainer);

		String title = "Contact list";
		JFrame frame = new JFrame();
		JList<Contact> contacts = new JList<Contact>();
		contacts.setModel(new DefaultListModel<Contact>());
		
		Server<String> server = new Server<String>(memoizer, new ServiceProvider<String>() {
			boolean recollecting = true;
			
			@Override
			public void serve(String request) {
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
					String firstName = arguments[0];
					String lastName = arguments[1];
					String phoneNumber = arguments[2];
					((DefaultListModel<Contact>)contacts.getModel()).addElement(new Contact(firstName, lastName, phoneNumber));

					// Only change selected index if not recollecting
					if(!recollecting)
						contacts.setSelectedIndex(contacts.getModel().getSize() - 1);
					break;
				} case "update": {
					int index = Integer.parseInt(arguments[0]);
					String firstName = arguments[1];
					String lastName = arguments[2];
					String phoneNumber = arguments[3];
					Contact contact = ((DefaultListModel<Contact>)contacts.getModel()).get(index);
					contact.setFirstName(firstName);
					contact.setLastName(lastName);
					contact.setPhoneNumber(phoneNumber);
					((DefaultListModel<Contact>)contacts.getModel()).set(index, contact);
					break;
				} case "delete": {
					int index = Integer.parseInt(arguments[0]);
					((DefaultListModel<Contact>)contacts.getModel()).remove(index);

					// Only change selected index if not recollecting
					if(!recollecting) {
						if(((DefaultListModel<Contact>)contacts.getModel()).getSize() > 0) {
							int selectedIndex = Math.min(((DefaultListModel<Contact>)contacts.getModel()).getSize() - 1, index);
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
			}
		});
		Client<String> client = server.newClient();
		
		// Create pusher
		// - a Swing GUI through which the requests are made from events
		JTextField txtFirstName = new JTextField();
		txtFirstName.setPreferredSize(new Dimension(120, txtFirstName.getPreferredSize().height));
		JTextField txtLastName = new JTextField();
		txtLastName.setPreferredSize(new Dimension(120, txtFirstName.getPreferredSize().height));
		JTextField txtPhoneNumber = new JTextField();
		txtPhoneNumber.setPreferredSize(new Dimension(80, txtFirstName.getPreferredSize().height));
		contacts.addListSelectionListener(e -> {
			Contact contact = ((DefaultListModel<Contact>)contacts.getModel()).get(e.getFirstIndex());
			txtFirstName.setText(contact.getFirstName());
			txtLastName.setText(contact.getLastName());
			txtPhoneNumber.setText(contact.getPhoneNumber());
		});
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(e -> {
			String firstName = txtFirstName.getText();
			txtFirstName.setText("");
			String lastName = txtLastName.getText();
			txtLastName.setText("");
			String phoneNumber = txtPhoneNumber.getText();
			txtPhoneNumber.setText("");
			txtFirstName.requestFocusInWindow();
			
			if(firstName.trim().length() > 0 && lastName.trim().length() > 0 && phoneNumber.trim().length() > 0) {
				try {
					client.put("new;" + firstName + ";" + lastName + ";" + phoneNumber);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(e -> {
			int selectedIndex = contacts.getSelectedIndex();
			if(selectedIndex != -1) {
				String firstName = txtFirstName.getText();
				String lastName = txtLastName.getText();
				String phoneNumber = txtPhoneNumber.getText();
				txtFirstName.requestFocusInWindow();

				if(firstName.trim().length() > 0 && lastName.trim().length() > 0 && phoneNumber.trim().length() > 0) {
					try {
						client.put("update;" + selectedIndex + ";" + firstName + ";" + lastName + ";" + phoneNumber);
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
					client.put("delete;" + selectedIndex);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("First name"));
		topPanel.add(txtFirstName);
		topPanel.add(new JLabel("Last name"));
		topPanel.add(txtLastName);
		topPanel.add(new JLabel("Phone number"));
		topPanel.add(txtPhoneNumber);
		topPanel.add(btnAdd);
		topPanel.add(btnUpdate);
		topPanel.add(btnDelete);
		
		frame.setLayout(new BorderLayout());
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(new JScrollPane(contacts), BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		
		frame.setEnabled(false);
		frame.setVisible(true);
		frame.setTitle(title + " - Loading...");
		
		RunningServer<String> streamProcessor = RunningServer.start(server);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				streamProcessor.stop();
			}
		});
	}
}
