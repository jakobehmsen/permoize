package permoize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Invocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Method method;
	private Object[] args;
	
	public Invocation(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}

	public Object invoke(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(obj, args);
	}
	
	private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
		Class<?> methodDeclaringClass = (Class<?>)inputStream.readObject();
		String methodName = inputStream.readUTF();
		Class<?>[] methodParameters = (Class<?>[])inputStream.readObject();
		
		try {
			method = methodDeclaringClass.getMethod(methodName, methodParameters);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		args = (Object[])inputStream.readObject();
	}

	private void writeObject(ObjectOutputStream outputStream) throws IOException {
		outputStream.writeObject(method.getDeclaringClass());
		outputStream.writeUTF(method.getName());
		outputStream.writeObject(method.getParameterTypes());
		outputStream.writeObject(args);
	}
}
