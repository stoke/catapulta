name := "CC"

version := "1.0"

scalaVersion := "2.11.7"

//lazy val mageCore = ProjectRef(id = "mage", base = file("../Mage"))
//lazy val mageCommon = ProjectRef(id = "mage-common", base = file("../Mage.Common"))

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.mage" % "mage" % "1.4.6",
  "org.mage" % "mage-common" % "1.4.6",
  "org.mage" % "mage-client" % "1.4.6",
  "com.github.finagle" %% "finch-core" % "0.9.2",
  "com.github.finagle" %% "finch-circe" % "0.9.2",
  "io.circe" %% "circe-generic" % "0.2.1"
)
