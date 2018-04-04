package training.recursion.ex07

import matryoshka._
import matryoshka.data._
import matryoshka.implicits._
import scalaz._

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)    extends Expr[A]
case class DecValue[A](v: Double) extends Expr[A]
case class Sum[A](a: A, b: A)     extends Expr[A]
case class Square[A](a: A)        extends Expr[A]

sealed trait ExprType
case object IntExpr extends ExprType
case object DecExpr extends ExprType
// -------------------------------------------------

object Ex07_Histomorphism extends App with Ex07_Traverse {

  val expr: Fix[Expr] =
    sum(
      square(square(int(3))),
      square(square(square(int(5))))
    )

  // ----------------------- histomorphism
  val smartPrint: GAlgebra[Cofree[Expr, ?], Expr, String] = {
    case IntValue(v)                     => s"$v"
    case DecValue(v)                     => s"$v"
    case Sum(Cofree(a, _), Cofree(b, _)) => s"($a + $b)"
    case Square(Cofree(a, history))      => ??? // TODO For (x²)²)² print x^6
  }

  println(expr.histo(smartPrint))
}
