/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClientTypesTest {

  @ParameterizedTest
  @MethodSource
  void getRpcClientTypeFromSenderClientType(final org.signal.registration.sender.ClientType senderClientType,
      final org.signal.registration.rpc.ClientType expectedRpcClientType) {

    assertEquals(expectedRpcClientType, ClientTypes.getRpcClientTypeFromSenderClientType(senderClientType));
  }

  private static Stream<Arguments> getRpcClientTypeFromSenderClientType() {
    return Stream.of(
        Arguments.of(org.signal.registration.sender.ClientType.IOS, org.signal.registration.rpc.ClientType.CLIENT_TYPE_IOS),
        Arguments.of(org.signal.registration.sender.ClientType.ANDROID_WITH_FCM, org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITH_FCM),
        Arguments.of(org.signal.registration.sender.ClientType.ANDROID_WITHOUT_FCM, org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITHOUT_FCM),
        Arguments.of(org.signal.registration.sender.ClientType.UNKNOWN, org.signal.registration.rpc.ClientType.CLIENT_TYPE_UNSPECIFIED)
    );
  }

  @ParameterizedTest
  @MethodSource
  void getSenderClientTypeFromRpcClientType(final org.signal.registration.rpc.ClientType rpcClientType,
      final org.signal.registration.sender.ClientType expectedSenderClientType) {

    assertEquals(expectedSenderClientType, ClientTypes.getSenderClientTypeFromRpcClientType(rpcClientType));
  }

  private static Stream<Arguments> getSenderClientTypeFromRpcClientType() {
    return Stream.of(
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_IOS, org.signal.registration.sender.ClientType.IOS),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITH_FCM, org.signal.registration.sender.ClientType.ANDROID_WITH_FCM),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITHOUT_FCM, org.signal.registration.sender.ClientType.ANDROID_WITHOUT_FCM),
        Arguments.of(org.signal.registration.rpc.ClientType.CLIENT_TYPE_UNSPECIFIED, org.signal.registration.sender.ClientType.UNKNOWN),
        Arguments.of(org.signal.registration.rpc.ClientType.UNRECOGNIZED, org.signal.registration.sender.ClientType.UNKNOWN)
    );
  }
}
