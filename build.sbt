// commentary builder

lazy val supportedScalaVersions = List("2.10.6", "2.11.8", "2.12.4")

resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")


name := "commentarybuilder"
organization := "edu.holycross.shot.cite"
version := "0.2.0"
licenses +=("GPL-3.0",url("https://opensource.org/licenses/gpl-3.0.html"))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.wvlet.airframe" %% "airframe-log" % "19.8.10",

  "edu.holycross.shot.cite" %% "xcite" % "4.1.1",
  "edu.holycross.shot" %% "ohco2" % "10.16.0",
  "org.planet42" %% "laika-core" % "0.12.1"
)
