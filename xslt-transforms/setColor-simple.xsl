<?xml version="1.0" encoding="utf-8"?>
<!-- Delete <kml> element from CD06aTASt-test-result.kml, rename it to CD06aTASt-test-result.xml
    Apply this style sheet to CD06aTASt-test-result.xml and get TAS-color.xml
    Add <kml> element to TAS-color.xml and get TAS-color.kml -->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0"
        encoding="utf-8" cdata-section-elements="description" indent="yes"/>
    
    <xsl:variable name="colorID">
        <xsl:value-of select="1"/>
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
                    <!--<color>ff000000</color>-->
                    <width>0</width>
                </LineStyle>
                <PolyStyle>
                    <color>b0336699</color>
                </PolyStyle>
            </Style>
           
            <Style id="color-2">
                <LineStyle>
                    <!--<color>ff000000</color>-->
                    <width>0</width>
                </LineStyle>
                <PolyStyle>
                    <color>b0663333</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-3">
                <LineStyle>
                    <!--<color>ff000000</color>-->
                    <width>0</width>
                </LineStyle>
                <PolyStyle>
                    <color>b0996633</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-4">
                <LineStyle>
                    <!--<color>ff000000</color>-->
                    <width>0</width>
                </LineStyle>
                <PolyStyle>
                    <color>b0ffcc66</color>
                </PolyStyle>
            </Style>
            
            <Style id="color-5">
                <LineStyle>
                    <!--<color>ff000000</color>-->
                    <width>0</width>
                </LineStyle>
                <PolyStyle>
                    <color>b0099969</color>
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
            <xsl:apply-templates select="name"/>
            <xsl:apply-templates select="description"/>
            <styleUrl>
                <xsl:choose>
                    <xsl:when test="((1 + count(preceding-sibling::Placemark)) mod 5)= '0'">
                        <xsl:value-of select="concat('color-', 5)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('color-', string((1 + count(preceding-sibling::Placemark)) mod 5))"/>
                    </xsl:otherwise>
                </xsl:choose>
            </styleUrl>
            
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

