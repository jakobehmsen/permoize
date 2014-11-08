---
layout: post
title:  "Abstracting push to pull"
date:   2014-11-07 16:26:58
categories: jekyll update
---

While [Event handling - push to pull]({% post_url 2014-11-05-event-handling-push-to-pull %}) provided a suggestion for how to convert events from pushes to pulls, by separating an application into a puller- and a pusher component, the suggestion wasn't too practical and very little would be transferable to other applications. Lets look at some possible improvements of these components.

The pusher can be improved for increasing reuse potential on multiple areas. First, the usage and interaction with threads can be hidden much better. Second, the processing of requests involves awkward extension requirements due to, e.g., the usage of a switch statement for routing of requests.

The puller seems to have less rough edges, but the need to explicitly construct requests consistently.

Further, the protocol, based on semicolon separated string requests, is an implicit interdependence among the pusher and puller.

From an end users point of view (a programmer), using plane old Java interfaces and implementations for pushing and pulling would intuitively improve the situation. More specifically, a push should be represented by a regular method invocation - and a pull should be realized by a resolved corresponding method. A simple first instance solution could be to use the reflection capabilities of Java, with the following steps for a push to pull:

* Invoke method on pusher where pusher is an interface **I**
* Convert method invocation into a request
  * E.g. a semicolon separated string request
* Send request to puller
* Resolve method to invoke on **I** using request
* Extract arguments from request
* Invoke resolved method with extracted arguments on implementer of **I**

This should help simplify the implementation of the pullers and pushers and, at the same time, shove off the need for knowledge about the applied meta protocol - in the presented case, semicolon separated string requests.

An illustration of an application of the abovementioned procedure is given below[^1]:

```Java
Memoizer memoizer = ...;
ContactListImpl contactListImpl = ...;
// PusherPullerFactory is a utility interface for constructing pushers and puller
// In the below line, a new such factory is created given the interface ContactList
// and an instance of its related implementer ContactListImpl.
PusherPullerFactory<String, ContactList> pusherPullerFactory = 
	StringRequestPusherPullerFactory.create(ContactList.class, contactListImpl);
Puller<String> puller = pusherPullerFactory.createPuller(memoizer);
ContactList contactListPusher = pusherPullerFactory.createPusher(puller);

// Usage of the pusher
component.addSomeListener(e -> {
	// Extract relevant arguments from e
	String arg1 = ...
	String arg2 = ...
	// The invocation is converted into a request behind the scenes
	contactListPusher.invokeMethodBasedOn(arg1, arg2);
});

// RunningPuller is used to create active pullers, which basically hides thread
// related implementation details.
RunningPuller<String> runningPuller = RunningPuller.start(puller);
```

With likely less irelevant implementation details, Permoize's usage potential seems to becoming more suitable to more practical applications. However, though the implicit push to request conversion and the request to pull conversion should improve usability, the solution still has some areas of improvement. E.g. a need to convert all argument into strings on the pushing side - and a restriction to only support String based parameters on the side of the puller.

[^1]: For more details, take a look at [the extended contact list](https://github.com/jakobehmsen/permoize/tree/master/eclipse/src/permoize/examples/contactlistx).