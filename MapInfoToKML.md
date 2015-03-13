## Introduction ##

In order to display ABS statistical data in our mapping service, we need create map overlays in a KML format using ABS geographic information first. What we have is [Local Government Area ASGC Ed 2010 Digital Boundaries in MapInfo Interchange Format and ESRI Shapefile Format](http://www.abs.gov.au/AUSSTATS/abs@.nsf/DetailsPage/1259.0.30.001July%202010?OpenDocument) from the ABS website.

To construct an ABS overlay which can be used in Google Earth, we need to convert a MapInfo or ESRI Shapefile format into a KML format. [ogr2ogr](http://www.gdal.org/ogr2ogr.html) is used for this purpose, see the following figure. This is a command line utility used to convert and manipulate geospatial data from the [Geospatial Data Abstraction Library](http://www.gdal.org/index.html). The best way to get this utility in ready-to-use form is to download the latest [FWTools](http://fwtools.maptools.org/) kit. ![http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/ogr2ogr.png](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/ogr2ogr.png)


## Construction Procedure ##

**1.**	Download and install [FWtools 2.4.7 for Windows](http://home.gdal.org/fwtools/FWTools247.exe). Follow the instructions on page http://fwtools.maptools.org/windows-main.html. Open the **setfw.bat** and **openev.bat** scripts in the FWTools install directory to setup the environment variables appropriately.

To run ogr2ogr, select  **Start -> All Programs menu -> FWTools 2.4.7 -> FWTools Shell**. FWTools shell launches a DOS shell (cmd.exe) with the environment pre-initialized to use all the FWTools commands.


**2.**	Type the following command in the Dos shell mentioned above. Specify the directory as part of the file path if necessary.

> `ogr2ogr -f "KML" -where "state_code_2010 = 7" LGA-7-NT.kml LGA10aAust.mid`

This command will extract the local government areas from the Northern Territory into a file in the KML format. For more details please refer to the [Thoughts by Techxplorer](http://techxplorer.com/2010/06/08/converting-map-data-into-kml/) website.


**3.**	Open the resulting KML file (an overlay, named **state.kml**) in Google Earth, adjust the transparency of that overlay from completely transparent to fully opaque whenever it is selected in the 3D viewer by dragging the transparency slider under the Places panel.

In some cases, you will find that one or more polygons are not filled with white color. One of the reasons is that the polygon has too many coordinates (>34,000 coordinates limit) to be displayed correctly in Google earth. The following steps can solve this problem by breaking down the polygon with too many coordinate into several sub polygons in ArcMap.


**a)**    Examine the KML file and locate the polygons with problem, see the Figure [KML By State](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/kml-By-State.png)

**b)**	Extract the polygon with problem and create a KML file (**polygon.kml**) for it by adding a KML header element. Please see the Figure [Break Down Procedure](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/broken-down-procedure.png)  for the overview of the broken down procedure as described in the following steps.

**c)**	To edit the polygon features (break down the polygon) in ArcMap, we need an ESRI shapefile format file of that polygon. Type the following command in the FWTools Shell as mentioned in **step 2** to transform the KML file with the problem polygon into an ESRI shapefile format (**mydata.shp**). Specify the directory as part of the file path if necessary.

> ` ogr2ogr -f "ESRI Shapefile"  mydata.shp polygon.kml `


**d)**	Start ArcMap with a new empty map, add layers to the new map by selecting **mydata.shp**. Following the instructions about splitting polygon features on [ArcGIS Desktop 9.3 Help](http://webhelp.esri.com/arcgisdesktop/9.3/index.cfm?TopicName=Splitting_polygon_features) website to break down the polygon into several sub-polygons by drawing one or more sketches.

**e)**	Convert the shapefile which contains several sub-polygons into a KML file (**sub-polygons.kml**) by selecting **Conversion Tools -> To KML -> Layer To KML** in the ArcToolbox.

**f)**	Open the file **sub-polygons.kml** in Google Earth to make sure that all the polygons has been filled with colour.

> If not, continue **step d** to further break down the polygon.

**g)**	Manually edit the **state.kml** to replace the problem polygon with sub-polygons created in **step e**. See the Figure [Replace the Problem Polygon](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/replace-with-sub-polygons.png).