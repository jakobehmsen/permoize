package permoize;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectiveServiceProvider<T, R> implements ServiceProvider<R> {
	private T target;
	private BiFunction<T, R, Method> methodResolver;
	private Function<R, Object[]> argsResolver;
	private BiFunction<T, R, Object> targetResolver;
	
	public ReflectiveServiceProvider(T target, BiFunction<T, R, Method> methodResolver, Function<R, Object[]> argsResolver, BiFunction<T, R, Object> targetResolver) {
		this.target = target;
		this.methodResolver = methodResolver;
		this.argsResolver = argsResolver;
		this.targetResolver = targetResolver;
	}

	@Override
	public void serve(R request) {
		Method method = methodResolver.apply(target, request);
		Object[] args = argsResolver.apply(request);
		Object targetForRequest = targetResolver.apply(target, request);
		try {
			method.invoke(targetForRequest, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
