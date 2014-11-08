package permoize;

import java.util.Arrays;

public class StringRequestPusherPullerFactory<P> implements PusherPullerFactory<String, P> {
	private Class<P> protocol;
	private P implementer;
	
	public StringRequestPusherPullerFactory(Class<P> protocol, P implementer) {
		this.protocol = protocol;
		this.implementer = implementer;
	}
	
	public static <P> StringRequestPusherPullerFactory<P> create(Class<P> protocol, P implementer) {
		return new StringRequestPusherPullerFactory<P>(protocol, implementer);
	}

	@Override
	public Puller<String> createPuller(Memoizer memoizer) {
		ServiceProvider<String> serviceProvider = new ReflectiveServiceProvider<P, String>(
				implementer,
			(target, request) -> {
				String[] requestSplit = request.split(";");
				String methodName = requestSplit[0];
				int argCount = requestSplit.length - 1;
				Class<?>[] parameterTypes = new Class<?>[argCount];
				Arrays.fill(parameterTypes, String.class);
				try {
					return target.getClass().getMethod(methodName, parameterTypes);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			},
			request -> {
				String[] requestSplit = request.split(";");
				String[] arguments = new String[requestSplit.length - 1];
				System.arraycopy(requestSplit, 1, arguments, 0, arguments.length);
				return arguments;
			}
		);
		
		return new Puller<String>(memoizer, serviceProvider);
	}

	@Override
	public P createPusher(Puller<String> server) {
		Pusher<String> client = server.newClient();
		return ReflectivePusher.create(protocol, client, (method, arguments) -> {
			StringBuilder requestBuilder = new StringBuilder();
			requestBuilder.append(method.getName());
			for(Object arg: arguments) {
				requestBuilder.append(";");
				requestBuilder.append((String)arg);
			}
			return requestBuilder.toString();
		});
	}
}
