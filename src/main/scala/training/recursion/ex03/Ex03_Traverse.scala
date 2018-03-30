package training.recursion.ex03

import scalaz._
import Scalaz._

trait Ex03_Traverse {
  // it's also a Functor[Expr]
  val traverseExpr: Traverse[Expr] = new Traverse[Expr] {

    override def traverseImpl[G[_], A, B](fa: Expr[A])(f: A => G[B])(implicit G: Applicative[G]): G[Expr[B]] =
      fa match {
        case IntValue(v)      => G.point(IntValue(v))
        case DecValue(v)      => G.point(DecValue(v))
        case Sum(a1, a2)      => (f(a1) ⊛ f(a2))(Sum.apply)
        case Multiply(a1, a2) => (f(a1) ⊛ f(a2))(Multiply.apply)
        case Divide(a1, a2)   => (f(a1) ⊛ f(a2))(Divide.apply)
        case Square(a)        => f(a) ∘ Square.apply
      }
  }
}
