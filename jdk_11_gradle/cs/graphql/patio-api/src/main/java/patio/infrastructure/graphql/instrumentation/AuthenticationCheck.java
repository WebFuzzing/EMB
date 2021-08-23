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
package patio.infrastructure.graphql.instrumentation;

import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import java.util.Optional;
import java.util.function.Predicate;
import patio.infrastructure.graphql.Context;
import patio.infrastructure.graphql.ResultUtils;
import patio.infrastructure.utils.ErrorConstants;
import patio.infrastructure.utils.FunctionsUtils;

/**
 * Responsible for handling the rules over the @anonymousAllowed directive which are:
 *
 * <ul>
 *   <li>By default all queries require authentication
 *   <li>Introspection queries are allowed to be accessed anonymously
 *   <li>Queries annotated with @anonymousAllowed are allowed to be accessed anonymously
 *   <li>Fields with parents annotated with @anonymousAllowed are allowed as well
 * </ul>
 *
 * @since 0.1.0
 * @see ErrorConstants#BAD_CREDENTIALS
 */
public class AuthenticationCheck extends SimpleInstrumentation {

  private static final String FIELD_INTROSPECTION = "__schema";
  private static final String DIRECTIVE_ANONYMOUS = "anonymousAllowed";
  private static final Predicate<CheckerParams> CHECKERS =
      FunctionsUtils.any(
          AuthenticationCheck::isHierarchyAllowed,
          AuthenticationCheck::isUserPresent,
          AuthenticationCheck::isDirectivePresent,
          AuthenticationCheck::isIntrospection);

  /**
   * CheckerParams required to evaluate the authentication check
   *
   * @since 0.1.0
   */
  private static class CheckerParams {
    private final transient AuthenticationCheckState state;
    private final transient Context context;
    private final transient GraphQLFieldDefinition field;

    private CheckerParams(
        AuthenticationCheckState state, Context context, GraphQLFieldDefinition field) {
      this.state = state;
      this.context = context;
      this.field = field;
    }
  }

  @Override
  public InstrumentationState createState(InstrumentationCreateStateParameters params) {
    return new AuthenticationCheckState(false);
  }

  @Override
  public DataFetcher<?> instrumentDataFetcher(
      DataFetcher<?> fetcher, InstrumentationFieldFetchParameters params) {
    AuthenticationCheckState state = params.getInstrumentationState();
    Context context = params.getEnvironment().getContext();
    GraphQLFieldDefinition fieldDefinition = params.getField();

    var isAllowed = CHECKERS.test(new CheckerParams(state, context, fieldDefinition));
    state.setAllowed(isAllowed);

    return isAllowed ? super.instrumentDataFetcher(fetcher, params) : renderBadCredentials();
  }

  @SuppressWarnings("unchecked")
  private static <A> DataFetcher<A> renderBadCredentials() {
    return (env) -> (A) ResultUtils.render(ErrorConstants.BAD_CREDENTIALS);
  }

  private static boolean isIntrospection(CheckerParams cond) {
    return Optional.ofNullable(cond.field)
        .map(GraphQLFieldDefinition::getName)
        .map(name -> name.equals(FIELD_INTROSPECTION))
        .orElse(false);
  }

  private static boolean isHierarchyAllowed(CheckerParams cond) {
    return Optional.ofNullable(cond.state).map(AuthenticationCheckState::isAllowed).orElse(false);
  }

  private static boolean isUserPresent(CheckerParams cond) {
    return Optional.ofNullable(cond.context)
        .flatMap(ctx -> Optional.ofNullable(ctx.getAuthenticatedUser()))
        .isPresent();
  }

  private static boolean isDirectivePresent(CheckerParams cond) {
    return Optional.ofNullable(cond.field)
        .map(GraphQLFieldDefinition::getDefinition)
        .map(def -> def.getDirective(DIRECTIVE_ANONYMOUS))
        .isPresent();
  }
}
