/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.metrics;

import org.signal.registration.rpc.ClientType;
import org.signal.registration.rpc.MessageTransport;

public class MetricsUtil {

  private static final String METRIC_NAME_PREFIX = "registration";

  public static final String CLIENT_TYPE_TAG_NAME = "clientType";
  public static final String COUNTRY_CODE_TAG_NAME = "countryCode";
  public static final String REGION_CODE_TAG_NAME = "regionCode";
  public static final String SENDER_TAG_NAME = "sender";
  public static final String SUCCESS_TAG_NAME = "success";
  public static final String TRANSPORT_TAG_NAME = "transport";
  public static final String VERIFIED_TAG_NAME = "verified";

  /**
   * Returns a qualified name for a metric contained within the given class.
   *
   * @param clazz the class that contains the metric
   * @param metricName the name of the metrics
   *
   * @return a qualified name for the given metric
   */
  public static String name(final Class<?> clazz, final String metricName) {
    return METRIC_NAME_PREFIX + "." + clazz.getSimpleName() + "." + metricName;
  }

  public static String getMessageTransportTagValue(final MessageTransport messageTransport) {
    return switch (messageTransport) {
      case MESSAGE_TRANSPORT_SMS -> "sms";
      case MESSAGE_TRANSPORT_VOICE -> "voice";
      case MESSAGE_TRANSPORT_UNSPECIFIED, UNRECOGNIZED -> "unrecognized";
    };
  }

  public static String getClientTypeTagValue(final ClientType clientType) {
    return switch (clientType) {
      case CLIENT_TYPE_IOS -> "ios";
      case CLIENT_TYPE_ANDROID_WITH_FCM -> "android-with-fcm";
      case CLIENT_TYPE_ANDROID_WITHOUT_FCM -> "android-without-fcm";
      case CLIENT_TYPE_UNSPECIFIED, UNRECOGNIZED -> "unrecognized";
    };
  }
}
