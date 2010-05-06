<?xml version="1.0" encoding="utf-8"?>
<!-- Delete <kml> element from CD06aTASt.kml, rename it to CD06aTASt-test.xml
    Apply this style sheet to CD06aTASt-test.xml and get CD06aTASt-test-result.xml
    Add <kml> element to CD06aTASt-test-result.xml and get CD06aTASt-test-result.kml -->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0"
        encoding="utf-8" cdata-section-elements="description" indent="yes"/>

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
            <xsl:apply-templates select="MultiPolygon"/>
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
    
    <xsl:template match="MultiPolygon">
        <MultiGeometry>
          <xsl:apply-templates select="polygonMember"/>       
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
    
    <xsl:template match="polygonMember">
        <xsl:for-each select="Polygon">
            <Polygon>
                <outerBoundaryIs>
                    <LinearRing>
                        <coordinates>
                            <xsl:value-of select="outerBoundaryIs/LinearRing/coordinates"/>
                        </coordinates>
                    </LinearRing>                          
                </outerBoundaryIs>
            </Polygon>    
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>

