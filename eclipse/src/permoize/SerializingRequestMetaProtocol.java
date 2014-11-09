package permoize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializingRequestMetaProtocol<P> implements MetaProtocol<byte[], P> {
	public static class Util {
		public static byte[] invocationToRequest(String methodName, Class<?>[] parameterTypes, Object[] arguments) throws IOException {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream objectsOut = new ObjectOutputStream(bytesOut);
            
            objectsOut.writeUTF(methodName);
            objectsOut.writeObject(parameterTypes);
            objectsOut.writeObject(arguments);
            
            return bytesOut.toByteArray();
		}
	}
	
	private Class<P> protocol;
	private P implementer;
	
	private SerializingRequestMetaProtocol(Class<P> protocol, P implementer) {
		this.protocol = protocol;
		this.implementer = implementer;
	}
	
	public static <P> SerializingRequestMetaProtocol<P> create(Class<P> protocol, P implementer) {
		return new SerializingRequestMetaProtocol<P>(protocol, implementer);
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
					objectsIn.readUTF(); // Consume method name
					objectsIn.readObject(); // Consume parameter types
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
				return Util.invocationToRequest(method.getName(), method.getParameterTypes(), arguments);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		});
	}
}
