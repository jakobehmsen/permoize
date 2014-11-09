---
layout: post
title:  "Simplifying argument pushing and -pulling"
date:   2014-11-09 10:51:32
categories: jekyll update
---

At this point, the usage of persistent memoization for requests has reached a level of usability that begins to scratch practical applicability. As mentioned in the end of [Abstracting push to pull]({{site.baseurl}}{% post_url 2014-11-07-abstracting-push-to-pull %}), the pushing and pulling arguments leaves significant room for improvement due to the restriction to String based arguments.

The restriction to String based arguments was a simple solution because the requests are string based themselves thus making concatenation straight forward. However, Java consists of several different primitive types and classes are commonly used with [String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html) being just one example. The Java Class Library has support for fairly easy implementation of serialization[^1]. In the spirit of lazyness, lets just try to use this for user friction reduction.

As it turns out, due to invoking the reuse gods, only few changes were required. The most significant change is a new meta protocol: [SerializingRequestMetaProtocol](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/SerializingRequestMetaProtocol.java). This meta protocol uses the abovementioned serialization library both for converting method invocations into byte array based requests and for resolving method and arguments for method invocations. SerializingRequestMetaProtocol is more general than the prevously used [StringRequestMetaProtocol](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/StringRequestMetaProtocol.java), thus it is ease to swap out these two for the contact list application[^2]:

```Java
// Before
MetaProtocol<String, ContactList> metaProtocol = 
    StringRequestMetaProtocol.create(ContactList.class, contactListImpl);
// After
MetaProtocol<byte[], ContactList> metaProtocol = 
	SerializingRequestMetaProtocol.create(ContactList.class, contactListImpl);
```

Swapping meta protocols doesn't improve usability for the pushing and pulling of arguments for the particular application - this requires a change in the [ContactList](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/examples/contactlistx/ContactList.java) protocol. The parts of a protocol that has awkward String based parameters can now be replaced by more direct types. In the case of the ContactList protocol, the String based integer index parameters can now be replaced by usages of int. This implicates reducing the, otherwise, inherent String to/from int conversion overhead for users/implementers. See below for an illustration of the changes that can be made to ContactList:

```Java
// Before
void add(String firstName, String lastName, String phoneNumber);
void update(String indexStr, String firstName, String lastName, String phoneNumber);
void delete(String indexStr);

// After
void add(String firstName, String lastName, String phoneNumber);
void update(int index, String firstName, String lastName, String phoneNumber);
void delete(int index);
```

After the above transformation, the usage and implementation of ContactList must of course be corrected accordingly which yields a contact list application[^3] with less String related conversion needs.

Though this is a subtle improvement for the specific application, one could easily imagine an application with lots of usages of non-String based parameters in its protocol. This is definitely an improvement compared to using StringRequestMetaProtocol. However, [the MetaProtocol interface itself](https://github.com/jakobehmsen/permoize/blob/master/eclipse/src/permoize/MetaProtocol.java) can be simplified further. Especially the details about the type of the request to be used among pushing and pulling should be hidden. This should make the life of the user easier.

[^1]: If one's curiosity about the used serialization approach hasn't been satisfied, then have a look at some of the the official documentation [here](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html) and [here](https://docs.oracle.com/javase/tutorial/jndi/objects/serial.html).
[^2]: There are other subtle changes that also needs to be made such as changing generic parameters from `String` to `byte[]`.
[^3]: You can check out the application [here](https://github.com/jakobehmsen/permoize/tree/master/eclipse/src/permoize/examples/contactlistx2).