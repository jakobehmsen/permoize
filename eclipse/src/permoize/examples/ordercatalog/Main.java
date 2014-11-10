package permoize.examples.ordercatalog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.MemoizeContainer;
import permoize.Memoizer;
import permoize.MetaProtocolBuilder;
import permoize.MetaPuller;
import permoize.RunningPuller;
import permoize.SerializingRequestMetaProtocol;
import permoize.SimpleMetaProtocolBuilder;
import permoize.StartEndMemoizeContainer;
import permoize.StreamMemoizeEntryList;

public class Main {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// Create pusher
		// - a "request stream processor", where each request is memoized
		
		byte[] start = SerializingRequestMetaProtocol.Util.invocationToRequest(
			"START", new Class<?>[0], new Object[0]);
		byte[] end = SerializingRequestMetaProtocol.Util.invocationToRequest(
			"END", new Class<?>[0], new Object[0]);
		
		MemoizeContainer memoizeContainer = new StartEndMemoizeContainer(
			start, end, new CommonMemoizeContainer(new StreamMemoizeEntryList("memoi.zer")));
		Memoizer memoizer = new CommonMemoizer(memoizeContainer);

		CatalogFrame frame = new CatalogFrame();
		JList<Order> contacts = new JList<Order>();
		contacts.setModel(new DefaultListModel<Order>());
		
		CatalogImpl contactListImpl = new CatalogImpl(frame, contacts);
		MetaProtocolBuilder<Catalog> metaProtocol = SimpleMetaProtocolBuilder.wrap(
			SerializingRequestMetaProtocol.create(Catalog.class, contactListImpl));
		MetaPuller<Catalog> metaPuller = metaProtocol.createPuller(memoizer);
		Catalog catalog = metaPuller.createPusher();
		
		// Create pusher
		// - a Swing GUI through which the requests are made from events
//		JTextField txtFirstName = new JTextField();
//		txtFirstName.setPreferredSize(new Dimension(120, txtFirstName.getPreferredSize().height));
//		JTextField txtLastName = new JTextField();
//		txtLastName.setPreferredSize(new Dimension(120, txtFirstName.getPreferredSize().height));
//		JTextField txtPhoneNumber = new JTextField();
//		txtPhoneNumber.setPreferredSize(new Dimension(80, txtFirstName.getPreferredSize().height));
//		contacts.addListSelectionListener(e -> {
//			Contact contact = ((DefaultListModel<Contact>)contacts.getModel()).get(e.getFirstIndex());
//			txtFirstName.setText(contact.getFirstName());
//			txtLastName.setText(contact.getLastName());
//			txtPhoneNumber.setText(contact.getPhoneNumber());
//		});
//		JButton btnAdd = new JButton("Add");
//		btnAdd.addActionListener(e -> {
//			String firstName = txtFirstName.getText();
//			txtFirstName.setText("");
//			String lastName = txtLastName.getText();
//			txtLastName.setText("");
//			String phoneNumber = txtPhoneNumber.getText();
//			txtPhoneNumber.setText("");
//			txtFirstName.requestFocusInWindow();
//			
//			if(firstName.trim().length() > 0 && lastName.trim().length() > 0 && phoneNumber.trim().length() > 0) {
//				try {
//					contactListPusher.add(firstName, lastName, phoneNumber);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//		JButton btnUpdate = new JButton("Update");
//		btnUpdate.addActionListener(e -> {
//			int selectedIndex = contacts.getSelectedIndex();
//			if(selectedIndex != -1) {
//				String firstName = txtFirstName.getText();
//				String lastName = txtLastName.getText();
//				String phoneNumber = txtPhoneNumber.getText();
//				txtFirstName.requestFocusInWindow();
//
//				if(firstName.trim().length() > 0 && lastName.trim().length() > 0 && phoneNumber.trim().length() > 0) {
//					try {
//						contactListPusher.update(selectedIndex, firstName, lastName, phoneNumber);
//					} catch (Exception e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//		});
//		JButton btnDelete = new JButton("Delete");
//		btnDelete.addActionListener(e -> {
//			int selectedIndex = contacts.getSelectedIndex();
//			if(selectedIndex != -1) {
//				try {
//					contactListPusher.delete(selectedIndex);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//		JPanel topPanel = new JPanel();
//		topPanel.add(new JLabel("First name"));
//		topPanel.add(txtFirstName);
//		topPanel.add(new JLabel("Last name"));
//		topPanel.add(txtLastName);
//		topPanel.add(new JLabel("Phone number"));
//		topPanel.add(txtPhoneNumber);
//		topPanel.add(btnAdd);
//		topPanel.add(btnUpdate);
//		topPanel.add(btnDelete);
//		
//		frame.setLayout(new BorderLayout());
//		
//		frame.add(topPanel, BorderLayout.NORTH);
//		frame.add(new JScrollPane(contacts), BorderLayout.CENTER);
//		
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(800, 600);
//		frame.setLocationRelativeTo(null);
//		
//		frame.setEnabled(false);
//		frame.setVisible(true);
//		frame.setTitle(title + " - Loading...");
		
		RunningPuller runningPuller = metaPuller.start();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				runningPuller.stop();
			}
		});
	}
}
