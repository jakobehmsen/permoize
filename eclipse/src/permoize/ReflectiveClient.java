package permoize;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;

public class ReflectiveClient {
	private ReflectiveClient() { }
	
	@SuppressWarnings("unchecked")
	public static <T, R> T create(Class<T> c, Client<R> client, BiFunction<Method, Object[], R> requestResolver) {
		return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c}, (proxy, method, args) -> {
			R request = requestResolver.apply(method, args);
			client.put(request);
			
			return null;
		});
	}
}
