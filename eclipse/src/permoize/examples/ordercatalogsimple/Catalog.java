package permoize.examples.ordercatalogsimple;

import permoize.Transient;

public interface Catalog {
	void addOrder(Order order);
	Object locationOfOrder(Order order);
	Order getOrder(Object location);
	@Transient
	void addListener(CatalogListener listener);
	@Transient
	void removeListener(CatalogListener listener);
}
