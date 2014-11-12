package permoize.examples.ordercatalogsimple;

public interface CatalogListener {
	void addedOrder(Order order);
	void startedLoading();
	void finishedLoading();
}
