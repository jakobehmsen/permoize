package permoize.examples.ordercatalog;

import javax.swing.JFrame;

public class CatalogFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String title = "Order Catalog";

	public void showAsLoading() {
		setTitle(title + " - Loading...");
		setEnabled(false);
	}

	public void showAsLoaded() {
		setTitle(title);
		setEnabled(true);
	}
}
