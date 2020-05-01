# MapFeaturesFetcher
This tool downloads specific map feature data within some polygon boundary. This data can be imported into a GIS program like QGIS for display or analysis. Currently the only data source it can download features from is OpenStreetMaps via the Overpass API. This tool was designed for downloading features relevant to hiking, but it can be easily modified to download any feature that is part of OpenStreetMaps

This tool started out as one large python script 

Eventually I would like to use this package to create a QGIS plugin that lets users draw a polygon on a map in QGIS and then download and display these features on the map. 

#Usage