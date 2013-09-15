package yangbajing.persistence

import org.squeryl.customtypes.CustomTypesMode._
import org.squeryl.customtypes._
/*
trait Domain[A] {
  self: CustomType =>

  def label: String

  def validate(a: A): Unit

  def value: A

  validate(value)
}

class Age(v: Int) extends IntField(v) with Domain[Int] {
  def validate(a: Int) = assert(a > 0, "age must be positive, got " + a)

  def label = "age"
}

class FirstName(v: String) extends StringField(v) with Domain[String] {
  def validate(s: String) = assert(s.length <= 50, "first name is waaaay to long : " + s)

  def label = "first name"
}

class WeightInKilograms(v: Double) extends DoubleField(v) with Domain[Double] {
  def validate(d: Double) = assert(d > 0, "weight must be positive, got " + d)

  def label = "weight (in kilograms)"
}

class Patient(val firstName: FirstName, val age: Age, val weight: WeightInKilograms)
*/
//val heavyWeights = from (patients) (p => where (p.weight > 250) select (p) )
