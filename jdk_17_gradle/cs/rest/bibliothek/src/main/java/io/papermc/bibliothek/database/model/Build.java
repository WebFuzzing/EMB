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
package io.papermc.bibliothek.database.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@CompoundIndex(def = "{'project': 1, 'version': 1}")
@CompoundIndex(def = "{'project': 1, 'version': 1, 'number': 1}")
@Document(collection = "builds")
public record Build(
  @Id ObjectId _id,
  ObjectId project,
  ObjectId version,
  int number,
  Instant time,
  List<Change> changes,
  Map<String, Download> downloads,
  @JsonProperty
  @Nullable Channel channel,
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable Boolean promoted
) {
  public Channel channelOrDefault() {
    return Objects.requireNonNullElse(this.channel(), Build.Channel.DEFAULT);
  }

  public boolean promotedOrDefault() {
    return Objects.requireNonNullElse(this.promoted(), false);
  }

  public enum Channel {
    @JsonProperty("default")
    DEFAULT,
    @JsonProperty("experimental")
    EXPERIMENTAL;
  }

  @Schema
  public record Change(
    @Schema(name = "commit")
    String commit,
    @Schema(name = "summary")
    String summary,
    @Schema(name = "message")
    String message
  ) {
  }

  @Schema
  public record Download(
    @Schema(name = "name", pattern = "[a-z0-9._-]+", example = "paper-1.18-10.jar")
    String name,
    @Schema(name = "sha256", pattern = "[a-f0-9]{64}", example = "f065e2d345d9d772d5cf2a1ce5c495c4cc56eb2fcd6820e82856485fa19414c8")
    String sha256
  ) {
    // NOTE: this pattern cannot contain any capturing groups
    @Language("RegExp")
    public static final String PATTERN = "[a-zA-Z0-9._-]+";
  }
}
