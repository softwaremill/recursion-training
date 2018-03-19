scalaVersion := "2.12.4"

name := "recursion-training"
organization := "com.softwaremill"
version := "1.0"

scalafmtVersion in ThisBuild := "1.4.0"

libraryDependencies += "com.slamdata" %% "matryoshka-core" % "0.18.3"

addCompilerPlugin("io.tryp" % "splain" % "0.2.8" cross CrossVersion.patch)

addCompilerPlugin("com.softwaremill.clippy" %% "plugin" % "0.5.3" classifier "bundle")

scalacOptions ++= List("-P:splain:all:true", "-Ypartial-unification", "-P:clippy:colors=true")

// Ammonite

libraryDependencies += {
  val version = scalaBinaryVersion.value match {
    case "2.10" => "1.0.3"
    case _ ⇒ "1.0.5"
  }
  "com.lihaoyi" % "ammonite" % version % "test" cross CrossVersion.full
}

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue