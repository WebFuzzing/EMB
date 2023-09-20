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
public class ProjectController {
  private static final CacheControl CACHE = HTTP.sMaxAgePublicCache(Duration.ofMinutes(30));
  private final ProjectCollection projects;
  private final VersionFamilyCollection families;
  private final VersionCollection versions;

  @Autowired
  private ProjectController(
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
      schema = @Schema(implementation = ProjectResponse.class)
    ),
    responseCode = "200"
  )
  @GetMapping("/v2/projects/{project:[a-z]+}")
  @Operation(summary = "Gets information about a project.")
  public ResponseEntity<?> project(
    @Parameter(name = "project", description = "The project identifier.", example = "paper")
    @PathVariable("project")
    @Pattern(regexp = "[a-z]+") //
    final String projectName
  ) {
    final Project project = this.projects.findByName(projectName).orElseThrow(ProjectNotFound::new);
    final List<VersionFamily> families = this.families.findAllByProject(project._id());
    final List<Version> versions = this.versions.findAllByProject(project._id());
    return HTTP.cachedOk(ProjectResponse.from(project, families, versions), CACHE);
  }

  @Schema
  private record ProjectResponse(
    @Schema(name = "project_id", pattern = "[a-z]+", example = "paper")
    String project_id,
    @Schema(name = "project_name", example = "Paper")
    String project_name,
    @Schema(name = "version_groups")
    List<String> version_groups,
    @Schema(name = "versions")
    List<String> versions
  ) {
    static ProjectResponse from(final Project project, final List<VersionFamily> families, final List<Version> versions) {
      return new ProjectResponse(
        project.name(),
        project.friendlyName(),
        families.stream().sorted(VersionFamily.COMPARATOR).map(VersionFamily::name).toList(),
        versions.stream().sorted(Version.COMPARATOR).map(Version::name).toList()
      );
    }
  }
}
