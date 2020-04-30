package constant

// todo figure out if there's a cleaner approach than using Enumerations here
object Features extends Enumeration {
  type Features = Value
  val trails, lakes, peaks, rivers, viewpoints, tracks, roads = Value
}

object OutputFormats extends Enumeration {
  type OutputFormats = Value
  val xml, json, geojson = Value
}

object DataSources extends Enumeration {
  type DataSources = Value
  val osm = Value
}
