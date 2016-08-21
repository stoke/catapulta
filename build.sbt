lazy val l = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

resolvers += "Local Maven Repository" at "file:/"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
    Library.scalaTest % "test",
    "org.mage" % "mage-root" % "1.4.14",
    "org.mage" % "mage-client" % "1.4.14",
    "org.mage" % "mage-common" % "1.4.14",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5"
)

fork in run := true
fork in Test := true

cancelable in Global := true
