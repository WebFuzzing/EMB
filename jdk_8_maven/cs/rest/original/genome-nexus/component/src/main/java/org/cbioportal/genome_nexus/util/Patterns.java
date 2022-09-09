package org.cbioportal.genome_nexus.util;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Patterns
{
    public static List<Pattern> toStartsWithPatternList(List<String> list)
    {
        return toStartsWithPatternList(list.stream());
    }

    public static List<Pattern> toStartsWithPatternList(String content)
    {
        return toStartsWithPatternList(Stream.of(content));
    }

    public static List<Pattern> toStartsWithPatternList(Stream<String> stream)
    {
        return stream.map(e -> Pattern.compile("^" + e + ".*")).collect(Collectors.toList());
    }
}
