package permoize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializingRequestMetaProtocol<P> implements MetaProtocol<byte[], P> {
	public static class Util {
		public static byte[] invocationToRequest(String methodName,
				Class<?>[] parameterTypes, Object[] arguments)
				throws IOException {
			return invocationToRequest(Address.Reference.INSTANCE, methodName, parameterTypes, arguments);
		}
		
		public static byte[] invocationToRequest(Address address, String methodName,
				Class<?>[] parameterTypes, Object[] arguments)
				throws IOException {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			ObjectOutputStream objectsOut = new ObjectOutputStream(bytesOut);

			objectsOut.writeUTF(methodName);
			objectsOut.writeObject(parameterTypes);
			objectsOut.writeObject(arguments);
			objectsOut.writeObject(address);

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
			},
			(target, request) -> {
				try {
					ByteArrayInputStream bytesIn = new ByteArrayInputStream(request);
					ObjectInputStream objectsIn = new ObjectInputStream(bytesIn);
					objectsIn.readUTF(); // Consume method name
					objectsIn.readObject(); // Consume parameter types
					objectsIn.readObject(); // Consume arguments
					Address address = (Address)objectsIn.readObject();
					
					return address.resolveFrom(target);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			}
		);
		
		return new Puller<byte[]>(memoizer, serviceProvider);
	}

	@Override
	public P createPusher(Puller<byte[]> puller) {
		return createPusher(Address.Reference.INSTANCE, puller);
	}
	
	@Override
	public P createPusher(Address address, Puller<byte[]> puller) {
		Pusher<byte[]> client = puller.newClient();
		return ReflectivePusher.create(protocol, implementer, client, (method, arguments) -> {
			try {
				return Util.invocationToRequest(address, method.getName(), method.getParameterTypes(), arguments);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		});
	}
}
