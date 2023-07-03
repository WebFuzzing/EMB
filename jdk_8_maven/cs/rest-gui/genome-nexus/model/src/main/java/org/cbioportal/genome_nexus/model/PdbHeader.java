package org.cbioportal.genome_nexus.model;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class PdbHeader
{
    private String pdbId;
    private String title;
    private Map<String, Object> compound;
    private Map<String, Object> source;

    public String getPdbId()
    {
        return pdbId;
    }

    public void setPdbId(String pdbId)
    {
        this.pdbId = pdbId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Map<String, Object> getCompound()
    {
        return compound;
    }

    public void setCompound(Map<String, Object> compound)
    {
        this.compound = compound;
    }

    public Map<String, Object> getSource()
    {
        return source;
    }

    public void setSource(Map<String, Object> source)
    {
        this.source = source;
    }
}
