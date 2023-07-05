/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.util;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtil {

  private UUIDUtil() {}

  public static ByteString uuidToByteString(final UUID uuid) {
    final ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    buffer.flip();

    return ByteString.copyFrom(buffer);
  }

  public static UUID uuidFromByteString(final ByteString byteString) {
    if (byteString.size() != 16) {
      throw new IllegalArgumentException("UUID byte string must be 16 bytes, but was actually " + byteString.size());
    }

    final ByteBuffer buffer = byteString.asReadOnlyByteBuffer();
    return new UUID(buffer.getLong(), buffer.getLong());
  }
}
