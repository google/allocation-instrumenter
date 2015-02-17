The Allocation Instrumenter is a Java agent written using the [java.lang.instrument][] API and
[ASM][]. Each allocation in your Java program is instrumented; a user-defined callback is invoked
on each allocation.

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

[java.lang.instrument]: http://java.sun.com/javase/6/docs/api/java/lang/instrument/package-summary.html
[ASM]: http://asm.ow2.org/
