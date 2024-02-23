ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.1"
ThisBuild / organization := "com.roshan"
ThisBuild / scalacOptions ++= Seq(
  "-Xlint:unused",
  "-Wconf:any:warning-verbose",
  "-Wconf:src=src_managed/.*:info-summary",
  "-Wconf:src=src_managed/.*&cat=unused:silent"
)

lazy val versions = new {
  val lambdaCore = "1.2.1"
  val lambdaEvents = "3.2.0"
  val zio = "2.0.2"
  val zioJson = "0.2.0-M2"
  val zioSchema = "0.4.2"
  val zioHttp = "3.0.0-RC2"
  val config = "3.0.7"
  val  scalatest = "3.2.17"
  val ibmMqClient = "9.1.1.0"
  val jms = "1.1"
  val zioFtp = "0.4.1"
}


lazy val root = (project in file("."))
  .settings(
    name := "SubscriptionModel",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % versions.zio,
      "dev.zio" %% "zio-json" % versions.zioJson,
      "dev.zio" %% "zio-schema" % versions.zioSchema,
      "dev.zio" %% "zio-schema-json" % versions.zioSchema,
      "dev.zio" %% "zio-http" % versions.zioHttp,
      "dev.zio" %% "zio-config" % versions.config,
      "org.scalatest" %% "scalatest" % versions.scalatest % Test,
      "com.ibm.mq" % "com.ibm.mq.allclient" % versions.ibmMqClient,
      "javax.jms" % "jms" % versions.jms,
      "dev.zio" %% "zio-streams" % versions.zio,
      "dev.zio" %% "zio-test" % versions.zio,
      "dev.zio" %% "zio-test-sbt" % versions.zio,
      "dev.zio" %% "zio-test-junit" % versions.zio,
      //"dev.zio" %% "zio-logging" % versions.zio,
      "dev.zio" %% "zio-ftp" % versions.zioFtp,



    ),
    autoCompilerPlugins := true,
    resolvers += "aws-sdk-java".at("https://repo.maven.apache.org/maven2/")
  )

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
