val scala3Version = "3.0.0"

val AkkaVersion = "2.6.9"
val AkkaHttpVersion = "10.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sentry-retriever",
    mainClass in (Compile, run) := Some("Main"),
    mainClass in assembly := Some("Main"),
    version := "0.1.0",

    scalaVersion := scala3Version,
    //mitigate for under 3.0 project
    //https://github.com/lampepfl/dotty-example-project/blob/master/README.md#getting-your-project-to-compile-with-dotty
    scalacOptions ++= Seq(
      //"-source:3.0-migration"
    ),

    libraryDependencies ++= Seq(
      ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).withDottyCompat(scalaVersion.value),
      //( "com.typesafe.akka" %% "akka-actor" % AkkaVersion).withDottyCompat(scalaVersion.value),
      ("com.typesafe.akka" %% "akka-http" % AkkaHttpVersion).withDottyCompat(scalaVersion.value),

      // slack
      ("com.github.slack-scala-client" %% "slack-scala-client" % "0.2.10").withDottyCompat(scalaVersion.value),

      // scalikejdbc
      ("org.scalikejdbc" %% "scalikejdbc" % "3.5.0").withDottyCompat(scalaVersion.value),
      ("org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0").withDottyCompat(scalaVersion.value),

      // JDBC and etc
      "org.xerial" % "sqlite-jdbc" % "3.32.3.2",
      "org.apache.commons" % "commons-dbcp2" % "2.8.0",

      //circe
      ("io.circe" %% "circe-core" % "0.14.0-M1").withDottyCompat(scalaVersion.value),
      ("io.circe" %% "circe-generic" % "0.14.0-M1").withDottyCompat(scalaVersion.value),
      ("io.circe" %% "circe-parser" % "0.14.0-M1").withDottyCompat(scalaVersion.value),

      // logback
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.slf4j" % "slf4j-api" % "1.7.30",

// junit
      "com.novocode" % "junit-interface" % "0.11" % "test"
    )
  )

trapExit := false