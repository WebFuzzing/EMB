/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.infrastructure.email.services.internal;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;

/**
 * Given a configuration provides AWS credentials
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultAwsCredentialsProvider implements AWSCredentialsProvider {
  private final transient String accessKey;
  private final transient String secretKey;

  /**
   * Initializes the provider with configuration
   *
   * @param awsAccessKey credentials access key
   * @param awsSecretKey credentials secret key
   * @since 0.1.0
   */
  public DefaultAwsCredentialsProvider(
      @Value("${aws.credentials.accesskey:none}") String awsAccessKey,
      @Value("${aws.credentials.secretkey:none}") String awsSecretKey) {
    this.accessKey = awsAccessKey;
    this.secretKey = awsSecretKey;
  }

  @Override
  public AWSCredentials getCredentials() {
    return new BasicAWSCredentials(this.accessKey, this.secretKey);
  }

  @Override
  public void refresh() {
    // not implemented
  }
}
