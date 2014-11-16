package permoize.examples.ordercatalog;


public interface Catalog {
	void addOrder(Order order);
	Object locationOfOrder(Order order);
	Order getOrder(Object location);
	void addListener(CatalogListener listener);
	void removeListener(CatalogListener listener);
}
