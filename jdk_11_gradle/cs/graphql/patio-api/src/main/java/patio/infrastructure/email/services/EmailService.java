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
package patio.infrastructure.email.services;

import patio.infrastructure.email.domain.Email;

/**
 * Represents the actions available in an email service
 *
 * @since 0.1.0
 */
public interface EmailService {

  /**
   * Sends the email passed as parameter
   *
   * @param email an instance of type {@link Email}
   * @since 0.1.0
   */
  void send(Email email);
}
