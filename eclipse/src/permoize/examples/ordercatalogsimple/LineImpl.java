package permoize.examples.ordercatalogsimple;

public class LineImpl implements Line {
	private String item;
	private int amount;
	
	@Override
	public String getItem() {
		return item;
	}

	@Override
	public void setItem(String item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public void setAmount(int amount) {
		this.amount = amount;
	}
}
