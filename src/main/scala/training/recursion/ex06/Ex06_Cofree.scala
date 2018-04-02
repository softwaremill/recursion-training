package training.recursion.ex06

import matryoshka.data._
import matryoshka._
import matryoshka.implicits._
import matryoshka.patterns._
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

object Ex06_Cofree extends App with Ex06_Traverse {

  def int(i: Int): Fix[Expr] = IntValue[Fix[Expr]](i).embed

  // ---------- labelling expressions with Cofree

  val inferType: Algebra[Expr, Cofree[Expr, ExprType]] = {
    case IntValue(v) => Cofree.apply(IntExpr, IntValue(v))
    case DecValue(v) => Cofree.apply(DecExpr, DecValue(v))
    case Sum(a, b)   => Cofree.apply(IntExpr, IntValue(10000)) // TODO
    case Square(a)   => Cofree.apply(IntExpr, IntValue(10000))
  }

  val expr: Fix[Expr] = Sum(Square(Square(int(3)).embed).embed, Square(Square(Square(int(5)).embed).embed).embed).embed

  val typedExpr: Cofree[Expr, ExprType] = expr.cata(inferType)

  val toTypedStr: Algebra[EnvT[ExprType, Expr, ?], String] = {
    case EnvT((exprType, IntValue(v))) => s"($v: $exprType)"
    case EnvT((exprType, DecValue(v))) => s"($v: $exprType)"
    case EnvT((exprType, Sum(a, b)))   => s"($a + $b): $exprType"
    case EnvT((exprType, Square(a)))   => s"($a^2): $exprType"
  }

  println(typedExpr.cata(toTypedStr))
}
