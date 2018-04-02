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

  def int(i: Int): Fix[Expr] = IntValue[Fix[Expr]](i).embed

  val expr: Fix[Expr] = Sum(Square(Square(int(3)).embed).embed, Square(Square(Square(int(5)).embed).embed).embed).embed

  // ----------------------- histomorphism

  val smartPrint: GAlgebra[Cofree[Expr, ?], Expr, String] = {
    case IntValue(v)                     => s"$v"
    case DecValue(v)                     => s"$v"
    case Sum(Cofree(a, _), Cofree(b, _)) => s"($a + $b)"
    case Square(Cofree(a, history))      => "For (x²)² print x⁴" // ??? TODO
  }

  println(expr.histo(smartPrint))
}
