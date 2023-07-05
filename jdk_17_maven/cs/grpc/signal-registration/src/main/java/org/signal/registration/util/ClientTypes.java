/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

public class ClientTypes {

  public static org.signal.registration.rpc.ClientType getRpcClientTypeFromSenderClientType(
      final org.signal.registration.sender.ClientType senderClientType) {

    return switch (senderClientType) {

      case IOS -> org.signal.registration.rpc.ClientType.CLIENT_TYPE_IOS;
      case ANDROID_WITH_FCM -> org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITH_FCM;
      case ANDROID_WITHOUT_FCM -> org.signal.registration.rpc.ClientType.CLIENT_TYPE_ANDROID_WITHOUT_FCM;
      case UNKNOWN -> org.signal.registration.rpc.ClientType.CLIENT_TYPE_UNSPECIFIED;
    };
  }

  public static org.signal.registration.sender.ClientType getSenderClientTypeFromRpcClientType(
      final org.signal.registration.rpc.ClientType rpcClientType) {

    return switch (rpcClientType) {

      case CLIENT_TYPE_UNSPECIFIED, UNRECOGNIZED -> org.signal.registration.sender.ClientType.UNKNOWN;
      case CLIENT_TYPE_IOS -> org.signal.registration.sender.ClientType.IOS;
      case CLIENT_TYPE_ANDROID_WITH_FCM -> org.signal.registration.sender.ClientType.ANDROID_WITH_FCM;
      case CLIENT_TYPE_ANDROID_WITHOUT_FCM -> org.signal.registration.sender.ClientType.ANDROID_WITHOUT_FCM;
    };
  }
}
