package permoize;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectiveServiceProvider<T, R> implements ServiceProvider<R> {
	private T target;
	private BiFunction<T, R, Method> methodResolver;
	private Function<R, Object[]> argsResolver;
	
	public ReflectiveServiceProvider(T target, BiFunction<T, R, Method> methodResolver, Function<R, Object[]> argsResolver) {
		this.target = target;
		this.methodResolver = methodResolver;
		this.argsResolver = argsResolver;
	}

	@Override
	public void serve(R request) {
		Method method = methodResolver.apply(target, request);
		Object[] args = argsResolver.apply(request);
		try {
			method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
