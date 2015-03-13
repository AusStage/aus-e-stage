## Introduction ##

In [MapInfo To KML](http://code.google.com/p/aus-e-stage/wiki/MapInfoToKML) article, we introduced the procedure to construct map overlays in a KML format using ABS geographic information. The popup infowindow of the map overlay includes the information about State Code, Local Government Code and Local Government Name. In this article, we will introduce how to add more information on the infowindow such as ABS Regional Profile URL, how to style infowindow using BalloonStyle template, HTML and CSS and how to highlight polygons.


## Adding Custom Data ##

`<ExtendedData>` element in KML provides the mechanism to associate untyped name/value pairs with a given feature (e.g. Placemark). In the following KML excerpt, **State\_Code** and **LGA\_Code** are attached to the Placemark through `<Data>` and `<value>` pairs. For more details about mechanisms to add custom data, please refer to [Google web site](http://code.google.com/apis/kml/documentation/extendeddata.html).
```
    <Placemark>
         <name>Adelaide (C)</name>
         <styleUrl>#sharedStyle</styleUrl>
         <ExtendedData>

            <Data name="State_Code">
               <value>4</value>
            </Data>

            <Data name="LGA_Code">
               <value>40070</value>
            </Data>

         </ExtendedData>
        <Polygon>…..</Polygon>
    </Placemark>

```

> `<KML excerpt 1>`

## Balloon Style ##

Descriptive balloons, so-called infowindows are attached to Placemarks in Google Earth and displayed when clicked. By default, a balloon in Google Earth only displays placemark `<name>`, `<description>` elements and links for driving direction. `<BalloonStyle>` element provides a mechanism to customise the balloon such as background color, text color etc. as shown in the following KML excerpt.

```
1 <style id=”sharedStyle”>
2   <BalloonStyle>
3   <!-- specific to BalloonStyle -->
4     <bgColor>ffffffff</bgColor>            <!-- kml:color -->
5     <textColor>ff000000</textColor>        <!-- kml:color --> 
6     <text>		                     <!-- string -->
7     <![CDATA[<html>
8     <head>
9       <link rel="stylesheet" type="text/css" href="style/infowindow-style.css" /> 
10      </head>
11      <body>
12        
13        <table class="infowindow">
14 
15         <tr class="odd">
16           <td class="label">State Code</td>
17           <td class="text">$[State_Code]</td>
18         </tr>
19
20         <tr class="even">
21           <td class="label">Link</td>
22           <td class="text"><a href="http://www.abs.gov.au/ausstats/abs@nrp.nsf/lookup/LGA$[LGA_Code]Main+Features12005-2009">ABS Profile</a></td>
23         </tr>
24       </table>
25     </body>
26     </html>]]>
27     </text>
28  </BalloonStyle>
29 </style>
```

> `<KML excerpt 2>`


As we can see from above KML excerpt, `<text>` element (line 6) contains the content displayed in the balloon. HTML code can be embedded inside it through the CDATA brackets (line 7). Therefore the content in `<text>` element can also be styled using external Cascading Style Sheets (CSS) through `<link>` element (line 9). In the above example, we use an external CSS file (style/infowindow-style.css) to format the table in HTML. For more details about `<BalloonStyle>`, please refer to [KML reference](http://code.google.com/apis/kml/documentation/kmlreference.html#balloonstyle).

## Entity Replacement ##

You may notice that there are two entities in the above `<text>` element. One is **`$[State_Code]`** (line 17), the other is **`$[LGA_Code]`** (line 22). These two entities refer to correspondent `<Data name=”State_Code”>` and `<Data name=”LGA_Code”>` elements in the `<ExtendedData>` of KML excerpt 1, or as shown in the following snippet. The contents of `<value>` elements will substitute those entities in the balloon. Google Earth supports such kind of entity replacement of certain extended data elements within the `<text>` element of `<BalloonStyle>`.

```
<Data name="State_Code">
   <value>4</value>
</Data>

<Data name="LGA_Code">
   <value>40070</value>
</Data>

```

After analysing the URLs of ABS regional profile for Local Government Area (LGA), we found that only LGA\_Code part is different, other parts are the same. For example, in the following URL,

`http://www.abs.gov.au/ausstats/abs@nrp.nsf/lookup/LGA`**40070**
`Main+Features12005-2009`,

40070 is the LGA Code which is different for each LGA. The parts before 40070 which is **`http://www.abs.gov.au/ausstats/abs@nrp.nsf/lookup/LGA`** and after it which is **Main+Features12005-2009** are fixed. So we can use entity **`$[LGA_Code]`** to replace 40070 as shown in the following URL.

`http://www.abs.gov.au/ausstats/abs@nrp.nsf/lookup/LGA`**`$[LGA_Code]`**
`Main+Features12005-2009`

When Google earth renders this KML, the value 40070 will replace the entity `$[LGA_Code]`. Since the #sharedStyle in KML excerpt 2 is attached to every Placemark using `<styleURL>` element and each Placemark represents one LGA, we can get the ABS profile URL for each LGA by replacing the `$[LGA_Code]` with the content of correspondent `<value>` element.


## Highlighted polygon when mouseover ##

`<StyleMap>` maps between two different styles through `<key>`/`<styleUrl>` pairs. It is used to provide normal and highlighted styles for a placemark (polygon in our case). When user mouses over a polygon, the highlighted style appears. To realize highlighted version, different fill colors can be set in `<PolyStyle>`. In the following KML excerpt, two styles (Highlight and Normal) are defined using `<Style>` element. They are linked through `<StyleMap>` element.

```
<Style id="Highlight">
      <PolyStyle>
         <color>ff21a5ff</color>
         <fill>true</fill>
      </PolyStyle>
      <BalloonStyle/><!-- style of infowindow (HTML, CSS), refer to KML excerpt 2 -->
</Style>

<Style id="Normal">
      <PolyStyle>
         <color>cf10e0ff</color>
         <fill>true</fill>
      </PolyStyle>
      <BalloonStyle/> <!--  style of infowindow (HTML, CSS), refer to KML excerpt 2 -->
</Style>

<StyleMap id="sharedStyle">
      <Pair>
         <key>normal</key>
         <styleUrl>#Normal</styleUrl>
      </Pair>

      <Pair>
         <key>highlight</key>
         <styleUrl>#Highlight</styleUrl>
      </Pair>
</StyleMap>
```

> `<KML excerpt 3>`


## XSL style sheet ##

Before applying above mentioned techniques, we need modify the KML (LGA.kml) that got in the [MapInfo To KML](http://code.google.com/p/aus-e-stage/wiki/MapInfoToKML) procedure. An XSL style sheet (place\_mark.xsl) is written to make the KML ready to be styled. It includes the following functions:

  * Extract name for each Placemark.
  * Set `<styleURL id=”sharedStyle”>` for each Placemark
  * Prepare `<Data>` / `<Value>` for each Placemark

After applying the XSL transformation we get dest.kml. Then we can copy the `<StyleMap id=”sharedStyle”>` in KML excerpt 3 and paste it to the `<Document>` element of dest.kml. It is worth to note that the id (sharedStyle) of `<StyleMap>` and `<styleURL>` elements should be same. The HTML and CSS in `<text>` element of `<BalloonStyle>` are designed in advance with suitable editors. The following snippet is the final KML skeleton.

```
<kml xmlns="http://www.opengis.net/kml/2.2">
   <Document>
      <open>1</open>

      <Style id="Highlight">
         <PolyStyle/> <!--  Set highlighted color here     -->
         <BalloonStyle>
           <text><![CDATA[
             <html>
               <head>
                <link rel="stylesheet" type="text/css" href="style/infowindow-style.css" /> 
               </head>
               <body> 
                     <!--  The content of infowindow.    -->
               </body>
             </html>]]>
           </text>
         </BalloonStyle>
      </Style>

      <Style id="Normal">
         <PolyStyle/>
         <BalloonStyle/> <!--  Same as above  -->
      </Style>
      
      <StyleMap id="sharedStyle">
         <Pair>
            <key>normal</key>
            <styleUrl>#Normal</styleUrl>
         </Pair>
      
         <Pair>
            <key>highlight</key>
            <styleUrl>#Highlight</styleUrl>
         </Pair>
      </StyleMap>

      <Folder>
         <name>South Australia</name>
         <open>1</open>
         <Placemark>
            <name>Adelaide (C)</name>
            <styleUrl>#sharedStyle</styleUrl>
            <ExtendedData>
               <Data name="STATE_CODE">
                  <value>4</value>
               </Data>
      
               <Data name="LGA_CODE">
                  <value>40070</value>
               </Data>
      
               <Data name="LGA_NAME">
                  <value>Adelaide (C)</value>
               </Data>
            </ExtendedData>
            <Polygon/> <!--   coordinates are here -->
       </Placemark>
	……
       <Placemark/>
   </Folder>
 </Document>
</kml>
```