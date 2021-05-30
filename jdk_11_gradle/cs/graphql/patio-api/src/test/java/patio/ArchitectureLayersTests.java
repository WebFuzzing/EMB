/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

/**
 * BaseService that the architecture layer constraints are respected
 *
 * @since 0.1.0
 */
@AnalyzeClasses(packages = "patio..")
public class ArchitectureLayersTests {

  private static final String LAYER_PERSISTENCE = "Persistence";
  private static final String LAYER_SERVICE = "Service";
  private static final String LAYER_GRAPH_QL = "GraphQL";
  private static final String LAYER_UTILS = "Utils";
  private static final String LAYER_DOMAIN = "Domain";
  private static final String LAYER_INPUT = "Input";

  @ArchTest
  void testLayers(JavaClasses classes) {
    Architectures.LayeredArchitecture architecture =
        layeredArchitecture()
            // layer definition
            .layer(LAYER_DOMAIN)
            .definedBy("..domain")
            .layer(LAYER_INPUT)
            .definedBy("..domain.input..")
            .layer(LAYER_PERSISTENCE)
            .definedBy("..repositories..")
            .layer(LAYER_SERVICE)
            .definedBy("..services..")
            .layer(LAYER_GRAPH_QL)
            .definedBy("..graphql..")
            .layer(LAYER_UTILS)
            .definedBy("..utils..")

            // layer constraints
            .whereLayer(LAYER_PERSISTENCE)
            .mayOnlyBeAccessedByLayers(LAYER_SERVICE)
            .whereLayer(LAYER_SERVICE)
            .mayOnlyBeAccessedByLayers(LAYER_GRAPH_QL)
            .whereLayer(LAYER_INPUT)
            .mayOnlyBeAccessedByLayers(LAYER_GRAPH_QL, LAYER_SERVICE)
            .whereLayer(LAYER_DOMAIN)
            .mayOnlyBeAccessedByLayers(LAYER_GRAPH_QL, LAYER_SERVICE, LAYER_PERSISTENCE);

    architecture.check(classes);
  }
}
