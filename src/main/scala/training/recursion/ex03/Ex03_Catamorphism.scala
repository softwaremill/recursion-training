package training.recursion.ex03

import scalaz.Functor

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)     extends Expr[A]
case class DecValue[A](v: Double)  extends Expr[A]
case class Sum[A](a: A, b: A)      extends Expr[A]
case class Multiply[A](a: A, b: A) extends Expr[A]
case class Divide[A](a: A, b: A)   extends Expr[A]
// -------------------------------------------------

object Ex03_Catamorphism extends App {

  import matryoshka.data._
  import matryoshka._
  import matryoshka.implicits._

  // a set of rules
  val transformation: Algebra[Expr, Double] = {
    case IntValue(v)      => v.toDouble
    case DecValue(v)      => v
    case Sum(d1, d2)      => d1 + d2
    case Multiply(d1, d2) => d1 * d2
    case Divide(d1, d2)   => d1 / d2
  }

  val sumExpr: Fix[Expr] = Fix(
    Sum(
      Fix(IntValue[Fix[Expr]](10)),
      Fix(DecValue[Fix[Expr]](5.5))
    )
  )

  implicit val ExprFunctor: Functor[Expr] = ??? // TODO

  sumExpr.cata(transformation)
}
