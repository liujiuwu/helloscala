package yangbajing.util.data

import scala.collection.mutable
import org.joda.time.DateTime

import yangbajing.common.A

case class AnyMetadata(
  name: String = "default",
  data: mutable.Map[String, Any] = mutable.Map[String, Any](),
  list: mutable.Map[String, MetaList[Any]] = mutable.Map[String, MetaList[Any]](),
  metadata: Option[MutableMetadata[Any]] = None) {

  def dataString(key: String): Option[String] =
    A orString data.get(key)

  def getDataString(key: String, deft: => String): String =
    dataString(key) getOrElse deft
}

case class MutableMetadata[V](
  name: String = "default",
  data: mutable.Map[String, V] = mutable.Map[String, V](),
  list: mutable.Map[String, MetaList[V]] = mutable.Map[String, MetaList[V]](),
  metadata: Option[MutableMetadata[V]] = None)

case class MutableMetadataStr(
  name: String = "default",
  data: mutable.Map[String, String] = mutable.Map[String, String](),
  list: mutable.Map[String, MetaListStr] = mutable.Map[String, MetaListStr](),
  createdAt: DateTime = new DateTime,
  metadata: Option[MutableMetadataStr] = None)

case class Metadata[V](
  name: String = "default",
  data: Map[String, V] = Map[String, V](),
  list: Map[String, MetaList[V]] = Map[String, MetaList[V]](),
  metadata: Option[Metadata[V]] = None)

trait TMutableMetadata[V, Self] {
  def self: Self

  def metadata: MutableMetadata[V]

  def putMetadata(key: String, value: V): Option[V] =
    metadata.data.put(key, value)

  def getMetadata(key: String): Option[V] =
    metadata.data.get(key)

  def getOrElseMetadata[B >: V](key: String, deft: B) =
    metadata.data.getOrElse(key, deft)
}

trait TMetadata[K, V] {
  def metadata: Metadata[V]
}
