package permoize.examples.ordercatalog;

public interface CatalogListener {
	void addedOrder(Order order);
	void startedLoading();
	void finishedLoading();
}
