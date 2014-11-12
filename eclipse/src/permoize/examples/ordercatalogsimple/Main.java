package permoize.examples.ordercatalogsimple;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import permoize.CommonMemoizeContainer;
import permoize.CommonMemoizer;
import permoize.MemoizeContainer;
import permoize.Memoizer;
import permoize.MetaProtocolBuilder;
import permoize.MetaPuller;
import permoize.RunningPuller;
import permoize.SerializingRequestMetaProtocol;
import permoize.SimpleMetaProtocolBuilder;
import permoize.StartEndMemoizeContainer;
import permoize.StreamMemoizeEntryList;

public class Main {
	private interface State {
		State next();
	}
	
	private static class CatalogState implements State {
		private Catalog catalog;
		
		private CatalogState(Catalog catalog) {
			this.catalog = catalog;
		}

		@Override
		public State next() {
			System.out.println("Please select one of the following options:");
			System.out.println("1: Add order");
			System.out.println("2: Show orders");
			System.out.println("0: Quit");
			
			int selection = Integer.parseInt(readLine());
			
			switch(selection) {
			case 1:
				return new AddOrderState(catalog);
			case 0:
				return new ExitState();
			default:
				System.out.println("Invalid selection.");
				return this;
			}
		}
	}
	
	private static class AddOrderState implements State {
		private Catalog catalog;
		
		private AddOrderState(Catalog catalog) {
			this.catalog = catalog;
		}
		
		@Override
		public State next() {
			System.out.println("Please select one of the following options:");
			System.out.println("1: Add line");
			System.out.println("2: Accept order");
			System.out.println("3: Cancel order");
			
			int selection = Integer.parseInt(readLine());
			
			switch(selection) {
			case 1:
//				return new AddLineState(catalog);
				return null;
			case 2:
				return new CatalogState(catalog);
			case 3:
				return new CatalogState(catalog);
			case 0:
				return new ExitState();
			default:
				System.out.println("Invalid selection.");
				return this;
			}
		}
	}
	
	private static class AddLineState implements State {
		@Override
		public State next() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private static class ExitState implements State {
		@Override
		public State next() {
			System.out.println("Goodbye.");
			return null;
		}
	}
	
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	private static String readLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// Create pusher
		// - a "request stream processor", where each request is memoized
		
		byte[] start = SerializingRequestMetaProtocol.Util.invocationToRequest(
			"START", new Class<?>[0], new Object[0]);
		byte[] end = SerializingRequestMetaProtocol.Util.invocationToRequest(
			"END", new Class<?>[0], new Object[0]);
		
		MemoizeContainer memoizeContainer = new StartEndMemoizeContainer(
			start, end, new CommonMemoizeContainer(new StreamMemoizeEntryList("memoi.zer")));
		Memoizer memoizer = new CommonMemoizer(memoizeContainer);

		JList<Order> contacts = new JList<Order>();
		contacts.setModel(new DefaultListModel<Order>());
		
		CatalogImpl contactListImpl = new CatalogImpl();
		MetaProtocolBuilder<Catalog> metaProtocol = SimpleMetaProtocolBuilder.wrap(
			SerializingRequestMetaProtocol.create(Catalog.class, contactListImpl));
		MetaPuller<Catalog> metaPuller = metaProtocol.createPuller(memoizer);
		
		Catalog catalog = metaPuller.createPusher();
		Order order = catalog.createOrder(); // Somehow, the order should be implicitly wrapped around a proxy pusher
		
		RunningPuller runningPuller = metaPuller.start();
		
		State state = new CatalogState(catalog);
		
		while(state != null) {
			state = state.next();
		}
		
		runningPuller.stop();
	}
}
