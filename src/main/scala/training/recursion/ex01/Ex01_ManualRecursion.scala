package training.recursion.ex01

// -------------------- the DSL --------------------
sealed trait Expr

case class IntValue(v: Int)           extends Expr
case class DecValue(v: Double)        extends Expr
case class Sum(a: Expr, b: Expr)      extends Expr
case class Multiply(a: Expr, b: Expr) extends Expr
case class Square(a: Expr)            extends Expr
case class Divide(a: Expr, b: Expr)   extends Expr
// -------------------------------------------------

object Ex01_ManualRecursion extends App {

  // it's not even tailrec :(
  def eval(e: Expr): Double =
    e match {
      case IntValue(v)      => v.toDouble
      case DecValue(v)      => v
      case Sum(e1, e2)      => eval(e1) + eval(e2) // would love to have case Sum(e1: Double, e2: Double) => e1 + e2
      case Multiply(e1, e2) => eval(e1) * eval(e2)
      case Square(e)        => eval(e) * eval(e)
      case Divide(e1, e2)   => eval(e1) / eval(e2)
    }

  def prettyPrint(e: Expr): String = ??? // TODO

  def optimize(e: Expr): Expr = ??? // TODO Multiply(x, x) -> Square(x)

  val expr1 = Sum(IntValue(3), Multiply(IntValue(5), DecValue(-2)))

  println(s"Evaluated $expr1: ${eval(expr1)}")
  println(s"Pretty-printed $expr1: ${prettyPrint(expr1)}")

}
