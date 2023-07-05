/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.signal.registration.analytics;

import org.reactivestreams.Publisher;
import org.signal.registration.sender.AttemptData;
import org.signal.registration.sender.VerificationCodeSender;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An "attempt pending analysis" repository stores partial information about completed verification attempts with the
 * expectation that another component will provide additional information (price, for example) later. Implementations
 * should store attempts pending analysis on a best-effort basis and may discard them at any time for any reason.
 */
public interface AttemptPendingAnalysisRepository {

  /**
   * Stores an attempt pending analysis. If an attempt pending analysis already exists with the given sender name and
   * remote ID, it will be overwritten by the given event.
   *
   * @param attemptPendingAnalysis the attempt pending analysis to be stored
   *
   * @return a future that completes when the attempt pending analysis has been stored
   */
  CompletableFuture<Void> store(AttemptPendingAnalysis attemptPendingAnalysis);

  /**
   * Attempts to retrieve a specific attempt pending analysis by its sender name (see
   * {@link VerificationCodeSender#getName()}) and remote ID (see {@link AttemptData#remoteId()}.
   *
   * @param senderName the name of the {@link VerificationCodeSender} that produced the attempt pending analysis
   * @param remoteId the remote ID of the attempt pending analysis
   *
   * @return a future that yields the attempt pending analysis if available or an empty {@code Optional} if no attempt
   * pending analysis was found for the given sender name and remote ID
   *
   * @see VerificationCodeSender#getName()
   * @see AttemptData#remoteId()
   */
  CompletableFuture<Optional<AttemptPendingAnalysis>> getByRemoteIdentifier(String senderName, String remoteId);

  /**
   * Returns a publisher that yields all attempts pending analysis for the given sender.
   *
   * @param senderName the name of the sender for which to retrieve attempts pending analysis
   *
   * @return a publisher that yields all attempts pending analysis for the given sender
   *
   * @see VerificationCodeSender#getName()
   */
  Publisher<AttemptPendingAnalysis> getBySender(String senderName);

  /**
   * Removes an individual attempt pending analysis from this repository. Has no effect if no attempt pending analysis
   * exists with the given sender name/remote ID.
   *
   * @param senderName the name of the {@link VerificationCodeSender} that produced the attempt pending analysis
   * @param remoteId the remote ID of the attempt pending analysis
   *
   * @return a future that completes when the identified attempt pending analysis is no longer present in this
   * repository
   *
   * @see VerificationCodeSender#getName()
   * @see AttemptData#remoteId()
   */
  CompletableFuture<Void> remove(String senderName, String remoteId);
}
