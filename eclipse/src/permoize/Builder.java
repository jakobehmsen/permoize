package permoize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public interface Builder {
	void build(Object reference, BiConsumer<Method, Object[]> invocationConsumer);
	
	public static class BuilderInvocationHandler implements InvocationHandler, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private static final int STATE_TRANSIENT = 0;
		private static final int STATE_PERSISTENT_LOADED = 1;
		private static final int STATE_PERSISTENT_UNLOADED = 2;
		
		private int state;
		private Invocation creation;
		private Object target;
		private ArrayList<Invocation> invocations = new ArrayList<Invocation>();
		
		private BiConsumer<Method, Object[]> invocationConsumer;

		public BuilderInvocationHandler(Invocation creation, Object target) {
			this.creation = creation;
			this.target = target;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getDeclaringClass() == Builder.class && method.getName().equals("build")) {
				switch(state) {
				case STATE_TRANSIENT:
					// Should something happen here for this state transition?
					
					state = STATE_PERSISTENT_LOADED;
					break;
				case STATE_PERSISTENT_UNLOADED:
					// Build 
					Object reference = args[0];
					invocationConsumer = (BiConsumer<Method, Object[]>)args[1];
					
					// Create base target from reference
					// How to derive the correct target of creation here?
					// For instance, when creating a line off of an order, which itself has been created
					// from a catalog. In this case, a catalog is supplied as a reference here.
					// - then, how should the order be derived?
					// Should a dedicated factory be used?
					target = creation.invoke(reference, reference, invocationConsumer);
					
					for(Invocation invocation: invocations)
						invocation.invoke(reference, target, invocationConsumer);
					
					state = STATE_PERSISTENT_LOADED;
				case STATE_PERSISTENT_LOADED:
					break;
				}
				return null;
			} else {
				if(state == STATE_TRANSIENT) {
					boolean memoize = method.isAnnotationPresent(Memoize.class);
					
					if(memoize)
						invocations.add(new Invocation(method, args));
					
					return method.invoke(target, args);
				} else {
					boolean memoize = method.isAnnotationPresent(Memoize.class);
					
					if(memoize) {
//						R request = requestResolver.apply(method, args);
//						puller.put(request);
						
						invocationConsumer.accept(method, args);
						
						return null;
					} else {
						boolean isCreator = method.isAnnotationPresent(Creator.class);
						if(!isCreator) {
							return method.invoke(target, args);
						} else {
							Object result = method.invoke(target, args);
							// Wrap result into some sort of builder proxy - how?
							/*
							What should the result be wrapped into? Some sort of builder that both collects and forwards messages
							in an entirely transient sense
							*/
							return Builder.create(method.getReturnType(), new Invocation(method, args), result);
						}
					}
					
//					return method.invoke(target, args);
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
			creation = (Invocation)inputStream.readObject();
			invocations = (ArrayList<Invocation>)inputStream.readObject();
			state = STATE_PERSISTENT_UNLOADED;
		}

		private void writeObject(ObjectOutputStream outputStream) throws IOException {
			outputStream.writeObject(creation);
			outputStream.writeObject(invocations);
		}
	}
	
	public static Object create(Class<?> c, Invocation creation, Object target) {
		if(1 != 2)
			return Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c, Builder.class}, new BuilderInvocationHandler(creation, target));
		
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
