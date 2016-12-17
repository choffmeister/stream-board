val akkaVersion = "2.4.14"
val akkaHttpVersion = "10.0.0"

lazy val root = Project("stream-board", file("."))
  .settings(
    organization := "de.choffmeister",
    version := com.typesafe.sbt.SbtGit.GitKeys.gitDescribedVersion.value.getOrElse("0.0.0-SNAPSHOT"),
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("-encoding", "utf8", "-deprecation", "-feature")
  )
  .settings(
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.bintrayRepo("choffmeister", "maven")
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
      "com.typesafe" % "config" % "1.3.0",
      "commons-codec" % "commons-codec" % "1.10",
      "de.choffmeister" %% "auth-common" % "0.3.0",
      "de.choffmeister" %% "microservice-utils" % "0.1.0",
      "io.spray" %% "spray-json" % "1.3.2",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.7",
      "org.apache.commons" % "commons-email" % "1.4",
      "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.6.2",
      "org.gnieh" %% "diffson" % "2.0.2",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test"
    )
  )
  .enablePlugins(DockerBuild.Plugins)
  .settings(DockerBuild.settings)
  .settings(webAppSettings: _*)
  .settings(mappings in Universal <<= (target, mappings in Universal, npmBuild) map { (target, mappings, web) =>
    val base = target / "web"

    def listFiles(cur: File): Seq[(File, String)] = IO.listFiles(cur).toSeq.flatMap {
      case dir if dir.isDirectory => (dir, IO.relativize(base, dir).get) +: listFiles(dir)
      case file => (file, IO.relativize(base, file).get) :: Nil
    }

    (mappings :+ (base, "web")) ++ listFiles(base).map(m => (m._1, "web/" + m._2))
  })
