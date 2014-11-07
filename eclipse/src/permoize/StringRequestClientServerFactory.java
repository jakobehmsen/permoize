package permoize;

import java.util.Arrays;

public class StringRequestClientServerFactory<P> implements ClientServerFactory<String, P> {
	private Class<P> protocol;
	private P implementer;
	
	public StringRequestClientServerFactory(Class<P> protocol, P implementer) {
		this.protocol = protocol;
		this.implementer = implementer;
	}

	@Override
	public Server<String> createServer(Memoizer memoizer) {
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
		
		return new Server<String>(memoizer, serviceProvider);
	}

	@Override
	public P createClient(Server<String> server) {
		Client<String> client = server.newClient();
		return ReflectiveClient.create(protocol, client, (method, arguments) -> {
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
