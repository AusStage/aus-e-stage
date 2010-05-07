<?xml version="1.0" encoding="utf-8"?>
<!--Clean up the output of FME Workbench where the NeighborColorSetter transformer is applied 
    to simple geometry such as Polygon. The output has no color conflict. It includes the following steps:
    1) Delete <kml> and <ScreenOverlay> elements from kml file and rename it to *.xml
    2) Apply this style sheet to the xml file and get *.xml. It dose the following jobs: 
       a) replace <Style> with <styleURL> for each <Placemark>
       b) assign 5 colors (from color-6 to color-10) for each <MultiGeometry>       
    3) Add <kml> element to get kml file-->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0"
        encoding="utf-8" cdata-section-elements="description" indent="yes"/>
   
    <xsl:variable name="colorCounter">
        <xsl:value-of select="0"/>
    </xsl:variable>
      
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="Document">
        <xsl:element name="Document">
            <xsl:apply-templates select="Folder"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="Folder">
        <xsl:element name="Folder">
            
            <Style id="color-1">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>b0336699</color>
                </PolyStyle>
            </Style>
           
            <Style id="color-2">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>b0663333</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-3">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>b0996633</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-4">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>b0ffcc66</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-5">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>b0099969</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-6">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>870000ff</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-7">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>7f00ff00</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-8">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>DC0066FF</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-9">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>cccc0060</color>
                </PolyStyle>
            </Style>
            <Style id="color-10">
                <LineStyle>
                    <color>ff000000</color>
                    <!--<width>0</width>-->
                </LineStyle>
                <PolyStyle>
                    <color>ffffc7ae</color>
                </PolyStyle>
            </Style>
            
            
            <xsl:apply-templates select="name"/>
            <xsl:apply-templates select="Placemark"/>
        </xsl:element> 
    </xsl:template>
    
    <xsl:template match="Folder/name">
        <name>
            <xsl:value-of select="."/>
        </name>
    </xsl:template>
    
    <xsl:template match="Placemark">
        <Placemark>
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:apply-templates select="name"/>
            <xsl:apply-templates select="description"/>
            <xsl:if test="not(Style)">
                <styleUrl>
                    <xsl:choose>
                        <xsl:when test="((1 + count(preceding-sibling::Placemark)) mod 5)= 0">
                            <xsl:value-of select="concat('color-', 10)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat('color-', string((1 + count(preceding-sibling::Placemark)) mod 5 + 5))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </styleUrl>
            </xsl:if>
            <xsl:apply-templates select="Style"/>                      
            <xsl:apply-templates select="MultiGeometry"/>
            <xsl:apply-templates select="Polygon"/>
        </Placemark>
    </xsl:template>
    
    <xsl:template match="Placemark/name">
        <name>
            <xsl:value-of select="."/>
        </name>
    </xsl:template>
    
    <xsl:template match="description">
        <description>
            <xsl:value-of select="."/>                     
        </description>
    </xsl:template>
    
    <xsl:template match="Style">
        <styleUrl>
            <xsl:variable name="color">
                <xsl:value-of select="PolyStyle/color"/>
            </xsl:variable>    
            
            <xsl:choose>
                <xsl:when test="$color='b0336699'">color-1</xsl:when>
                <xsl:when test="$color='b0663333'">color-2</xsl:when>
                <xsl:when test="$color='b0996633'">color-3</xsl:when>
                <xsl:when test="$color='b0ffcc66'">color-4</xsl:when>
                <xsl:otherwise>color-5</xsl:otherwise>    
            </xsl:choose>                
        </styleUrl>
    </xsl:template>
    
    <xsl:template match="MultiGeometry">
        <MultiGeometry>
          <xsl:apply-templates select="Polygon"/>       
        </MultiGeometry>
    </xsl:template>
    
    <xsl:template match="Polygon">
        <Polygon>
            <outerBoundaryIs>
                <LinearRing>
                    <coordinates>
                        <xsl:value-of select="outerBoundaryIs/LinearRing/coordinates"/>
                    </coordinates>
                </LinearRing>                          
            </outerBoundaryIs>
        </Polygon>       
    </xsl:template>
    
</xsl:stylesheet>

