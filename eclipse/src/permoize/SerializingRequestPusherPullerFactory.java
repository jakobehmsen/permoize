package permoize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializingRequestPusherPullerFactory<P> implements PusherPullerFactory<byte[], P> {
	private Class<P> protocol;
	private P implementer;
	
	private SerializingRequestPusherPullerFactory(Class<P> protocol, P implementer) {
		this.protocol = protocol;
		this.implementer = implementer;
	}
	
	public static <P> SerializingRequestPusherPullerFactory<P> create(Class<P> protocol, P implementer) {
		return new SerializingRequestPusherPullerFactory<P>(protocol, implementer);
	}

	@Override
	public Puller<byte[]> createPuller(Memoizer memoizer) {
		ServiceProvider<byte[]> serviceProvider = new ReflectiveServiceProvider<P, byte[]>(
				implementer,
			(target, request) -> {
				try {
					ByteArrayInputStream bytesIn = new ByteArrayInputStream(request);
		            ObjectInputStream objectsIn = new ObjectInputStream(bytesIn);
		            
		            String methodName = objectsIn.readUTF();
					Class<?>[] parameterTypes = (Class<?>[])objectsIn.readObject();
					
					return target.getClass().getMethod(methodName, parameterTypes);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			},
			request -> {
				try {
					ByteArrayInputStream bytesIn = new ByteArrayInputStream(request);
		            ObjectInputStream objectsIn = new ObjectInputStream(bytesIn);
		            Object[] arguments = (Object[])objectsIn.readObject();

					return arguments;
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			}
		);
		
		return new Puller<byte[]>(memoizer, serviceProvider);
	}

	@Override
	public P createPusher(Puller<byte[]> server) {
		Pusher<byte[]> client = server.newClient();
		return ReflectivePusher.create(protocol, client, (method, arguments) -> {
			try {
				ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	            ObjectOutputStream objectsOut = new ObjectOutputStream(bytesOut);
	            
	            objectsOut.writeUTF(method.getName());
	            objectsOut.writeObject(method.getParameterTypes());
	            objectsOut.writeObject(arguments);
	            
	            return bytesOut.toByteArray();
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
			
//			StringBuilder requestBuilder = new StringBuilder();
//			requestBuilder.append(method.getName());
//			for(Object arg: arguments) {
//				requestBuilder.append(";");
//				requestBuilder.append((String)arg);
//			}
//			return requestBuilder.toString();
		});
	}
}
