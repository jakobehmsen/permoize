package permoize.examples.contactlistx3;

public interface ContactList {
	void add(String firstName, String lastName, String phoneNumber);
	void update(int index, String firstName, String lastName, String phoneNumber);
	void delete(int index);
}
