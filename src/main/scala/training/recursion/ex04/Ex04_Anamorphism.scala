package training.recursion.ex04

import matryoshka._
import matryoshka.implicits._
import matryoshka.data._

import scalaz.Functor

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)     extends Expr[A]
case class Sum[A](a: A, b: A)      extends Expr[A]
case class Multiply[A](a: A, b: A) extends Expr[A]
// -------------------------------------------------

object Ex04_Anamorphism extends App with Ex04_Traverse {

  implicit val fun: Functor[Expr] = traverseExpr

  // Int => Expr[Int]
  val toBinary: Coalgebra[Expr, Int] = (n: Int) =>
    n match {
      case 0 => IntValue(0)
      case 1 => IntValue(1)
      case 2 => IntValue(2)
      case _ if n % 2 == 0 => Multiply(2, n / 2)
      case _ => Sum(1, n - 1)
  }

  val toText: Algebra[Expr, String] = {
    case IntValue(v)    => v.toString
    case Sum(a, b)      => s"($a + $b)"
    case Multiply(a, b) => s"($a * $b)"
  }

  val expr = 31.ana.apply[Fix[Expr]](toBinary)
  println(expr.cata(toText))
  println(31.hylo(toText, toBinary))
}
