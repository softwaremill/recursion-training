scalaVersion := "2.12.4"

name := "recursion-training"
organization := "com.softwaremill"
version := "1.0"

scalafmtVersion in ThisBuild := "1.4.0"

libraryDependencies ++= Seq(
  "com.slamdata" %% "matryoshka-core" % "0.18.3",
  "com.lihaoyi" % "ammonite" % "1.1.0" % "test" cross CrossVersion.full)

addCompilerPlugin("io.tryp" % "splain" % "0.2.10" cross CrossVersion.patch)

addCompilerPlugin("com.softwaremill.clippy" %% "plugin" % "0.5.3" classifier "bundle")

scalacOptions ++= Seq(
  "-deprecation",                   // Emit warning and location for usages of deprecated APIs.
  "-encoding", "UTF-8",             // Specify character encoding used by source files.
  "-explaintypes"  ,                // Explain type errors in more detail.
  "-language:existentials",         // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds",          // Allow higher-kinded types
  "-language:experimental.macros",  // Allow macro definition (besides implementation and application)
  "-language:implicitConversions",  // Allow definition of implicit functions called views
  "-language:postfixOps",           // Allow postfix operators
  "-Xfuture",                       // Turn on future language features.
  "-Yno-adapted-args",              // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",
  "-P:splain:all:true",
  "-P:clippy:colors=true")


scalacOptions ++= List("-P:splain:all:true", "-Ypartial-unification", "-P:clippy:colors=true")

// Ammonite
sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue