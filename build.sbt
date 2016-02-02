name := "CC"

version := "1.0"

scalaVersion := "2.11.7"

lazy val versions = new {
  val mage =  "1.4.8"
  val finch = "0.9.2"
}

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.mage" % "mage" % versions.mage,
  "org.mage" % "mage-common" % versions.mage,
  "org.mage" % "mage-client" % versions.mage,
  "com.github.finagle" %% "finch-core" % versions.finch,
  "com.github.finagle" %% "finch-circe" % versions.finch,
  "io.circe" %% "circe-generic" % "0.2.1",
  "org.specs2" %% "specs2-core" % "3.6.6" % "test"
)
