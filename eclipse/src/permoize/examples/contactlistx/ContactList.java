package permoize.examples.contactlistx;

public interface ContactList {
	void add(String firstName, String lastName, String phoneNumber);
	void update(String indexAsString, String firstName, String lastName, String phoneNumber);
	void delete(String indexAsString);
}
