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
A transformation which takes:
- A recursive expression of type Expr, 
- a function which evaluates `Expr => Double`  
  For example `case Sum(d1, d2) => d1 + d2`
And which produces a `Double`

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

That's much better, because this allows us to build our desired awesome
transformation:
```scala 
def transformation(exp: Expr[Double]): Double = exp match {
  case IntValue(v) => v.toDouble
  case DecValue(v) => v
  case Sum(d1, d2) => d1 + d2
  case Multiply(d1, d2) => d1 * d2
  case Divide(d1, d2) => d1 / d2
} 
```
Such transformation is what we aim for, because it doesn't look like
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

First, we wanted a transformation which doesn't look like recursion,
but like a simple, flat set of rules. 

A transformation which takes:
- A recursive expression of type Expr, 
- a function which evaluates `Expr => Double`  
  For example `case Sum(d1, d2) => d1 + d2`

To be able to express such rules, we needed to go from `Expr` to `Expr[A]`.
To avoid issues with nested types, we introduced `Fix[Expr]`

### Putting it all together

Once we have:
- A polymorphic recursive structure based on `Expr[A]`
- A transformation expressed as a set of rules for 
  each sub-type (`Expr[B] => B`)
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

def transformation(expr: Expr[Double]): Double = ???

// the magic call
rcursiveExpression.cata(transformation) // returns Double
```

The `.cata()` call runs the whole folding process and constructs 
the final `Double` value for you, provided just a set of rules for
indiviual node types.

### Expression functor

Matryoshka's `.cata()` is a blackbox, but it has one more requirement.
