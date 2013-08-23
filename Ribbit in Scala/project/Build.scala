import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "RibbitInScala"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
		"org.mindrot" % "jbcrypt" % "0.3m",
		"com.typesafe.slick" %% "slick" % "1.0.1",
		"com.h2database" % "h2" % "1.3.166"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
		// Add your own project settings here
    )

}
