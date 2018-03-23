Recursion schemes
=========================

This repository contains documentation, samples and exercises for the Recursion Schemes workshop.

**Running**  
Load this project in sbt and just launch `run`, then select the exercise you'd like to execute.

## Introduction

Recursive structures are a common pattern and most developers have worked with such data at least a few times. 

### Trees
**Nodes** and **leafs**

```
           root-node
           /   \    
  child-node   leaf
  /     \
leaf    leaf  
```

### Json

TODO

### List

TODO

### ASTs

```sql
SELECT * FROM users u WHERE u.age > 25 AND UPPER(u.name) LIKE "J%"  
```

```
FILTER:
- source:  
    SELECT  
        -- selection: *  
        -- from: users  
- criteria:  
    AND  
        - GT(u.age, 25)  
        - LIKE(  
            UPPER(  
                u.name)  
            J%)      
```

### What you can do with recursive data
- Print
- Translate into another structure
- Enrich
- Optimize


## Manual recursion 

```scala
sealed trait Expr

case class IntValue(v: Int)           extends Expr
case class DecValue(v: Double)        extends Expr
case class Sum(a: Expr, b: Expr)      extends Expr
case class Multiply(a: Expr, b: Expr) extends Expr
case class Divide(a: Expr, b: Expr)   extends Expr
  
def eval(e: Expr): Double =
e match {
  case IntValue(v)      => v.toDouble
  case DecValue(v)      => v
  case Sum(e1, e2)      => eval(e1) + eval(e2)
  case Multiply(e1, e2) => eval(e1) * eval(e2)
  case Divide(e1, e2)   => eval(e1) / eval(e2)
}
```

## Fixed point datatypes

### Making an ADT polymorphic

Ideally we'd love to have something more elegant.
We are looking for a tool which takes:
- A recursive expression of type Expr, 
- a function which evaluates `Expr => Double`  
  For example `case Sum(d1, d2) => d1 + d2`
Such tool evaluates whole expression to a `Double`

Types like `Sum(a: Expr, b: Expr)` force us to deal only with Exprs. 
Ideally we'd like to have our eval definition to look like:
```scala
// does not compile, but it's only an illustration of a direction
def eval(e: Expr): Double = 
e match {
  case Sum(dbl1: Double, dbl2: Double) => dbl1 + dbl2 // etc
} 
``` 

Let's make our expression **polymorphic**.

```scala
sealed trait Expr[A]

case class IntValue[A](v: Int)           extends Expr[A]
case class DecValue[A](v: Double)        extends Expr[A]
case class Sum[A](a: A, b: A)            extends Expr[A]
case class Multiply[A](a: A, b: A)       extends Expr[A]
case class Divide[A](a: A, b: A)         extends Expr[A]
```

That's much better, because this allows us express our evaluations as:
```scala 
def transformation(exp: Expr[Double]): Double = exp match {
  case IntValue(v) => v.toDouble
  case DecValue(v) => v
  case Sum(d1, d2) => d1 + d2
  case Multiply(d1, d2) => d1 * d2
  case Divide(d1, d2) => d1 / d2
} 
```
Such evaluation is what we aim for, because it doesn't look like
recursion. It looks more like a set of rules, which we can **apply** to
a recursive structure with some blackbox tool which will recursively
build the result.

But let's stop here for a while, because polymorphic expressions
come with a cost... First, consider a trivial expression:  
`val intVal = IntValue[Unit](10) // Expr[Unit]`

What about more complex expressions?

```scala
  val sumExp: Expr[Expr[Unit]] =
    Sum(
      IntValue[Unit](10), // Expr[Unit]
      IntValue[Unit](5)
    )
```

### Fixing nested Exprs

how to deal with types like `Expr[Expr[Expr[A]]]`?
Let's wrap in:  

```scala
case class Fix[F[_]](unFix: F[Fix[F]])
  
val fixedIntExpr: Fix[Expr] = Fix(IntValue[Fix[Expr]](10))
```

The `Fix` type allows us to represent any `Expr[Expr[Expr....[A]]]` as `Fix[Expr]`

Wait, why did we need this`Fix` thing?

### A step back

We wanted to use evaluation definition which doesn't look like recursion. 

We are looking for a tool which takes:
- A recursive expression of type Expr, 
- a function which evaluates a single simple `Expr => Double`  
  For example `case Sum(d1, d2) => d1 + d2`

To be able to express such rules, we needed to go from `Expr` to `Expr[A]`.
To avoid issues with nested types, we introduced `Fix[Expr]`

### Putting it all together

Once we have:
- A polymorphic recursive structure based on `Expr[A]`
- An evaluation recipe expressed as a set of rules for  each sub-type (`Expr[B] => B`)
- A `Fix[F[_]]` wrapper

We can now use a tool to put this all together. Such tool is called...

## Catamorphism

### Scheme

A generic **foldRight** for data stuctures. In case of recursive data,
this means **folding bottom-up**:

```scala
  val division =
    Divide(DecValue(5.2), Sum(IntValue(10), IntValue(5)))
```

```
            Divide                             Divide
           /    \                              /    \
DecValue(5.2)   Sum            -->   DecValue(5.2)  Sum
                / \                                 / \
     IntValue(10)  IntValue(5)                   10.0 5.0
```

```
            Divide                             Divide
           /    \                              /    \
DecValue(5.2)   Sum            -->            5.2  15.0
                / \                                 
             10.0  5.0                  
```

```
            Divide             -->            5.2 / 15.0                             
           /    \                              
         5.2   15.0            
```

Going **bottom-up**, we use our set of rules on leafs, then we build
higher nodes **basing** on lower nodes. Catamorphism is a **generic** tool,
so you don't have to implement it!

### Matryoshka and cata

The Matryoshka library does catamorphism for you:

```scala

val recursiveExpr: Fix[Expr] = ??? // your tree

def evalToDouble(expr: Expr[Double]): Double

// the magic call
rcursiveExpression.cata(evalToDouble) // returns Double
```

The `.cata()` call runs the whole folding process and constructs 
the final `Double` value for you, provided just a set of rules for
indiviual node types.

### Expression functor

Matryoshka's `.cata()` is a blackbox, but it has one more requirement.
It's mechanism assumes that a `Functor` instance is available for your datatype.

This means that you must provide a recipe for how to **map** `Expr[A]` to `Expr[B]`
having a function `f: A => B`.
For example to transform `Sum[A](a1: A, a2: A)` into `Sum[B](b1: B, b2: B)` you need
to do `Sum(f(a1), f(a2))`. Such recipe has to be provided for all
possible cases of `Expr`.

```scala
import scalaz.Functor  

implicit val exprFunctor: Functor[Expr] = new Functor[Expr] {
  override def map[A, B](expr: Expr[A])(f: A => B): Expr[B] = expr match {
    case IntVal(v) => IntVal[B](v)
    case Sum(a1, a2) => Sum(f(a1), f(a2))
    case ... // etc.
  }      
}
```

This is finally all what we need! Here's a summary of our ingredients:

1. A recursive structure `Expr[A]`
2. A Functor for this type
3. A set or evaulation rules for **individual cases**
4. `Fix[[_]]`
5. catamorphism

4 & 5 are provided by Matryoshka.

### Algebra

Our evaluation function (point 3.) is called an **Algebra**. From Matryoshka:

```scala
type Algebra[F[_], A] = scala.Function1[F[A], A]
    
def evalToDouble(expr: Expr[Double]): Double
  
val evalToDouble: Algebra[Expr, Double]
```