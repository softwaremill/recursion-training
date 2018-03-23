package training.recursion.ex02

// -------------------- the DSL --------------------
sealed trait Expr[A]

case class IntValue[A](v: Int)     extends Expr[A]
case class DecValue[A](v: Double)  extends Expr[A]
case class Sum[A](a: A, b: A)      extends Expr[A]
case class Multiply[A](a: A, b: A) extends Expr[A]
case class Divide[A](a: A, b: A)   extends Expr[A]
// -------------------------------------------------

case class Fix[F[_]](unFix: F[Fix[F]])

object Ex02_FixedPoint extends App {

  // a set of rules
  def evalToDouble(exp: Expr[Double]): Double = exp match {
    case IntValue(v)      => v.toDouble
    case DecValue(v)      => v
    case Sum(d1, d2)      => d1 + d2
    case Multiply(d1, d2) => d1 * d2
    case Divide(d1, d2)   => d1 / d2
  }

  val intExpr = IntValue[Unit](10) // Expr[Unit]

  val sumExp: Expr[Expr[Unit]] =
    Sum(
      IntValue[Unit](10), // Expr[Unit]
      IntValue[Unit](5)
    )

  val division = // TODO type?
    Divide(DecValue(5.2), Sum(IntValue[Unit](10), IntValue[Unit](5)))

  val fixedIntExpr: Fix[Expr] = Fix(IntValue[Fix[Expr]](10))

  val fixedSum: Fix[Expr] = Fix(
    Sum(
      Fix(IntValue[Fix[Expr]](10)),
      Fix(DecValue[Fix[Expr]](5.5))
    )
  )

  val fixedDivision: Fix[Expr] = ??? // TODO fix the division expression to match type

}
