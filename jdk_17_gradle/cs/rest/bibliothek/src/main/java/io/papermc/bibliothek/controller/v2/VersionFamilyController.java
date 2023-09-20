/*
 * This file is part of bibliothek, licensed under the MIT License.
 *
 * Copyright (c) 2019-2023 PaperMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.papermc.bibliothek.controller.v2;

import io.papermc.bibliothek.database.model.Project;
import io.papermc.bibliothek.database.model.Version;
import io.papermc.bibliothek.database.model.VersionFamily;
import io.papermc.bibliothek.database.repository.ProjectCollection;
import io.papermc.bibliothek.database.repository.VersionCollection;
import io.papermc.bibliothek.database.repository.VersionFamilyCollection;
import io.papermc.bibliothek.exception.ProjectNotFound;
import io.papermc.bibliothek.exception.VersionNotFound;
import io.papermc.bibliothek.util.HTTP;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings("checkstyle:FinalClass")
public class VersionFamilyController {
  private static final CacheControl CACHE = HTTP.sMaxAgePublicCache(Duration.ofMinutes(5));
  private final ProjectCollection projects;
  private final VersionFamilyCollection families;
  private final VersionCollection versions;

  @Autowired
  private VersionFamilyController(
    final ProjectCollection projects,
    final VersionFamilyCollection families,
    final VersionCollection versions
  ) {
    this.projects = projects;
    this.families = families;
    this.versions = versions;
  }

  @ApiResponse(
    content = @Content(
      schema = @Schema(implementation = VersionFamilyResponse.class)
    ),
    responseCode = "200"
  )
  @GetMapping("/v2/projects/{project:[a-z]+}/version_group/{family:" + Version.PATTERN + "}")
  @Operation(summary = "Gets information about a project's version group.")
  public ResponseEntity<?> family(
    @Parameter(name = "project", description = "The project identifier.", example = "paper")
    @PathVariable("project")
    @Pattern(regexp = "[a-z]+") //
    final String projectName,
    @Parameter(description = "The version group name.")
    @PathVariable("family")
    @Pattern(regexp = Version.PATTERN) //
    final String familyName
  ) {
    final Project project = this.projects.findByName(projectName).orElseThrow(ProjectNotFound::new);
    final VersionFamily family = this.families.findByProjectAndName(project._id(), familyName).orElseThrow(VersionNotFound::new);
    final List<Version> versions = this.versions.findAllByProjectAndGroup(project._id(), family._id());
    return HTTP.cachedOk(VersionFamilyResponse.from(project, family, versions), CACHE);
  }

  @Schema
  private record VersionFamilyResponse(
    @Schema(name = "project_id", pattern = "[a-z]+", example = "paper")
    String project_id,
    @Schema(name = "project_name", example = "Paper")
    String project_name,
    @Schema(name = "version_group", pattern = Version.PATTERN, example = "1.18")
    String version_group,
    @Schema(name = "versions")
    List<String> versions
  ) {
    static VersionFamilyResponse from(final Project project, final VersionFamily family, final List<Version> versions) {
      return new VersionFamilyResponse(
        project.name(),
        project.friendlyName(),
        family.name(),
        versions.stream().sorted(Version.COMPARATOR).map(Version::name).toList()
      );
    }
  }
}
