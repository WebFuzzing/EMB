package org.graphqlncs.resolver

import graphql.kickstart.tools.GraphQLQueryResolver
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
        return fisher.exe(m, n, x)
    }

    fun gammq(a: Double, x:Double  ): Double {
        return gammq.exe(a, x)
    }

    fun remainder(a: Int, b:Int  ): Int {
        return remainder.exe(a, b)
    }

    fun bessj(n:Int, x:Double  ): Double {
        return bessj(n, x)
    }
}