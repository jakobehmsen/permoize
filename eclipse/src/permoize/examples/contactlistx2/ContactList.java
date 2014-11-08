package permoize.examples.contactlistx2;

public interface ContactList {
	void add(String firstName, String lastName, String phoneNumber);
	void update(int index, String firstName, String lastName, String phoneNumber);
	void delete(int index);
}
