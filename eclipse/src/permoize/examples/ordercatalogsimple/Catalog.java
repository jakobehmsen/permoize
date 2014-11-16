package permoize.examples.ordercatalogsimple;

import permoize.Creator;
import permoize.Memoize;

public interface Catalog {
	@Memoize
	void addOrder(Order order);
	Object locationOfOrder(Order order);
	Order getOrder(Object location);

	void addListener(CatalogListener listener);
	void removeListener(CatalogListener listener);
	
	@Creator
	Order createOrder();
	@Creator
	Line createLine();
}
