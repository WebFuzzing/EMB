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
package patio.user.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests some functions in domain classes other than getters and setters.
 *
 * @since 0.1.0
 */
public class UserTests {

  @ParameterizedTest(name = "Test getting hash emails: email [{0}]")
  @ValueSource(strings = {"somebody@email.com", "SOMEBODY@email.com", "somebody@EMAIL.com"})
  void testGetHash(String email) {
    // given: a user with email
    User user = User.builder().with(u -> u.setEmail(email)).build();

    // when: getting user's hash
    String hash = user.getHash();

    // then: it should match the provided value
    assertEquals(hash, "66b0ef1ce525f909ad733d06415331d5");
  }
}
