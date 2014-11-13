package permoize;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectiveServiceProvider<T, R> extends Puller<R> {
	private T target;
	private MetaProtocol<R, T> metaProtocol;
	private BiFunction<T, R, Method> methodResolver;
	private Function<R, Object[]> argsResolver;
	private BiFunction<T, R, Object> targetResolver;
	
	public ReflectiveServiceProvider(Memoizer memoizer, T target, MetaProtocol<R, T> metaProtocol, BiFunction<T, R, Method> methodResolver, Function<R, Object[]> argsResolver, BiFunction<T, R, Object> targetResolver) {
		super(memoizer);
		this.target = target;
		this.metaProtocol = metaProtocol;
		this.methodResolver = methodResolver;
		this.argsResolver = argsResolver;
		this.targetResolver = targetResolver;
	}

	@Override
	protected void serve(R request) {
		Method method = methodResolver.apply(target, request);
		
		Object[] args = argsResolver.apply(request);
		Object targetForRequest = targetResolver.apply(target, request);
		try {
			method.invoke(targetForRequest, args);
			Address address = null;
			metaProtocol.createPusher(address, this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
