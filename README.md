Minimal Akka kernels for testing ZeroMQ pub/sub.
Please see [zeromq-demo](https://github.com/mslinn/zeromq-demo) for instructions on building zeromq.

Requires the following entry in `/etc/hosts`:

````
127.0.0.1 publisher subscriber
````

To run, type the following into a console:

````
cd subKernel
sbt dist && run
````

In another console, type:

````
cd pubKernel
sbt dist && run
````

Beware that `sbt clean` is not thorough enough, and you should type the following in each console to properly clean out the projects and re-run them:

````
rm -rf target
sbt clean dist && run
````
