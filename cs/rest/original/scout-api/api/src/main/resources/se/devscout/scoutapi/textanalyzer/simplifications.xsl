<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
                <style>
                    body, table {
                    font-family: sans-serif;
                    font-size: 90%;
                    }
                    td {
                    vertical-align: top;
                    border: 1px solid #555;
                    padding: 0.2em;
                    }
                    thead {
                    font-weight: bold;
                    }
                    table {
                    border-collapse: collapse;
                    }
                    .relatedName {
                    padding-left: 1em;
                    white-space: nowrap;
                    }
                </style>
            </head>
            <body>
                <xsl:apply-templates select="metadata"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="metadata">
        <ul>
            <li>ignored words:
                <ul>
                    <xsl:for-each select="commonWords/v">
                        <xsl:sort select="text()"/>
                        <li>
                            <xsl:value-of select="text()"/>
                        </li>
                    </xsl:for-each>
                </ul>
            </li>
            <li>interchangable words:
                <ul>
                    <xsl:for-each select="translations/v">
                        <xsl:sort select="@to"/>
                        <li><xsl:value-of select="@to"/>:
                            <!--<xsl:choose>-->
                                <!--<xsl:when test="count(v) &gt; 5 or string-length(@to) &lt; 5">-->
                                    <ul>
                                        <xsl:for-each select="v">
                                            <xsl:sort select="text()"/>
                                            <li>
                                                <xsl:value-of select="text()"/>
                                            </li>
                                        </xsl:for-each>
                                    </ul>
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                    <!--<xsl:for-each select="v">-->
                                        <!--<xsl:value-of select="concat(text(), ' ')"/>-->
                                    <!--</xsl:for-each>-->
                                <!--</xsl:otherwise>-->
                            <!--</xsl:choose>-->
                        </li>
                    </xsl:for-each>
                </ul>
            </li>
        </ul>
    </xsl:template>
</xsl:stylesheet>