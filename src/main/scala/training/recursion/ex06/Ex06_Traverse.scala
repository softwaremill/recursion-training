package training.recursion.ex06

import scalaz._
import Scalaz._

trait Ex06_Traverse {
  // it's also a Functor[Expr]
  implicit val traverseExpr: Traverse[Expr] with Functor[Expr] = new Traverse[Expr] {

    override def traverseImpl[G[_], A, B](fa: Expr[A])(f: A => G[B])(implicit G: Applicative[G]): G[Expr[B]] =
      fa match {
        case IntValue(v)      => G.point(IntValue(v))
        case DecValue(v)      => G.point(DecValue(v))
        case Sum(a1, a2)      => (f(a1) ⊛ f(a2))(Sum.apply)
        case Square(a)        => f(a).map(Square.apply)
      }
  }
}
