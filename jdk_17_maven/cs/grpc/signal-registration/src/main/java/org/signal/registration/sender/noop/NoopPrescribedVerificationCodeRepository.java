/*
 * Copyright 2022 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.signal.registration.sender.noop;

import com.google.i18n.phonenumbers.Phonenumber;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.signal.registration.Environments;
import org.signal.registration.sender.prescribed.PrescribedVerificationCodeRepository;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Requires(env = Environments.DEVELOPMENT)
@Requires(missingBeans = PrescribedVerificationCodeRepository.class)
@Singleton
public class NoopPrescribedVerificationCodeRepository implements PrescribedVerificationCodeRepository {
  private Map<Phonenumber.PhoneNumber, String> map = Collections.emptyMap();

  @Override
  public CompletableFuture<Map<Phonenumber.PhoneNumber, String>> getVerificationCodes() {
    return CompletableFuture.completedFuture(map);
  }
}
