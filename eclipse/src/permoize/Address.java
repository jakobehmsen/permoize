package permoize;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public interface Address extends Serializable {
	public static class Reference implements Address {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public static final Reference INSTANCE = new Reference();
		
		private Reference() { }

		@Override
		public Object resolveFrom(Object reference, BiConsumer<Method, Object[]> invocationConsumer) {
			return reference;
		}
		
		private Object readResolve() throws ObjectStreamException {
			return INSTANCE;
		}
	}
	
	Object resolveFrom(Object reference, BiConsumer<Method, Object[]> invocationConsumer);
	default Address combinedWith(Address other) {
		return new Address() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object resolveFrom(Object reference, BiConsumer<Method, Object[]> invocationConsumer) {
				return other.resolveFrom(this.resolveFrom(reference, invocationConsumer), invocationConsumer);
			}
		};
	}
}
