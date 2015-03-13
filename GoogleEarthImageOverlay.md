## Introduction ##

In Google Earth, you can add image overlays over the Earth in the 3D viewer. If the image is a historical map, you can compare the historical maps to the state-of-the-art geographical content that appears in Google Earth. To create an image overlay, you need to specify the image file to display and its position. In the following section, we will introduce the image format requirements, how to position the image boundaries to the earth data beneath and how to save it as KML file.


## Overlay Image Format ##

The image format must be one of the following:
  * BMP
  * DDS
  * GIF
  * JPG
  * PGM
  * PNG
  * PPM
  * TGA
  * TIFF

Since the overlay feature in Google Earth is memory intensive, Google recommends keeping the image size smaller than 2000 x 2000 pixels to minimize the system strain.


## Creating an overlay ##

1.	Open Google Earth and position the 3D viewer in the location where you want to place the overlay image file.

Try to position the viewer so that it corresponds in viewing altitude to the overlay. If the overlay is of a detailed view, zoom into the subject area so that you don't have to make large adjustments later. By contrast, if the overlay covers a large area, make sure the entire area is encompassed in the 3D viewer with some margins for adjusting the imagery.

2.	Click **Add** > **Image Overlay** or click the highlighted button in the following image. The 'New Image Overlay' dialog box appears.
![http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/google-earth-button-bar.png](http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/google-earth-button-bar.png)

3.	Provide a descriptive name in the **Name** field.

4.	In the **Link** field, enter the location of the image file you want to use as an overlay or use the **Browse** button to locate it on your computer or network. The image appears in the 3D viewer, with anchor points that you use to position it.

5.	Specify the descriptive information for the overlay.

6.	Move the **Transparency slider** to the left to make the image a little transparent, which will assist you in placing the image in the correct location.

7.	Once you have inserted the overlay image into the viewer, you can use the green markers to stretch and move the image in a number of ways to get the most exact positioning required.

  * Use the **center cross-hair marker** to move the image overlay on the globe and position it in the right location.
  * Use the **triangle marker** at the left to rotate the image for better placement.
  * Use any of the **corner or edge anchors** to stretch or skew the selected corner or side. If you press the Shift key when selecting this marker, the image is scaled from the center.

8.	Click **OK** when you are finished. The map is now listed in the **Places** panel, and can be saved to a KML file.


## Exporting image overlay as KML/KMZ ##

Select the image overlay that you want to export in the **Places** panel, right click and select **Save Place As...** in the pop up window. The **Save file** window will open for you to specify the file name, select KML or KMZ as **Save as type** and select a folder on your computer where the file should be saved.

A KMZ archive is a collection of files used to create a single KML presentation. It includes all the local files that are referenced in the .kml file. In our case, it includes the image file and kml file for the overlay. Moreover, the data in a KMZ archive is compressed, so the archive is smaller than the original collection of files. Before you edit a KMZ archive, you have to change the file name from **.kmz** to **.zip** and unzip it. For more information about KMZ archive, please refer to [http://code.google.com/apis/kml/documentation/kmzarchives.html](http://code.google.com/apis/kml/documentation/kmzarchives.html).


## Setting a TimeSpan for the overlay ##

An overlay in KML can have time data associated with it which can restrict the visibility of the data to a given time period or point in time. The time slider in the Google Earth controls which parts of the data are visible. **TimeSpan** object can specify a _begin_ and _end_ time for a given overlay that transition instantly from one to the next.

To set a TimeSpan (from 1990 to 2000, for example) for an overlay, you can add the following red snippets in the KML file created in the above procedure. For more information about TimeSpan, please refer to [http://code.google.com/apis/kml/documentation/time.html](http://code.google.com/apis/kml/documentation/time.html).

```
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2" 
     xmlns:kml="http://www.opengis.net/kml/2.2">

<GroundOverlay>
	<name>Australia 1990 - 2000 </name>

	<TimeSpan>
           <begin>1990</begin>
           <end>2000</end>
        </TimeSpan>

	<Icon>
		<href>au_1990_2000.PNG</href>		
	</Icon>

	<LatLonBox>
		<north>3.611860048781208</north>
		<south>-54.94562083452288</south>
		<east>187.1711290053903</east>
		<west>94.29433524529206</west>
		<rotation>2.068405396262002</rotation>
	</LatLonBox>
</GroundOverlay>
</kml>


```