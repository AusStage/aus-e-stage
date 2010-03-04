<?xml version="1.0" encoding="UTF-8"?>
<!--
 /*
  * This file is part of the Aus-e-Stage Beta Root Website
  *
  * The Aus-e-Stage Beta Root Website is free software: you can redistribute
  * it and/or modify it under the terms of the GNU General Public License 
  * as published by the Free Software Foundation, either version 3 of the
  * License, or (at your option) any later version.
  *
  * The Aus-e-Stage Beta Root Website is distributed in the hope that it will 
  * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
  * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with the Aus-e-Stage Beta Root Website.  
  * If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:r="http://beta.ausstage.edu.au/xmlns/report" exclude-result-prefixes="r">
    
    <!-- output as html -->
    <xsl:output method="html"/>

    <!-- match elements in the default namespace -->
    <xsl:template match="r:report">
        <div class="report">
            <!-- add a id attribute to the containing div for report specific styling -->
            <xsl:attribute name="id">
                <xsl:value-of select="child::r:analysis_id"/>
            </xsl:attribute>
            <xsl:apply-templates select="child::r:title"/>
            <xsl:apply-templates select="child::r:section"/>
        </div>
    </xsl:template>

    <!-- match the title element -->
    <xsl:template match="r:title">
        <h1>
            <xsl:value-of select="."/>
        </h1>
        <div id="generated">
            <p>
                <xsl:value-of select="following::r:generated"/>
            </p>
        </div>
    </xsl:template>

    <!-- match the description element -->
    <!-- need to fix html entities after xslt processing -->
    <xsl:template match="r:description">
        <div id="description">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <!-- output the sections -->
    <xsl:template match="r:section">
        <div class="report_section">
            <!-- add a id attribute to the containing div for section specific styling -->
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <!-- output the section title -->
            <h2>
                <xsl:value-of select="child::r:title"/>
            </h2>
            <!-- output the div and img tag for a chart -->
            <xsl:apply-templates select="child::r:chart"/>
            <!-- output the content of the section -->
            <xsl:apply-templates select="child::r:content"/>
        </div>
    </xsl:template>

    <!-- template to output a content section -->
    <xsl:template match="r:content">
        <xsl:if test="normalize-space(.)">
            <!-- output the content of the section -->
            <div class="report_section_content">
                <xsl:value-of select="."/>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- template to output a chart -->
    <xsl:template match="r:chart">
        <xsl:if test="@href">
            <div class="report_section_chart">
                <img>
                    <xsl:attribute name="src">
                        <xsl:value-of select="@href"/>
                    </xsl:attribute>
                    <xsl:attribute name="width">
                        <xsl:value-of select="@width"/>
                    </xsl:attribute>
                    <xsl:attribute name="height">
                        <xsl:value-of select="@height"/>
                    </xsl:attribute>
                </img>
            </div>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
