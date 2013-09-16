import sbt._
import Keys._

object Dependencies {
  def compile(dep: ModuleID): Seq[ModuleID] = compile(Seq(dep))

  def compile(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "compile")

  def provided(dep: ModuleID): Seq[ModuleID] = provided(Seq(dep))

  def provided(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "provided")

  def test(dep: ModuleID): Seq[ModuleID] = test(Seq(dep))

  def test(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "test")

  def runtime(dep: ModuleID): Seq[ModuleID] = runtime(Seq(dep))

  def runtime(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "runtime")

  def container(dep: ModuleID): Seq[ModuleID] = container(Seq(dep))

  def container(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "container")

  val liftVersion = "2.5.1"

  val _liftwebCommon = "net.liftweb" %% "lift-webkit" % liftVersion

  val _liftweb = Seq(
    //"net.liftmodules" %% "textile" % (liftVersion + "-1.3"),
    //"net.liftweb" %% "lift-squeryl-record" % liftVersion,
    "net.liftweb" %% "lift-webkit" % liftVersion)

  val _scalaArm = "com.jsuereth" %% "scala-arm" % "1.3"

  val akkaVersion = "2.2.1"

  val _akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val _akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val _akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val _akkaTestkit = Seq(
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion)

  val _scalatest = Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0.M7"
  )

  val scalaIoVersion = "0.4.2"
  val _scalaIo = Seq(
    "com.github.scala-incubator.io" %% "scala-io-core" % scalaIoVersion,
    "com.github.scala-incubator.io" %% "scala-io-file" % scalaIoVersion)

  val squerylVersion = "0.9.6-RC1"
  val _squeryl = "org.squeryl" %% "squeryl" % squerylVersion

  val _typesafeConfig = "com.typesafe" % "config" % "1.0.1"

  val slickVersion = "2.0.0-M1"
  val _slick = Seq(
    "com.typesafe.slick" %% "slick" % slickVersion)

  val salatVersion = "1.9.2"
  val _salat = "com.novus" %% "salat" % salatVersion

  val _nscalaTime = "com.github.nscala-time" %% "nscala-time" % "0.6.0"

  val _casbah = "org.mongodb" %% "casbah" % "2.6.2"
 
  // 使用scala编写的markdown解析器
  val _actuarius = "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6"

  val sprayVersion = "1.2-20130912"

  val _sprayIo =  "io.spray" % "spray-io" % sprayVersion

  val _sprayClient = "io.spray" % "spray-client" % sprayVersion

  val _sprayCan = "io.spray" % "spray-can" % sprayVersion

  val _sprayJson = "io.spray" %% "spray-json" % "1.2.5"

  val json4sVersion = "3.2.5"
  val _json4s = Seq(
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.json4s" %% "json4s-ext" % json4sVersion)

  val _sfreechart = "com.github.wookietreiber.sfreechart" %% "sfreechart" % "latest.integration"

  //val jettyVersion = "8.1.12.v20130726"
  val jettyVersion = "9.0.5.v20130815"

  val _jetty = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion

  val _servlet30 = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar")

  val ___servlet30 = ("org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar")) % "provided"

  val _tomcatJdbc = "org.apache.tomcat" % "tomcat-jdbc" % "7.0.42"

  val _bson = "org.mongodb" % "bson" % "2.11.2"

  val _netty = "io.netty" % "netty" % "3.6.6.Final"

  val _ehcache = "net.sf.ehcache" % "ehcache-core" % "2.7.2"

  val _slf4j = "org.slf4j" % "slf4j-api" % "1.7.5"

  val _logback = "ch.qos.logback" % "logback-classic" % "1.0.13"

  val _rhino = "org.mozilla" % "rhino" % "1.7R4"

  val _googleHashMap = "com.googlecode.concurrentlinkedhashmap" % "concurrentlinkedhashmap-lru" % "1.4"

  val _guava = "com.google.guava" % "guava" % "14.0.1"

  val _pegdown = "org.pegdown" % "pegdown" % "1.4.1"

  val _bouncycastle = "org.bouncycastle" % "bcprov-ext-jdk15on" % "1.49"

  val _jodaTime = Seq(
    "joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.4")

  val _quartz = "org.quartz-scheduler" % "quartz" % "2.1.7"

  val _itextpdf = Seq(
    "com.itextpdf" % "itext-asian" % "5.2.0",
    "com.itextpdf" % "itextpdf" % "5.4.3")

  val _xmlworker = "com.itextpdf.tool" % "xmlworker" % "5.4.3"

  val _javaxMail = "javax.mail" % "mail" % "1.5.0-b01"

  val _javaxActivation = "javax.activation" % "activation" % "1.1.1"

  val _javaxJta = "javax.transaction" % "jta" % "1.1"

  val _commonsEmail = "org.apache.commons" % "commons-email" % "1.3.1"

  val _jfreechart = "org.jfree" % "jfreechart" % "1.0.15"

  val _httpClient = "org.apache.httpcomponents" % "httpclient" % "4.2.5"

  val apachePoiVersion = "3.9"
  val _apachePoi = Seq(
    "org.apache.poi" % "poi" % apachePoiVersion,
    "org.apache.poi" % "poi-scratchpad" % apachePoiVersion,
    "org.apache.poi" % "poi-ooxml" % apachePoiVersion,
    "org.apache.poi" % "poi-ooxml-schemas" % apachePoiVersion)

  val _axis = Seq(
    "org.apache.axis" % "axis" % "1.4",
    "axis" % "axis-saaj" % "1.4",
    "axis" % "axis-wsdl4j" % "1.5.1",
    "commons-logging" % "commons-logging" % "1.1.1",
    "commons-discovery" % "commons-discovery" % "20040218.194635",
    "axis" % "axis-jaxrpc" % "1.4")

  val _commonsFileupload = "commons-fileupload" % "commons-fileupload" % "1.3"

  val _h2 = "com.h2database" % "h2" % "1.3.173"

  val _mysql = "mysql" % "mysql-connector-java" % "5.1.26"

  //val _postgresql = "postgresql" % "postgresql" % "9.1-901.jdbc4"
  val _postgresql = "postgresql" % "postgresql" % "9.2-1002.jdbc4"

}

