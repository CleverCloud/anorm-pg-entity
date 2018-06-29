package pgentity.automagic

import anorm._
import anorm.SqlParser._
import shapeless._
import shapeless.labelled._
import shapeless.ops.record._

import pgentity.automagic.ToHlist._

// Automatically derive RowParser[A] from a case class thanks to shapeless
// Generic support.
// WIP

object shape {

  // Transform a HList of FieldType[K,A] into a HList of Symbol => RowParser[A]
  trait FieldToParser[A <: HList] {
    type Out <: HList
    def apply(): Out
  }

  object FieldToParser {

    type Aux[A <: HList, Out0 <: HList] = FieldToParser[A] { type Out = Out0 }

    // This is needed to help scalac keep track of the real type
    // (else it shadows everything behind Out
    def apply[L <: HList](implicit fp: FieldToParser[L]): Aux[L, fp.Out] = fp

    implicit val hnilFieldToParser: Aux[HNil, HNil] = new FieldToParser[HNil] {
      type Out = HNil
      def apply(): HNil = HNil
    }

    implicit def hconsFieldToParser[K <: Symbol, A, B <: HList](
      implicit
      w: FieldToParser[B], // Transform the tail
      c: Column[A] // Make sure the type is parseable by anorm
    ): Aux[FieldType[K, A] :: B, (Symbol => RowParser[A]) :: w.Out] =
      new FieldToParser[FieldType[K, A] :: B] {
        type Out = (Symbol => RowParser[A]) :: w.Out
        def apply(): Out = { (x: Symbol) => get[A](x.name) } :: w()
      }
  }

  // ToDo extract the case class
  case class Book(name: String, author: String, id: Int, price: Double)
  val labelledGen = LabelledGeneric[Book]
  val gen = Generic[Book]
  // End ToDo

  val keys = Keys[labelledGen.Repr]

  val functionList = FieldToParser[labelledGen.Repr].apply()

  val parsers = functionList zipApply keys().unifySubtypes[Symbol]

  object composeParsers extends Poly2 {
    implicit def merge[A, B] = at[RowParser[A], RowParser[B]]((fp: RowParser[A], parseAcc: RowParser[B]) => fp ~ parseAcc)
  }

  // Since RowParser[A] does not have a meaningful zero, manually initialize
  // the accumulator with the head and fold over the tail
  val parser = parsers.tail.foldLeft(parsers.head)(composeParsers).map(x => gen.from(hlist(x)))
}
