Akka mini-kernels for testing pubsub.
Please see [zeromq-demo](https://github.com/mslinn/zeromq-demo) for instructions on building zeromq.

Requires the following entry in `/etc/hosts`:

````
127.0.0.1 pubsubbroadcast bookviewgeneratoractor
````
The subscriber should start before the publisher.
To run:

````
cd subKernel
sbt dist && run
````

in another console:

````
cd pubKernel
sbt dist && run
````

