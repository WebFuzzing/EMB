/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.messagebird;

import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.MessageBirdException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import com.messagebird.objects.ErrorReport;
import io.micronaut.http.HttpStatus;
import org.signal.registration.sender.IllegalSenderArgumentException;
import org.signal.registration.sender.SenderException;
import org.signal.registration.sender.SenderRejectedRequestException;
import org.signal.registration.util.CompletionExceptions;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MessageBirdExceptions {

  private static final Set<Integer> REJECTED_REQUEST_ERROR_CODES = Set.of(
      2,  // Request not allowed
      20, // Not found
      101 // Duplicate entry
  );

  private static final Set<Integer> ILLEGAL_ARGUMENT_ERROR_CODES = Set.of(
      9,  // Missing params
      10, // Invalid params
      21  // Bad request
  );

  private static final Set<HttpStatus> REJECTED_REQUEST_HTTP_CODES = Set.of(
      HttpStatus.UNPROCESSABLE_ENTITY,
      HttpStatus.TOO_MANY_REQUESTS
  );

  /**
   * Attempts to wrap a MessageBird {@link MessageBirdException} in a more specific exception type. If the given
   * exception does not have a classifiable error code, then the original exception is returned.
   *
   * @param e the MessageBirdException to wrap in a more specific exception type
   *
   * @return the potentially-wrapped throwable
   */
  public static Throwable toSenderException(final MessageBirdException e) {
    // First check for any messagebird specific api errors we are interested in
    final List<ErrorReport> errorsReports = errorReports(e);
    if (errorsReports.stream().map(ErrorReport::getCode).anyMatch(ILLEGAL_ARGUMENT_ERROR_CODES::contains)) {
      return new IllegalSenderArgumentException(e);
    }
    if (errorsReports.stream().map(ErrorReport::getCode).anyMatch(REJECTED_REQUEST_ERROR_CODES::contains)) {
      return new SenderRejectedRequestException(e);
    }

    // Then check for http error codes
    final Optional<HttpStatus> httpError = httpError(e);

    return httpError
        .filter(REJECTED_REQUEST_HTTP_CODES::contains)
        .map(ignored -> (Throwable) new SenderRejectedRequestException(e))
        .orElse(e);

  }

  public static @Nullable String extract(@NotNull Throwable throwable) {
    throwable = CompletionExceptions.unwrap(throwable);
    throwable = unwrap(throwable);

    final List<ErrorReport> errorsReports = errorReports(throwable);
    if (!errorsReports.isEmpty()) {
      return String.valueOf(errorsReports.get(0).getCode());
    }

    final Optional<HttpStatus> httpError = httpError(throwable);
    if (httpError.isPresent()) {
      return String.valueOf(httpError.get().getCode());
    }
    if (throwable instanceof NotFoundException) {
      return "notFound";
    }
    if (throwable instanceof UnauthorizedException) {
      return "unauthorized";
    }
    return null;
  }

  /**
   * If throwable is a SenderException, unwrap and get the cause
   */
  private static Throwable unwrap(Throwable throwable) {
    while (throwable instanceof SenderException e && throwable.getCause() != null) {
      throwable = e.getCause();
    }
    return throwable;
  }

  private static Optional<HttpStatus> httpError(final Throwable throwable) {
    if (throwable instanceof GeneralException generalException && generalException.getResponseCode() != null) {
      try {
        return Optional.of(HttpStatus.valueOf(generalException.getResponseCode()));
      } catch (IllegalArgumentException e) {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  private static List<ErrorReport> errorReports(final Throwable throwable) {
    if (throwable instanceof MessageBirdException mbException && mbException.getErrors() != null) {
      return mbException.getErrors();
    }
    return Collections.emptyList();
  }
}
