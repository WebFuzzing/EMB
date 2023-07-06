/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import org.junit.jupiter.api.Test;
import org.signal.registration.sender.MessageTransport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTransportsTest {

  @Test
  void getRpcMessageTransportFromSenderTransport() {
    assertEquals(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS,
        MessageTransports.getRpcMessageTransportFromSenderTransport(
            org.signal.registration.sender.MessageTransport.SMS));

    assertEquals(org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_VOICE,
        MessageTransports.getRpcMessageTransportFromSenderTransport(
            MessageTransport.VOICE));
  }

  @Test
  void getSenderMessageTransportFromRpcTransport() {
    assertEquals(org.signal.registration.sender.MessageTransport.SMS,
        MessageTransports.getSenderMessageTransportFromRpcTransport(
            org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_SMS));

    assertEquals(org.signal.registration.sender.MessageTransport.VOICE,
        MessageTransports.getSenderMessageTransportFromRpcTransport(
            org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_VOICE));

    //noinspection ResultOfMethodCallIgnored
    assertThrows(IllegalArgumentException.class, () -> MessageTransports.getSenderMessageTransportFromRpcTransport(
        org.signal.registration.rpc.MessageTransport.MESSAGE_TRANSPORT_UNSPECIFIED));
  }
}
