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
package patio.infrastructure.graphql.scalars.internal;

import static patio.infrastructure.graphql.scalars.internal.ScalarsUtils.throwLiteralException;
import static patio.infrastructure.graphql.scalars.internal.ScalarsUtils.throwValueException;
import static patio.infrastructure.utils.FunctionsUtils.safely;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Operations to convert from {@link String} to {@link UUID} and viceversa
 *
 * @since 0.1.0
 */
public class UUIDCoercing implements Coercing<UUID, String> {

  private static final Function<String, String> REMOVE_SIMPLE_QUOTES =
      (source) -> source.replaceAll("'", "");

  @Override
  public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
    UUID id = (UUID) dataFetcherResult;

    return id.toString();
  }

  @Override
  public UUID parseValue(Object input) throws CoercingParseValueException {
    return Optional.ofNullable(input)
        .map(Object::toString)
        .map(REMOVE_SIMPLE_QUOTES)
        .flatMap(safely(UUID::fromString, (th) -> throwValueException("ID", th)))
        .orElse(null);
  }

  @Override
  public UUID parseLiteral(Object input) throws CoercingParseLiteralException {
    return Optional.ofNullable(input)
        .filter(o -> o instanceof StringValue)
        .map(StringValue.class::cast)
        .map(StringValue::getValue)
        .map(REMOVE_SIMPLE_QUOTES)
        .flatMap(safely(UUID::fromString, (th) -> throwLiteralException("ID", th)))
        .orElse(null);
  }
}
