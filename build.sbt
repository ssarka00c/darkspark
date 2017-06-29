name := "Muvins"

version := "1.0"

scalaVersion := "2.11.10"


libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.0.0-RC2"

// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.3" % "test"


resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies += "com.github.melrief" %% "purecsv" % "0.0.9"