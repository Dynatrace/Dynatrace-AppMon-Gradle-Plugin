<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <title>dynaTrace Performance Report</title>
                <link rel="stylesheet" type="text/css" title="Style" href="stylesheet.css" />
            </head>
            <body>
                <h1> <b><span style="color:#A80014;">dyna</span>Trace</b>Performance Report</h1>
                <hr width="100%" />
                <xsl:for-each select="dashboardreport/data/*">
                    <h2>
                        <xsl:value-of select="@name" />
                    </h2>
                    <table class="details" border="0" cellpadding="5"
                        cellspacing="2" width="95%">
                        <tr bgcolor="white">
                            <xsl:for-each select="*[1]/layout/field">
                                <xsl:sort select="@order" data-type="number"/>
                                <th>
                                    <xsl:value-of select="@display" />
                                </th>
                            </xsl:for-each>
                        </tr>
                        <xsl:for-each select="*[1]/*[not(layout)]">
                            <xsl:variable name="record" select="." />
                            <tr bgcolor="white">
                                <xsl:for-each
                                    select="../layout/field">
                                    <xsl:sort select="@order" data-type="number"/>
                                    <xsl:variable name='name'
                                        select="@name" />
                                    <xsl:for-each select="$record">
                                        <xsl:for-each select=".">
                                            <xsl:choose>
                                                <xsl:when test="string(number(@*[name()=$name])) = 'NaN'">
                                                    <td>
                                                        <xsl:value-of
                                                            select="@*[name()=$name]" />
                                                    </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <td align='right'>
                                                        <xsl:value-of
                                                            select="format-number(@*[name()=$name],'0.##')"/>
                                                    </td>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:for-each>
                                    </xsl:for-each>
                                </xsl:for-each>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>