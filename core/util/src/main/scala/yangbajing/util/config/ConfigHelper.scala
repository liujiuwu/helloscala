package yangbajing.util.config

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

import org.json4s.native._
import org.json4s.JValue

trait ConfigHelper {

  def jsonConfig(file: String): JValue =
    jsonConfig(new File(file))

  def jsonConfig(file: File): JValue =
    jsonConfig(new FileInputStream(file))

  def jsonConfig(in: InputStream): JValue =
    JsonParser.parse(new InputStreamReader(in, "UTF-8"))


//  def saveJsonConfig[T <: AnyRef](config: T, out: OutputStream) {
//    Serialization.writePretty(config, new PrintWriter(out))
//  }

}
