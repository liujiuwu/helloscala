import sbt._
import Keys._
import com.earldouglas.xsbtwebplugin.PluginKeys.scanDirectories

object BuildSettings {
    
  lazy val basicSettings = Seq(
    version               := "0.1",
    homepage              := Some(new URL("http://helloscala.com/")),
    organization          := "com.helloscala",
    organizationHomepage  := Some(new URL("http://helloscala.com/")),
    startYear             := Some(2013),
    licenses              := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion          := "2.10.2",
    scalacOptions         := Seq(
      "-encoding", "utf8",
      "-target:jvm-1.7",
      "-unchecked",
      "-feature",
//      "-language:postfixOps",
//      "-language:implicitConversions",
      "-Xlog-reflective-calls",
      "-Ywarn-adapted-args",
      "-deprecation"
    ),
    javacOptions          := Seq(
      "-encoding", "utf8",
      "-Xlint:unchecked",
      "-Xlint:deprecation"
    ),
    scanDirectories in Compile := Nil,
    offline               := true
  )
  
  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := ()
  )

}

