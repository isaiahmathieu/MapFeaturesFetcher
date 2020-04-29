package constant

// todo should these be Enumerations?
object Features extends Enumeration {
  type Features = Value
  val trails, lakes, peaks, rivers, viewpoints, tracks, roads = Value
}

object OutputFormats extends Enumeration {
  type OutputFormats = Value
  val xml, json, geojson, csv = Value
}

object DataSources extends Enumeration {
  type DataSources = Value
  val osm = Value
}
