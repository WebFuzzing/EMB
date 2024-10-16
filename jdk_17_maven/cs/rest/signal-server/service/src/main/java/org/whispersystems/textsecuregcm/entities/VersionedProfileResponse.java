/*
 * Copyright 2013-2021 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.whispersystems.textsecuregcm.util.ByteArrayBase64WithPaddingAdapter;

public class VersionedProfileResponse {

  @JsonUnwrapped
  private BaseProfileResponse baseProfileResponse;

  @JsonProperty
  @JsonSerialize(using = ByteArrayBase64WithPaddingAdapter.Serializing.class)
  @JsonDeserialize(using = ByteArrayBase64WithPaddingAdapter.Deserializing.class)
  private byte[] name;

  @JsonProperty
  @JsonSerialize(using = ByteArrayBase64WithPaddingAdapter.Serializing.class)
  @JsonDeserialize(using = ByteArrayBase64WithPaddingAdapter.Deserializing.class)
  private byte[] about;

  @JsonProperty
  @JsonSerialize(using = ByteArrayBase64WithPaddingAdapter.Serializing.class)
  @JsonDeserialize(using = ByteArrayBase64WithPaddingAdapter.Deserializing.class)
  private byte[] aboutEmoji;

  @JsonProperty
  private String avatar;

  @JsonProperty
  @JsonSerialize(using = ByteArrayBase64WithPaddingAdapter.Serializing.class)
  @JsonDeserialize(using = ByteArrayBase64WithPaddingAdapter.Deserializing.class)
  private byte[] paymentAddress;

  public VersionedProfileResponse() {
  }

  public VersionedProfileResponse(final BaseProfileResponse baseProfileResponse,
      final byte[] name,
      final byte[] about,
      final byte[] aboutEmoji,
      final String avatar,
      final byte[] paymentAddress) {

    this.baseProfileResponse = baseProfileResponse;
    this.name = name;
    this.about = about;
    this.aboutEmoji = aboutEmoji;
    this.avatar = avatar;
    this.paymentAddress = paymentAddress;
  }

  public BaseProfileResponse getBaseProfileResponse() {
    return baseProfileResponse;
  }

  public byte[] getName() {
    return name;
  }

  public byte[] getAbout() {
    return about;
  }

  public byte[] getAboutEmoji() {
    return aboutEmoji;
  }

  public String getAvatar() {
    return avatar;
  }

  public byte[] getPaymentAddress() {
    return paymentAddress;
  }
}
