// Code shamelessly copy-pasted from this gist:
// https://gist.github.com/jto/7eb761fc3a0a85116aea
// Courtesy of @skaalf

package pgentity.automagic

import shapeless._
import shapeless.ops.hlist._
import record._
import syntax.singleton._
import ops.record._

import anorm._
import anorm.SqlParser._

trait ToHlist[S] {
  type Out <: HList
  def apply(s: S): Out
}

trait LowPriorityToHlist {
  implicit def toHlist0[A, B] = new ToHlist[A ~ B] {
    type Out = A :: B :: HNil
    def apply(s: A ~ B): Out = s._1 :: s._2 :: HNil
  }
}

object ToHlist extends LowPriorityToHlist {
  type Aux[A, O] = ToHlist[A] { type Out = O }

  implicit def toHlistN[A, B, O <: HList](implicit u: ToHlist.Aux[A, O], p: Prepend[O, B :: HNil]) = new ToHlist[A ~ B] {
    type Out = p.Out
    def apply(s: A ~ B): Out = p(u(s._1), s._2 :: HNil)
  }

  def hlist[A, B](p: A ~ B)(implicit u: ToHlist[A ~ B]) = u(p)
}

