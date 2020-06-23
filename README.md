The Allocation Instrumenter is a Java agent written using the [java.lang.instrument][] API and
[ASM][]. Each allocation in your Java program is instrumented; a user-defined callback is invoked
on each allocation.

## How to get it

The [latest release][] is available from [Maven Central][] as:

```xml
<dependency>
  <groupId>com.google.code.java-allocation-instrumenter</groupId>
  <artifactId>java-allocation-instrumenter</artifactId>
  <version>3.3.0</version>
</dependency>
```

## Basic usage

In order to write your own allocation tracking code, you have to implement the `Sampler` interface
and pass an instance of that to `AllocationRecorder.addSampler()`:

```java
AllocationRecorder.addSampler(new Sampler() {
  public void sampleAllocation(int count, String desc, Object newObj, long size) {
    System.out.println("I just allocated the object " + newObj +
      " of type " + desc + " whose size is " + size);
    if (count != -1) { System.out.println("It's an array of size " + count); }
  }
});
```

You can also use the allocation instrumenter to instrument constructors of particular classes.
You do this by instantiating a `ConstructorCallback` and passing it to
`ConstructorInstrumenter.instrumentClass()`:

```java
try {
  ConstructorInstrumenter.instrumentClass(
      Thread.class, new ConstructorCallback<Thread>() {
        @Override public void sample(Thread t) {
          System.out.println("Instantiating a thread");
        }
      });
} catch (UnmodifiableClassException e) {
  System.out.println("Class cannot be modified");
}
```

For more information on how to get or use the allocation instrumenter, see [Getting Started][].

[java.lang.instrument]: http://java.sun.com/javase/6/docs/api/java/lang/instrument/package-summary.html
[ASM]: http://asm.ow2.org/
[latest release]: https://github.com/google/allocation-instrumenter/releases/tag/java-allocation-instrumenter-3.3.0
[Maven Central]: http://search.maven.org/#artifactdetails%7Ccom.google.code.java-allocation-instrumenter%7Cjava-allocation-instrumenter%7C3.3.0%7Cjar
[Getting Started]: https://github.com/google/allocation-instrumenter/wiki
