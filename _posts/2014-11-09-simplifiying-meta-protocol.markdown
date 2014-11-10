---
layout: post
title:  "Simplifying meta protocol"
date:   2014-11-09 18:54:06
categories: jekyll update
---

With [a simplification of argument pushing and -pulling]({{site.baseurl}}{% post_url 2014-11-09-simplifiying-argument-pushing-and-pulling %}), the users' lives were relieved a bit. Lets prolong this usability focus by centering on the need to repeatedly express the type of request[^1] to exchange among pushers and pullers.

The chosen solution for this problem partly consists of the definition of [a MetaProtocolBuilder interface](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/MetaProtocolBuilder.java) in companion with [a MetaPuller interface](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/MetaPuller.java), for both of which only the protocol is to be expressed[^2]:

```Java
public interface MetaProtocolBuilder<T> {
	MetaPuller<T> createPuller(Memoizer memoizer);
}

public interface MetaPuller<T> {
	T createPusher();
	RunningPuller start();
}
```

As an implementation of MetaProtocolBuilder, [SimpleMetaProtocolBuilder](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/SimpleMetaProtocolBuilder.java) has been devised with its most important, userwise, part being a static `wrap` method which simply takes a MetaProtocol instance and simulates MetaProtocolBuilder behavior. The only matter left is invoking the `wrap` method of SimpleMetaProtocolBuilder provided the used MetaProtocol in an application and make according adjustment where needed[^3]:

```Java
Memoizer memoizer = ...;
ContactListImpl contactListImpl = ...;

// Before
MetaProtocol<byte[], ContactList> metaProtocol = 
	SerializingRequestMetaProtocol.create(ContactList.class, contactListImpl);
Puller<byte[]> puller = metaProtocol.createPuller(memoizer);
ContactList contactListPusher = metaProtocol.createPusher(puller);
...
RunningPuller runningPuller = RunningPuller.start(puller);

// After
MetaProtocolBuilder<ContactList> metaProtocol = SimpleMetaProtocolBuilder.wrap(
	SerializingRequestMetaProtocol.create(ContactList.class, contactListImpl));
MetaPuller<ContactList> metaPuller = metaProtocol.createPuller(memoizer);
ContactList contactListPusher = metaPuller.createPusher();
...
RunningPuller runningPuller = metaPuller.start();
```

The creation of the particular MetaProtocol becomes a tad more complex due to the usage of the `wrap` method - nonetheless the request type is expressed - that even being in an implicit sense. This seems like a positive tradeoff from an aggregate view.

Now, the contact list example applications consisted of a rather simple two level hierarchi, that being of a contact list entailing contacts. But what if one needed to represent more complicated interrelations, such as a three level hierachi? Say, an order catalog, order, and order line hierarchi? This could implicate a need to pass on requests in more indirect sense.

[^1]: E.g. `String` or `byte[]`.
[^2]: Such as `ContactList` in the case of (several of) the contact list applications in [examples](https://github.com/jakobehmsen/permoize/tree/master/eclipse/src/permoize/examples).
[^3]: Check out [the whole application](https://github.com/jakobehmsen/permoize/tree/master/eclipse/src/permoize/examples/contactlistx3).