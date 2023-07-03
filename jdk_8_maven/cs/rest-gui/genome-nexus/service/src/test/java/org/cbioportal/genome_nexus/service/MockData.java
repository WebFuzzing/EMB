package org.cbioportal.genome_nexus.service;

import java.io.IOException;
import java.util.Map;

public interface MockData<T>
{
    Map<String, T> generateData() throws IOException;
}
