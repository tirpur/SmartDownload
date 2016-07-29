name := "SmartDownload"

scalaVersion  :="2.11.4"

version :="1.0"


libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.11" % "0.11.3"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"


libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.1"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.53"

libraryDependencies += "commons-net" % "commons-net" % "3.5"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4"