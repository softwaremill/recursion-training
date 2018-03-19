package training.recursion

object Ex02_FixedPoint extends App {
  // -------------------- the DSL --------------------
  sealed trait Expr[A]

  case class IntValue[A](v: Int)     extends Expr[A]
  case class DecValue[A](v: Double)  extends Expr[A]
  case class Sum[A](a: A, b: A)      extends Expr[A]
  case class Multiply[A](a: A, b: A) extends Expr[A]
  case class Divide[A](a: A, b: A)   extends Expr[A]
  // -------------------------------------------------

  val exp1: Expr[Expr[Unit]] = Sum(IntValue[Unit](10), IntValue[Unit](5))

  val result = // TODO type?
    Divide(DecValue(5.2), Sum(IntValue[Unit](10), IntValue[Unit](5)))

  case class Fix[F[_]](unFix: F[Fix[F]])
}
