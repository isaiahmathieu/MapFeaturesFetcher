
name := "MapFeaturesFetcher"
version := "0.1"
scalaVersion := "2.13.2"

// responsible for downloading nodejs and npm in order to use osmtogeojson tool
enablePlugins(FrontendPlugin)
// responsible for generating the bash and batch startup scripts
enablePlugins(JavaAppPackaging)
// responsible for generating the zip file containing the startup scripts, the project jar and all dependencies
// needed to run it
// todo is this still needed?
enablePlugins(UniversalPlugin)

import com.typesafe.sbt.packager.MappingsHelper
//import sbtfrontend.FrontendPlugin.autoImport.npm
// tells the UniversalPlugin to include the two nodejs-related folders in the zip package
mappings in Universal ++= MappingsHelper.directory(baseDirectory.value.absolutePath + "/node_modules")
mappings in Universal ++= MappingsHelper.directory(baseDirectory.value.absolutePath + "/.frontend")

// tells the JavaAppPackaging plugin to add jvm args that tell the program the location of node and osmtogeojson executables
// to the unix and windows startup scripts. apparently the variable app_home evaluates to a different path relative to
// the startup script on unix systems than APP_HOME evaluates to on windows
bashScriptExtraDefines += """addJava "-DnodeJsPath=${app_home}/../.frontend/node/node""""
bashScriptExtraDefines += """addJava "-DosmtogeojsonPath=${app_home}/../node_modules/osmtogeojson/osmtogeojson""""
batScriptExtraDefines += """call :add_java "-DnodeJsPath=%APP_HOME%\.frontend\node\node""""
batScriptExtraDefines += """call :add_java "-DosmToGeoJsonPath=%APP_HOME%\node_modules\osmtogeojson\osmtogeojson""""

lazy val root = (project in file("."))
  .settings(
    name := "MapFeaturesFetcher",
    // defines the dependencies for this project
    libraryDependencies += "commons-cli" % "commons-cli" % "1.4",
    libraryDependencies += "commons-io" % "commons-io" % "2.6",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.12",
  )

lazy val generateZip = taskKey[Unit](
"""downloads all dependencies, compiles MapFeaturesFetcher code, generates startup scripts, then creates
  |a zip file with all of these""".stripMargin)

generateZip := Def.sequential(
  (Compile / compile),
  nodeInstall,
  npm.toTask(" install osmtogeojson"),
  (Universal / packageBin)
).value
