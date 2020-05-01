# MapFeaturesFetcher
This tool downloads specified map feature data within some polygon boundary defined by latitude/longitude points. This data can be imported into a GIS program like [QGIS](https://qgis.org) for display or analysis. Currently the only data source it can download features from is [OpenStreetMap](https://www.openstreetmap.org/) via the [Overpass API](https://wiki.openstreetmap.org/wiki/Overpass_API). This tool was designed for downloading features relevant to hiking, but it can be easily modified to download any feature that is part of OpenStreetMap. The code is structured so that it shouldn't be too difficult to modify it to download data from other sources. 


#System Requirements
This tool works on Linux, Mac, and Windows. [SBT (Scala Build Tool)](https://www.scala-sbt.org/) and Java 8 (or higher) must be installed in order to build and package the app. Only Java 8 (or higher) is needed to run it once packaged.
#Getting the code running
clone this git repo, `cd` into the project, then run the following:
```
sbt generateZip
```
This command will generate a zip file at 
```
{project root}/target/universal/mapfeaturesfetcher-0.1.zip
```

Unzip this file. I've never successfully unzipped this file with the built-in extraction tool on Windows 10. I use [7 zip](https://www.7-zip.org/) instead.

`cd` into the directory that is created from the zip file, then use the appropriate startup script to run the tool (bash script in this example):
```
./bin/mapfeaturesfetcher \
--output_folder \
/home/person/featureDataDestination  \
--boundary_coordinates_file \
/home/person/boundaryPolygonCoordinatesFile \
--features \
"rivers trails peaks lakes" \
--output_format \
geojson \
--overpass_endpoint \
https://overpass.kumi.systems/api/interpreter \
--data_source \
osm
```
#Usage Notes
run `./bin/mapfeaturesfetcher --help` to print usage

An example of a valid boundary coordinates file is a text file containing the following line. The format is `latitude1 longitude1 latitude2 longitude2`. I use [this tool](http://apps.headwallphotonics.com/) to get the latitude/longitude coordinates of a polygon on a map. 
```
47.6405097 -121.3802642 47.5186916 -121.3775176 47.4871488 -121.1928099 47.6666482 -121.1722106 47.6869906 -121.3054198
```

The startup script sets two JVM properties `nodeJsPath` and `osmtogeojsonPath`, which tell the tool the location of the nodejs executable and the [osmtogeojson](https://github.com/tyrasd/osmtogeojson) module that are packaged in the zip file, which are used for converting map feature data to geojson format. You can have the tool use a different nodejs or osmtogeojson executable by specifying a location with the command line options `--nodejs_path <path>` and 
`--osmtogeojson_path <path>`. This is all irrelevant if `--output_format` is not `geojson`

#Build and packaging info
The command `sbt generateZip` does the following:
1. downloads code dependencies of MapFeaturesFetcher
1. compiles MapFeaturesFetcher code and creates a jar of these class files
1. installs NodeJS inside the project directory
1. installs the NodeJS module [osmtogeojson](https://github.com/tyrasd/osmtogeojson), which is used by MapFeaturesFetcher to convert [OSM XML](https://wiki.openstreetmap.org/wiki/OSM_XML) to [geojson](https://geojson.org/) format
1. generates a startup script for Linux/Mac and a startup script for Windows
1. creates a zip file containing all of the above, which can be moved to any location that java is installed and run.

#Inspiration
Hiking map apps lack the controls that I desire for displaying map features. For example, trails and other relevant features disappear when you zoom out, and it's not possible to change the color of lakes so that they stand out better (useful if you want to plan a hike to an alpine lake). While traveling in 2019 I learned to use [QGIS](qgis.org) so that I could create custom maps of locations of interest. To keep my coding skills sharp I wrote a python script to download specific features within some boundary so I could import these to QGIS. During covid-19 pandemic I decided to turn this script into something more extensible and easy for others to use, so I re-wrote it in Scala and added SBT configurations so it can easily be packaged and run on any platform. 
#Future Plans
Eventually I would like to use this tool to create a plugin for QGIS that lets users draw a polygon on the map and display the features of interest within the polygon boundary.
