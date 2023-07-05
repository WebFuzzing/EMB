/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.sender.twilio.verify;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;

@Context
@ConfigurationProperties("twilio.verify")
class TwilioVerifyConfiguration {

  @NotBlank
  private String serviceSid;

  @Nullable
  private String serviceFriendlyName;

  @NotBlank
  private String androidAppHash;

  private List<@NotBlank String> supportedLanguages = Collections.emptyList();

  public String getServiceSid() {
    return serviceSid;
  }

  public void setServiceSid(final String serviceSid) {
    this.serviceSid = serviceSid;
  }

  public String getServiceFriendlyName() {
    return serviceFriendlyName;
  }

  public void setServiceFriendlyName(final String serviceFriendlyName) {
    this.serviceFriendlyName = serviceFriendlyName;
  }

  public String getAndroidAppHash() {
    return androidAppHash;
  }

  public void setAndroidAppHash(final String androidAppHash) {
    this.androidAppHash = androidAppHash;
  }

  public List<String> getSupportedLanguages() {
    return supportedLanguages;
  }

  public void setSupportedLanguages(final List<String> supportedLanguages) {
    this.supportedLanguages = supportedLanguages;
  }
}
