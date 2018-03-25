package training.recursion.ex04

import scalaz.Scalaz._
import scalaz._

trait Ex04_Traverse {
  val traverseExpr: Traverse[Expr] = new Traverse[Expr] {

    override def traverseImpl[G[_], A, B](fa: Expr[A])(f: A => G[B])(implicit G: Applicative[G]): G[Expr[B]] =
      fa match {
        case IntValue(v)      => G.point(IntValue(v))
        case Sum(a1, a2)      => (f(a1) ⊛ f(a2))(Sum.apply)
        case Multiply(a1, a2) => (f(a1) ⊛ f(a2))(Multiply.apply)
      }
  }
}
