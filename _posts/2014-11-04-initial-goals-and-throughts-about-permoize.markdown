---
layout: post
title:  "Initial goals and thoughts about Permoize"
date:   2014-11-04 07:21:38
categories: jekyll update
---
Permoize is an offspin project from [another Github project named Duro](https://github.com/jakobehmsen/duro). Duro is a project concerned with attempting to develop a runtime for persistent processes. In this project, a special kind of expression is supported, which is used to memoize the runtime value of that expression in a persistent manner. Explained pedagogically, this can be used to turn otherwise non-deterministic expressions into deterministic expressions. 

For instance, assume `gets` is an expression which reads a single line entered from an end user and `puts` is an expression which writes a single line. Then the following example illustrate how to make the runtime value of `gets` deterministic:

```
var name = $readName gets
puts: name
```

In this example, `$readName` is used as a prefix for the `gets` expression which means the runtime value is memoized across processes. `$` is used to indicate the start of a memoization expression, and the following id (as `readName` in the example) is used to name the memoization and is used for later retrival of the memoized value. The first time the program is run, the user will enter a name. For the subsequent runs, the user will not enter a name - instead the initially entered name is consistently the runtime value for the expression.

Calling this deterministic may be stretching the term's usual meaning too much but I hope the example and its purpose makes a little sense.

Now, back to Permoize. The memoization construct in Duro is used as the main inspiration for building a simple Java library with the same purposes as summarized above. Instead of having memoization as an innate part of Java, a simple Java interface is designed and implemented. Here, the most important interface is `Memoizer`. Lets look at an example corresponding to the above example in Duro:

```Java
Memoizer memoizer = ...; // Initialization of a Memoizer

String name = memoizer.recollect("readName", () -> gets());
puts(name);
```

In this example, a Memoizer is initialized (somehow). In addition, the Memoizer.recollect method is invoked supplied with a name and a conditional producer of that particular value.

So, how can Permoize be used for generel persistence in larger applications which are often event based which usually implies a push oriented architecture? This is an issue which is to be investigated further and will be part of future posts.
