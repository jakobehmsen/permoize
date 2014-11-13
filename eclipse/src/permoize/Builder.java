package permoize;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public interface Builder {
	List<Invocation> getInvocations();
	
	public static Object create(Class<?> c, Object target) {
		ArrayList<Invocation> invocations = new ArrayList<Invocation>();
		
		return Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c, Builder.class}, (proxy, method, args) -> {
			// Collect and forward messages in a transient sense
			
			/*
			How to figure out, how to create this kind of object on during replay?
			A method invocation? To what? Something relative to a reference. How is the reference supplied?
			The method invocation should be invoked, during reply, when a corresponding add method is invoked?
			
			Order o = catalog.createOrder(); // Method invocation for creation
			o.addLine("Item", "Amount"); // Collected/forwarded invocation
			catalog.addOrder(o); // Order becomes persistent and something special happens here when the add invocation is replayed
			
			What should happen for the replay of the add request?
			The add request should something forward the reference (catalog) for the builder 
			
			void addOrder(Order order) {
				// For the first request:
				order.becomePersistentFrom(this, metaProtocol, whatever); // From now on, requests sent to order must be persisted
				associateCreationToRequest(order); // Somehow, the abstract creation (method invocation?) must be associated to the request
				associateInvocationsToRequest(order); // Somehow, the invocations must be associated to the request
				implementer.addOrder(order); // This is done now
				
				// For the subsequent requests (replay):
				order.replayFrom(this);
				implementer.addOrder(order); // This is done now
			}
			
			*/
			
			if(method.getName().equals("getInvocations")) {
				return invocations;
			} else {
				invocations.add(new Invocation(method, args));
				return method.invoke(target, args);
			}
		});
	}
}
