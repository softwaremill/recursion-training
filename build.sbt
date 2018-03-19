scalaVersion := "2.12.4"

name := "recursion-training"
organization := "com.softwaremill"
version := "1.0"

libraryDependencies += "com.slamdata" %% "matryoshka-core" % "0.18.3"

addCompilerPlugin("io.tryp" % "splain" % "0.2.8" cross CrossVersion.patch)

addCompilerPlugin("com.softwaremill.clippy" %% "plugin" % "0.5.3" classifier "bundle")

scalacOptions ++= List("-P:splain:all:true", "-Ypartial-unification", "-P:clippy:colors=true")