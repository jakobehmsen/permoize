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

import permoize.MetaProtocol;
import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.MemoizeContainer;
import permoize.Memoizer;
import permoize.RunningPuller;
import permoize.Puller;
import permoize.StartEndMemoizeContainer;
import permoize.StreamMemoizeEntryList;
import permoize.StringRequestMetaProtocol;

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
		
		ContactListImpl contactListImpl = new ContactListImpl(title, frame, contacts);
		MetaProtocol<String, ContactList> pusherPullerFactory = new StringRequestMetaProtocol<ContactList>(ContactList.class, contactListImpl);
		Puller<String> puller = pusherPullerFactory.createPuller(memoizer);
		ContactList contactListPusher = pusherPullerFactory.createPusher(puller);
		
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
					contactListPusher.add(firstName, lastName, phoneNumber);
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
						contactListPusher.update("" + selectedIndex, firstName, lastName, phoneNumber);
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
					contactListPusher.delete("" + selectedIndex);
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
		
		RunningPuller<String> runningPuller = RunningPuller.start(puller);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				runningPuller.stop();
			}
		});
	}
}
