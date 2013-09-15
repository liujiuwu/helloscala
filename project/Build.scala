import sbt._
import Keys._

object Build extends Build {

  import BuildSettings._
  import Dependencies._

  override lazy val settings = super.settings :+ {
    shellPrompt := (s => Project.extract(s).currentProject.id + " > ")
  }

  ///////////////////////////////////////////////////////////////
  // root project
  ///////////////////////////////////////////////////////////////
  lazy val root = Project("root", file("."))
    .aggregate(
    sampleCommon, sampleSqueryl,
    appHelloscala,
    helloCommon, helloUtil, helloPersistence, helloLog)
    .settings(basicSettings: _*)
    .settings(noPublishing: _*)

  ///////////////////////////////////////////////////////////////
  // samples
  ///////////////////////////////////////////////////////////////
  lazy val sampleSqueryl = helloProject("sample-squeryl", file("sample/squeryl"))
    .dependsOn(sampleCommon)
    .settings(
    description := "Squeryl 示例程序",
    libraryDependencies ++= (
      compile(_postgresql) ++
        compile(_squeryl)))

  lazy val sampleCommon = helloProject("sample-common", file("sample/common"))
    .settings(
    description := "Squeryl 示例程序",
    libraryDependencies ++= (
      compile(_bouncycastle) ++
        compile(_jodaTime)))

  ///////////////////////////////////////////////////////////////
  // helloscala.com
  ///////////////////////////////////////////////////////////////
  lazy val appHelloscala = webProject("app-helloscala", file("app/helloscala"), 58080)
    .dependsOn(sampleCommon)
    .settings(
    description := "helloscala.com",
    libraryDependencies ++= (
      compile(_postgresql) ++
        compile(_squeryl)))

  ///////////////////////////////////////////////////////////////
  // hello projects
  ///////////////////////////////////////////////////////////////
  lazy val helloLog = helloProject("hello-log", file("core/log"))
    .dependsOn(helloPersistence, helloUtil, helloCommon)
    .settings(
    description := "羊八井平台-日志",
    libraryDependencies ++= (
      compile(_salat) ++
        compile(_casbah) ++
        compile(_postgresql) ++
        compile(_tomcatJdbc) ++
        compile(_squeryl)))

  lazy val helloPersistence = helloProject("hello-persistence", file("core/persistence"))
    .dependsOn(helloUtil, helloCommon)
    .settings(
    description := "羊八井平台-持久化",
    libraryDependencies ++= (
      provided(_salat) ++
        provided(_casbah) ++
        provided(_postgresql) ++
        provided(_tomcatJdbc) ++
        provided(_squeryl)))

  lazy val helloUtil = helloProject("hello-util", file("core/util"))
    .dependsOn(helloCommon)
    .settings(
    description := "羊八井平台-实用工具库",
    libraryDependencies ++= (
      compile(_apachePoi) ++
        compile(_commonsEmail) ++
        compile(_javaxMail) ++
        provided(_servlet30) ++
        compile(_salat) ++
        compile(_casbah) ++
        compile(_json4s) ++
        compile(_liftwebCommon)))

  lazy val helloCommon = helloProject("hello-common", file("core/common"))
    .settings(
    description := "羊八井平台-公共库",
    libraryDependencies ++= (
      compile(_bson) ++
        compile(_jodaTime) ++
        compile(_bouncycastle) ++
        compile(_scalaArm) ++
        compile(_akkaActor) ++
        compile(_slf4j) ++
        compile(_logback)))

  private def webProject(id: String, base: File, iPort: Int): Project = {
    import com.earldouglas.xsbtwebplugin.{WebPlugin, PluginKeys}
    helloProject(id, base)
      .settings(WebPlugin.webSettings: _*)
      .settings(
      PluginKeys.port in WebPlugin.container.Configuration := iPort,
      unmanagedResourceDirectories in Test <+= (baseDirectory) {
        _ / "src/main/webapp"
      },
      libraryDependencies ++= (container(_servlet30) ++ container(_jetty) ++ compile(_liftweb)))
  }

  private def helloProject(id: String, base: File): Project =
    Project(id, base)
      .settings(basicSettings: _*)
      .settings(
      resolvers ++= Seq(
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
        "spray repo" at "http://repo.spray.io",
        "spray nightlies repo" at "http://nightlies.spray.io",
        "release" at "http://mvn-adamgent.googlecode.com/svn/maven/release",
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases" at "http://oss.sonatype.org/content/repositories/releases"),
      libraryDependencies ++= (
        compile(_typesafeConfig) ++
          test(_scalatest)))

}

