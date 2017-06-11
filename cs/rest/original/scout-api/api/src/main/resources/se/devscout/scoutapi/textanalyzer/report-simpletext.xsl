<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="iso-8859-1"/>
    <xsl:template match="/report">
        <xsl:apply-templates select="activities"/>
    </xsl:template>

    <xsl:template match="activities">
        <xsl:value-of select="name"/>
        <xsl:text>&#0010;</xsl:text>
        <xsl:for-each select="relations">
            <xsl:variable name="padding"
                          select="string('                                                  ')"/>
            <xsl:value-of select="concat('   ', substring(concat(name, substring($padding, 1, 50-string-length(name))), 1, 50))"/>
<!--
            <xsl:for-each select="comparatorValues/v">
                <xsl:value-of select="concat('   ', text())"/>
            </xsl:for-each>
-->
            <xsl:text>&#0010;</xsl:text>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>