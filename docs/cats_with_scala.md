## FP Design patterns
Monads and related concepts are functional programming design patterns,
as in architectural building blocks that turn up over and over in code

2 main characteristics that make it differ with oop
1. they are formally and thus precisely defined
2. they are general or abstract (makes it difficult to understand)

## Type classes
The FP tools in cats are delivered in the form of type classes, that
we can apply to existing scala types.  Type classes are programming pattern
that originated in Haskell.

They allow us to extend existing libraries with new functionality, without
using traditional inheritance, and without altering original library code

### Parts of a TypeClass
Three parts to a type class:
1. The TC itself as in _trait_
2. Instances for particular types -- _implicit values_
3. Methods that use the TC -- _implicit params_

Implicit classes make TC's easier to use

#### The Typeclass As in Trait
An interface(keyword) or API as in trait that represents some functionality we would
like to implement.

#### The Typeclass Instance as in the Implicit Values
The instances of a type class provide implementations of the type class for
specific types we care about, which can include types from the Scala standard
library and types from our domain model.

#### The Typeclass Use as in the Implicit Parameters
Cats provides utilities that make type classes easier to use, and you will some‐
times seem these patterns in other libraries. There are two ways it does this:
Interface Objects and Interface Syntax.

_Interface Objects as in placing methods in a singleton object_
The compiler spots that we’ve called the TC method without providing
the implicit parameters. It tries to fix this by searching for type class instances
of the relevant types and inserting them at the call site

_Interface Syntax as in extension methods with interface methods_
The compiler searches for candidates for the implicit parameters and
fills them in for us

The power of type classes and implicits lies in the compiler’s ability to combine
implicit definitions when searching for candidate instances. This is sometimes
known as type class composition.

We can actually define instances in two ways:
1. by defining concrete instances as implicit vals of the required
   type
2. by defining implicit methods to construct instances from other type
   class instances

### Variance
When we define type classes we can add variance annotations to the type
parameter to affect the variance of the type class and the compiler’s ability to
select instances during implicit resolution.

Variance relates to subtypes. We say that B is a sub‐ type of A if 
we can use a value of type B anywhere we expect a value of type A.

#### Co-Variance +A
Covariance means that the type F[B] is a subtype of the type F[A] if B is a
subtype of A . This is useful for modelling many types, including collections like
List and Option.

#### Contra-Variance -A
Covariance means that the type F[B] is a subtype of the type F[A] if A is a
subtype of B . This is useful for modelling types that represent inputs.

#### Invariance
Invariance is the easiest situation to describe. It’s what we get when we don’t
write a + or - in a type constructor

## Monoids and Semigroups
These allow us to add or combine values.

### Monoids
Formally, a monoid for a type A is:
• an operation combine with type (A, A) => A
• an element empty of type A known as identity

In addition to providing the combine and empty operations, monoids must
formally obey several laws. For all values x , y , and z , in A , combine must be
associative and empty must be an identity element.

In practice, we only need to think about laws when we are writing our own
Monoid instances. Unlawful instances are dangerous because they can yield
unpredictable results when used with the rest of Cats’ machinery. Most of
the time we can rely on the instances provided by Cats and assume the library
authors know what they’re doing.

### Semigroup
A semigroup is just the combine part of a monoid, without the empty part.
While many semigroups are also monoids, there are some data types for which
we cannot define an empty element. For example, we have just seen that
sequence concatenation and integer addition are monoids. However, if we
restrict ourselves to non‐empty sequences and positive integers, we are no
longer able to define a sensible empty element.

## Functors
An abstraction that allows us to represent sequences of operations within a context 
such as a List , an Option , or any one of a thousand other possibilities. 
Functors on their own aren’t so useful, but special cases of functors, 
such as monads and applicative functors , are some of the most commonly used abstractions in Cats

Informally, a functor is anything with a map method

Examples are List Option Either Functions Futures

Functor provides a method called _lift_ , which converts a function of type A
=> B to one that operates over a functor and has type F[A] => F[B]

The _as_ method is the other method you are likely to use. It replaces with value
inside the Functor with the given value

Functors map method as "appending" transformation to a chain

### ContraVariant Functors
Models prepending operations to a chain.  The first of our type classes, the contravariant functor, provides an operation
called contramap that represents “prepending” an operation to a chain.
The contravariant functor provides and operation called contramap.  Only makes sense for transformations.

### InVariant Functors
Models creating bi-directional operations to a chain.  The invariant functor, provides an operation
called imap that is informally equiva‐
lent to a combination of map and contramap . If map generates new type class
instances by appending a function to a chain, and contramap generates them
by prepending an operation to a chain, imap generates them via a pair of bidi‐rectional transformations.
The most intuitive examples of this are a type class that represents encoding
and decoding as some data type, such as Play JSON’s Format and scodec’s
Codec .

## Monads A monad is a mechanism for sequencing computations.
Monads are one of the most common abstractions in Scala. Many Scala pro‐
grammers quickly become intuitively familiar with monads, even if we don’t
know them by name.

Informally, a monad is anything with a constructor and a flatMap method. All
of the functors we saw in the last chapter are also monads, including Option ,
List , and Future . We even have special syntax to support monads: for comprehensions.

Monadic behaviour is formally captured in two operations:
• pure , of type A => F[A] ; -- comes from Applicative (extends functor)
• flatMap , of type (F[A], A => F[B]) => F[B] -- FlatMap

### Option
Option allows us to sequence computations that may or may not return values.  
Methods on options might fail by returning None.  
The flatMap method allows us to ignore this when we sequence operations
Every monad is also a functor

### Identity Monad
Allows us to call monadic methods with plain values

### Cats- either
Some advantages:
1. smart constructors asRight and asLeft which returns Either as opposed to left and right
2. Either catchOnly and catchNonFatal
3. FromTry
4. fromOption

### Cats- Eval Monad
cats.Eval is a monad that allows us to abstract over different models of evaluation. 
We typically talk of two such models: eager and lazy, also called call‐
by‐value and call‐by‐name respectively. Eval also allows for a result to be
memoized, which gives us call‐by‐need evaluation.
Eval is also stack‐safe, which means we can use it in very deep recursions
without blowing up the stack

## Monad Transformers
The transformer is the inner monad, while the first type param is the outer monad

OptionT[List, A] == List[Option[A]]





