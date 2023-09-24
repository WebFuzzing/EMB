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

import io.papermc.bibliothek.database.model.Build;
import io.papermc.bibliothek.database.model.Project;
import io.papermc.bibliothek.database.model.Version;
import io.papermc.bibliothek.database.model.VersionFamily;
import io.papermc.bibliothek.database.repository.BuildCollection;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
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
public class VersionFamilyBuildsController {
  private static final CacheControl CACHE = HTTP.sMaxAgePublicCache(Duration.ofMinutes(5));
  private final ProjectCollection projects;
  private final VersionFamilyCollection families;
  private final VersionCollection versions;
  private final BuildCollection builds;

  @Autowired
  private VersionFamilyBuildsController(
    final ProjectCollection projects,
    final VersionFamilyCollection families,
    final VersionCollection versions,
    final BuildCollection builds
  ) {
    this.projects = projects;
    this.families = families;
    this.versions = versions;
    this.builds = builds;
  }

  @ApiResponse(
    content = @Content(
      schema = @Schema(implementation = VersionFamilyBuildsResponse.class)
    ),
    responseCode = "200"
  )
  @GetMapping("/v2/projects/{project:[a-z]+}/version_group/{family:" + Version.PATTERN + "}/builds")
  @Operation(summary = "Gets all available builds for a project's version group.")
  public ResponseEntity<?> familyBuilds(
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
    final Map<ObjectId, Version> versions = this.versions.findAllByProjectAndGroup(project._id(), family._id()).stream()
      .collect(Collectors.toMap(Version::_id, Function.identity()));
    final List<Build> builds = this.builds.findAllByProjectAndVersionIn(project._id(), versions.keySet());
    return HTTP.cachedOk(VersionFamilyBuildsResponse.from(project, family, versions, builds), CACHE);
  }

  @Schema
  private record VersionFamilyBuildsResponse(
    @Schema(name = "project_id", pattern = "[a-z]+", example = "paper")
    String project_id,
    @Schema(name = "project_name", example = "Paper")
    String project_name,
    @Schema(name = "version_group", pattern = Version.PATTERN, example = "1.18")
    String version_group,
    @Schema(name = "versions")
    List<String> versions,
    @Schema(name = "builds")
    List<VersionFamilyBuild> builds
  ) {
    static VersionFamilyBuildsResponse from(final Project project, final VersionFamily family, final Map<ObjectId, Version> versions, final List<Build> builds) {
      return new VersionFamilyBuildsResponse(
        project.name(),
        project.friendlyName(),
        family.name(),
        versions.values().stream().sorted(Version.COMPARATOR).map(Version::name).toList(),
        builds.stream().map(build -> new VersionFamilyBuild(
          versions.get(build.version()).name(),
          build.number(),
          build.time(),
          build.channelOrDefault(),
          build.promotedOrDefault(),
          build.changes(),
          build.downloads()
        )).toList()
      );
    }

    @Schema
    public static record VersionFamilyBuild(
      @Schema(name = "version", pattern = Version.PATTERN, example = "1.18")
      String version,
      @Schema(name = "build", pattern = "\\d+", example = "10")
      int build,
      @Schema(name = "time")
      Instant time,
      @Schema(name = "channel")
      Build.Channel channel,
      @Schema(name = "promoted")
      boolean promoted,
      @Schema(name = "changes")
      List<Build.Change> changes,
      @Schema(name = "downloads")
      Map<String, Build.Download> downloads
    ) {
    }
  }
}
