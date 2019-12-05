// commentary builder

resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith", "maven")

libraryDependencies ++= Seq(
  "edu.holycross.shot.cite" %% "xcite" % "4.1.1",
  "edu.holycross.shot" %% "ohco2" % "10.16.0",
  "org.planet42" %% "laika-core" % "0.12.1",

  "org.wvlet.airframe" %% "airframe-log" % "19.8.10"
)
