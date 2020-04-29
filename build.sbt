name := "MapFeaturesFetcher"
version := "0.1"
scalaVersion := "2.13.2"

enablePlugins(FrontendPlugin)



lazy val mapFeaturesFetcher = (project in file("."))
  .settings(
    name := "MapFeaturesFetcher",
    libraryDependencies += "commons-cli" % "commons-cli" % "1.4",
    libraryDependencies += "commons-io" % "commons-io" % "2.6",
    libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.12",
  )
