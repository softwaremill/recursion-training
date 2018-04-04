package training.recursion.ex03

import scalaz._
import Scalaz._

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)     extends Expr[A]
case class DecValue[A](v: Double)  extends Expr[A]
case class Sum[A](a: A, b: A)      extends Expr[A]
case class Multiply[A](a: A, b: A) extends Expr[A]
case class Divide[A](a: A, b: A)   extends Expr[A]
case class Square[A](a: A)         extends Expr[A]
// -------------------------------------------------

object Ex03_Catamorphism extends App with Ex03_Traverse {

  import matryoshka.data._
  import matryoshka._
  import matryoshka.implicits._

  // a set of rules
  def evalToDouble(expr: Expr[Double]): Double = expr match {
    case IntValue(v)      => v.toDouble
    case DecValue(v)      => v
    case Sum(d1, d2)      => d1 + d2
    case Multiply(d1, d2) => d1 * d2
    case Divide(d1, d2)   => d1 / d2
    case Square(d)        => d * d
  }

  val sumExpr: Fix[Expr] = Fix(
    Sum(
      Fix(IntValue[Fix[Expr]](10)),
      Fix(DecValue[Fix[Expr]](5.5))
    )
  )

  // comment this out and recompile to see an implicit resolution error
  implicit val ExprFunctor: Functor[Expr] = ??? // TODO

  println(s"Expression: $sumExpr\nExpr evaluated to double: ${sumExpr.cata(evalToDouble)}")

  // fix sugar
  val division =
    Divide(DecValue(5.2), Sum(IntValue[Unit](10), IntValue[Unit](5)))

  val fixedSum: Fix[Expr] =
    Sum(
      IntValue[Fix[Expr]](10).embed, // extension method
      IntValue[Fix[Expr]](5).embed
    ).embed

  val fixedDivision: Fix[Expr] = ??? // TODO use .embed

  // optimization
  def optimizeSqr(expr: Fix[Expr]): Fix[Expr] = ??? // TODO (use .project and .embed)

  // how to apply this function?
  // transCataT
  val initialExpr: Fix[Expr] =
    Sum(
      DecValue[Fix[Expr]](5.2).embed,
      Multiply(
        DecValue[Fix[Expr]](3.0).embed,
        DecValue[Fix[Expr]](3.0).embed
      ).embed
    ).embed

  val optimizedExpr = initialExpr.transCataT(optimizeSqr)
  println(optimizedExpr)

  // cataM

  // AlgebraM
  def evalToDoubleOrErr(exp: Expr[Double]): \/[String, Double] = exp match {
    case _ => ??? // TODO
  }

  val correctExpr: Fix[Expr] =
    Sum(
      DecValue[Fix[Expr]](5.2).embed,
      Divide(
        DecValue[Fix[Expr]](3.0).embed,
        DecValue[Fix[Expr]](3.0).embed
      ).embed
    ).embed

  val incorrectExpr: Fix[Expr] =
    Sum(
      DecValue[Fix[Expr]](5.2).embed,
      Divide(
        DecValue[Fix[Expr]](3.0).embed,
        DecValue[Fix[Expr]](0.0).embed // !!!!!!!!
      ).embed
    ).embed

  implicit val traverse = traverseExpr

  correctExpr.cataM(evalToDoubleOrErr)   // Right(6.2)
  incorrectExpr.cataM(evalToDoubleOrErr) // Left("Division by zero!")
}
