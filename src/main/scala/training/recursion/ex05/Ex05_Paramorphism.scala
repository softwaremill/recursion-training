package training.recursion.ex05

import matryoshka.data.Fix
import matryoshka._
import matryoshka.implicits._
import scalaz._
import Scalaz._

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)     extends Expr[A]
case class Sum[A](a: A, b: A)      extends Expr[A]
case class Multiply[A](a: A, b: A) extends Expr[A]
case class Square[A](a: A)         extends Expr[A]
// -------------------------------------------------

object Ex05_Paramorphism extends App with Ex05_Traverse {

  // handy utility functions if you want to build expressions by hand
  def int(i: Int): Fix[Expr]                          = IntValue[Fix[Expr]](i).embed
  def sum(a: Int, b: Int): Fix[Expr]                  = Sum(int(a), int(b)).embed
  def multiply(a: Fix[Expr], b: Fix[Expr]): Fix[Expr] = Multiply(a, b).embed
  def square(e: Fix[Expr]): Fix[Expr]                 = Square(e).embed

  // here: Expr[(Fix[Expr], String)] => String
  def algebra(srcAndExpr: (Expr[(Fix[Expr], String)])): String = srcAndExpr match {
    case IntValue(v) => v.toString
    case Sum((leftExpr, leftStr), (rightExpr, rightStr)) =>
      leftExpr.project match {
        case IntValue(a) =>
          rightExpr.project match {
            case IntValue(b) if a > 0 && b < 0 => s"($a - ${-b})"
            case IntValue(b) if b > 0 && a < 0 => s"($b - ${-a})"
            case _                             => s"($leftStr + $rightStr)"
          }
        case _ => s"$leftStr + $rightStr"
      }
    case Multiply((leftExpr, leftStr), (rightExpr, rightStr)) => ??? // TODO print (a² * a) as a³
    case Square((_, str))                                     => s"$str²"
  }

  val expr: Fix[Expr] =
    multiply(
      square(sum(-3, 5)),
      sum(-3, 5)
    )

  println(expr.para(algebra))
}
