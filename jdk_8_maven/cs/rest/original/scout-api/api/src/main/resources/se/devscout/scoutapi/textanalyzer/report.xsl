<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/report">
        <html>
            <head>
                <style>
                    body, table {
                    font-family: sans-serif;
                    font-size: 90%;
                    }
                    td {
                    border: 1px solid #bbb;
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
                    strong {
                    font-weight: bold;
                    font-size: 125%
                    }
                </style>
            </head>
            <body>
                <table>
                    <thead>
                        <tr>
                            <td rowspan="2">
                                Activity
                                <br/>
                                <span class="relatedName">Related Activity</span>
                            </td>
                            <td colspan="{count(comparatorValuesLabels/v)}">Comparison Values</td>
                            <td rowspan="2">
                                Description
                            </td>
                        </tr>
                        <tr>
                            <xsl:for-each select="comparatorValuesLabels/v">
                                <td>
                                    <xsl:value-of select="text()"/>
                                </td>
                            </xsl:for-each>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="activities"/>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="activities">
        <xsl:for-each select="relations">
            <xsl:if test="position()=1">
                <tr>
                    <td colspan="{1+count(comparatorValues/v)}">
                        <strong>
                            <xsl:value-of select="../name"/>
                        </strong>
                    </td>
                    <td>
                        <xsl:value-of select="../description"/>
                    </td>
                </tr>
            </xsl:if>
            <tr>
                <td>
                    <span class="relatedName">
                        <xsl:value-of select="name"/>
                    </span>
                </td>
                <xsl:for-each select="comparatorValues/v">
                    <td>
                        <xsl:value-of select="text()"/>
                    </td>
                </xsl:for-each>
                <td>
                    <xsl:value-of select="description"/>
                </td>
            </tr>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>