package org.cbioportal.genome_nexus.util;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class designed to parse raw PDB file text content.
 *
 * @author Selcuk Onur Sumer
 */
@Component
public class PdbHeaderParser
{
    public PdbHeader convertToInstance(String plainText)
    {
        PdbHeader pdbHeader = null;

        if (plainText != null && plainText.length() > 0)
        {
            Map<String, String> content = this.parsePdbFile(plainText);

            pdbHeader = new PdbHeader();

            pdbHeader.setPdbId(this.parseId(content.get("header")));
            pdbHeader.setTitle(this.parseTitle(content.get("title")));
            pdbHeader.setCompound(this.parseCompound(content.get("compnd")));
            pdbHeader.setSource(this.parseCompound(content.get("source")));
        }

        return pdbHeader;
    }

    /**
     * Parses the raw PDB file retrieved from the server and
     * creates a mapping for each main identifier.
     *
     * @param rawInput  raw file contents as a String
     * @return  data mapped on the main identifier names
     */
    public Map<String, String> parsePdbFile(String rawInput)
    {
        String[] lines = rawInput.toLowerCase().split("\n");

        // count for distinct identifiers
        Map<String, Integer> countMap = new HashMap<String, Integer>();

        // map of builders to build content for each identifier
        Map<String, StringBuilder> contentMap = new HashMap<String, StringBuilder>();

        // actual content map to return
        Map<String, String> content = new HashMap<String, String>();

        for (String line: lines)
        {
            String[] tokens = line.split("[\\s]+");

            // first token is the identifier
            if (tokens.length == 0)
            {
                // empty line, just skip
                continue;
            }

            String identifier = tokens[0];

            // get the corresponding count
            Integer count = countMap.get(identifier);

            if (count == null)
            {
                count = 0;
            }

            // update count
            countMap.put(identifier, ++count);

            String str = line;

            // if there is more than one identifier lines,
            // than the line starts with the line number
            // we should get rid of the line number as well
            if (count > 1)
            {
                str = str.replaceFirst(count.toString(), "");
            }

            // get the corresponding string builder
            StringBuilder sb = contentMap.get(identifier);

            if (sb == null)
            {
                sb = new StringBuilder();
                contentMap.put(identifier, sb);
            }

            // get rid of the identifier itself
            str = str.replaceFirst(identifier, "").trim();

            // append the parsed line
            sb.append(str);
            sb.append("\n");
        }

        for (String identifier: contentMap.keySet())
        {
            String value = contentMap.get(identifier).toString().trim();
            content.put(identifier, value);
        }

        return content;
    }

    /**
     * Parses the raw compound/source content returned by the service,
     * and creates a nested map structure for each sub-field.
     *
     * @param rawInput  raw data from the service
     * @return  nested map structure
     */
    public Map<String, Object> parseCompound(String rawInput)
    {
        String[] lines = rawInput.split("\n");
        Map<String, Object> content = new HashMap<String, Object>();
        Map<String, Object> mol = null;

        // buffering lines (just in case if an entity consists of multiple lines)
        StringBuilder buffer = new StringBuilder();

        for (String line: lines)
        {
            buffer.append(line);

            // process the buffer if line end with a semicolon
            if (line.trim().endsWith(";"))
            {
                String[] tokens = buffer.toString().split(":");

                if (tokens.length > 1)
                {
                    String field = tokens[0].trim();
                    String value = tokens[1].trim();
                    // remove the semicolon
                    value = value.substring(0, value.length() - 1);
                    List<String> list = null;

                    // create a new mapping object for each mol_id
                    if (field.equals("mol_id"))
                    {
                        mol = new HashMap<String, Object>();
                        content.put(value, mol);
                    }
                    // convert comma separated chain and gene lists into an array
                    else if (field.equals("chain") ||
                        field.equals("gene"))
                    {
                        String[] values = value.split("[\\s,]+");
                        list = new LinkedList<>();
                        list.addAll(Arrays.asList(values));
                    }

                    // add the field for the current mol
                    if (mol != null)
                    {
                        if (list != null)
                        {
                            mol.put(field, list);
                        }
                        else
                        {
                            mol.put(field, value);
                        }
                    }
                }

                // reset buffer for the next entity
                buffer = new StringBuilder();
            }
            else
            {
                // add a whitespace before adding the next line
                buffer.append(" ");
            }
        }

        return content;
    }

    /**
     * Parses the raw PDB title content returned by the service,
     * and creates a human readable info string.
     *
     * @param rawTitle   data lines to process
     * @return  a human readable info string
     */
    public String parseTitle(String rawTitle)
    {
        String[] lines = rawTitle.split("\n");
        StringBuilder sb = new StringBuilder();

        for (String line: lines)
        {
            sb.append(line);

            // whether to add a space at the end or not
            if (!line.endsWith("-"))
            {
                sb.append(" ");
            }
        }

        return sb.toString().trim();
    }

    /**
     * Parses the raw PDB header content returned by the service,
     * and extracts the id.
     *
     * @param rawHeader   data line to process
     * @return  a human readable info string
     */
    public String parseId(String rawHeader)
    {
        String[] parts = rawHeader.split("\\s");
        String id = null;

        // assuming that the last item is always the id
        if (parts.length > 0)
        {
            id = parts[parts.length - 1];
        }

        if (id == null || id.length() == 0) {
            return null;
        }
        else {
            return id.trim().toLowerCase();
        }
    }
}
