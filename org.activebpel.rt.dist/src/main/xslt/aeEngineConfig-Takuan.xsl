<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
   xmlns:xalan="http://xml.apache.org/xslt">

   <xsl:include href="aeEngineConfig-InMemory.xsl"/>

   <!-- Set the Description of the config. -->
   <xsl:template match="/config/entry[@name = 'Description']">
      <entry value="ActiveBPEL Takuan Configuration" name="Description"/>
   </xsl:template>

   <!-- Use full logging -->
   <xsl:template match="/config/entry[@name = 'Logging']">
     <xsl:copy>
       <xsl:copy-of select="@*"/>
       <xsl:attribute name="value">urn:ae:full</xsl:attribute>
     </xsl:copy>
   </xsl:template>
</xsl:stylesheet>
