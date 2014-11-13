package permoize;

public class OffsetMetaProtocolBuilder<T> implements MetaProtocolBuilder<T>{
	private MetaProtocolBuilder<T> metaProtocolBuilder;
	private Address address;
	
	private OffsetMetaProtocolBuilder(MetaProtocolBuilder<T> metaProtocolBuilder, Address address) {
		this.metaProtocolBuilder = metaProtocolBuilder;
		this.address = address;
	}
	
	@Override
	public MetaPuller<T> createPuller(Memoizer memoizer) {
		return new MetaPuller<T>() {
			MetaPuller<T> metaPuller = metaProtocolBuilder.createPuller(memoizer);
			
			@Override
			public T createPusher() {
				return metaPuller.createPusher(address);
			}
			
			@Override
			public T createPusher(Address address) {
				// How to combine addresses?
				return null;
			}

			@Override
			public RunningPuller start() {
				return null;
			}
		};
	}
}
