package converter

import java.io.{File, PrintWriter}

import scala.io.Source

class FormatConverter {
  val OsmToGeoJsonToolPath = "/Users/admin/AppData/Roaming/npm/node_modules/osmtogeojson/osmtogeojson"

  def convertOsmXmlToGeoJson(dataToConvert: String): String = {
    // save the data to a temporary file
    val xmlTempFile = File.createTempFile("temp_osm_xml_",".xml")
    xmlTempFile.deleteOnExit()
    val xmlTempFilePath = xmlTempFile.getAbsoluteFile
    val geoJsonTempFile = File.createTempFile("temp_geojson_", ".geojson")
    geoJsonTempFile.deleteOnExit()
    val geoJsonTempFilePath = geoJsonTempFile.getAbsolutePath
    import java.io._
    val pw = new PrintWriter(xmlTempFile)
    pw.write(dataToConvert)
    pw.close
    println(333)
    import scala.sys.process._
    val cmd = s"""node "$OsmToGeoJsonToolPath" "$xmlTempFilePath" > "$geoJsonTempFilePath""""
    println(cmd)
    val output = cmd.!
    println("xml to geojson conversion output:")
    //println(output)
    val result = Source.fromFile(xmlTempFile).getLines().mkString

    xmlTempFile.delete()
    geoJsonTempFile.delete()
    result
  }

}

object FormatConverter {
  def main(args: Array[String]): Unit = {
    val converter = new FormatConverter
    converter.convertOsmXmlToGeoJson("oijojiojiojiojioij")
  }

}