package training.recursion.ex07

import scalaz.Scalaz._
import scalaz._
import matryoshka.data.Fix
import matryoshka.implicits._

trait Ex07_Traverse {
  // it's also a Functor[Expr]
  implicit val traverseExpr: Traverse[Expr] with Functor[Expr] = new Traverse[Expr] {

    override def traverseImpl[G[_], A, B](fa: Expr[A])(f: A => G[B])(implicit G: Applicative[G]): G[Expr[B]] =
      fa match {
        case IntValue(v) => G.point(IntValue(v))
        case DecValue(v) => G.point(DecValue(v))
        case Sum(a1, a2) => (f(a1) ⊛ f(a2))(Sum.apply)
        case Square(a)   => f(a).map(Square.apply)
      }
  }

  def int(i: Int): Fix[Expr]                     = IntValue[Fix[Expr]](i).embed
  def dec(d: Double): Fix[Expr]                  = DecValue[Fix[Expr]](d).embed
  def sum(a: Int, b: Int): Fix[Expr]             = Sum(int(a), int(b)).embed
  def sum(a: Fix[Expr], b: Fix[Expr]): Fix[Expr] = Sum(a, b).embed
  def square(e: Fix[Expr]): Fix[Expr]            = Square(e).embed

}
