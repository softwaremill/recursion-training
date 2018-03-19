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

```scala
sealed trait Expr[A]

case class IntValue[A](v: Int)           extends Expr[A]
case class DecValue[A](v: Double)        extends Expr[A]
case class Sum[A](a: A, b: A)            extends Expr[A]
case class Multiply[A](a: A, b: A)       extends Expr[A]
case class Divide[A](a: A, b: A)         extends Expr[A]
```