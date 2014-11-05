---
layout: post
title:  "Event handling - push to pull"
date:   2014-11-05 18:05:47
categories: jekyll update
---

Using Permoize in its current state for push based architectures requires a little bit of fiddling due to its pull oriented design. Take for instance the development of a GUI based application implemented using Swing, in which event handling is push based. If Java had powerfull coroutine constructs, then these could likely be used for solving this mismatch. Instead, concurrency is chosen as the savior.

Luckily, a quite practical kind of collection is available for smoothing the friction: a [BlockingQueue](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html). In short, the push-to-pull processing conversion has two significant components both interacting via a BlockingQueue - a pusher and a puller:

* A pusher component handles pushed Swing events by pushing them to the BlockingQueue. Such a component will probably encompass multiple Swing event listeners.
* A puller component pulls events from the BlockingQueue where the events now can be seen as requests. Each of these requests is recollected using Permoize.

This push-to-pull processing conversion is illustrated below [^1]:

```Java
Memoizer memoizer = ...;
// The BlockingQueue that the pusher and the puller interact via 
BlockingQueue requestStream = ...;

// The pusher
component.addSomeListener(e -> {
	// e could be converted into something more meaningfull here before being pushed
	requestStream.push(e);
});

// The puller
new Thread(() -> {
	while(true) {
		Object request = memoizer.recollect("request", () -> {
			try {
				return requestStream.take();
			} catch(InterruptedException e) {
				// If the BlockingQueue is interrupted, indicate to the Memoizer that 
				// the value shouldn't be collected.
				throw new DontCollectException();
			}
		});
		
		// Process request
	}
}).start();
```

Using this combination, the persisted requests are "replayed" against the puller during each application startup and thus simulates persistance.

This marks the beginning of how to use Permoize for more real applications.

[^1]: For a full example, look at [the simple contact list](https://github.com/jakobehmsen/permoize/tree/master/eclipse/src/permoize/examples/contactlist).