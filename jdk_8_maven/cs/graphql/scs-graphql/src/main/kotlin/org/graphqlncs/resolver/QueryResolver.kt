package org.graphqlncs.resolver

import org.graphqlncs.type.TriangleClassification
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
open class QueryResolver(
    private val triangleClassif: TriangleClassification
) : GraphQLQueryResolver {


    fun triangleClassification(a: Int, b: Int, c: Int): Int {
        return triangleClassif.classify(a, b, c)
    }

}