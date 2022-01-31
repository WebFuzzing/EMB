package org.graphqlncs

import graphql.ErrorClassification
import graphql.ErrorType


open class ExpTy(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

     override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

     override fun getErrorType(): ErrorClassification {
        return ErrorType.ValidationError
    }
}
