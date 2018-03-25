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
  implicit val exprFunctor: Functor[Expr] = traverseExpr

  def int(i: Int): Fix[Expr] = IntValue[Fix[Expr]](i).embed

  // here: Expr[(Fix[Expr], String)] => String
  def algebra(srcAndExpr: (Expr[(Fix[Expr], String)])): String = srcAndExpr match {
    case IntValue(v) => v.toString
    case Sum((leftSrc, leftStr), (rightSrc, rightStr)) =>
      leftSrc.project match {
        case IntValue(a) =>
          rightSrc.project match {
            case IntValue(b) if a > 0 && b < 0 => s"($a - ${-b})"
            case IntValue(b) if b > 0 && a < 0 => s"($b - ${-a})"
            case _                             => s"$leftStr + $rightStr"
          }
        case _ => s"$leftStr + $rightStr"
      }
    case Multiply((leftSrc, leftStr), (rightSrc, rightStr)) => ??? // TODO print (a² * a) as a³
    case Square((_, str))                                   => s"$str²"
  }

  val expr: Fix[Expr] =
    Multiply(
      Square(Sum(int(-3), int(5)).embed).embed,
      Sum(int(-3), int(5)).embed,
    ).embed

  println(expr.para(algebra))
}
