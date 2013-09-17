package yangbajing.util.data

import org.scalatest._

import PoiTypes._

class PoiHelperSpec extends FlatSpec with Matchers {

  "PoiHelper" should "create" in {
    val header = "11" -> "11" :: "22" -> "22" :: Nil

    val opts = PoiCode.opts
    val objOpts = PoiCode.objOpts

    println(opts)
    println(objOpts)

    val p = PoiCode.option("xls")
    println(p.getClass)
    p should be(Some(PoiCode.Xls))

    val opts2 = TestCode.opts
    val objOpts2 = TestCode.objOpts

    println(opts2)
    println(objOpts2)

    val t = TestCode.option("xls")
    println(t.getClass)
    t should be(Some(TestCode.Xls))

    p should be(t)

    //    val tables = Seq(
    //      Map("11" -> "11", "22" -> "22"),
    //      Map("11" -> "11", "22" -> "22"),
    //      Map("11" -> "11", "22" -> "22")
    //    )
    //
    //    val wb = PoiHelper.toXls(header, tables)
    //
    //    val out = new FileOutputStream("/tmp/111.xls")
    //    wb.write(out)
    //    out.close()
  }

}
