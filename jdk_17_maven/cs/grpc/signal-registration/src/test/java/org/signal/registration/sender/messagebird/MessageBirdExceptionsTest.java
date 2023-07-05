/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.messagebird;

import java.util.List;
import java.util.stream.Stream;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.objects.ErrorReport;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.signal.registration.sender.IllegalSenderArgumentException;
import org.signal.registration.sender.SenderException;
import org.signal.registration.sender.SenderRejectedRequestException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageBirdExceptionsTest {

  private static Stream<Arguments> selectException() {
    return Stream.of(
        Arguments.of(List.of(), HttpStatus.TOO_MANY_REQUESTS, SenderRejectedRequestException.class),
        Arguments.of(List.of(9999), HttpStatus.TOO_MANY_REQUESTS, SenderRejectedRequestException.class),
        Arguments.of(List.of(9), HttpStatus.OK, IllegalSenderArgumentException.class),
        Arguments.of(List.of(10), HttpStatus.OK, IllegalSenderArgumentException.class),
        Arguments.of(List.of(9, 10), HttpStatus.OK, IllegalSenderArgumentException.class),
        Arguments.of(List.of(2), HttpStatus.OK, SenderRejectedRequestException.class),
        Arguments.of(List.of(2, 9), HttpStatus.OK, IllegalSenderArgumentException.class),
        Arguments.of(List.of(9), HttpStatus.TOO_MANY_REQUESTS, IllegalSenderArgumentException.class),
        Arguments.of(List.of(), HttpStatus.I_AM_A_TEAPOT, GeneralException.class)
    );
  }

  @ParameterizedTest
  @MethodSource
  public void selectException(List<Integer> messageBirdErrors, HttpStatus status, Class<? extends Exception> expectedType) {
    final GeneralException ex = mock(GeneralException.class);
    when(ex.getErrors())
        .thenReturn(messageBirdErrors
            .stream()
            .map(i -> new ErrorReport(i, "", "", ""))
            .toList());

    when(ex.getResponseCode()).thenReturn(status.getCode());

    final Throwable throwable = MessageBirdExceptions.toSenderException(ex);
    assertTrue(expectedType.isInstance(throwable));
  }

}
