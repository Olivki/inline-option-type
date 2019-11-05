## Inline Option Type

**This project is rather experimental, as inline classes are still considered experimental as of Kotlin 1.3.50**

This project is a proof-of-concept for implementing an [option type](https://en.wikipedia.org/wiki/Option_type) in Kotlin while avoiding the overhead usually caused by [boxing/wrapping](https://en.wikipedia.org/wiki/Object_type_(object-oriented_programming)#Boxing) the value which most option-type implementations available on the JVM do, using the [inline class](https://kotlinlang.org/docs/reference/inline-classes.html) feature introduced in Kotlin 1.3.

As an example, this is generally how an option type would be implemented in Kotlin *(very simplified)*;

```kot
sealed class Option<out T> {
	companion object {
		// factory functions..
    }
    
    // generic monadic operations..
}

object None : Option<Nothing>()

data class Some<out T>(val item: T) : Option<T>()
```

The "problem" with this type of implementation is that `Some` will be [wrapping](https://en.wikipedia.org/wiki/Object_type_(object-oriented_programming)#Boxing) the `item` value, meaning that some additional overhead may be caused by this, while this *generally* should not be a problem, especially not with the specs of computers nowadays, it would still be nice if we could just avoid that overhead. That's where inline classes come into the picture, they essentially allow us to avoid that overhead by never creating an instance where `item` will be wrapped, and instead we're *actually* always just working on the value we give to our `Option` type.

Now, an `Option` type may not actually be very useful here in Kotlin as it already gives us a lot of tools to work with nullable types, like the `?` operator and the various scoping functions *(`let`, `run`, `apply`, etc..)* which can be chained to easily replicate the use cases of an option type. Under the hood of the `Option` class implemented in this project we're essentially just doing the same things as the compiler does when we use the `?` operator. One might argue that the point of an option type is for interop with Java who does not have nullable and non-nullable types built into its type-system, but this implementation would not work with that, as inline classes can not be used from the Java side, hence why this project is nothing more than a proof of concept.

The Kotlin standard library does actually have an [ABT](https://en.wikipedia.org/wiki/Abstract_data_type) implemented in a similar fashion to how our `Option` type is implemented, which is the `Result` class.

Some food for thought would be to create a [disjoint union](https://en.wikipedia.org/wiki/Disjoint_union) type in a similar fashion, as that one works a bit differently to an option type, but that might not be easily done in a nice way with how inline classes only accept *one* value per class.

## License

````
Copyright {CURRENT_YEAR} Oliver Berg

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
````