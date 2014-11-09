package permoize;

public class SimpleMetaProtocolBuilder<T, P> implements MetaProtocolBuilder<P> {
	private MetaProtocol<T, P> metaProtocol;
	
	private SimpleMetaProtocolBuilder(MetaProtocol<T, P> metaProtocol) {
		this.metaProtocol = metaProtocol;
	}
	
	public static <T, P> MetaProtocolBuilder<P> wrap(MetaProtocol<T, P> metaProtocol) {
		return new SimpleMetaProtocolBuilder<T, P>(metaProtocol);
	}

	@Override
	public MetaPuller<P> createPuller(Memoizer memoizer) {
		Puller<T> puller = metaProtocol.createPuller(memoizer);
		
		return new MetaPuller<P>() {
			public P createPusher() {
				return metaProtocol.createPusher(puller);
			}

			@Override
			public RunningPuller start() {
				return RunningPuller.start(puller);
			}
		};
	}
}
