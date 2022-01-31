package org.graphqlncs.resolver

import graphql.ErrorType
import graphql.GraphQLError
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.language.SourceLocation
import org.graphqlncs.type.*
import org.springframework.stereotype.Component


@Component
open class QueryResolver(
        private val triangleClassification: TriangleClassification,
        private val expint: Expint,
        private val fisher: Fisher,
        private val gammq: Gammq,
        private val remainder: Remainder,
        private val bessj: Bessj
) : GraphQLQueryResolver {


    fun triangleClassification(a: Int, b: Int, c: Int): Int {
        return triangleClassification.classify(a, b, c)
    }

    fun expint(n: Int, x: Double ): Double {
        return expint.exe(n, x)
    }

    fun fisher(m: Int, n: Int, x:Double  ): Double {

        if(m > 1000 || n > 1000){
            throw CustomException(400, "Invalid input")
        }
        return fisher.exe(m, n, x)
    }

    fun gammq(a: Double, x:Double  ): Double {
        return gammq.exe(a, x)
    }

    fun remainder(a: Int, b:Int  ): Int {
        val lim = 10000
        if (a > lim || a < -lim || b > lim || b < -lim)
            throw CustomException(400, "Invalid input")

        return remainder.exe(a, b)
    }

    fun bessj(n:Int, x:Double  ): Double {

        if (n <= 2 || n > 1000) {
            throw CustomException(400, "Invalid input")
        }

        return bessj.bessj(n, x)
    }
}

/**
 * This does not fully work... but at least we get:
 *
 * "Unexpected error occurred"
 *
 * instead of:
 *
 * "Internal Server Error(s)..."
 */
class CustomException(private val errorCode: Int, errorMessage: String) : RuntimeException(errorMessage), GraphQLError {

    override fun getExtensions(): Map<String, Any?>? {
        val customAttributes: MutableMap<String, Any?> = LinkedHashMap()
        customAttributes["errorCode"] = errorCode
        customAttributes["errorMessage"] = message
        return customAttributes
    }

    override fun getLocations(): List<SourceLocation>? {
        return null
    }

    override fun getErrorType(): ErrorType? {
        return ErrorType.ValidationError
    }

    @Suppress("ACCIDENTAL_OVERRIDE")
    override fun getMessage(): String? {
        return message
    }

}