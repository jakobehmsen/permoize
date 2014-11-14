package permoize;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;

public class ReflectivePusher {
	private ReflectivePusher() { }
	
	@SuppressWarnings("unchecked")
	public static <T, R> T create(Class<T> c, T implementer, Pusher<R> puller, BiFunction<Method, Object[], R> requestResolver) {
		return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c}, (proxy, method, args) -> {
			boolean isTransient = method.isAnnotationPresent(Transient.class);
			
			if(!isTransient) {
				boolean isCreator = method.isAnnotationPresent(Creator.class);
				if(!isCreator) {
					// If an argument is a builder, then something special should happen...
					for(int i = 0; i < args.length; i++) {
						Object arg = args[i];
						if(arg instanceof Builder) {
							Builder builderArg = (Builder)arg;
							// Somehow, the builder should be bound to the request
							// The args are bound to the request...
							// So, when a builder is serialized, it should be serialized in such
							// a way that when it is read, then it somehow recreates itself... How? Where?
						}
					}
					
					R request = requestResolver.apply(method, args);
					puller.put(request);
					
					return null;
				} else {
					Object result = method.invoke(implementer, args);
					// Wrap result into some sort of builder proxy - how?
					/*
					What should the result be wrapped into? Some sort of builder that both collects and forwards messages
					in an entirely transient sense
					*/
					return Builder.create(method.getReturnType(), result);
				}
			} else {
				return method.invoke(implementer, args);
			}
		});
	}
}
