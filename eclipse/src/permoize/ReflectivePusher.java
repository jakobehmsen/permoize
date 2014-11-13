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
					R request = requestResolver.apply(method, args);
					puller.put(request);
					
					return null;
				} else {
					Object result = method.invoke(implementer, args);
					// Wrap result into some sort of builder proxy - how?
					return result;
				}
			} else {
				return method.invoke(implementer, args);
			}
		});
	}
}
