import sbt._
import Keys._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._
import com.faacets.literator.plugin.LiteratorPlugin._

object FaacetsRootBuild extends Build {
  override def settings = super.settings ++ Seq(
    organization := "com.faacets",
    version := "0.1",
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    scalaVersion in ThisBuild := "2.10.3"
  )

  def standardSettings = Defaults.defaultSettings

  lazy val root = Project(id = "faacets-root",
    base = file(".")) aggregate(core, polyta, alasc)

  val assemblyCustomize = mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
    {
      case PathList("scala","reflect","api", xs @ _*) => MergeStrategy.first
      case x => old(x)
    }
  }

  lazy val core = Project(id = "core",
    base = file("core"), //com.github.retronym
    settings = standardSettings ++ literatorSettings ++ assemblySettings ++ Seq(assemblyCustomize)
  ) dependsOn(alasc, polyta)

  lazy val polyta = Project(id = "polyta",
    base = file("polyta")) dependsOn(alasc)

  lazy val alasc = Project(id = "alasc",
    base = file("alasc"), settings = standardSettings ++ literatorSettings)
}
