package permoize;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface Address extends Serializable {
	public static class Reference implements Address {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public static final Reference INSTANCE = new Reference();
		
		private Reference() { }

		@Override
		public Object resolveFrom(Object reference) {
			return reference;
		}
		
		private Object readResolve() throws ObjectStreamException {
			return INSTANCE;
		}
	}
	
	Object resolveFrom(Object reference);
	default Address combinedWith(Address other) {
		return new Address() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object resolveFrom(Object reference) {
				return other.resolveFrom(this.resolveFrom(reference));
			}
		};
	}
}
