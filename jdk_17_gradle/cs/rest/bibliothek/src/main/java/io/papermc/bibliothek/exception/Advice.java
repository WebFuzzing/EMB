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
package io.papermc.bibliothek.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@SuppressWarnings("checkstyle:FinalClass")
class Advice {
  private final ObjectMapper json;

  @Autowired
  private Advice(final ObjectMapper json) {
    this.json = json;
  }

  @ExceptionHandler(BuildNotFound.class)
  @ResponseBody
  public ResponseEntity<?> buildNotFound(final BuildNotFound exception) {
    return this.error(HttpStatus.NOT_FOUND, "Build not found.");
  }

  @ExceptionHandler(DownloadFailed.class)
  @ResponseBody
  public ResponseEntity<?> downloadFailed(final DownloadFailed exception) {
    return this.error(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred while serving your download.");
  }

  @ExceptionHandler(DownloadNotFound.class)
  @ResponseBody
  public ResponseEntity<?> downloadNotFound(final DownloadNotFound exception) {
    return this.error(HttpStatus.NOT_FOUND, "Download not found.");
  }

  @ExceptionHandler(ProjectNotFound.class)
  @ResponseBody
  public ResponseEntity<?> projectNotFound(final ProjectNotFound exception) {
    return this.error(HttpStatus.NOT_FOUND, "Project not found.");
  }

  @ExceptionHandler(VersionNotFound.class)
  @ResponseBody
  public ResponseEntity<?> versionNotFound(final VersionNotFound exception) {
    return this.error(HttpStatus.NOT_FOUND, "Version not found.");
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseBody
  public ResponseEntity<?> endpointNotFound(final NoHandlerFoundException exception) {
    return this.error(HttpStatus.NOT_FOUND, "Endpoint not found.");
  }

  private ResponseEntity<?> error(final HttpStatus status, final String error) {
    return new ResponseEntity<>(
      this.json.createObjectNode()
        .put("error", error),
      status
    );
  }
}
