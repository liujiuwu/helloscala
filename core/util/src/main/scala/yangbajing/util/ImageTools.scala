package yangbajing.util

import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader

import scala.util.Try

object ImageTools {

  def getImageReader(in: InputStream, format: String = "gif") = {
    val ir = ImageIO.getImageReadersByFormatName(format).next
    if (ir == null) throw new IllegalArgumentException("ImageReader is null")

    val iis = ImageIO.createImageInputStream(in)
    if (iis == null) throw new IllegalArgumentException("ImageInputStream is null")

    ir.setInput(iis, true)
    ir
  }

  def imageReader[R](in: InputStream, format: String = "gif")(func: ImageReader => R): Try[R] =
    try {
      scala.util.Success(imageReader_!(in, format)(func))
    } catch {
      case e: Exception =>
        scala.util.Failure(e)
    } finally {
      in.close
    }

  def imageReader_![R](in: InputStream, format: String = "gif")(func: ImageReader => R): R = {
    val reader = getImageReader(in, format)
    func(reader)
  }

  def getDpi(imageReader: ImageReader): (Int, Int) = {
    var xDpi = 72
    var yDpi = 72
    val meta = imageReader.getImageMetadata(0)
    var n = meta.getAsTree("javax_imageio_1.0")
    n = n.getFirstChild()
    while (n ne null) {
      if (n.getNodeName == "Dimension") {
        var n2 = n.getFirstChild()
        while (n2 ne null) {
          if (n2.getNodeName == "HorizontalPixelSize") {
            val nnm = n2.getAttributes()
            val n3 = nnm.item(0)
            val hps = n3.getNodeValue.toFloat
            xDpi = math.round(25.4f / hps)
          }
          if (n2.getNodeName == "VerticalPixelSize") {
            val nnm = n2.getAttributes()
            val n3 = nnm.item(0)
            val vps = n3.getNodeValue.toFloat
            yDpi = math.round(25.4f / vps)
          }
          n2 = n2.getNextSibling
        }
      }
      n = n.getNextSibling
    }

    (xDpi, yDpi)
  }

}
