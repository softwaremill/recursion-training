package training.recursion.ex08

import matryoshka._
import matryoshka.data._
import matryoshka.implicits._
import scalaz.{Scalaz, _}
import Scalaz._

// -------------------- the DSL --------------------
sealed trait CalcAction[A]

case class IntValue(v: Int) extends CalcAction[Int]

case class Sum(a: Int, b: Int) extends CalcAction[Int]

case class Square(a: Int) extends CalcAction[Int]

case class Draw(a: Int) extends CalcAction[Unit]

// -------------------------------------------------

object Ex08_Free extends App {

  val program: Free[CalcAction, Unit] = for {
    i1 <- Free.point[CalcAction, Int](5)
    i2 <- Free.point[CalcAction, Int](1)
    sum <- Free.liftF(Sum(i1, i2): CalcAction[Int])
    sq <- Free.liftF(Square(sum): CalcAction[Int])
    _ <- Free.liftF(Draw(sq): CalcAction[Unit])
  }
    yield ()


  val interpreter: ~>[CalcAction, Id] = ???

  program.foldMap(interpreter) // catamorphism
}
