package permoize.examples.contactlistx3;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

public class ContactListImpl implements ContactList {
	private boolean recollecting;
	private String title;
	private JFrame frame;
	private JList<Contact> contacts;
	
	public ContactListImpl(String title, JFrame frame, JList<Contact> contacts) {
		this.title = title;
		this.frame = frame;
		this.contacts = contacts;
	}

	public void START() {
		recollecting = true;
	}
	
	public void END() {
		recollecting = false;
		frame.setTitle(title);
		frame.setEnabled(true);
	}
	
	@Override
	public void add(String firstName, String lastName, String phoneNumber) {
		((DefaultListModel<Contact>)contacts.getModel()).addElement(new Contact(firstName, lastName, phoneNumber));

		// Only change selected index if not recollecting
		if(!recollecting)
			contacts.setSelectedIndex(contacts.getModel().getSize() - 1);
	}

	@Override
	public void update(int index, String firstName, String lastName, String phoneNumber) {
		Contact contact = ((DefaultListModel<Contact>)contacts.getModel()).get(index);
		contact.setFirstName(firstName);
		contact.setLastName(lastName);
		contact.setPhoneNumber(phoneNumber);
		((DefaultListModel<Contact>)contacts.getModel()).set(index, contact);
	}

	@Override
	public void delete(int index) {
		((DefaultListModel<Contact>)contacts.getModel()).remove(index);

		// Only change selected index if not recollecting
		if(!recollecting) {
			if(((DefaultListModel<Contact>)contacts.getModel()).getSize() > 0) {
				int selectedIndex = Math.min(((DefaultListModel<Contact>)contacts.getModel()).getSize() - 1, index);
				contacts.setSelectedIndex(selectedIndex);
			}
		}
	}
}
