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
package io.papermc.bibliothek.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class HTTP {
  public static final String APPLICATION_JAVA_ARCHIVE_VALUE = "application/java-archive";
  public static final MediaType APPLICATION_JAVA_ARCHIVE = new MediaType("application", "java-archive");

  private HTTP() {
  }

  public static <T> ResponseEntity<T> cachedOk(final T response, final CacheControl cache) {
    return ResponseEntity.ok().cacheControl(cache).body(response);
  }

  public static CacheControl sMaxAgePublicCache(final Duration sMaxAge) {
    return CacheControl.empty()
      .cachePublic()
      .sMaxAge(sMaxAge);
  }

  public static ContentDisposition attachmentDisposition(final Path filename) {
    return ContentDisposition.attachment().filename(filename.getFileName().toString(), StandardCharsets.UTF_8).build();
  }
}
