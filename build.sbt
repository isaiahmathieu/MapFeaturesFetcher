import com.typesafe.sbt.packager.MappingsHelper

name := "MapFeaturesFetcher"
version := "0.1"
scalaVersion := "2.13.2"

exportJars := true

//val configFileLocation = "./src/universal/conf/application.ini"

enablePlugins(FrontendPlugin)
enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)

mappings in Universal ++= MappingsHelper.directory(baseDirectory.value.absolutePath + "/node_modules")
mappings in Universal ++= MappingsHelper.directory(baseDirectory.value.absolutePath + "/.frontend")

bashScriptExtraDefines += """addJava "-DrelativePathToNode=${app_home}/../.frontend/node/node""""
bashScriptExtraDefines += """addJava "-DrelativePathToOsmtogeojson=${app_home}/../node_modules/osmtogeojson/osmtogeojson""""

batScriptExtraDefines += """call :add_java "-DrelativePathToNode=%APP_HOME%\.frontend\node\node""""
batScriptExtraDefines += """call :add_java "-DrelativePathToOsmtogeojson=%APP_HOME%\node_modules\osmtogeojson\osmtogeojson""""

lazy val root = (project in file("."))
  .settings(
    name := "MapFeaturesFetcher",
    libraryDependencies += "commons-cli" % "commons-cli" % "1.4",
    libraryDependencies += "commons-io" % "commons-io" % "2.6",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.12",
  )



//lazy val installNpmAndOsmtogeojson = (project in file("."))
//  .settings(
//    name := "installNpmAndOsmtogeojson",
//
//  )


//val myTask = taskKey[String]("installs osmtogeojson")
//myTask := baseDirectory.value.absolutePath

//lazy val createConfigFileTask = taskKey[Unit]("creates the application config file")


//createConfigFileTask := {
//  import java.nio.file.Files
//  import java.nio.file.Paths
//  import java.util.Properties
//  import java.io.FileOutputStream
//  val configFilePath = Paths.get(configFileLocation)
//  // make parent directories if they don't exist
//  configFilePath.getParent.toFile.mkdirs()
//  Files.deleteIfExists(configFilePath)
//  Files.createFile(configFilePath)
//  val props = new Properties
//  props.setProperty("relativePathToNode", Paths.get("../.frontend/node/node").toString)
//  props.setProperty("relativePathToOsmgeojson", Paths.get("../node_modules/osmtogeojson/osmtogeojson").toString)
//  val fos = new FileOutputStream(configFilePath.toFile)
//  props.store(fos,"")
//  fos.close()
//}

//lazy val tmpTask1 = taskKey[Unit]("tmptask1")
//tmpTask1 := {
//  println(FrontendKeys.npmVersion.value)
//}
